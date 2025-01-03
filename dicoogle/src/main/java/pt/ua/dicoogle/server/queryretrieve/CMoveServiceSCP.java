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
package pt.ua.dicoogle.server.queryretrieve;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerConfigurationException;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.ConfigurationException;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.Status;
import pt.ua.dicoogle.core.exceptions.CFindNotSupportedException;

import pt.ua.dicoogle.DicomLog.LogDICOM;
import pt.ua.dicoogle.DicomLog.LogLine;
import pt.ua.dicoogle.DicomLog.LogXML;
import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.StorageInterface;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;
import pt.ua.dicoogle.server.DicomNetwork;
import pt.ua.dicoogle.server.SearchDicomResult;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class CMoveServiceSCP extends CMoveService {
    private static final Logger logger = LoggerFactory.getLogger(CMoveServiceSCP.class);


    private DicomNetwork service = null;

    public CMoveServiceSCP(String[] sopClasses, Executor executor) {
        super(sopClasses, executor);
    }

    public CMoveServiceSCP(String sopClass, Executor executor) {
        super(sopClass, executor);
    }

    @Override

    protected DimseRSP doCMove(Association as, int pcid, DicomObject cmd, DicomObject data, DicomObject rsp)
            throws DicomServiceException {
        /**
         * Verify Permited AETs
         */
        boolean permited = false;

        if (ServerSettingsManager.getSettings().getDicomServicesSettings().getAllowedAETitles().isEmpty()) {
            permited = true;
        } else {
            permited = ServerSettingsManager.getSettings().getDicomServicesSettings().getAllowedAETitles()
                    .contains(as.getCallingAET());
        }

        if (!permited) {
            as.abort();
            return new MoveRSP(data, rsp);
        } else {
            logger.info("Client association with {} is permitted!", as.getCallingAET());
        }

        try {
            Thread.sleep(ServerSettingsManager.getSettings().getDicomServicesSettings().getQueryRetrieveSettings()
                    .getRspDelay());
        } catch (Exception e) {
            logger.error("Error while waiting ", e);
        }

        /**
         *
         * Now it is the code to move
         * In this sense it have a fork, besides we can open the store request
         * in the source direction, or it can make a store to a third party
         *
         */
        /** Get the real IP to move */
        InetAddress ip = as.getSocket().getInetAddress();
        /** Get the port to move */
        int portAddr = as.getSocket().getPort();

        String destination = cmd.getString(org.dcm4che2.data.Tag.MoveDestination);

        /** Verify if it have the field destination */
        if (destination == null) {
            throw new DicomServiceException(cmd, Status.UnrecognizedOperation, "Missing Move Destination");
        }

        String SOPUID = new String(data.get(Integer.parseInt("0020000D", 16)).getBytes());
        String CMoveID = cmd.getString(org.dcm4che2.data.Tag.MessageID);

        /**
         * Get object to search
         */
        ArrayList<String> extrafields = null;
        extrafields = new ArrayList<String>();

        extrafields.add("PatientName");
        extrafields.add("PatientID");
        extrafields.add("Modality");
        extrafields.add("StudyDate");
        extrafields.add("Thumbnail");
        extrafields.add("StudyInstanceUID");

        SearchDicomResult.QUERYLEVEL level = null;
        if (CFindBuilder.isPatientRoot(rsp)) {
            level = SearchDicomResult.QUERYLEVEL.PATIENT;
        } else if (CFindBuilder.isStudyRoot(rsp)) {
            level = SearchDicomResult.QUERYLEVEL.STUDY;
        }

        CFindBuilder cfind = null;
        try {
            cfind = new CFindBuilder(data, rsp);
        } catch (CFindNotSupportedException ex) {
            ex.printStackTrace();
        }
        String query = cfind.getQueryString();

        SearchDicomResult search = new SearchDicomResult(query, true, extrafields, SearchDicomResult.QUERYLEVEL.IMAGE);
        ArrayList<URI> files = new ArrayList<URI>();

        if (search != null) {
            while (search.hasNext()) {
                DicomObject obj = search.next();
                DicomElement e = obj.get(Integer.parseInt("0020000D", 16));
                String tmp = null;
                if (e != null) {
                    tmp = new String(e.getBytes());
                }
                if (SOPUID != null && tmp != null) {
                    // files.add(new File(search.getCurrentFile()));
                    String uriString = search.getCurrentFile();

                    try {
                        URI nURI = new URI(uriString);

                        files.add(nURI);
                    } catch (URISyntaxException ex) {
                        LoggerFactory.getLogger(CMoveServiceSCP.class).error(ex.getMessage(), ex);
                    }
                }

            }
        }

        if (files.size() != 0) {

            /**
             * What is the destination?
             *
             */
            String hostDest = ip.getHostAddress();
            ServerSettings.DicomServices ob = ServerSettingsManager.getSettings().getDicomServicesSettings();
            for (MoveDestination m : ob.getMoveDestinations()) {
                if (m.getAETitle().equals(destination)) {
                    hostDest = m.getIpAddrs();
                    portAddr = m.getPort();
                }
            }


            LogLine ll = new LogLine("cmove", destination,
                    "Files: " + files.size() + " -- (" + hostDest + ":" + portAddr + ")",
                    "studyUID=" + data.getString(Tag.StudyInstanceUID));
            LogDICOM.getInstance().addLine(ll);

            if (LogDICOM.getInstance().isPersistent()) {
                synchronized (LogDICOM.getInstance()) {
                    try {
                        LogXML l = new LogXML();
                        l.printXML();
                    } catch (TransformerConfigurationException ex) {
                        LoggerFactory.getLogger(CMoveServiceSCP.class).error(ex.getMessage(), ex);
                    }
                }
            }

            if (CMoveID == null || CMoveID.equals("")) {
                logger.warn("No originator message ID, aborting");
                return null;
            }
            try {
                logger.debug("Destination: {}", destination);
                SendReport report = callDCMSend(files, portAddr, hostDest, destination, CMoveID);

                // fill in properties about sub-operations
                rsp.putInt(Tag.NumberOfCompletedSuboperations, VR.US, report.filesSent);
                int remaining = report.filesToSend - report.filesSent - report.filesFailed;
                if (remaining > 0) {
                    rsp.putInt(Tag.NumberOfRemainingSuboperations, VR.US, remaining);
                }
                int totalErrors = report.filesFailed + report.filesSkipped;
                if (totalErrors > 0) {
                    rsp.putInt(Tag.NumberOfFailedSuboperations, VR.US, totalErrors);
                }
                if (report.errorID == 0) {
                    // report warning if there is at least one file failure
                    int code = totalErrors > 0 ? 0xB000 : Status.Success;
                    rsp.putInt(Tag.Status, VR.US, code);
                } else {
                    rsp.putInt(Tag.Status, VR.US, Status.ProcessingFailure);
                    rsp.putInt(Tag.ErrorID, VR.US, ERROR_ID_FILE_TRANSMISSION);
                }

            } catch (Exception ex) {
                logger.error("Failed to send files to DICOM node {}", destination, ex);
                rsp.putInt(Tag.Status, VR.US, Status.ProcessingFailure);
                rsp.putInt(Tag.ErrorID, VR.US, ERROR_ID_GENERAL_FAILURE);
            }
        }

        return new MoveRSP(data, rsp);
    }

    private static final int ERROR_ID_FILE_TRANSMISSION = 1;
    private static final int ERROR_ID_GENERAL_FAILURE = 10;

    /**
     * @return the service
     */
    public DicomNetwork getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(DicomNetwork service) {
        this.service = service;
    }

    private static class SendReport {
        final int errorID;
        final int filesSent;
        final int filesToSend;
        final int filesFailed;
        final int filesSkipped;

        public SendReport(int errorID, int filesSent, int filesToSend, int filesFailed, int filesSkipped) {
            this.errorID = errorID;
            this.filesSent = filesSent;
            this.filesToSend = filesToSend;
            this.filesFailed = filesFailed;
            this.filesSkipped = filesSkipped;
        }
    }

    private static SendReport callDCMSend(List<URI> files, int port, String hostname, String AETitle, String cmoveID)
            throws IOException, ConfigurationException, InterruptedException {

        DicoogleDcmSend dcmsnd = new DicoogleDcmSend();

        dcmsnd.setRemoteHost(hostname);
        dcmsnd.setRemotePort(port);

        for (URI uri : files) {
            StorageInterface plugin = PluginController.getInstance().getStorageForSchema(uri);
            logger.debug("uri: {}", uri);

            if (plugin != null) {
                logger.debug("Retrieving {}", uri);
                Iterable<StorageInputStream> it = plugin.at(uri);

                for (StorageInputStream file : it) {
                    dcmsnd.addFile(file);
                    logger.debug("Added file to DcmSend: {}", uri);
                }
            }

        }
        dcmsnd.setCalledAET(AETitle);

        dcmsnd.configureTransferCapability();

        dcmsnd.setMoveOriginatorMessageID(cmoveID);
        dcmsnd.start();
        dcmsnd.open();
        int errorID;
        try {
            dcmsnd.send();
            errorID = 0;
        } catch (IOException ex) {
            // assume that there was a fatal error in the transmission
            errorID = ERROR_ID_FILE_TRANSMISSION;
        } finally {
            dcmsnd.close();
        }

        return new SendReport(errorID, dcmsnd.getNumberOfFilesSent(), dcmsnd.getNumberOfFilesToSend(),
                dcmsnd.getNumberOfFilesFailed(), dcmsnd.getNumberOfFilesSkipped());
    }
}
