/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.server;

import pt.ua.dicoogle.core.settings.ServerSettingsManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.CommandUtils;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.DicomServiceException;

///import org.dcm4che2.net.Executor;
/** dcm4che doesn't support Executor anymore, so now import from java.util */
import org.slf4j.LoggerFactory;

import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.PDVInputStream;
import org.dcm4che2.net.Status;
import org.dcm4che2.net.TransferCapability;
import org.dcm4che2.net.service.StorageService;
import org.dcm4che2.net.service.VerificationService;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.IndexerInterface;
import pt.ua.dicoogle.sdk.StorageInterface;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;

/**
 * DICOM Storage Service is provided by this class
 *
 * @author Luís Bastião Silva <bastiao@bmd-softwre.com>
 * @author Marco Pereira
 */

public class DicomStorage extends StorageService {

    public class PoiDicomDevice extends Device {
        /**
         * Constructor which sets the name of this device.
         * 
         * @param deviceName String
         */
        public PoiDicomDevice(String deviceName) {
            super(deviceName);
        }

        /**
         * Get a specific <code>NetworkApplicationEntity</code> object by it's AE title.
         * 
         * @param aet A String containing the AE title.
         * @return The <code>NetworkApplicationEntity</code> corresponding to the aet parameter.
         */
        @Override
        public NetworkApplicationEntity getNetworkApplicationEntity(String aet) {
            NetworkApplicationEntity[] naes = this.getNetworkApplicationEntity();
            if (naes.length >= 1) {
                return naes[0];
            }
            return null;
        }
    }

    private SOPList list;
    private ServerSettings settings;

    private Executor executor = new NewThreadExecutor("DicoogleStorage");
    private Device device = new PoiDicomDevice("DicoogleStorage");
    private NetworkApplicationEntity nae = new NetworkApplicationEntity();
    private NetworkConnection nc = new NetworkConnection();

    private String path;

    private int threadPoolSize = 10;

    private ExecutorService pool = Executors.newFixedThreadPool(threadPoolSize);

    private Set<String> alternativeAETs = new HashSet<>();
    private Set<String> priorityAETs = new HashSet<>();

    // Changed to support priority queue.
    private BlockingQueue<ImageElement> queue = new PriorityBlockingQueue<ImageElement>();
    private NetworkApplicationEntity[] naeArr = null;

    /**
     *
     * @param Services List of supported SOP Classes
     * @param l        list of Supported SOPClasses with supported Transfer Syntax
     */

    public DicomStorage(String[] Services, SOPList l) {
        // just because the call to super must be the first instruction
        super(Services);

        // our configuration format
        list = l;
        settings = ServerSettingsManager.getSettings();

        // Added default alternative AETitle.
        // alternativeAETs.add(ServerSettingsManager.getSettings().getNodeName());

        path = settings.getArchiveSettings().getMainDirectory();
        if (path == null) {
            path = "/dev/null";
        }

        this.priorityAETs = new HashSet<>(settings.getDicomServicesSettings().getPriorityAETitles());
        LoggerFactory.getLogger(DicomStorage.class).debug("Priority C-STORE: " + this.priorityAETs);

        device.setNetworkApplicationEntity(nae);

        device.setNetworkConnection(nc);
        nae.setNetworkConnection(nc);

        // we accept assoociations, this is a server
        nae.setAssociationAcceptor(true);
        // we support the VerificationServiceSOP
        nae.register(new VerificationService());
        // and the StorageServiceSOP
        nae.register(this);

        nae.setAETitle(settings.getDicomServicesSettings().getAETitle());

        nc.setPort(settings.getDicomServicesSettings().getStorageSettings().getPort());

        this.nae.setInstalled(true);
        this.nae.setAssociationAcceptor(true);
        this.nae.setAssociationInitiator(false);

        int maxPDULengthReceive =
                settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxPDULengthReceive();
        int maxPDULengthSend = settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxPDULengthSend();

        ServerSettings s = ServerSettingsManager.getSettings();
        this.nae.setDimseRspTimeout(60000 * 300);
        this.nae.setIdleTimeout(60000 * 300);
        this.nae.setMaxPDULengthReceive(maxPDULengthReceive + 1000);
        this.nae.setMaxPDULengthSend(maxPDULengthSend + 1000);
        this.nae.setRetrieveRspTimeout(60000 * 300);

        // Added alternative AETitles.

        naeArr = new NetworkApplicationEntity[alternativeAETs.size() + 1];
        // Just adding the first AETitle
        naeArr[0] = nae;

        int k = 1;

        for (String alternativeAET : alternativeAETs) {
            NetworkApplicationEntity nae2 = new NetworkApplicationEntity();
            nae2.setNetworkConnection(nc);
            nae2.setDimseRspTimeout(60000 * 300);
            nae2.setIdleTimeout(60000 * 300);
            nae2.setMaxPDULengthReceive(maxPDULengthReceive + 1000);
            nae2.setMaxPDULengthSend(maxPDULengthSend + 1000);
            nae2.setRetrieveRspTimeout(60000 * 300);
            // we accept assoociations, this is a server
            nae2.setAssociationAcceptor(true);
            // we support the VerificationServiceSOP
            nae2.register(new VerificationService());
            // and the StorageServiceSOP
            nae2.register(this);
            nae2.setAETitle(alternativeAET);
            ServerSettings settings = ServerSettingsManager.getSettings();
            Collection<String> caet = settings.getDicomServicesSettings().getAllowedAETitles();
            if (!caet.isEmpty()) {
                String[] array = new String[caet.size()];
                caet.toArray(array);
                nae2.setPreferredCallingAETitle(array);
            }
            naeArr[k] = nae2;
            k++;

        }

        // Just set the Network Application Entity array - which accepts a set of AEs.
        device.setNetworkApplicationEntity(naeArr);

        initTS(Services);
    }

    /**
     * Sets the tranfer capability for this execution of the storage service
     * 
     * @param Services Services to be supported
     */
    private void initTS(String[] Services) {
        int count = list.getAccepted();
        // System.out.println(count);
        TransferCapability[] tc = new TransferCapability[count + 1];
        String[] Verification = {UID.ImplicitVRLittleEndian, UID.ExplicitVRLittleEndian, UID.ExplicitVRBigEndian};
        String[] TS;
        TransfersStorage local;

        tc[0] = new TransferCapability(UID.VerificationSOPClass, Verification, TransferCapability.SCP);
        int j = 0;
        for (int i = 0; i < Services.length; i++) {
            count = 0;
            local = list.getTS(Services[i]);
            if (local.getAccepted()) {
                TS = local.getVerboseTS();
                if (TS != null) {

                    tc[j + 1] = new TransferCapability(Services[i], TS, TransferCapability.SCP);
                    j++;
                }
            }
        }

        // Setting the TS in all NetworkApplicationEntitys
        for (int i = 0; i < naeArr.length; i++) {

            naeArr[i].setTransferCapability(tc);
        }
        nae.setTransferCapability(tc);
    }

    @Override
    /**
     * Called when a C-Store Request has been accepted Parameters defined by dcm4che2
     */
    public void cstore(final Association as, final int pcid, DicomObject rq, PDVInputStream dataStream, String tsuid)
            throws DicomServiceException, IOException {
        // DebugManager.getSettings().debug(":: Verify Permited AETs @??C-Store Request ");

        boolean permited = false;
        // TODO(nagi): Read allowed calling AETs
        Collection<String> allowedAETitles = new ArrayList<String>();
        if (allowedAETitles.isEmpty()) {
            permited = true;
        } else {
            permited = allowedAETitles.contains(as.getCallingAET());
        }

        if (!permited) {
            // DebugManager.getSettings().debug("Client association NOT permited: " + as.getCallingAET() + "!");
            System.err.println("Client association NOT permited: " + as.getCallingAET() + "!");
            as.abort();

            return;
        }

        final DicomObject rsp = CommandUtils.mkRSP(rq, CommandUtils.SUCCESS);
        onCStoreRQ(as, pcid, rq, dataStream, tsuid, rsp);
        as.writeDimseRSP(pcid, rsp);
        // onCStoreRSP(as, pcid, rq, dataStream, tsuid, rsp);
    }

    @Override
    /**
     * Actually do the job of saving received file on disk on this server with extras such as Lucene
     * indexing and DICOMDIR update
     */
    protected void onCStoreRQ(Association as, int pcid, DicomObject rq, PDVInputStream dataStream, String tsuid,
            DicomObject rsp) throws IOException, DicomServiceException {
        try {

            String cuid = rq.getString(Tag.AffectedSOPClassUID);
            String iuid = rq.getString(Tag.AffectedSOPInstanceUID);

            DicomObject d = dataStream.readDataset();

            d.initFileMetaInformation(cuid, iuid, tsuid);

            Iterable<StorageInterface> plugins = PluginController.getInstance().getStoragePlugins(true);

            URI uri = null;
            Boolean isStored = false;
            for (StorageInterface storage : plugins) {
                uri = storage.store(as.getCalledAET(), d);
                if (uri != null) {
                    isStored = true;
                    // queue to index
                    ImageElement element = new ImageElement();
                    element.setCallingAET(as.getCallingAET());
                    element.setUri(uri);
                    queue.add(element);
                }
            }
            if (!isStored) {
                throw new IOException("Object is not stored");
            }

        } catch (IOException e) {
            throw new DicomServiceException(rq, Status.ProcessingFailure, e.getMessage());
        }
    }

    /**
     * ImageElement is a entry of a C-STORE. For Each C-STORE RQ an ImageElement is created and are put
     * in the queue to index.
     *
     * This only happens after the store in Storage Plugins.
     *
     * @param <E>
     */
    class ImageElement<E extends Comparable<? super E>> implements Comparable<ImageElement<E>> {
        private URI uri;
        private String callingAET;

        public URI getUri() {
            return uri;
        }

        public void setUri(URI uri) {
            this.uri = uri;
        }

        public String getCallingAET() {
            return callingAET;
        }

        public void setCallingAET(String callingAET) {
            this.callingAET = callingAET;
        }

        @Override
        public int compareTo(ImageElement<E> o1) {
            if (o1.getCallingAET().equals(this.getCallingAET()))
                return 0;
            else if (priorityAETs.contains(this.getCallingAET()))
                return -1;
            else
                return 1;
        }
    }

    class Indexer extends Thread {
        public Collection<IndexerInterface> plugins;

        public void run() {
            while (true) {
                try {
                    // Fetch an element by the queue taking into account the priorities.
                    ImageElement element = queue.take();
                    URI exam = element.getUri();
                    if (exam != null) {
                        List<Report> reports = PluginController.getInstance().indexBlocking(exam);
                    }
                } catch (InterruptedException ex) {
                    LoggerFactory.getLogger(DicomStorage.class).error(ex.getMessage(), ex);
                }

            }

        }
    }

    private String getFullPath(DicomObject d) {

        return getDirectory(d) + File.separator + getBaseName(d);

    }

    private String getFullPathCache(String dir, DicomObject d) {
        return dir + File.separator + getBaseName(d);

    }

    private String getBaseName(DicomObject d) {
        String result = "UNKNOWN.dcm";
        String sopInstanceUID = d.getString(Tag.SOPInstanceUID);
        return sopInstanceUID + ".dcm";
    }

    private String getDirectory(DicomObject d) {

        String result = "UN";

        String institutionName = d.getString(Tag.InstitutionName);
        String modality = d.getString(Tag.Modality);
        String studyDate = d.getString(Tag.StudyDate);
        String accessionNumber = d.getString(Tag.AccessionNumber);
        String studyInstanceUID = d.getString(Tag.StudyInstanceUID);
        String patientName = d.getString(Tag.PatientName);

        if (institutionName == null || institutionName.equals("")) {
            institutionName = "UN_IN";
        }
        institutionName = institutionName.trim();
        institutionName = institutionName.replace(" ", "");
        institutionName = institutionName.replace(".", "");
        institutionName = institutionName.replace("&", "");

        if (modality == null || modality.equals("")) {
            modality = "UN_MODALITY";
        }

        if (studyDate == null || studyDate.equals("")) {
            studyDate = "UN_DATE";
        } else {
            try {
                String year = studyDate.substring(0, 4);
                String month = studyDate.substring(4, 6);
                String day = studyDate.substring(6, 8);

                studyDate = year + File.separator + month + File.separator + day;

            } catch (Exception e) {
                e.printStackTrace();
                studyDate = "UN_DATE";
            }
        }

        if (accessionNumber == null || accessionNumber.equals("")) {
            patientName = patientName.trim();
            patientName = patientName.replace(" ", "");
            patientName = patientName.replace(".", "");
            patientName = patientName.replace("&", "");

            if (patientName == null || patientName.equals("")) {
                if (studyInstanceUID == null || studyInstanceUID.equals("")) {
                    accessionNumber = "UN_ACC";
                } else {
                    accessionNumber = studyInstanceUID;
                }
            } else {
                accessionNumber = patientName;

            }

        }

        result = path + File.separator + institutionName + File.separator + modality + File.separator + studyDate
                + File.separator + accessionNumber;

        return result;

    }

    private Indexer indexer = new Indexer();

    /*
     * Start the Storage Service
     * 
     * @throws java.io.IOException
     */
    public void start() throws IOException {
        // dirc = new DicomDirCreator(path, "Dicoogle");
        pool = Executors.newFixedThreadPool(threadPoolSize);
        device.startListening(executor);
        indexer.start();

    }

    /**
     * Stop the storage service
     */
    public void stop() {
        this.pool.shutdown();
        try {
            pool.awaitTermination(6, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(DicomStorage.class).error(ex.getMessage(), ex);
        }
        device.stopListening();

        // dirc.dicomdir_close();
    }
}
