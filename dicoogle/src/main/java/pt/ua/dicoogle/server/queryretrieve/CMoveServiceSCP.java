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

import aclmanager.core.LuceneQueryACLManager;
import aclmanager.models.Principal;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import org.dcm4che2.data.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerConfigurationException;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.Status;
import pt.ua.dicoogle.core.exceptions.CFindNotSupportedException;

import pt.ua.dicoogle.DicomLog.LogDICOM;
import pt.ua.dicoogle.DicomLog.LogLine;
import pt.ua.dicoogle.DicomLog.LogXML;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.server.DicomNetwork;
import pt.ua.dicoogle.server.SearchDicomResult;
import pt.ua.dicoogle.core.ServerSettings;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class CMoveServiceSCP extends CMoveService {
    private static final Logger logger = LoggerFactory.getLogger(CMoveServiceSCP.class);


    private DicomNetwork service = null;
    private LuceneQueryACLManager luke;
    private boolean loggingDICOM = false;


    public CMoveServiceSCP(String[] sopClasses, Executor executor, LuceneQueryACLManager luke) {
        super(sopClasses, executor);
         this.luke = luke;
    }

    public CMoveServiceSCP(String sopClass, Executor executor) {
        super(sopClass, executor);
        this.luke = null;
    }
    
    
    @Override
    protected DimseRSP doCMove(Association as, int pcid, DicomObject cmd,
            DicomObject data, DicomObject rsp) throws DicomServiceException {

        DimseRSP replay = null;

        /**
         * Verify Permited AETs
         */

        boolean permited = false;

        if (ServerSettings.getInstance().getPermitAllAETitles()) {
            permited = true;
        } else 
        {
            String permitedAETs[] = ServerSettings.getInstance().getCAET();

            for (int i = 0; i < permitedAETs.length; i++) {
                if (permitedAETs[i].equals(as.getCallingAET())) {
                    permited = true;
                    break;
                }
            }
        }

        if (!permited) {
            as.abort();

            return new MoveRSP(data, rsp);
        } else {
            logger.info("Client association with {} is permitted!", as.getCallingAET());
        }

        try {
            Thread.sleep(ServerSettings.getInstance().getRspDelay());
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
            throw new DicomServiceException(cmd, Status.UnrecognizedOperation,
                    "Missing Move Destination");
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
               
        if(luke != null){
            String filterQuery = luke.produceQueryFilter(new Principal("AETitle", as.getCallingAET()));
            if(query.length() > 0 )
                 query += filterQuery;
        }
        //TODO: FIlter Query;
        SearchDicomResult search = new SearchDicomResult(query,
                true, extrafields, SearchDicomResult.QUERYLEVEL.IMAGE);
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
                    //files.add(new File(search.getCurrentFile()));
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
            ServerSettings ob = ServerSettings.getInstance();
            for (MoveDestination m : ob.getMoves()) {
                if (m.getAETitle().equals(destination)) {
                    hostDest = m.getIpAddrs();
                    portAddr = m.getPort();
                }
            }

            if (loggingDICOM) {
                LogLine ll = new LogLine("cmove", LogLine.getDateTime(), destination,
                        "Files: " + files.size() + " -- (" + hostDest + ":" + portAddr + ")", "studyUID=" + data.getString(Tag.StudyInstanceUID));
                LogDICOM.getInstance().addLine(ll);

                synchronized (LogDICOM.getInstance()) {
                    try {
                        LogXML l = new LogXML();
                        l.printXML();
                    } catch (TransformerConfigurationException ex) {
                        LoggerFactory.getLogger(CMoveServiceSCP.class).error(ex.getMessage(), ex);
                    }
                }
            }

            if (CMoveID==null||CMoveID.equals(""))
            {
                logger.warn("No originator message ID, aborting");
                return null;
            }
            try
            {
                System.out.println("Destination: " + destination);
                new CallDCMSend(files, portAddr, hostDest, destination, CMoveID);
            } catch (Exception ex)
            {
                logger.error("Error Sending files to Storage Server! ", ex);
            }
        }


        replay = new MoveRSP(data, rsp); // Third Party Move
        return replay;

    }

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
}
