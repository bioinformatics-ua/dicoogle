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


import java.io.File;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pt.ua.dicoogle.core.settings.ServerSettingsManager;

import org.dcm4che3.data.UID;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.TransferCapability;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import pt.ua.dicoogle.utils.Platform;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;
import pt.ua.dicoogle.server.DicomNetwork;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class QueryRetrieve extends DicomNetwork {

    /**** Class Atributes ****/


    ServerSettings.DicomServices.QueryRetrieve s =
            ServerSettingsManager.getSettings().getDicomServicesSettings().getQueryRetrieveSettings();

    /* Implemented SOP Classes */
    private Collection<String> sopClass = null;

    private EchoReplyService verifService = null;

    /* DEFAULT Implemented module name */
    private static final String MODULE_NAME = "DICOOGLE-STORAGE";
    /* Remote Application Entity (client) */
    private final ApplicationEntity remoteAE = new ApplicationEntity();
    /* Remote connection associated with remoteAE */
    private final Connection remoteConn = new Connection();
    /* Module device */
    private final Device device = new Device(MODULE_NAME);
    /* Local Application Entity (this server) 'ea' */
    private final ApplicationEntity localAE = new ApplicationEntity();
    /* Local connection associated with localAE 'conn' */
    private final Connection localConn = new Connection();
    /* True if server is allready started */
    private boolean started = false;
    /* True if server is allready started as a Windows Service*/
    private boolean startedAsService = false;
    /* Response (to clients) delay (in milisec) */
    private int rspdelay = 0;


    /* Module executor  -  Server thread */
    private static Executor executor = Executors.newCachedThreadPool(
        new ThreadFactoryBuilder().setNameFormat(MODULE_NAME).build()
    );

    private Collection<String> transfCap = s.getTransferCapabilities();
    private TransferCapability[] tc = new TransferCapability[5];

    private static String[] multiSop = {UID.StudyRootQueryRetrieveInformationModelFind,

            UID.PatientRootQueryRetrieveInformationModelFind};

    private static String[] moveSop =
            {UID.StudyRootQueryRetrieveInformationModelMove, UID.PatientRootQueryRetrieveInformationModelMove};

    public QueryRetrieve() {

        super("DICOOGLE-QUERYRETRIEVE");

        // super(multiSop, executor);
        this.sopClass = s.getSOPClass();


        // DebugManager.getSettings().debug("SOP Class: ");
        // DebugManager.getSettings().debug(s.getSOPClasses());

        for (String s : transfCap) {
            // DebugManager.getSettings().debug("TransCap : " + s );
        }

        String[] arr = new String[transfCap.size()];
        transfCap.toArray(arr);

        tc[0] = new TransferCapability("Study Root Query/Retrieve C-Find", UID.StudyRootQueryRetrieveInformationModelFind, TransferCapability.Role.SCP, arr);
        tc[1] = new TransferCapability("Study Root Query/Retrieve C-Move", UID.StudyRootQueryRetrieveInformationModelMove, TransferCapability.Role.SCP, arr);

        tc[2] = new TransferCapability("Patient Root Query/Retrieve C-Find", UID.PatientRootQueryRetrieveInformationModelFind, TransferCapability.Role.SCP, arr);
        tc[3] = new TransferCapability("Patient Root Query/Retrieve C-Move", UID.PatientRootQueryRetrieveInformationModelMove, TransferCapability.Role.SCP, arr);
        String[] verificationTS = { UID.ImplicitVRLittleEndian, UID.ExplicitVRLittleEndian };
        tc[4] = new TransferCapability("Verification", UID.Verification, TransferCapability.Role.SCP, verificationTS);

        this.verifService = null;
        /* server */
        this.started = false;
        this.startedAsService = false;
        this.rspdelay = s.getRspDelay();
        this.localAE.setInstalled(true);
        this.localAE.setAssociationAcceptor(true);
        this.localAE.setAssociationInitiator(false);
        this.localAE.setConnection(this.localConn);
        this.localAE.setAETitle(ServerSettingsManager.getSettings().getDicomServicesSettings().getAETitle());
        this.localAE.setTransferCapability(tc);
        this.localAE.setDimseRspTimeout(s.getDIMSERspTimeout());
        this.localAE.setIdleTimeout(s.getIdleTimeout());
        this.localAE.setMaxPDULengthReceive(s.getMaxPDULengthReceive() + 1000);
        this.localAE.setMaxPDULengthSend(s.getMaxPDULengthSend() + 1000);

        this.localAE.register(new CMoveServiceSCP(moveSop, executor));
        this.localAE.register(new CFindServiceSCP(multiSop, executor));
        this.localAE.register(new VerificationService());

        this.localConn.setPort(s.getPort());
        this.localConn.setMaxScpAssociations(s.getMaxClientAssoc());
        this.localConn.setAcceptTimeout(s.getAcceptTimeout());
        this.localConn.setConnectTimeout(s.getConnectionTimeout());

        this.device
                .setDescription(ServerSettingsManager.getSettings().getDicomServicesSettings().getDeviceDescription());
        this.device.setApplicationEntity(this.localAE);
        this.device.setConnection(this.localConn);
    }



    @Override
    public boolean doStartService() {


        if (this.device != null) {
            this.verifService = new EchoReplyService();
            CommandUtils.setIncludeUIDinRSP(true);

            try {
                this.device.startListening(QueryRetrieve.executor);
                this.verifService.start();
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
            this.started = true;
            // DebugManager.getSettings().debug("Starting server " +
            // "- cmove server was started right now .. ");
            this.startedAsService = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean doStopService() {
        this.device.stopListening();
        return true;
    }

}
