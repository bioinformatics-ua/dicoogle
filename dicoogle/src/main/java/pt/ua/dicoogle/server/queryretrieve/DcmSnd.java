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

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.zip.GZIPInputStream;


import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.UIDDictionary;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.dcm4che2.io.TranscoderInputHandler;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.ConfigurationException;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.DimseRSPHandler;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.NoPresentationContextException;
import org.dcm4che2.net.PDVOutputStream;
import org.dcm4che2.net.TransferCapability;
import org.dcm4che2.net.UserIdentity;
import org.dcm4che2.net.service.StorageCommitmentService;
import org.dcm4che2.util.CloseUtils;
import org.dcm4che2.util.StringUtils;
import org.dcm4che2.util.UIDUtils;
import pt.ua.dicoogle.core.settings.ServerSettings;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Revision: 10933 $ $Date: 2009-04-21 01:48:38 +0100 (Ter, 21 Abr 2009) $
 * @since Oct 13, 2005
 */
public class DcmSnd extends StorageCommitmentService {

    private static final int KB = 1024;

    private static final int MB = KB * KB;

    private static final int PEEK_LEN = 1024;

    private static final String USAGE =
        "dcmsnd [Options] <aet>[@<host>[:<port>]] <file>|<directory>...";

    private static final String DESCRIPTION =
        "\nLoad composite DICOM Object(s) from specified DICOM file(s) and send it "
      + "to the specified remote Application Entity. If a directory is specified,"
      + "DICOM Object in files under that directory and further sub-directories "
      + "are sent. If <port> is not specified, DICOM default port 104 is assumed. "
      + "If also no <host> is specified, localhost is assumed. Optionally, a "
      + "Storage Commitment Request for successfully tranferred objects is sent "
      + "to the remote Application Entity after the storage. The Storage Commitment "
      + "result is accepted on the same association or - if a local port is "
      + "specified by option -L - in a separate association initiated by the "
      + "remote Application Entity\n"
      + "OPTIONS:";

    private static final String EXAMPLE =
        "\nExample: dcmsnd -stgcmt -L DCMSND:11113 STORESCP@localhost:11112 image.dcm \n"
      + "=> Start listening on local port 11113 for receiving Storage Commitment "
      + "results, send DICOM object image.dcm to Application Entity STORESCP, "
      + "listening on local port 11112, and request Storage Commitment in same association.";

    private static String[] TLS1 = { "TLSv1" };

    private static String[] SSL3 = { "SSLv3" };

    private static String[] NO_TLS1 = { "SSLv3", "SSLv2Hello" };

    private static String[] NO_SSL2 = { "TLSv1", "SSLv3" };

    private static String[] NO_SSL3 = { "TLSv1", "SSLv2Hello" };

    private static char[] SECRET = { 's', 'e', 'c', 'r', 'e', 't' };

    private static final String[] ONLY_IVLE_TS = {
        UID.ImplicitVRLittleEndian
    };

    private static final String[] IVLE_TS = {
        UID.ImplicitVRLittleEndian,
        UID.ExplicitVRLittleEndian,
        UID.ExplicitVRBigEndian,
    };

    private static final String[] EVLE_TS = {
        UID.ExplicitVRLittleEndian,
        UID.ImplicitVRLittleEndian,
        UID.ExplicitVRBigEndian,
    };

    private static final String[] EVBE_TS = {
        UID.ExplicitVRBigEndian,
        UID.ExplicitVRLittleEndian,
        UID.ImplicitVRLittleEndian,
    };

    private static final int STG_CMT_ACTION_TYPE = 1;

    /** TransferSyntax: DCM4CHE URI Referenced */
    private static final String DCM4CHEE_URI_REFERENCED_TS_UID =
            "1.2.40.0.13.1.1.2.4.94";

    private Executor executor = new NewThreadExecutor("DCMSND");

    private NetworkApplicationEntity remoteAE = new NetworkApplicationEntity();

    private NetworkApplicationEntity remoteStgcmtAE;

    private NetworkConnection remoteConn = new NetworkConnection();

    private NetworkConnection remoteStgcmtConn = new NetworkConnection();

    private Device device = new Device("DCMSND");

    private NetworkApplicationEntity ae = new NetworkApplicationEntity();

    private NetworkConnection conn = new NetworkConnection();

    private Map<String, Set<String>> as2ts = new HashMap<String, Set<String>>();

    private ArrayList<FileInfo> files = new ArrayList<FileInfo>();

    private Association assoc;

    private int priority = 0;

    private int transcoderBufferSize = 1024;

    private int filesSent = 0;

    private long totalSize = 0L;

    private boolean fileref = false;

    private boolean stgcmt = false;

    private long shutdownDelay = 1000L;

    private DicomObject stgCmtResult;

    private String keyStoreURL = "resource:tls/test_sys_1.p12";

    private char[] keyStorePassword = SECRET;

    private char[] keyPassword;

    private String trustStoreURL = "resource:tls/mesa_certs.jks";

    private char[] trustStorePassword = SECRET;
    
    private String MoveOriginatorMessageID = null;

    private boolean gzip = ServerSettings.getInstance().isGzipStorage();
    
    public DcmSnd() {
        remoteAE.setInstalled(true);
        remoteAE.setAssociationAcceptor(true);
        remoteAE.setNetworkConnection(new NetworkConnection[] { remoteConn });

        device.setNetworkApplicationEntity(ae);
        device.setNetworkConnection(conn);
        ae.setNetworkConnection(conn);
        ae.setAssociationInitiator(true);
        ae.setAssociationAcceptor(true);
        ae.register(this);
        ae.setAETitle(ServerSettings.getInstance().getAE());
    }

    public final void setLocalHost(String hostname) {
        conn.setHostname(hostname);
    }

    public final void setLocalPort(int port) {
        conn.setPort(port);
    }

    public final void setRemoteHost(String hostname) {
        remoteConn.setHostname(hostname);
    }

    public final void setRemotePort(int port) {
        remoteConn.setPort(port);
    }

    public final void setRemoteStgcmtHost(String hostname) {
        remoteStgcmtConn.setHostname(hostname);
    }

    public final void setRemoteStgcmtPort(int port) {
        remoteStgcmtConn.setPort(port);
    }

    public final void setTlsProtocol(String[] tlsProtocol) {
        conn.setTlsProtocol(tlsProtocol);
    }

    public final void setTlsWithoutEncyrption() {
        conn.setTlsWithoutEncyrption();
        remoteConn.setTlsWithoutEncyrption();
        remoteStgcmtConn.setTlsWithoutEncyrption();
    }

    public final void setTls3DES_EDE_CBC() {
        conn.setTls3DES_EDE_CBC();
        remoteConn.setTls3DES_EDE_CBC();
        remoteStgcmtConn.setTls3DES_EDE_CBC();
    }

    public final void setTlsAES_128_CBC() {
        conn.setTlsAES_128_CBC();
        remoteConn.setTlsAES_128_CBC();
        remoteStgcmtConn.setTlsAES_128_CBC();
    }

    public final void setTlsNeedClientAuth(boolean needClientAuth) {
        conn.setTlsNeedClientAuth(needClientAuth);
    }

    public final void setKeyStoreURL(String url) {
        keyStoreURL = url;
    }

    public final void setKeyStorePassword(String pw) {
        keyStorePassword = pw.toCharArray();
    }

    public final void setKeyPassword(String pw) {
        keyPassword = pw.toCharArray();
    }

    public final void setTrustStorePassword(String pw) {
        trustStorePassword = pw.toCharArray();
    }

    public final void setTrustStoreURL(String url) {
        trustStoreURL = url;
    }

    public final void setCalledAET(String called) {
        remoteAE.setAETitle(called);
    }

    public final void setCalling(String calling) {
        ae.setAETitle(calling);
    }

    public final void setUserIdentity(UserIdentity userIdentity) {
        ae.setUserIdentity(userIdentity);
    }

    public final void setOfferDefaultTransferSyntaxInSeparatePresentationContext(
            boolean enable) {
        ae.setOfferDefaultTransferSyntaxInSeparatePresentationContext(enable);
    }

    public final void setSendFileRef(boolean fileref) {
        this.fileref = fileref;
    }

    public final void setStorageCommitment(boolean stgcmt) {
        this.stgcmt = stgcmt;
    }

    public final boolean isStorageCommitment() {
        return stgcmt;
    }

    public final void setStgcmtCalledAET(String called) {
        remoteStgcmtAE = new NetworkApplicationEntity();
        remoteStgcmtAE.setInstalled(true);
        remoteStgcmtAE.setAssociationAcceptor(true);
        remoteStgcmtAE.setNetworkConnection(
                new NetworkConnection[] { remoteStgcmtConn });
        remoteStgcmtAE.setAETitle(called);
    }

    public final void setShutdownDelay(int shutdownDelay) {
        this.shutdownDelay = shutdownDelay;
    }


    public final void setConnectTimeout(int connectTimeout) {
        conn.setConnectTimeout(connectTimeout);
    }

    public final void setMaxPDULengthReceive(int maxPDULength) {
        ae.setMaxPDULengthReceive(maxPDULength);
    }

    public final void setMaxOpsInvoked(int maxOpsInvoked) {
        ae.setMaxOpsInvoked(maxOpsInvoked);
    }

    public final void setPackPDV(boolean packPDV) {
        ae.setPackPDV(packPDV);
    }

    public final void setAssociationReaperPeriod(int period) {
        device.setAssociationReaperPeriod(period);
    }

    public final void setDimseRspTimeout(int timeout) {
        ae.setDimseRspTimeout(timeout);
    }

    public final void setPriority(int priority) {
        this.priority = priority;
    }

    public final void setTcpNoDelay(boolean tcpNoDelay) {
        conn.setTcpNoDelay(tcpNoDelay);
    }

    public final void setAcceptTimeout(int timeout) {
        conn.setAcceptTimeout(timeout);
    }

    public final void setReleaseTimeout(int timeout) {
        conn.setReleaseTimeout(timeout);
    }

    public final void setSocketCloseDelay(int timeout) {
        conn.setSocketCloseDelay(timeout);
    }

    public final void setMaxPDULengthSend(int maxPDULength) {
        ae.setMaxPDULengthSend(maxPDULength);
    }

    public final void setReceiveBufferSize(int bufferSize) {
        conn.setReceiveBufferSize(bufferSize);
    }

    public final void setSendBufferSize(int bufferSize) {
        conn.setSendBufferSize(bufferSize);
    }

    public final void setTranscoderBufferSize(int transcoderBufferSize) {
        this.transcoderBufferSize = transcoderBufferSize;
    }

    public final int getNumberOfFilesToSend() {
        return files.size();
    }

    public final int getNumberOfFilesSent() {
        return filesSent;
    }

    public final long getTotalSizeSent() {
        return totalSize;
    }

    public List<FileInfo> getFileInfos() {
        return files;
    }


    private static void promptStgCmt(DicomObject cmtrslt, float seconds) {
        
        DicomElement refSOPSq = cmtrslt.get(Tag.ReferencedSOPSequence);
        System.out.print(refSOPSq.countItems());
        System.out.println(" successful");
        DicomElement failedSOPSq = cmtrslt.get(Tag.FailedSOPSequence);
        if (failedSOPSq != null) {
            System.out.print(failedSOPSq.countItems());
            System.out.println(" FAILED!");
        }
    }

    private synchronized DicomObject waitForStgCmtResult() throws InterruptedException {
        while (stgCmtResult == null) wait();
        return stgCmtResult;
    }

    private static void prompt(DcmSnd dcmsnd, float seconds) {
        System.out.print("\nSent ");
        System.out.print(dcmsnd.getNumberOfFilesSent());
        System.out.print(" objects (=");
        promptBytes(dcmsnd.getTotalSizeSent());
        System.out.print(") in ");
        System.out.print(seconds);
        System.out.print("s (=");
        promptBytes(dcmsnd.getTotalSizeSent() / seconds);
        System.out.println("/s)");
    }

    private static void promptBytes(float totalSizeSent) {
        if (totalSizeSent > MB) {
            System.out.print(totalSizeSent / MB);
            System.out.print("MB");
        } else {
            System.out.print(totalSizeSent / KB);
            System.out.print("KB");
        }
    }

    private static int toPort(String port) {
        return port != null ? parseInt(port, "illegal port number", 1, 0xffff)
                : 104;
    }

    private static String[] split(String s, char delim) {
        String[] s2 = { s, null };
        int pos = s.indexOf(delim);
        if (pos != -1) {
            s2[0] = s.substring(0, pos);
            s2[1] = s.substring(pos + 1);
        }
        return s2;
    }

    private static void exit(String msg) {
        System.err.println(msg);
        System.err.println("Try 'dcmsnd -h' for more information.");
        System.exit(1);
    }

    private static int parseInt(String s, String errPrompt, int min, int max) {
        try {
            int i = Integer.parseInt(s);
            if (i >= min && i <= max)
                return i;
        } catch (NumberFormatException e) {
            // parameter is not a valid integer; fall through to exit
        }
        exit(errPrompt);
        throw new RuntimeException();
    }

    public void addFile(File f) {
        if (f.isDirectory()) {
            File[] fs = f.listFiles();
            for (int i = 0; i < fs.length; i++)
                addFile(fs[i]);
            return;
        }
        FileInfo info = new FileInfo(f);
        DicomObject dcmObj = new BasicDicomObject();
        DicomInputStream in = null;
        try {
            if (f.getAbsolutePath().endsWith(".gz"))
            {
                in = new DicomInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(f), 256)));
            }
            else
            {
                in = new DicomInputStream(f);
            }
            in.setHandler(new StopTagInputHandler(Tag.StudyDate));
            in.readDicomObject(dcmObj, PEEK_LEN);
            info.tsuid = in.getTransferSyntax().uid();
            info.fmiEndPos = in.getEndOfFileMetaInfoPosition();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("WARNING: Failed to parse " + f + " - skipped.");
            System.out.print('F');
            return;
        } finally {
            CloseUtils.safeClose(in);
        }
        info.cuid = dcmObj.getString(Tag.SOPClassUID);
        if (info.cuid == null) {
            System.err.println("WARNING: Missing SOP Class UID in " + f
                    + " - skipped.");
            System.out.print('F');
            return;
        }
        info.iuid = dcmObj.getString(Tag.SOPInstanceUID);
        if (info.iuid == null) {
            System.err.println("WARNING: Missing SOP Instance UID in " + f
                    + " - skipped.");
            System.out.print('F');
            return;
        }
        addTransferCapability(info.cuid, info.tsuid);
        files.add(info);
        System.out.print('.');
    }

    public void addTransferCapability(String cuid, String tsuid) {
        Set<String> ts = as2ts.get(cuid);
        if (fileref) {
            if (ts == null) {
                as2ts.put(cuid,
                        Collections.singleton(DCM4CHEE_URI_REFERENCED_TS_UID));
            }
        } else {
            if (ts == null) {
                ts = new HashSet<String>();
                ts.add(UID.ImplicitVRLittleEndian);
                as2ts.put(cuid, ts);
            }
            ts.add(tsuid);
        }
    }
    


    public void configureTransferCapability() {
        int off = stgcmt || remoteStgcmtAE != null ? 1 : 0;
        TransferCapability[] tc = new TransferCapability[off + as2ts.size()];
        if (off > 0) {
            tc[0] = new TransferCapability(
                    UID.StorageCommitmentPushModelSOPClass,
                    ONLY_IVLE_TS,
                    TransferCapability.SCU);
        }
        Iterator<Map.Entry<String, Set<String>>> iter = as2ts.entrySet().iterator();
        for (int i = off; i < tc.length; i++) {
            Map.Entry<String, Set<String>> e = iter.next();
            String cuid = e.getKey();
            Set<String> ts = e.getValue();
            tc[i] = new TransferCapability(cuid,
                    ts.toArray(new String[ts.size()]),
                    TransferCapability.SCU);
        }
        ae.setTransferCapability(tc);
    }

    public void start() throws IOException {
        if (conn.isListening()) {
            conn.bind(executor );
            System.out.println("Start Server listening on port " + conn.getPort());
        }
    }

    public void stop() {
        if (conn.isListening()) {
            try {
                Thread.sleep(shutdownDelay);
            } catch (InterruptedException e) {
                // Should not happen
                e.printStackTrace();
            }
            conn.unbind();
        }
    }

    public void open() throws IOException, ConfigurationException,
            InterruptedException {
        assoc = ae.connect(remoteAE, executor);
    }

    public void openToStgcmtAE() throws IOException, ConfigurationException,
            InterruptedException {
        assoc = ae.connect(remoteStgcmtAE, executor);
    }

    public void send() {
        for (int i = 0, n = files.size(); i < n; ++i) {
            FileInfo info = files.get(i);
            TransferCapability tc = assoc.getTransferCapabilityAsSCU(info.cuid);
            if (tc == null) {
                System.out.println();
                System.out.println(UIDDictionary.getDictionary().prompt(
                        info.cuid)
                        + " not supported by " + remoteAE.getAETitle());
                System.out.println("skip file " + info.f);
                continue;
            }
            
            
            String tsuid = selectTransferSyntax(tc.getTransferSyntax(),
                    fileref ? DCM4CHEE_URI_REFERENCED_TS_UID : info.tsuid);
            if (tsuid == null) {
                System.out.println();
                System.out.println(UIDDictionary.getDictionary().prompt(
                        info.cuid)
                        + " with "
                        + UIDDictionary.getDictionary().prompt(
                                fileref ? DCM4CHEE_URI_REFERENCED_TS_UID
                                        : info.tsuid)
                        + " not supported by " + remoteAE.getAETitle());
                System.out.println("skip file " + info.f);
                continue;
            }

            try {
                DimseRSPHandler rspHandler = new DimseRSPHandler() {
                    @Override
                    public void onDimseRSP(Association as, DicomObject cmd,
                            DicomObject data) {
                        DcmSnd.this.onDimseRSP(cmd);
                    }
                };
                
                if(MoveOriginatorMessageID!=null)
                {
                    int messageID = Integer.parseInt(MoveOriginatorMessageID);
                    assoc.cstore(info.cuid, info.iuid, priority, assoc.getCallingAET(), messageID,
                        new DataWriter(info), tsuid, rspHandler);
                }
                else
                {
                    assoc.cstore(info.cuid, info.iuid, priority, 
                        new DataWriter(info), tsuid, rspHandler);
                }
                
                
                
                //assoc.cstore(info.cuid, info.iuid, priority, assoc.getCallingAET(), priority, new DataWriter(info), tsuid, rspHandler);
                
            } catch (NoPresentationContextException e) {
                System.err.println("WARNING: " + e.getMessage()
                        + " - cannot send " + info.f);
                System.out.print('F');
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("ERROR: Failed to send - " + info.f + ": "
                        + e.getMessage());
                System.out.print('F');
            } catch (InterruptedException e) {
                // should not happen
                e.printStackTrace();
            }
        }
        try {
            assoc.waitForDimseRSP();
        } catch (InterruptedException e) {
            // should not happen
            e.printStackTrace();
        }
    }

    public boolean commit() {
        DicomObject actionInfo = new BasicDicomObject();
        actionInfo.putString(Tag.TransactionUID, VR.UI, UIDUtils.createUID());
        
        DicomElement refSOPSq = actionInfo.putSequence(Tag.ReferencedSOPSequence);
        for (int i = 0, n = files.size(); i < n; ++i) {
            FileInfo info = files.get(i);
            if (info.transferred) {
                BasicDicomObject refSOP = new BasicDicomObject();
                
                refSOP.putString(Tag.ReferencedSOPClassUID, VR.UI, info.cuid);
                refSOP.putString(Tag.ReferencedSOPInstanceUID, VR.UI, info.iuid);

                refSOPSq.addDicomObject(refSOP);
            }
        }
        try {
            stgCmtResult = null;
            DimseRSP rsp = assoc.naction(UID.StorageCommitmentPushModelSOPClass,
                UID.StorageCommitmentPushModelSOPInstance, STG_CMT_ACTION_TYPE,
                actionInfo, UID.ImplicitVRLittleEndian);
            rsp.next();
            DicomObject cmd = rsp.getCommand();
            int status = cmd.getInt(Tag.Status);
            if (status == 0) {
                return true;
            }
            System.err.println(
                    "WARNING: Storage Commitment request failed with status: "
                    + StringUtils.shortToHex(status) + "H");
            System.err.println(cmd.toString());
        } catch (NoPresentationContextException e) {
            System.err.println("WARNING: " + e.getMessage()
                    + " - cannot request Storage Commitment");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(
                    "ERROR: Failed to send Storage Commitment request: "
                    + e.getMessage());
        } catch (InterruptedException e) {
            // should not happen
            e.printStackTrace();
        }
        return false;
    }

    private String selectTransferSyntax(String[] available, String tsuid) {
        if (tsuid.equals(UID.ImplicitVRLittleEndian))
            return selectTransferSyntax(available, IVLE_TS);
        if (tsuid.equals(UID.ExplicitVRLittleEndian))
            return selectTransferSyntax(available, EVLE_TS);
        if (tsuid.equals(UID.ExplicitVRBigEndian))
            return selectTransferSyntax(available, EVBE_TS);
        for (int j = 0; j < available.length; j++)
            if (available[j].equals(tsuid))
                return tsuid;
        return null;
    }

    private String selectTransferSyntax(String[] available, String[] tsuids) {
        for (int i = 0; i < tsuids.length; i++)
            for (int j = 0; j < available.length; j++)
                if (available[j].equals(tsuids[i]))
                    return available[j];
        return null;
    }

    public void close() {
        try {
            assoc.release(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the MoveOriginatorMessageID
     */
    public String getMoveOriginatorMessageID() {
        return MoveOriginatorMessageID;
    }

    /**
     * @param MoveOriginatorMessageID the MoveOriginatorMessageID to set
     */
    public void setMoveOriginatorMessageID(String MoveOriginatorMessageID) {
        this.MoveOriginatorMessageID = MoveOriginatorMessageID;
    }

    public static final class FileInfo {
        File f;

        String cuid;

        String iuid;

        String tsuid;

        long fmiEndPos;

        long length;

        boolean transferred;

        int status;

        public FileInfo(File f) {
            this.f = f;
            this.length = f.length();
        }

    }

    
    
    private class DataWriter implements org.dcm4che2.net.DataWriter {

        private FileInfo info;

        public DataWriter(FileInfo info) {
            this.info = info;
        }

        public void writeTo(PDVOutputStream out, String tsuid)
                throws IOException {
            if (tsuid.equals(info.tsuid)) {
                InputStream fis = null;
                if (info.f.getAbsolutePath().endsWith(".gz"))
                    fis = new GZIPInputStream(new BufferedInputStream(new FileInputStream(info.f), 256));
                else
                    fis = new FileInputStream(info.f);
                
                try {
                    long skip = info.fmiEndPos;
                    while (skip > 0)
                        skip -= fis.skip(skip);
                    out.copyFrom(fis);
                } finally {
                    fis.close();
                }
            } else if (tsuid.equals(DCM4CHEE_URI_REFERENCED_TS_UID)) {
                DicomObject attrs;
                DicomInputStream dis = null;
                if (info.f.getAbsolutePath().endsWith(".gz"))
                {
                    dis = new DicomInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(info.f), 256)));
                }
                else
                {
                    dis= new DicomInputStream(info.f);
                }
                
                try {
                    dis.setHandler(new StopTagInputHandler(Tag.PixelData));
                    attrs = dis.readDicomObject();
                } finally {
                    dis.close();
                }
                DicomOutputStream dos = new DicomOutputStream(out);
                attrs.putString(Tag.RetrieveURI, VR.UT, info.f.toURI().toString());
                
                dos.writeDataset(attrs, tsuid);
             } else {
                DicomInputStream dis = null;
                if (info.f.getAbsolutePath().endsWith(".gz"))
                {
                     dis = new DicomInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(info.f), 256)));
                }
                else
                {
                    dis = new DicomInputStream(info.f);
                }
                try {
                    DicomOutputStream dos = new DicomOutputStream(out);
                    dos.setTransferSyntax(tsuid);
                    TranscoderInputHandler h = new TranscoderInputHandler(dos,
                            transcoderBufferSize);
                    dis.setHandler(h);
                    dis.readDicomObject();
                } finally {
                    dis.close();
                }
            }
        }

    }

    private void promptErrRSP(String prefix, int status, FileInfo info,
            DicomObject cmd) {
        System.err.println(prefix + StringUtils.shortToHex(status) + "H for "
                + info.f + ", cuid=" + info.cuid + ", tsuid=" + info.tsuid);
        System.err.println(cmd.toString());
    }

    private void onDimseRSP(DicomObject cmd) {
        int status = cmd.getInt(Tag.Status);
        int msgId = cmd.getInt(Tag.MessageIDBeingRespondedTo);
        FileInfo info = files.get(msgId - 1);
        info.status = status;
        switch (status) {
        case 0:
            info.transferred = true;
            totalSize += info.length;
            ++filesSent;
            System.out.print('.');
            break;
        case 0xB000:
        case 0xB006:
        case 0xB007:
            info.transferred = true;
            totalSize += info.length;
            ++filesSent;
            promptErrRSP("WARNING: Received RSP with Status ", status, info,
                    cmd);
            System.out.print('W');
            break;
        default:
            promptErrRSP("ERROR: Received RSP with Status ", status, info, cmd);
            System.out.print('F');
        }
    }

    @Override
    protected synchronized void onNEventReportRSP(Association as, int pcid,
            DicomObject rq, DicomObject info, DicomObject rsp) {
        stgCmtResult = info;
        notifyAll();
    }

    public void initTLS() throws GeneralSecurityException, IOException {
        KeyStore keyStore = loadKeyStore(keyStoreURL, keyStorePassword);
        KeyStore trustStore = loadKeyStore(trustStoreURL, trustStorePassword);
        device.initTLS(keyStore,
                keyPassword != null ? keyPassword : keyStorePassword,
                trustStore);
    }

    private static KeyStore loadKeyStore(String url, char[] password)
            throws GeneralSecurityException, IOException {
        KeyStore key = KeyStore.getInstance(toKeyStoreType(url));
        InputStream in = openFileOrURL(url);
        try {
            key.load(in, password);
        } finally {
            in.close();
        }
        return key;
    }

    private static InputStream openFileOrURL(String url) throws IOException {
        if (url.startsWith("resource:")) {
            return DcmSnd.class.getClassLoader().getResourceAsStream(
                    url.substring(9));
        }
        try {
            return new URL(url).openStream();
        } catch (MalformedURLException e) {
            return new FileInputStream(url);
        }
    }

    private static String toKeyStoreType(String fname) {
        return fname.endsWith(".p12") || fname.endsWith(".P12")
                 ? "PKCS12" : "JKS";
    }
}
