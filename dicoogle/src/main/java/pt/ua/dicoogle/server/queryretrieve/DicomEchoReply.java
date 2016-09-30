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
/**
 *
 *  Generic DICOM Worklist Server SCP
 */
package pt.ua.dicoogle.server.queryretrieve;

//import configurations.ConfigurationsValues;

//import gui.MainWindow;
//import gui.MainWindow.LOG_MODES;
import java.io.IOException;
import java.util.concurrent.Executor;


import org.dcm4che2.data.DicomObject;
import org.dcm4che2.net.Association;
import org.dcm4che2.data.UID;
import org.dcm4che2.net.CommandUtils;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.TransferCapability;
import org.dcm4che2.net.UserIdentity;
import org.dcm4che2.net.service.VerificationService;
import pt.ua.dicoogle.core.settings.ServerSettings;

/**
 *
 * @author Joaoffr  <joaoffr@ua.pt>
 * @author DavidP   <davidp@ua.pt>
 *
 * @version $Revision: 002 $ $Date: 2008-11-22 14:25:00  $
 * @since Nov 21, 2008
 *
 */
public class DicomEchoReply extends VerificationService {

    
    /** Settings */         
    ServerSettings s = ServerSettings.getInstance();
    
    private static final String[] TRANSF_CAP = {
        UID.ImplicitVRLittleEndian,
        UID.ExplicitVRBigEndian,
        UID.ExplicitVRLittleEndian
    };

    /* Implemented SOP Class */
    private static final String SOP_CLASS = UID.VerificationSOPClass;

    private static final int PORT_OFFSET = 100;

    private static final String SERVICE_LABEL = "";


    /**** Class Atributes ****/

    private static final String MODULE_NAME = "DICOMWLS_EchoReply";                     /* DEFAULT Implemented module name */
    private final NetworkApplicationEntity remoteAE = new NetworkApplicationEntity();   /* Remote Application Entity (client) */
    private final NetworkConnection remoteConn = new NetworkConnection();               /* Remote connection associated with remoteAE */
    private final Device device = new Device(MODULE_NAME);                              /* Module device */
    private final NetworkApplicationEntity localAE = new NetworkApplicationEntity();    /* Local Application Entity (this server) 'ea' */
    private final NetworkConnection localConn = new NetworkConnection();                /* Local connection associated with localAE 'conn' */
    private boolean started = false;                                                    /* True if server is allready started */
    private int port = 0;



    /* Module executor */
    private static final Executor executor = (Executor) new NewThreadExecutor(MODULE_NAME);        /* Server thread */



    public DicomEchoReply() {

        super();

        this.port = s.getWlsPort() + DicomEchoReply.PORT_OFFSET; ;
        /* clients */
        this.remoteAE.setInstalled(true);
        this.remoteAE.setAssociationInitiator(true);
        this.remoteAE.setNetworkConnection(this.remoteConn);
        


        /* server */
        this.started = false;
        this.localAE.setInstalled(true);
        this.localAE.setAssociationAcceptor(true);
        this.localAE.setAssociationInitiator(false);
        this.localAE.setNetworkConnection(this.localConn );
        this.localAE.setAETitle(s.getAE() + DicomEchoReply.SERVICE_LABEL);
        this.localAE.register(new VerificationService());
        this.localAE.register(this);

        TransferCapability[] tc = { new TransferCapability( DicomEchoReply.SOP_CLASS,
                                                            DicomEchoReply.TRANSF_CAP,
                                                            TransferCapability.SCP) };
        this.localAE.setTransferCapability(tc);

/*        this.localAE.setDimseRspTimeout(confs.getWlsConfigs().getDIMSE_RSP_TIMEOUT());
        this.localAE.setIdleTimeout(confs.getWlsConfigs().getIDLE_TIMEOUT());*/

        /*this.localAE.setMaxPDULengthReceive(confs.getWlsConfigs().getMAX_PDU_LENGTH_RECEIVE());
        this.localAE.setMaxPDULengthSend(confs.getWlsConfigs().getMAX_PDU_LENGTH_SEND());*/
        /**
        this.localAE.setSupportedCharacterSet("CharacterSetSup");*/

        this.localConn.setPort(this.port);
        //this.localConn.setMaxScpAssociations(confs.getWlsConfigs().getMAX_CLIENT_ASSOCS());
        /*this.localConn.setHostname(DicomWLS.LOCAL_HOST_NAME);*/
       // this.localConn.setAcceptTimeout(confs.getWlsConfigs().getACCEPT_TIMEOUT());
        //this.localConn.setConnectTimeout(confs.getWlsConfigs().getCONNECTION_TIMEOUT());


        //this.device.setDescription(confs.getWlsConfigs().getDEVICE_DESCRIPTION());
        this.device.setNetworkApplicationEntity(this.localAE);
        this.device.setNetworkConnection(this.localConn);
        /*this.device.setDeviceName(DicomWLS.MODULE_NAME);*/

    }


    /**
     * Set local Host Name
     * @param hostname
     */
    public final void setLocalHost(String hostname) {
        this.localConn.setHostname(hostname);
    }

    /**
     * Get Local Host Name
     * @return
     */
    public final String getLocalHost() {
        return this.localConn.getHostname();
    }

    /**
     * Set Remote Host Name
     * @param hostname
     */
    public final void setRemoteHost(String hostname) {
        this.remoteConn.setHostname(hostname);
    }

    /**
     * Get Remote Host Name
     * @return
     */
    public final String getRemoteHost() {
        return this.remoteConn.getHostname();
    }

    /**
     * Set Local Port
     * @param port
     */
    public final void setLocalPort(int port) {
         int tmp = (port >= 1 && port <= 0xffff) ? port : 105;
         this.localConn.setPort(tmp);
    }

    /**
     * Get Local Port
     * @return
     */
    public final int getLocalPort() {
        return this.localConn.getPort();
    }

    /**
     * Set Remote Application Entity Title
     * @param called
     */
    public final void setRemoteAET(String called) {
        this.remoteAE.setAETitle(called);
    }

    /**
     * Get Remote Application Entity Title
     * @return
     */
    public final String getRemoteAET() {
        return this.remoteAE.getAETitle();
    }

    /**
     * Set Local Application Entity Title
     * @param calling
     */
    public final void setLocalAET(String calling) {
        this.localAE.setAETitle(calling);
    }

    /**
     * Get Local Application Entity Title
     * @return
     */
    public final String getLocalAET() {
        return this.localAE.getAETitle();
    }

    /**
     * Set Local User Identity
     * @param userIdentity
     */
    public final void setUserIdentity(UserIdentity userIdentity) {
        this.localAE.setUserIdentity(userIdentity);
    }

    /**
     * Get Local User Identity
     * @return
     */
    public final UserIdentity getUserIdentity() {
        return this.localAE.getUserIdentity();
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    /**
     *
     * @param connectTimeout
     */
    public final void setConnectTimeout(int connectTimeout) {
        this.localConn.setConnectTimeout(connectTimeout);
    }

    /**
     *
     * @param maxPDULength
     */
    public final void setMaxPDULengthReceive(int maxPDULength) {
        this.localAE.setMaxPDULengthReceive(maxPDULength);
    }

    /**
     *
     * @param maxPDULength
     */
    public final void setMaxPDULengthSend(int maxPDULength) {
        this.localAE.setMaxPDULengthSend(maxPDULength);
    }

    /**
     *
     * @param packPDV
     */
    public final void setPackPDV(boolean packPDV) {
        this.localAE.setPackPDV(packPDV);
    }

    /**
     *
     * @param period
     */
    public final void setAssociationReaperPeriod(int period) {
        this.device.setAssociationReaperPeriod(period);
    }

    /**
     *
     * @param timeout
     */
    public final void setDimseRspTimeout(int timeout) {
        this.localAE.setDimseRspTimeout(timeout);
    }

    /**
     *
     * @param tcpNoDelay
     */
    public final void setTcpNoDelay(boolean tcpNoDelay) {
        this.localConn.setTcpNoDelay(tcpNoDelay);
    }

    /**
     *
     * @param timeout
     */
    public final void setAcceptTimeout(int timeout) {
        this.localConn.setAcceptTimeout(timeout);
    }

    /**
     *
     * @param timeout
     */
    public final void setReleaseTimeout(int timeout) {
        this.localConn.setReleaseTimeout(timeout);
    }

    /**
     *
     * @param timeout
     */
    public final void setSocketCloseDelay(int timeout) {
        this.localConn.setSocketCloseDelay(timeout);
    }

    /**
     *
     * @param bufferSize
     */
    public final void setReceiveBufferSize(int bufferSize) {
        this.localConn.setReceiveBufferSize(bufferSize);
    }

    /**
     *
     * @param bufferSize
     */
    public final void setSendBufferSize(int bufferSize) {
        this.localConn.setSendBufferSize(bufferSize);
    }

    /**
     *
     * @param ts
     */
    public void setTransferSyntax(String[] ts) {
        TransferCapability[] tc = { new TransferCapability(
                UID.ModalityWorklistInformationModelFIND, ts,
                TransferCapability.SCU) };
        this.localAE.setTransferCapability(tc);
    }

    /**
     *
     * @param timeout
     */
    public final void setIdleTimeout(int timeout) {
        this.localAE.setIdleTimeout(timeout);
    }



    /**
     * Start server socket accept client(s) connection
     *
     * @return
     * @throws java.io.IOException
     * @throws org.dcm4che2.net.ConfigurationException
     * @throws java.lang.InterruptedException
     */
    public boolean startListening() {
        if (this.device != null) {
            try {
                CommandUtils.setIncludeUIDinRSP(true);
                this.device.startListening(DicomEchoReply.executor);
            } catch (Exception ex) {
                 ///MainWindow.getMw().add2ServerLogln(ex.getMessage(), LOG_MODES.ERROR);
                 return false;
            }
            this.started = true;
            return true;
        }

        return false;
    }

    /**
     * Stop all active client socket connections
     *
     * @return
     * @throws java.io.IOException
     * @throws org.dcm4che2.net.ConfigurationException
     * @throws java.lang.InterruptedException
     */
    public boolean stopListening() {
        if (this.device != null) {
            this.device.stopListening();
            this.started = false;
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return( "[START INFO PRINT]" + "\n" +
                    "\tModuleName = " + DicomEchoReply.MODULE_NAME + "\n" +
                    "\tLocalAETitle = " + this.localAE.getAETitle() + "\n" +
                    "\tPort = " + this.localConn.getPort() + "\n" +
                    "\tTransfereCapabilities = " + strVectConcat(this.localAE.getTransferCapability()[0].getTransferSyntax(), "; ") + "\n" +
                    "\tModality = " + DicomEchoReply.SOP_CLASS + "\n" +
                    "\tStarted = " + this.started + "\n" +
                "[END INFO PRINT]" + "\n"
              );
    }

    @Override
    public void cecho(Association as, int pcid, DicomObject cmd) throws IOException {
        ///MainWindow.getMw().add2ServerLogln("CEcho request from " + as.getRemoteAET() + " received!", LOG_MODES.INFO);

        super.cecho(as, pcid, cmd);

        //System.out.println(cmd);

        ///MainWindow.getMw().add2ServerLogln("CEcho response send to " + as.getRemoteAET() + "!", LOG_MODES.INFO);

    }
    

    private static String strVectConcat(String[] stVectIN, String sep) {

        if (stVectIN == null) return new String("[any]");

        String temp = new String("");
        for (int i = 0; i < stVectIN.length - 1; i++)
            temp = temp + stVectIN[i] + sep;

        temp = temp + stVectIN[stVectIN.length - 1];

        return temp;
    }

}

