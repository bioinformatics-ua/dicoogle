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

package pt.ua.dicoogle.core;

/**
 * 
 * @author Marco Pereira
 * @modified Luís A. Bastião Silva <bastiao@ua.pt>
 * @modified Samuel da Costa Campos <samuelcampos@ua.pt>
 */


import org.apache.logging.log4j.core.jmx.Server;
import pt.ua.dicoogle.core.settings.ServerSettings;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.server.*;

import java.io.*;

// SAX classes.
import org.xml.sax.*;
import org.xml.sax.helpers.*;

//JAXP 
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;

import org.xml.sax.XMLReader;
import org.dcm4che2.data.UID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ua.dicoogle.sdk.Utils.Platform;

@Deprecated
public class XMLSupport extends DefaultHandler
{
    private static final Logger logger = LoggerFactory.getLogger(XMLSupport.class);
    
    private boolean isEncrypt = false;
    private boolean isPort = false;
    private boolean isRGUIPort = false;
    private boolean isRGUIExtIP = false;
    private boolean isIndexEffort = false;
    private boolean isZIPFile = false;
    private boolean isGZIPStorage = false;
    private boolean isTS = false;    
    private boolean isAET = false;
    private boolean isCAET = false;
    private boolean isPermitAllAETitles = false;
    private boolean isPath = false;   
    private boolean isDicoogleDir = false;
    private boolean isFullContentIndex = false;
    private boolean isSaveThumbnails = false;
    private boolean isThumbnailsMatrix = false;
    
    private boolean isStorage = false;


    /** P2P **/
    private boolean isP2P = false ;
    private boolean autoConnect = false ;
    private boolean maxmsg = false;
    private boolean isNode = false ;
    private boolean isNodeName = false ; 
    private boolean isDefined = false ;

    /** 
     * 
     * Query Retreive Server configs
     */ 
    private boolean isQRConfigs = false ;
    private boolean isDeviceDescription = false ; 
    private boolean isLocalAETName = false ; 
    private boolean isPermitedRemoteAETsNames = false ; 
    private boolean isPermitedLocalInterfaces = false ; 
    private boolean isPermitedRemoveHostnames = false ; 
    private boolean isRspDelay = false ; 
    private boolean isDIMSERspTimeout = false ; 
    private boolean isIdleTimeout = false ; 
    private boolean isAcceptTimeout = false ; 
    private boolean isConnectTimeout = false ; 
    private boolean isTransfCap = false ; 
    private boolean isSOPClass = false ;
    private boolean QREnable = false;
    private boolean isMaxClientAssoc = false ; 
    private boolean isMaxPDULengthReceive = false ; 
    private boolean isMaxPDULengthSend = false ;

    private boolean isWeb = false ;

    private boolean isMonitorWatcher = false;

    private boolean options = false ;
    private boolean modality = false ;
    private boolean cfind = false ;
    private boolean find = false ;


    private String sopId = null;

    private boolean destinations = false ;
    private boolean dest = false ;

    private int port = 0 ;
    private String AETitle = null ;
    private String IP = null ;
    private String description;
    private String isPublic;

    private boolean priorityAET = false;


    private String currentService;
    
    private SOPList list;
    private ServerSettings s;
    private TransfersStorage LocalTS;

	private DefaultListModel m;
    
    
    private boolean isIndexAnonymous = false;
	private boolean isWANModeEnabled = false;

    public XMLSupport(ServerSettings settings, SOPList list) {
        this.list = list;
        this.s = settings;
        LocalTS = new TransfersStorage();
        m = new DefaultListModel();
    }

    public XMLSupport()
    {
        this(ServerSettings.getInstance(), SOPList.getInstance());
    }
    
    @Override
    public void startElement( String uri, String localName, String qName, Attributes attribs )
    {
        if (localName.equals("QueryRetrieve"))
        {
            isQRConfigs = true ;
        }else if(localName.equals("WANModeEnabled"))
        {
            isWANModeEnabled = true;
        }
        else if(localName.equals("EncryptUsersFile"))
        {
            isEncrypt = true;
        }
        else if(localName.equals( "IndexEffort" )){
            isIndexEffort = true;
        }
        else if( localName.equals( "Port" ) && !isQRConfigs  )
        {
            isPort = true;            
        }
        else if( localName.equals( "IndexAnonymous" )  )
        {
            isIndexAnonymous = true;
        }
        else if( localName.equals( "IndexZipFiles" )  )
        {
            isZIPFile = true;
        }
        else if( localName.equals( "GZipStorage" )  )
        {
            isGZIPStorage = true;
        }
        else if( localName.equals( "MonitorWatcher" ) )
        {
            isMonitorWatcher = true ;
        }    
        else if( localName.equals( "P2P" ) && !isQRConfigs  )
        {
            isP2P = true;
        }
        else if( localName.equals( "AutoConnect" ) && isP2P  )
        {
            autoConnect = true ;
        }
        else if( localName.equals( "MaxMsg" ) && isP2P  )
        {
            maxmsg = true ;
        }
        else if( localName.equals( "Node" ) && isP2P  )
        {
            isNode = true ;
        }
        else if( localName.equals( "name" ) && isP2P  && isNode)
        {
            isNodeName = true ;
        }
        else if( localName.equals( "defined" ) && isP2P  && isNode)
        {
            isDefined = true ;
        }

        else if( localName.equals( "Storage" ))
        {
            isStorage = true;
        }
        else if( localName.equals( "DicoogleDir" ) )
        {
            isDicoogleDir = true;            
        }
        else if(localName.equals("fullContentIndex"))
        {
            isFullContentIndex = true;            
        }    
        else if(localName.equals("SaveThumbnails"))
        {
            isSaveThumbnails = true;            
        }        
        else if(localName.equals("ThumbnailsMatrix"))
        {
            isThumbnailsMatrix = true;            
        }
        else if(localName.equals("AETitle"))
        {
            isAET = true;
        }
        else if(localName.equals("CAETitle"))
        {
            isCAET = true;
        }
        else if(localName.equals("PermitAllAETitles"))
        {
            isPermitAllAETitles = true;
        }
        else if(localName.equals("Path"))
        {
            isPath = true;
        }
        else if(localName.equals("Service"))
        {
            currentService = resolveAttrib(uri, localName, attribs, UID.VerificationSOPClass);            
        }
        else if(localName.equals("TS"))
        {
            isTS = true;            
        } 
        /** Now Worklist Server parsing */ 
        else if (localName.equals("DeviceDescription"))
        {
            isDeviceDescription = true;
        }
        else if (localName.equals("LocalAETName"))
        {
            isLocalAETName = true ; 
        }
        else if (localName.equals("PermitedRemoteAETsNames"))
        {
            isPermitedRemoteAETsNames = true ; 
        }
        else if (localName.equals("Port") && isQRConfigs)
        {
            isPort = true ; 
        }
        else if (localName.equals("PermitedLocalInterfaces"))
        {
            isPermitedLocalInterfaces = true ; 
        }
        else if (localName.equals("PermitedRemoveHostnames"))
        {
            isPermitedRemoveHostnames = true ; 
        }
        else if (localName.equals("RspDelay"))
        {
            isRspDelay = true ;
        }
        else if (localName.equals("DIMSERspTimeout"))
        {
            isDIMSERspTimeout = true ; 
        }
        else if (localName.equals("IdleTimeout"))
        {
            isIdleTimeout = true ; 
        }        
        else if (localName.equals("AcceptTimeout"))
        {
            isAcceptTimeout = true ; 
        }
        else if (localName.equals("ConnectionTimeout"))
        {
            isConnectTimeout = true ; 
        }
        else if (localName.equals("SOPClass"))
        {
            isSOPClass = true ; 
        }
        else if (localName.equals("QREnable"))
        {
            QREnable = true ;
        }
        else if (localName.equals("MAX_CLIENT_ASSOCS"))
        {
            isMaxClientAssoc = true ; 
        }
        else if (localName.equals("MAX_PDU_LENGTH_RECEIVE"))
        {
            isMaxPDULengthReceive = true ; 
        }
        else if (localName.equals("MAX_PDU_LENGTH_SEND"))
        {
            isMaxPDULengthSend = true ; 
        }

        if (localName.equals("options"))
        {
            this.options = true ;
        }
        else if (localName.equals("modality") && this.options)
        {
            this.modality = true ;
        }
        else if (localName.equals("cfind") && this.options && this.modality)
        {
            this.cfind = true ;
        }
        else if (localName.equals("find") && this.options && this.modality &&
                this.cfind)
        {
            this.find = true ;
            sopId = resolveAttrib(uri, localName, attribs, localName);

        }

        else if(localName.equals("destinations"))
        {
            this.destinations = true ;
        }

        else if (destinations && localName.equals("dest"))
        {
            this.dest  = true ;
            this.AETitle = this.resolveAttrib("ae", attribs, localName);
            this.port = Integer.parseInt(this.resolveAttrib("port", attribs, localName));
            this.IP = this.resolveAttrib("ip", attribs, localName);
            this.description = this.resolveAttrib("description", attribs, "");
            this.isPublic = this.resolveAttrib("public", attribs, "false");

            MoveDestination tmp = new MoveDestination(this.AETitle, this.IP,
                    this.port, this.isPublic.contains("true"), this.description);
            s.add(tmp);
        }

        else if(localName.equals("CSTOREPriorities"))
        {
            this.priorityAET = true ;
        }
        else if (priorityAET && localName.equals("aetitle"))
        {
            String aet = this.resolveAttrib("aetitle", attribs, localName);
            //ServerSettings.getInstance().addPriorityAETitle(aet);
        }


        else if (localName.equals("web"))
        {
            this.isWeb = true ; 
        }
        else if (localName.equals("server") && this.isWeb)
        {
            String tmp = "";
            ServerSettings.Web web = ServerSettings.getInstance().getWeb() ;
            tmp = this.resolveAttrib("enable", attribs, localName);
            web.setWebServer(Boolean.parseBoolean(tmp));
            int port = Integer.valueOf(this.resolveAttrib("port", attribs, localName));
            web.setServerPort(port);

            String allowedOrigins = this.resolveAttrib("allowedOrigins", attribs, "");
            web.setAllowedOrigins(allowedOrigins);
        }

        else if (localName.equals("services") && this.isWeb)
        {
            String tmp = "";
            ServerSettings.Web web = ServerSettings.getInstance().getWeb() ;
            tmp = this.resolveAttrib("enable", attribs, localName);
            if (tmp.equals("true"))
            {
                web.setWebServices(true);
            }
            else
            {
                web.setWebServices(false);
            }
            int port = Integer.valueOf(this.resolveAttrib("port", attribs, localName));
            web.setServicePort(port);



        }
        else if( localName.equals("RGUIPort") )
        {
            isRGUIPort = true;
        }
        else if(localName.equals("RGUIExtIP"))
        {
            isRGUIExtIP = true;
        }



     }
     
    @Override
     public void endElement( String uri, String localName, String qName ) {
        
        if (localName.equals("QueryRetrieve"))
        {
            isQRConfigs = false ;
        }else if(localName.equals("WANModeEnabled"))
        {
            isWANModeEnabled = false;
        }
        else if(localName.equals("EncryptUsersFile"))
        {
            isEncrypt = false;
        }
        else if( localName.equals( "Port" ) )
        {
            isPort = false;            
        }
        else if(localName.equals( "IndexEffort" )){
            isIndexEffort = false;
        }
                
        else if( localName.equals( "IndexAnonymous" )  )
        {
            isIndexAnonymous = false;
        }
                
        else if( localName.equals( "IndexZipFiles" ) )
        {
            isZIPFile = false ;
        }
        else if( localName.equals( "GZipStorage" )  )
        {
            isGZIPStorage = false;
        }
        else if( localName.equals( "MonitorWatcher" ) )
        {
            isMonitorWatcher = false ;
        }        
                
        else if( localName.equals( "P2P" ) )
        {
            isP2P = false;
        }
        else if( localName.equals( "AutoConnect" ) && isP2P  )
        {
            autoConnect = false ;
        }
        else if( localName.equals( "MaxMsg" ) && isP2P  )
        {
            maxmsg = false ;
        }
        else if( localName.equals( "Node" ) && isP2P  )
        {
            isNode = false ;
        }
        else if( localName.equals( "name" ) && isP2P  && isNode)
        {
            isNodeName = false ;
        }
        else if( localName.equals( "defined" ) && isP2P  && isNode)
        {
            isDefined = false ;
        }


        else if( localName.equals( "Storage" ) )
        {
            isStorage = false;
        }
        else if( localName.equals( "DicoogleDir" ) )
        {
            isDicoogleDir = false;            
        }
        else if( localName.equals( "fullContentIndex" ) )
        {
            isFullContentIndex = false;            
        }        
        else if( localName.equals( "SaveThumbnails" ) )
        {
            isSaveThumbnails = false;            
        }          
        else if( localName.equals( "ThumbnailsMatrix" ) )
        {
            isThumbnailsMatrix = false;            
        }  
        else if(localName.equals("AETitle"))
        {
            isAET = false;
        }
        else if(localName.equals("CAETitle"))
        {
            isCAET = false;
        }
        else if(localName.equals("PermitAllAETitles"))
        {
            isPermitAllAETitles = false;
        }
        else if(localName.equals("Path"))
        {
            isPath = false;
        }        
        else if(localName.equals("TS"))
        {
            isTS = false;            
        }
        if(localName.equals("Service"))
        {             
            list.updateTS(currentService, LocalTS.getTS(), true);
        }
        
        /** Worklist Server */
        if (localName.equals("DeviceDescription"))
        {
            isDeviceDescription = false ; 
        }
        else if (localName.equals("LocalAETName"))
        {
            isLocalAETName = false ; 
        }
        else if (localName.equals("PermitedRemoveAETsNames"))
        {
            isPermitedRemoteAETsNames = false ; 
        }
        else if (localName.equals("PermitedLocalInterfaces"))
        {
           isPermitedLocalInterfaces = false ;
        }
        else if (localName.equals("PermitedRemoveHostnames"))
        {
            isPermitedRemoveHostnames = false ; 
        }
        else if (localName.equals("RspDelay"))
        {
            isRspDelay = false ; 
        }
        else if (localName.equals("DIMSERspTimeout"))
        {
            isDIMSERspTimeout = false ; 
        }
        else if (localName.equals("IdleTimeout"))
        {
            isIdleTimeout = false ; 
        }
        else if (localName.equals("AcceptTimeout"))
        {
            isAcceptTimeout = false ; 
        }
        else if (localName.equals("ConnectionTimeout"))
        {
            isConnectTimeout = false ; 
        }
        else if (localName.equals("SOPClass"))
        {
            isSOPClass = false ; 
        }
        else if (localName.equals("QREnable"))
        {
            QREnable = false ;
        }
        else if (localName.equals("MAX_CLIENT_ASSOCS"))
        {
            isMaxClientAssoc = false ; 
        }
        else if (localName.equals("MAX_PDU_LENGTH_RECEIVE"))
        {
            isMaxPDULengthReceive = false ; 
        }
        else if (localName.equals("MAX_PDU_LENGTH_SEND"))
        {
            isMaxPDULengthSend = false; 
        }
        if (localName.equals("options"))
        {
            this.options = false ;
        }
        else if (localName.equals("modality") && this.options)
        {
            this.modality = false ;
        }
        else if (localName.equals("cfind") && this.options && this.modality)
        {
            this.cfind = false ;
        }
        else if (localName.equals("find") && this.options && this.modality &&
                this.cfind)
        {
            this.find = false ;
        }

        else if(this.options && this.destinations && localName.equals("dest"))
        {
            this.dest = false  ;
        }

        else if (this.options && localName.equals("destinations"))
        {
            this.destinations = false ;
        }

        else if (this.priorityAET && localName.equals("CSTOREPriorities"))
        {
            this.priorityAET = false ;
        }



        else if (isWeb && localName.equals("web"))
        {
            this.isWeb = false ;
        }
        else if(isRGUIPort && localName.equals("RGUIPort") )
        {
            isRGUIPort = false;
        }
        else if(localName.equals("RGUIExtIP"))
        {
            isRGUIExtIP = false;
        }


     }
     
    @Override
    public void characters( char[] data, int start, int length ) {
        if (isIndexEffort) {
            String sEffort = new String(data, start, length);
            s.setIndexerEffort(Integer.parseInt(sEffort));
            return;
        }
        if (isPort && !isQRConfigs) {
            String sPort = new String(data, start, length);
            s.setStoragePort(Integer.parseInt(sPort));
            return;
        }
        if (isPort && isQRConfigs) {
            String sPort = new String(data, start, length);
            s.setWlsPort(Integer.parseInt(sPort));
            return;
        }
        if (isEncrypt) {
            String sView = new String(data, start, length);
            boolean result = false;

            if (sView.compareToIgnoreCase("true") == 0)
                result = true;

            s.setEncryptUsersFile(result);
            return;
        }


        if (isZIPFile) {
            String sView = new String(data, start, length);
            boolean result = false;
            if (sView.compareToIgnoreCase("true") == 0)
                result = true;
            s.setIndexZIPFiles(result);
            return;
        }

        if (isGZIPStorage) {
            String sView = new String(data, start, length);
            boolean result = false;
            if (sView.compareToIgnoreCase("true") == 0)
                result = true;
            s.setGzipStorage(result);
            return;

        }

        if (isIndexAnonymous) {
            String sView = new String(data, start, length);
            boolean result = false;
            if (sView.compareToIgnoreCase("true") == 0)
                result = true;
            s.setIndexAnonymous(result);
            return;
        }

        if (isMonitorWatcher) {
            String sView = new String(data, start, length);
            boolean result = false;
            if (sView.compareToIgnoreCase("true") == 0)
                result = true;
            s.setMonitorWatcher(result);
            return;
        }
        if (isP2P) {

            if (autoConnect) {
                String sView = new String(data, start, length);
                boolean result = false;
                if (sView.compareToIgnoreCase("true") == 0)
                    result = true;
//                 s.setP2P(result);
                return;

            } else if (maxmsg) {
                String max = new String(data, start, length);


                int maxMsg = Integer.valueOf(max);
                s.setMaxMessages(maxMsg);
                return;

            } else if (isNode && isNodeName) {
                String nodeName = new String(data, start, length);
                s.setNodeName(nodeName);
            } else if (isNode && isDefined) {
                String tmp = new String(data, start, length);
                boolean result = false;
                if (tmp.compareToIgnoreCase("true") == 0)
                    result = true;
                s.setNodeNameDefined(result);
                return;
            }
        }

        if (priorityAET)
        {
            String aetitle = new String(data, start, length);
            s.addPriorityAETitle(aetitle);
            return;

        }

         if(isStorage)
         {
             String sView = new String(data, start, length);
             boolean result = false;
             if (sView.compareToIgnoreCase("true") == 0)
                result = true;
             s.setStorage(result);
             return;
         }
         if(isDicoogleDir) //( "DicoogleDir" ) )
         {
             String sView = new String(data, start, length);
             s.setDicoogleDir(sView);
             return;           
         } 
         if(isFullContentIndex) 
         {
             String sView = new String(data, start, length);
             boolean result = false;
             if (sView.compareToIgnoreCase("true") == 0)
                result = true;
             s.setFullContentIndex(result);
             return;           
         }     
         if(isSaveThumbnails)
         {
             String sView = new String(data, start, length);
             boolean result = false;
             if (sView.compareToIgnoreCase("true") == 0)
                result = true;
             s.setSaveThumbnails(result);
             return;           
         }         
         if(isThumbnailsMatrix) 
         {
             String sView = new String(data, start, length);
             s.setThumbnailsMatrix(sView);
             return;              
         }
         if(isAET)
         { 
             String sAET = new String(data, start, length);
             if(sAET.equals(" "))
             {                
                s.setAE(null);
             }
             else
             {
                s.setAE(sAET);
             }
             return;
         }
         if(isCAET)
         { 
             String sCAET = new String(data, start, length);             
             m.addElement(sCAET);
             String [] CAET = new String[m.getSize()];
             m.copyInto(CAET);
             s.setCAET(CAET);
             return;
         }
         if(isPermitAllAETitles){
            String sPermit = new String(data, start, length);
             boolean result = false;
             if (sPermit.compareToIgnoreCase("true") == 0)
                result = true;
             s.setPermitAllAETitles(result);
             return;
         }
         if(isPath)
         { 
             String sPath = new String(data, start, length);
             if(sPath.equals(" "))
             {
                s.setPath(".");
             }
             else
             {
                s.setPath(sPath);
             }
             return;
         }
         if(isTS)
         {
            String sTS = new String(data, start, length);
            if(UID.ImplicitVRLittleEndian.equals(sTS))
            {
                    LocalTS.setTS(true, 0);                    
                    return;
            }
            if(UID.ExplicitVRLittleEndian.equals(sTS))
            {
                    LocalTS.setTS(true, 1);
                    return;
            }
            if(UID.DeflatedExplicitVRLittleEndian.equals(sTS))
            {
                    LocalTS.setTS(true, 2);
                    return;
            }              
            if(UID.ExplicitVRBigEndian.equals(sTS))
            {
                    LocalTS.setTS(true, 3);
                    return;
            }
            if(UID.JPEGLossless.equals(sTS))
            {
                    LocalTS.setTS(true, 4);
                    return;
            }
            if(UID.JPEGLSLossless.equals(sTS))
            {
                    LocalTS.setTS(true, 5);
                    return;
            }
            if(UID.JPEGLosslessNonHierarchical14.equals(sTS))
            {
                    LocalTS.setTS(true, 6);
                    return;
            }
            if(UID.JPEG2000LosslessOnly.equals(sTS))
            {
                    LocalTS.setTS(true, 7);
                    return;
            }
            if(UID.JPEGBaseline1.equals(sTS))
            {
                    LocalTS.setTS(true, 8);
                    return;
            }
            if(UID.JPEGExtended24.equals(sTS))
            {
                    LocalTS.setTS(true, 9);
                    return;
            }
            if(UID.JPEGLSLossyNearLossless.equals(sTS))
            {
                    LocalTS.setTS(true, 10);
                    return;
            }
            if(UID.JPEG2000.equals(sTS))
            {
                    LocalTS.setTS(true, 11);
                    return;
            }
            if(UID.RLELossless.equals(sTS))
            {
                    LocalTS.setTS(true, 12);
                    return;
            }
            if(UID.MPEG2.equals(sTS))
            {
                    LocalTS.setTS(true, 13);
                    return;
            }
         }

         /** QueryRetrieve Server */
         if (isQRConfigs && QREnable)
         {
             String tmp = new String(data, start, length);
             if(tmp.equals("true")){
                 s.setQueryRetrive(true);
             }
             else{
                s.setQueryRetrive(false);
                //DebugManager.getInstance().debug("QueryRetrieve service is disable by default");
             }
             return;

         }
         if (isQRConfigs && isSOPClass)
         {
             String tmp = new String(data, start, length);
             return;
         }
         if (isQRConfigs && isTransfCap)
         {
            return;
         }
         
        if (isQRConfigs && options && modality && cfind )
        {
            if (find)
            {
                String sop = new String(data, start, length);

                s.addModalityFind(sopId, sop.trim());
                return ;
            }

        }
        if (isQRConfigs && options && destinations)
        {
            return;


        }
        if(isMaxClientAssoc){
            String sNumber = new String(data, start, length);
            s.setMaxClientAssoc(Integer.parseInt(sNumber));
            return;
        }
        if(isMaxPDULengthReceive){
            String sNumber = new String(data, start, length);
            s.setMaxPDULengthReceive(Integer.parseInt(sNumber));
            return;
        }
        if(isMaxPDULengthSend){
            String sNumber = new String(data, start, length);
            s.setMaxPDULengthSend(Integer.parseInt(sNumber));
            return;
        }
        if(isRspDelay){
            String sNumber = new String(data, start, length);
            s.setRspDelay(Integer.parseInt(sNumber));
            return;
        }
        if(isIdleTimeout){
            String sNumber = new String(data, start, length);
            s.setIdleTimeout(Integer.parseInt(sNumber));
            return;
        }
        if(isAcceptTimeout){
            String sNumber = new String(data, start, length);
            s.setAcceptTimeout(Integer.parseInt(sNumber));
            return;
        }
        if(isConnectTimeout){
            String sNumber = new String(data, start, length);
            s.setConnectionTimeout(Integer.parseInt(sNumber));
            return;
        }
        if(isRGUIPort){
            String sPort = new String(data, start, length);
            s.setRemoteGUIPort(Integer.parseInt(sPort));
            return;
        }
        if(isRGUIExtIP){
            String sPort = new String(data, start, length);
            s.setRGUIExternalIP(sPort);
            return;
        }
        if(isWANModeEnabled){
            String a = new String(data, start, length);
            boolean b = Boolean.parseBoolean(a);
            s.setWanmode(b);
            return;
        }
         
     }


     
     private String resolveAttrib( String attr, Attributes attribs, String defaultValue)
     {

         String tmp = attribs.getValue(attr);



         return (tmp!=null)?(tmp):(defaultValue);
     }


     private String resolveAttrib( String uri, String localName, Attributes attribs, String defaultValue) {
         String tmp = attribs.getValue("UID"); 
         return (tmp!=null)?(tmp):(defaultValue);
     } 
     
     /** 
      * Worklist Server - Verify if qr is enable
      * @param uri
      * @param localName
      * @param attribs
      * @param defaultValue
      * @return
      */
     private boolean getStatus( String uri, String localName, Attributes attribs, String defaultValue) {
         String tmp = attribs.getValue("state"); 
         return (tmp!=null && tmp.equals("on"))?(true):(false);
     } 
     
     
  
    public ServerSettings getXML()
    {        
        try 
        {
            File file = new File(Platform.homePath() + "config.xml");
            if (!file.exists())
            {   
                s.setDefaultSettings();
                list.setDefaultSettings();                
                printXML();
                return s;
            }
            InputSource src = new InputSource( new FileInputStream(file) );
            XMLReader r = XMLReaderFactory.createXMLReader();
            r.setContentHandler(this);
            r.parse(src);
            return s;
        }
        catch (IOException | SAXException ex)
        {
            logger.warn("Failed to read XML config file", ex);
        }        
        return null;
    }
    
    public void printXML()
    {
        FileOutputStream out = null;
        list.CleanList();
        try {
            out = new FileOutputStream(Platform.homePath() + "config.xml");
            PrintWriter pw = new PrintWriter(out);
            StreamResult streamResult = new StreamResult(pw);
            SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
            //      SAX2.0 ContentHandler.
            TransformerHandler hd = tf.newTransformerHandler();
            Transformer serializer = hd.getTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");            
            serializer.setOutputProperty(OutputKeys.METHOD, "xml");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");            
            hd.setResult(streamResult);
            hd.startDocument();
            
            //Get a processing instruction
            //hd.processingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"mystyle.xsl\"");
            AttributesImpl atts = new AttributesImpl();
            
            //root element
            hd.startElement("", "", "Config", atts);            


            String curTitle = String.valueOf(s.getStoragePort());
            //port            
            hd.startElement("", "", "Port", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "Port");

            atts.clear();
            hd.startElement("", "", "RemoteGUI", atts);

            curTitle = String.valueOf(s.getRemoteGUIPort());
            //GUIport
            hd.startElement("", "", "RGUIPort", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "RGUIPort");


            curTitle = s.getRGUIExternalIP();
            if(curTitle != null && !curTitle.equals(""))
            {
                hd.startElement("", "", "RGUIExtIP", atts);
                hd.characters(curTitle.toCharArray(), 0, curTitle.length());
                hd.endElement("", "", "RGUIExtIP");
            }
            hd.endElement("", "", "RemoteGUI");
            
            
            curTitle = s.getPath();
            //path          
            hd.startElement("", "", "Path", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "Path");

            curTitle = String.valueOf(s.getIndexerEffort());
            //path
            hd.startElement("", "", "IndexEffort", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "IndexEffort");

            hd.startElement("", "", "IndexZipFiles", atts);

            if (s.isIndexZIPFiles())
                curTitle = "true";
            else
                curTitle = "false";

            //Enable IndexZipFiles

            hd.characters(curTitle.toCharArray(), 0, curTitle.length());

            hd.endElement("", "", "IndexZipFiles");
            
            
            
            hd.startElement("", "", "GZipStorage", atts);

            if (s.isGzipStorage())
                curTitle = "true";
            else
                curTitle = "false";

            //Enable IndexZipFiles

            hd.characters(curTitle.toCharArray(), 0, curTitle.length());

            hd.endElement("", "", "GZipStorage");
            
            
            
            

            hd.startElement("", "", "IndexAnonymous", atts);

            if (s.isIndexAnonymous())
                curTitle = "true";
            else
                curTitle = "false";

            //Enable IndexZipFiles

            hd.characters(curTitle.toCharArray(), 0, curTitle.length());

            hd.endElement("", "", "IndexAnonymous");
            
            hd.startElement("", "", "MonitorWatcher", atts);
            if (s.isMonitorWatcher())
                curTitle = "true";
            else
                curTitle = "false";

            //Enable IndexZipFiles

            hd.characters(curTitle.toCharArray(), 0, curTitle.length());

            hd.endElement("", "", "MonitorWatcher");
            
            

            hd.startElement("", "", "EncryptUsersFile", atts);
            if (s.isEncryptUsersFile())
                curTitle = "true";
            else
                curTitle = "false";
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "EncryptUsersFile");

            if (s.isWANModeEnabled())
                curTitle = "true";
            else
                curTitle = "false";
            hd.startElement("", "", "WANModeEnabled", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "WANModeEnabled");
            
            hd.startElement("", "", "P2P", atts);
            
            hd.startElement("", "", "AutoConnect", atts);

/*            if (s.isP2P())
                curTitle = "true";
            else
                curTitle = "false";*/

            //Enable P2P
            
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            
            hd.endElement("", "", "AutoConnect");

            hd.startElement("", "", "MaxMsg", atts);
            curTitle = String.valueOf(s.getMaxMessages());
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());

            hd.endElement("", "", "MaxMsg");

            hd.startElement("", "", "Node", atts);

            hd.startElement("", "", "name", atts);
            hd.characters(s.getNodeName().toCharArray(), 0, s.getNodeName().length());
            hd.endElement("", "", "name");

            if (s.isNodeNameDefined())
                curTitle = "true";
            else
                curTitle = "false";
            hd.startElement("", "", "defined", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "defined");


            hd.endElement("", "", "Node");


            hd.endElement("", "", "P2P");

            if (s.isStorage())
                curTitle = "true";
            else
                curTitle = "false";

            //Enable P2P
            hd.startElement("", "", "Storage", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "Storage");

            curTitle = s.getDicoogleDir();
            //Diccogle Scan Dir           
            hd.startElement("", "", "DicoogleDir", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "DicoogleDir");
            
            
            curTitle = Boolean.toString(s.getFullContentIndex());
            // FullContentIndex           
            hd.startElement("", "", "fullContentIndex", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "fullContentIndex");

            curTitle = Boolean.toString(s.getSaveThumbnails());
            // saveThumbnails           
            hd.startElement("", "", "SaveThumbnails", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "SaveThumbnails");

            curTitle = s.getThumbnailsMatrix();
            // saveThumbnails           
            hd.startElement("", "", "ThumbnailsMatrix", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "ThumbnailsMatrix");
            
            curTitle = s.getAE();
            //AET
            hd.startElement("", "", "AETitle", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "AETitle");


            if (s.getPermitAllAETitles())
                curTitle = "true";
            else
                curTitle = "false";

            //Enable P2P
            hd.startElement("", "", "PermitAllAETitles", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "PermitAllAETitles");

            //Permited Client AETitles
            String [] CAET = s.getCAET();
            if(CAET!= null)
            {
                for(int i=0; i<CAET.length; i++)
                {
                    curTitle = CAET[i];
                    hd.startElement("", "", "CAETitle", atts);
                    hd.characters(curTitle.toCharArray(), 0, curTitle.length());
                    hd.endElement("", "", "CAETitle");
                }
            }
            
            
            List l = list.getKeys();
            boolean [] TS;
            int i = l.size()-1;
            for(i = l.size()-1; i >= 0; i--)
            {
               
               LocalTS = list.getTS(l.get(i).toString());
               if(LocalTS.getAccepted())
               {
                    atts.addAttribute("", "", "UID", "", l.get(i).toString());            
                    hd.startElement("", "", "Service", atts);
                    atts.clear();
                    TS = LocalTS.getTS();
                    if(TS[0])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.ImplicitVRLittleEndian.toCharArray(), 0, UID.ImplicitVRLittleEndian.length());
                        hd.endElement("", "", "TS");
                    }
                    if(TS[1])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.ExplicitVRLittleEndian.toCharArray(), 0, UID.ExplicitVRLittleEndian.length());
                        hd.endElement("", "", "TS");
                    }
                    if(TS[2])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.DeflatedExplicitVRLittleEndian.toCharArray(), 0, UID.DeflatedExplicitVRLittleEndian.length());
                        hd.endElement("", "", "TS");
                    }
                    if(TS[3])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.ExplicitVRBigEndian.toCharArray(), 0, UID.ExplicitVRBigEndian.length());
                        hd.endElement("", "", "TS");
                    }
                    if(TS[4])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.JPEGLossless.toCharArray(), 0, UID.JPEGLossless.length());
                        hd.endElement("", "", "TS");
                    }
                    if(TS[5])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.JPEGLSLossless.toCharArray(), 0, UID.JPEGLSLossless.length());
                        hd.endElement("", "", "TS");
                    }
                    if(TS[6])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.JPEGLosslessNonHierarchical14.toCharArray(), 0, UID.JPEGLosslessNonHierarchical14.length());
                        hd.endElement("", "", "TS");
                    }
                    if(TS[7])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.JPEG2000LosslessOnly.toCharArray(), 0, UID.JPEG2000LosslessOnly.length());
                        hd.endElement("", "", "TS");
                    }
                    if(TS[8])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.JPEGBaseline1.toCharArray(), 0, UID.JPEGBaseline1.length());
                        hd.endElement("", "", "TS");
                    }
                    if(TS[9])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.JPEGExtended24.toCharArray(), 0, UID.JPEGExtended24.length());
                        hd.endElement("", "", "TS");
                    }
                    if(TS[10])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.JPEGLSLossyNearLossless.toCharArray(), 0, UID.JPEGLSLossyNearLossless.length());
                        hd.endElement("", "", "TS");
                    }
                    if(TS[11])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.JPEG2000.toCharArray(), 0, UID.JPEG2000.length());
                        hd.endElement("", "", "TS");
                    }
                    if(TS[12])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.RLELossless.toCharArray(), 0, UID.RLELossless.length());
                        hd.endElement("", "", "TS");
                    }
                    if(TS[13])
                    {
                        hd.startElement("", "", "TS",atts);
                        hd.characters(UID.MPEG2.toCharArray(), 0, UID.MPEG2.length());
                        hd.endElement("", "", "TS");
                    }
                    hd.endElement("", "", "Service");


               }
            }


            // LocalAETName
            curTitle = s.getLocalAETName();
            hd.startElement("", "", "LocalAETName", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "LocalAETName");



             /**
               * Query Retrieve stuff
             */
            
            //Query Retrieve
            hd.startElement("", "", "QueryRetrieve", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());

            curTitle = s.getDeviceDescription();
            // Device Description
            curTitle = s.getDeviceDescription();
            hd.startElement("", "", "DeviceDescription", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "DeviceDescription");

            if (s.isQueryRetrive())
                curTitle = "true";
            else
                curTitle = "false";

            //Enable QueryRetrieve by default
            hd.startElement("", "", "QREnable", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "QREnable");

            // Port
            curTitle = Integer.toString(s.getWlsPort());
            hd.startElement("", "", "Port", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "Port");

            // Permited Local Interfaces
            curTitle = s.getPermitedLocalInterfaces();
            hd.startElement("", "", "PermitedLocalInterfaces", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "PermitedLocalInterfaces");

            curTitle = s.getPermitedRemoteHostnames();
            hd.startElement("", "", "PermitedHostnames", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "PermitedHostnames");

            curTitle = Integer.toString(s.getRspDelay());
            hd.startElement("", "", "RspDelay", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "RspDelay");

            curTitle = Integer.toString(s.getDIMSERspTimeout());
            hd.startElement("", "", "DIMSERspTimeout", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "DIMSERspTimeout");

            curTitle = Integer.toString(s.getIdleTimeout());
            hd.startElement("", "", "IdleTimeout", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "IdleTimeout");

            curTitle = Integer.toString(s.getAcceptTimeout());
            hd.startElement("", "", "AcceptTimeout", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "AcceptTimeout");

            curTitle = Integer.toString(s.getConnectionTimeout());
            hd.startElement("", "", "ConnectionTimeout", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "ConnectionTimeout");

            curTitle = s.getTransfCap();
            hd.startElement("", "", "TRANSF_CAP", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "TRANSF_CAP");

            curTitle = s.getSOPClass() ; 
            hd.startElement("", "", "SOP_CLASS", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "SOP_CLASS");

            curTitle = Integer.toString(s.getMaxClientAssoc());
            hd.startElement("", "", "MAX_CLIENT_ASSOCS", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "MAX_CLIENT_ASSOCS");

            curTitle = Integer.toString(s.getMaxPDULengthReceive());
            hd.startElement("", "", "MAX_PDU_LENGTH_RECEIVE", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "MAX_PDU_LENGTH_RECEIVE");

            curTitle = Integer.toString(s.getMaxPDULenghtSend());
            hd.startElement("", "", "MAX_PDU_LENGTH_SEND", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "MAX_PDU_LENGTH_SEND");


            hd.startElement("", "", "Retrieve", atts);

            curTitle = s.getSOPClass();
            hd.startElement("", "", "SOPClass", atts);
            hd.characters(curTitle.toCharArray(), 0, curTitle.length());
            hd.endElement("", "", "SOPClass");


            hd.endElement("", "", "Retrieve");


            //root element
            hd.startElement("", "", "options", atts);

            //modality
            hd.startElement("", "", "modality", atts);



            // cfind
            hd.startElement("", "", "cfind", atts);

            HashMap<String, String> map = s.getModalityFind();
            for (String sop : map.keySet())
            {
                atts.addAttribute("", "", "sop", "", sop);
                hd.startElement("", "", "find", atts);
                atts.clear();
                String tmp = map.get(sop);
                hd.characters(tmp.toCharArray(), 0, tmp.length());
                hd.endElement("", "", "find");
            }
            hd.endElement("", "", "cfind");


            hd.endElement("", "", "modality");


            // destinations

            hd.startElement("", "", "destinations", atts);


            ArrayList<MoveDestination> moves = s.getMoves();
            for (MoveDestination m : moves)
            {

                atts.clear();
                atts.addAttribute("", "", "ae", "", m.getAETitle());
                atts.addAttribute("", "", "ip", "", m.getIpAddrs());
                atts.addAttribute("", "", "description", "", m.getDescription());
                atts.addAttribute("", "", "public", "",Boolean.toString(m.isIsPublic()));

                atts.addAttribute("", "", "port", "", String.valueOf(m.getPort()));

                hd.startElement("", "", "dest", atts);

                hd.endElement("", "", "dest");
                atts.clear();
            }


            hd.endElement("", "", "destinations");


            // CSTOREPriorities

            hd.startElement("", "", "CSTOREPriorities", atts);

            for (String aet : ServerSettings.getInstance().getPriorityAETitles())
            {
                atts.clear();
                hd.startElement("", "", "aetitle", atts);
                hd.characters(aet.toCharArray(), 0, aet.length());
                hd.endElement("", "", "aetitle");
            }

            hd.endElement("", "", "CSTOREPriorities");




            hd.endElement("", "", "options");

            hd.endElement("", "", "QueryRetrieve");


            /**
             * Web (including web server, webservices, etc)
             */

            hd.startElement("", "", "web", atts);


            ServerSettings.Web web = ServerSettings.getInstance().getWeb() ;

            // WebServer

            String tmp = "false";
            if (web.isWebServer())
                tmp = "true";

            atts.clear();
            atts.addAttribute("", "", "enable", "", tmp);
            atts.addAttribute("", "", "port", "", String.valueOf(web.getServerPort()));
            atts.addAttribute("", "", "allowedOrigins", "", web.getAllowedOrigins());

            hd.startElement("", "", "server", atts);

            hd.endElement("", "", "server");



            // WebServices

            tmp = "false";
            if (web.isWebServices())
                tmp = "true";

            atts.clear();
            atts.addAttribute("", "", "enable", "", tmp);
            atts.addAttribute("", "", "port", "", String.valueOf(web.getServicePort()));

            hd.startElement("", "", "services", atts);

            hd.endElement("", "", "services");


            hd.endElement("", "", "web");



            hd.endElement("", "", "Config");
            
            hd.endDocument();
                        
        } catch (TransformerConfigurationException ex) {
            
        } catch (SAXException ex) {
            
        } catch (FileNotFoundException ex) {
            
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                
            }
        }
   }
}