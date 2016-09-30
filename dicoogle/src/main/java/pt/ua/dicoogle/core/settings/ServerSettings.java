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
package pt.ua.dicoogle.core.settings;

import org.dcm4che2.data.UID;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.sdk.core.ServerSettingsReader;
import pt.ua.dicoogle.sdk.core.WebSettingsReader;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.server.web.utils.types.DataTable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Data type representing the settings of a server.
 *
 * @author Marco Pereira
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author António Novo <antonio.novo@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 *
 *
 */
@XmlRootElement(name = "Config")
public class ServerSettings implements ServerSettingsReader
{
    @XmlElement(name = "AETitle")
    private String AETitle;

    //Access List Settings
    private String [] CAETitle;
    @XmlElement(name = "PermitAllAETitles")
    private boolean permitAllAETitles;

    @XmlElement(name = "Path")
    private String Path;
    private String ID;
    private int storagePort;

    @Deprecated
    private int rGUIPort;
    @Deprecated
    private String RGUIExternalIP;

    //Dicoogle Settings
    @XmlElement(name = "DicoogleDir")
    private String dicoogleDir;
    @XmlElement(name = "fullContentIndex")
    private boolean fullContentIndex;
    @XmlElement(name = "SaveThumbnails")
    private boolean saveThumbnails;
    @XmlElement(name = "ThumbnailsMatrix")
    private String thumbnailsMatrix;

    /// auto-start storage service
    @XmlElement(name = "Storage")
    private boolean storage;
    private boolean encryptUsersFile;

    public static class QueryRetrieveSettings {
        @XmlElement(name = "QREnable")
        private boolean enabled;

        /* DEFAULT Brief class description */
        @XmlElement(name = "DeviceDescription")
        private String deviceDescription;

        /* DEFAULT Listening TCP port */
        @XmlElement(name = "Port")
        private int wlsPort;

        /* DEFAULT ("any"->null)Permited local interfaces to incomming connection
         * ('null'->any interface; 'ethx'->only this interface |->separator */
        @XmlElement(name = "PermitedLocalInterfaces")
        private String permitedLocalInterfaces;

        /* DEFAULT ("any"->null)Permited remote host name connections
         * ('null'->any can connect; 'www.x.com'->only this can connect |->separator */
        @XmlElement(name = "PermitedHostnames")
        private String permitedRemoteHostnames;

        /* DEFAULT Response delay (in miliseconds) */
        @XmlElement(name = "RspDelay")
        private int rspDelay;

        /* DEFAULT Dimse response timeout (in sec) */
        @XmlElement(name = "DIMSERspTimeout")
        private int DIMSERspTimeout;

        /* DEFAULT Idle timeout (in sec) */
        @XmlElement(name = "IdleTimeout")
        private int idleTimeout;

        /* DEFAULT Accept timeout (in sec) */
        @XmlElement(name = "AcceptTimeout")
        private int acceptTimeout;

        /* DEFAULT Connection timeout (in sec) */
        @XmlElement(name = "ConnectionTimeout")
        private int connectionTimeout;

        @XmlElement(name = "TRANSF_CAP")
        private String transfCAP;

        @XmlElement(name = "SOP_CLASS")
        private String SOPClass;

        @XmlElement(name = "MAX_CLIENT_ASSOCS")
        /* DEFAULT Max Client Associations */
        private int maxClientAssocs;

        @XmlElement(name = "MAX_PDU_LENGTH_RECEIVE")
        private int maxPDULengthReceive;

        @XmlElement(name = "MAX_PDU_LENGTH_SEND")
        private int maxPDULengthSend;


    }

    @XmlElement(name = "QueryRetrieve")
    private QueryRetrieveSettings queryRetrieve = new QueryRetrieveSettings();

    @XmlElement(name = "web")
    private Web web = new Web();

    @Deprecated
    @XmlElement(name = "WANModeEnabled")
    private boolean wanmode;

    /**
     * QueryRetrieve Server
     */

    /* DEFAULT Process Worklist Server AE Title */
    @XmlElement(name = "LocalAETName")
    private String localAETName ;



    // Connection settings
    @Deprecated
    private int maxMessages = 2000;

    HashMap<String, String> modalityFind = new HashMap<>();

    ArrayList<MoveDestination> dest = new ArrayList<>();

    private Set<String> priorityAETitles = new HashSet<>();

    private boolean indexAnonymous = false;

    @XmlElement(name = "IndexZipFiles")
    private boolean indexZIPFiles = true;

    @XmlElement(name = "MonitorWatcher")
    private boolean monitorWatcher = false;


    /**
     * P2P
     */

    private String p2pLibrary = "JGroups";
    private String nodeName = "Dicoogle";
    private boolean nodeNameDefined = false ;

    private String networkInterfaceName ="";

    /** Indexer */
    private String indexer = "lucene2.2";
    @XmlElement(name = "IndexEffort")
    private int indexerEffort = 0 ;
    private HashSet<String> extensionsAllowed = new HashSet<>();

    @XmlElement(name = "GZipStorage")
    private boolean gzipStorage = false;
    private final String aclxmlFileName = "aetitleFilter.xml";

    /**
     * Indicates, for each plugin, if it is to start at server init or not.
     */
    private final ConcurrentHashMap<String, Boolean> autoStartPlugin; // NOTE the concurrent hash map is used to prevent having to synchronize the methods that use it

    /**
     * The name of the Remote GUI setting that indicates the External IP address.
     */
    public static final String RGUI_SETTING_EXTERNAL_IP = "External IP";

    /**
     * The name of the Query Retrieve setting that indicates the Maximum number of Client Associations.
     */
    public static final String QUERYRETRIEVE_MAX_ASSOCIATIONS = "Max. Associations";
    /**
     * The name of the Query Retrieve setting that indicates the Maximum PDU Length Receive.
     */
    public static final String QUERYRETRIEVE_MAX_PDU_RECEIVE = "Max. PDU Receive";
    /**
     * The name of the Query Retrieve setting that indicates the Maximum PDU Length Send.
     */
    public static final String QUERYRETRIEVE_MAX_PDU_SEND = "Max. PDU Send";
    /**
     * The name of the Query Retrieve setting that indicates the Idle Timeout.
     */
    public static final String QUERYRETRIEVE_IDLE_TIMEOUT = "Idle Timeout";
    /**
     * The name of the Query Retrieve setting that indicates the Accpet Timeout.
     */
    public static final String QUERYRETRIEVE_ACCEPT_TIMEOUT = "Accept Timeout";
    /**
     * The name of the Query Retrieve setting that indicates the Response Timeout.
     */
    public static final String QUERYRETRIEVE_RESPONSE_TIMEOUT = "Response Timeout";
    /**
     * The name of the Query Retrieve setting that indicates the Connection Timeout.
     */
    public static final String QUERYRETRIEVE_CONNECTION_TIMEOUT = "Connection Timeout";

    /**
     * The name of the Storage setting that indicates the Servers Destinations.
     */
    public static final String STORAGE_SETTING_SERVERS_DESTINATIONS = "Storage Servers Destinations";
    /**
     * The name of the Storage setting that indicates the Storage Path.
     */
    public static final String STORAGE_SETTING_PATH = "Storage Path";

    /**
     * The help for the Remote GUI External IP setting.
     */
    public static final String RGUI_SETTING_EXTERNAL_IP_HELP = "If your Dicoogle GUI Server is running behind a router, you need to provide your external IP address to have access from outside of the router.\nBesides that, you need to configure your router to open the Remote GUI Port!";

    /**
     * @return the web
     */
    @Override
    public Web getWeb()
    {
        return web;
    }

    /**
     * @param web the web to set
     */
    public void setWeb(Web web)
    {
        this.web = web;
    }

    /**
     * @return the p2pLibrary
     */
    public String getP2pLibrary() {
        return p2pLibrary;
    }

    /**
     * @param p2pLibrary the p2pLibrary to set
     */
    public void setP2pLibrary(String p2pLibrary) {
        this.p2pLibrary = p2pLibrary;
    }

    /**
     * @return the indexer
     */
    @Override
    public String getIndexer() {
        return indexer;
    }

    /**
     * @param indexer the indexer to set
     */
    public void setIndexer(String indexer) {
        this.indexer = indexer;
    }

    /**
     * @return the nodeName
     */
    @Override
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName the nodeName to set
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the nodeNameDefined
     */
    @Override
    public boolean isNodeNameDefined() {
        return nodeNameDefined;
    }

    /**
     * @param nodeNameDefined the nodeNameDefined to set
     */
    public void setNodeNameDefined(boolean nodeNameDefined) {
        this.nodeNameDefined = nodeNameDefined;
    }

    @Override
    public String getNetworkInterfaceName()
    {
        return networkInterfaceName;
    }

    public void setNetworkInterfaceName(String interfaceName)
    {
        this.networkInterfaceName = interfaceName;
    }


    /**
     * @return the indexerEffort
     */
    @Override
    public int getIndexerEffort() {
        return indexerEffort;
    }

    /**
     * @param indexerEffort the indexerEffort to set
     */
    public void setIndexerEffort(int indexerEffort) {
        this.indexerEffort = indexerEffort;
    }

    /**
     * @return the encryptUsersFile
     */
    @Override
    public boolean isEncryptUsersFile() {
        return encryptUsersFile;
    }

    /**
     * @param encryptUsersFile the encryptUsersFile to set
     */
    public void setEncryptUsersFile(boolean encryptUsersFile) {
        this.encryptUsersFile = encryptUsersFile;
    }

    /**
     * @return the indexZIPFiles
     */
    @Override
    public boolean isIndexZIPFiles() {
        return indexZIPFiles;
    }

    /**
     * @param indexZIPFiles the indexZIPFiles to set
     */
    public void setIndexZIPFiles(boolean indexZIPFiles) {
        this.indexZIPFiles = indexZIPFiles;
    }

    /**
     * @return the RGUIExternalIP
     */
    @Deprecated
    public String getRGUIExternalIP() {
        return RGUIExternalIP;
    }

    /**
     * @param RGUIExternalIP the RGUIExternalIP to set
     */
    @Deprecated
    public void setRGUIExternalIP(String RGUIExternalIP) {
        this.RGUIExternalIP = RGUIExternalIP;
    }

    /**
     * @return the monitorWatcher
     */
    @Override
    public boolean isMonitorWatcher() {
        return monitorWatcher;
    }

    /**
     * @param monitorWatcher the monitorWatcher to set
     */
    public void setMonitorWatcher(boolean monitorWatcher) {
        this.monitorWatcher = monitorWatcher;
    }

    /**
     * @return the indexAnonymous
     */
    @Override
    public boolean isIndexAnonymous() {
        return indexAnonymous;
    }

    /**
     * @param indexAnonymous the indexAnonymous to set
     */
    public void setIndexAnonymous(boolean indexAnonymous) {
        this.indexAnonymous = indexAnonymous;
    }

    /**
     * @return the gzipStorage
     */
    @Override
    public boolean isGzipStorage() {
        return gzipStorage;
    }

    /**
     * @param gzipStorage the gzipStorage to set
     */
    public void setGzipStorage(boolean gzipStorage) {
        this.gzipStorage = gzipStorage;
    }

    @Override
    public String getAccessListFileName() {
        return this.aclxmlFileName;
    }

    /**
     * Web (including web server, webservices, etc)
     */
    public class Web implements WebSettingsReader
    {
        private boolean webServer = true;
        private int serverPort = 8080;
        private String accessControlAllowOrigins = "*";

        @Deprecated
        private boolean webServices = false;
        @Deprecated
        private int servicePort = 6060;
        


        public Web()
        {
        }

        /**
         * @return the webServer
         */
        @Override
        public boolean isWebServer() {
            return webServer;
        }

        /**
         * @param webServer the webServer to set
         */
        public void setWebServer(boolean webServer) {
            this.webServer = webServer;
        }

        /**
         * @return the webServices
         */
        @Deprecated
        public boolean isWebServices() {
            return webServices;
        }

        /**
         * @param webServices the webServices to set
         */
        @Deprecated
        public void setWebServices(boolean webServices) {
            this.webServices = webServices;
        }

        /**
         * @return the serverPort
         */
        @Override
        public int getServerPort() {
            return serverPort;
        }

        /**
         * @param serverPort the serverPort to set
         */
        public void setServerPort(int serverPort) {
            this.serverPort = serverPort;
        }

        /**
         * @return the servicePort
         */
        @Deprecated
        public int getServicePort() {
            return servicePort;
        }

        /**
         * @param servicePort the servicePort to set
         */
        @Deprecated
        public void setServicePort(int servicePort) {
            this.servicePort = servicePort;
        }

        @Override
        public String getAllowedOrigins() {
            return this.accessControlAllowOrigins;
        }

        public void setAllowedOrigins(String origins) {
            this.accessControlAllowOrigins = origins;
        }

    }

    private static ServerSettings instance = null;
    
    public static synchronized ServerSettings getInstance()
    {
        if (instance == null) {
            instance = new ServerSettings();
        }
        return instance;
    }

    private ServerSettings()
    {
        rGUIPort = 9014;
        storagePort = 104;
        AETitle = "DICOOGLE";
        CAETitle = new String[0];
        permitAllAETitles = true;
        Path = "";
        dicoogleDir = "";
        fullContentIndex = false;
        saveThumbnails = false;
        thumbnailsMatrix = "64";

        encryptUsersFile = false;

        this.localAETName  = "Dicoogle";
        /**
         * Set default values of QueryRetrieve Server
         */
        this.queryRetrieve = new QueryRetrieveSettings();

        this.queryRetrieve.deviceDescription = "Dicoogle - Server SCP" ;
        this.queryRetrieve.permitedLocalInterfaces = "any";
        this.queryRetrieve.permitedRemoteHostnames = "any";
        this.queryRetrieve.wlsPort = 1045 ;  // default: 104
        this.queryRetrieve.idleTimeout = 60 ;
        this.queryRetrieve.acceptTimeout = 60 ;
        this.queryRetrieve.rspDelay = 0 ;
        this.queryRetrieve.DIMSERspTimeout = 60 ;
        this.queryRetrieve.connectionTimeout = 60 ;
        
        this.queryRetrieve.transfCAP = UID.ImplicitVRLittleEndian + "|" + UID.ExplicitVRBigEndian + "|" + UID.ExplicitVRLittleEndian;

        this.queryRetrieve.SOPClass = UID.StudyRootQueryRetrieveInformationModelFIND
        + "|" + UID.PatientRootQueryRetrieveInformationModelFIND;
               
        fillModalityFindDefault();
        this.queryRetrieve.maxClientAssocs = 20 ;
        this.queryRetrieve.maxPDULengthReceive = 16364 ;
        this.queryRetrieve.maxPDULengthSend = 16364 ;

        // TODO get this out of here
        System.setProperty("java.net.preferIPv4Stack", "true");

        autoStartPlugin = new ConcurrentHashMap<>();
    }

    // Nasty bug fix; no thumbnails references here = null pointers
    public void setDefaultSettings()
    {
        rGUIPort = 9014;
        storagePort = 6666;
        AETitle = "DICOOGLE-STORAGE";
        Path = System.getProperty("java.io.tmpdir");
        CAETitle = new String[0];
        permitAllAETitles = true;
        dicoogleDir = System.getProperty("java.io.tmpdir");
        fullContentIndex = false;
        saveThumbnails = false;
        thumbnailsMatrix = "64";
        autoStartPlugin.clear();

        setEncryptUsersFile(false);
    }

    public void setAE(String AE)
    {
        AETitle = AE;
    }

    @Override
    public String getAE()
    {
        return AETitle;
    }

    public void setID(String I)
    {
        ID = I;
    }

    @Override
    public String getID()
    {
        return ID;
    }

    public void setCAET(String[] CAET)
    {
        CAETitle = CAET;            
    }

    @Override
    public String[] getCAET()
    {
        return CAETitle;
    }

    public void setPermitAllAETitles(boolean value){
        permitAllAETitles = value;
    }

    @Override
    public boolean getPermitAllAETitles(){
        return permitAllAETitles;
    }

    public void setStoragePort(int p)
    {
        storagePort = p;
    }

    public void setPath(String p)
    {
        Path = p;
    }

    @Override
    public String getPath()
    {
        return Path;
    }

    @Override
    public int getStoragePort()
    {
        return storagePort;
    }

    @Deprecated
    public void setRemoteGUIPort(int port){
        rGUIPort = port;
    }

    @Deprecated
    public int getRemoteGUIPort(){
        return rGUIPort;
    }

    @Override
    public String getDicoogleDir() {
        return dicoogleDir;
    }

    public void setDicoogleDir(String dicoogleDir) {
        this.dicoogleDir = dicoogleDir;
    }


    @Override
    public boolean getFullContentIndex() {
        return fullContentIndex;
    }

    public void setFullContentIndex(boolean fullContentIndex) {
        this.fullContentIndex = fullContentIndex;
    }

    @Override
    public boolean getSaveThumbnails() {
        return saveThumbnails;
    }

    public void setSaveThumbnails(boolean saveThumbnails) {
        this.saveThumbnails = saveThumbnails;
    }
    
    @Override
    public String getThumbnailsMatrix() {
        return thumbnailsMatrix;
    }

    public void setThumbnailsMatrix(String thumbnailsMatrix) {
        this.thumbnailsMatrix = thumbnailsMatrix;
    }

    /*
     * Query Retrieve Server
     */

    public void setWlsPort(int port)
    {
        this.queryRetrieve.wlsPort = port ;
    }

    @Override
    public int getWlsPort()
    {
        return this.queryRetrieve.wlsPort ;
    }

    public void setIdleTimeout(int timeout)
    {
        this.queryRetrieve.idleTimeout = timeout ;
    }

    @Override
    public int getIdleTimeout()
    {
        return this.queryRetrieve.idleTimeout ;
    }

    public void setRspDelay(int delay)
    {
        this.queryRetrieve.rspDelay = delay ;
    }

    @Override
    public int getRspDelay()
    {
        return this.queryRetrieve.rspDelay  ;
    }

    public void setAcceptTimeout(int timeout)
    {
        this.queryRetrieve.acceptTimeout = timeout ;
    }
    @Override
    public int getAcceptTimeout()
    {
        return this.queryRetrieve.acceptTimeout;
    }

    public void setConnectionTimeout(int timeout)
    {
        this.queryRetrieve.connectionTimeout = timeout;
    }
    @Override
    public int getConnectionTimeout()
    {
        return this.queryRetrieve.connectionTimeout ;
    }
    
    public void setSOPClass(String SOPClass)
    {
        this.queryRetrieve.SOPClass = SOPClass ;
    }
    
    @Override
    public String[] getSOPClasses()
    {
        String []tmp = {
            UID.StudyRootQueryRetrieveInformationModelFIND ,
            UID.PatientRootQueryRetrieveInformationModelFIND
        };
        return tmp ; 
    }

    @Override
    public String getSOPClass()
    {
        return this.queryRetrieve.SOPClass ;
    }
    public void setDIMSERspTimeout(int timeout)
    {
        this.queryRetrieve.DIMSERspTimeout = timeout ;
    }
    @Override
    public int getDIMSERspTimeout()
    {
        return this.queryRetrieve.DIMSERspTimeout ;
    }
    public void setDeviceDescription(String desc)
    {
        this.queryRetrieve.deviceDescription = desc ;
    }
    
    @Override
    public String getDeviceDescription()
    {
        return this.queryRetrieve.deviceDescription;
    }
    
    public void setTransfCap(String transfCap)
    {
        this.queryRetrieve.transfCAP = transfCap;
    }
        
    @Override
    public String getTransfCap()
    {
        return this.queryRetrieve.transfCAP;
    }
    
    public void setMaxClientAssoc(int maxClients)
    {
        this.queryRetrieve.maxClientAssocs = maxClients;
    }
    
    @Override
    public int getMaxClientAssoc()
    {
        return this.queryRetrieve.maxClientAssocs;
    }
    
    public void setMaxPDULengthReceive(int len)
    {
        this.queryRetrieve.maxPDULengthReceive = len;
    }
    
    @Override
    public int getMaxPDULengthReceive()
    {
        return this.queryRetrieve.maxPDULengthReceive;
    }
    public void setMaxPDULengthSend(int len)
    {
        this.queryRetrieve.maxPDULengthSend = len;
    }
    @Override
    public int getMaxPDULenghtSend() // FIXME typo
    {
        return this.queryRetrieve.maxPDULengthSend;
    }
    
    public void setLocalAETName(String name)
    {
        this.localAETName = name; 
    }
    @Override
    public String getLocalAETName()
    {
        return this.localAETName; 
    }
    
    public void setPermitedLocalInterfaces(String localInterfaces)
    {
        this.queryRetrieve.permitedLocalInterfaces  = localInterfaces;
    }
    
    @Override
    public String getPermitedLocalInterfaces()
    {
        return this.queryRetrieve.permitedLocalInterfaces;
    }
    
    public void setPermitedRemoteHostnames(String remoteHostnames)
    {
        this.queryRetrieve.permitedRemoteHostnames = remoteHostnames;
    }
    
    @Override
    public String getPermitedRemoteHostnames()
    {
        return this.queryRetrieve.permitedRemoteHostnames;
    }

    /**
     * @return the P2P
     */
   /* public boolean isP2P() {
        return P2P;
    }*/
    @Override
    public boolean isStorage() {
        return storage;
    }
    @Override
    public boolean isQueryRetrive() {
        return queryRetrieve.enabled;
    }

    public void add(MoveDestination m)
    {
        this.dest.add(m);
    }
    public boolean remove(MoveDestination m)
    {
        return this.dest.remove(m);
    }
    public boolean removeMoveDestination(String AETitle, String ipAddr, int port)
    {
    	for(int i=0;i<dest.size(); i++)
    	{
    		MoveDestination mv = dest.get(i);
    		if(mv.getAETitle().equals(AETitle) && mv.getIpAddrs().equals(ipAddr) && mv.getPort() == port)
    		{
    			dest.remove(i);
    			return true;
    		}
    			
    	}
    	return false;
    }
    public boolean contains(MoveDestination m){
        return this.dest.contains(m);
    }
    @Override
    public ArrayList<MoveDestination> getMoves()
    {
        return this.dest ;
    }

    @Override
    public Set<String> getPriorityAETitles() {
        return priorityAETitles;
    }

    public void addPriorityAETitle(String aet)
    {
        this.priorityAETitles.add(aet);
    }
    public void removePriorityAETitle(String aet)
    {
        this.priorityAETitles.remove(aet);
    }

    public void setMoves(ArrayList<MoveDestination> moves)
    {
        if(moves != null)
            this.dest = moves;
    }



    private void fillModalityFindDefault()
    {
         addModalityFind("1.2.840.10008.5.1.4.1.2.2.1",
                 "Study Root Query/Retrieve Information Model");

         addModalityFind("1.2.840.10008.5.1.4.1.2.1.1",
                    "Patient Root Query/Retrieve Information Model"
                 );

    }

    /**
     * Set default values
     */
    public void setDefaultsValues()
    {
        this.fillModalityFindDefault();
    }

    /**
     * Add a modality
     * @param sop Number like 1.2.3.5.6.7.32.1
     * @param description Description like "Modality Worklist Model"
     */
    public void addModalityFind(String sop, String description)
    {
        this.modalityFind.put(sop, description);
    }

    /**
     *
     * @return HashMap with Modalitys FIND
     */
    @Override
    public HashMap<String, String> getModalityFind()
    {
        return this.modalityFind;
    }


    /**
     * Sets the plugin start on server init value.
     * <b>NOTE</b>: this method is strictly for plugins, not embedded services.
     *
     * @param name the name of the plugin.
     * @param value true to auto start the plugin on server init.
     */
    public void setAutoStartPlugin(String name, boolean value)
    {
    	// remove the previous setting, if there is one
    	autoStartPlugin.remove(name);

        // insert the new setting
        autoStartPlugin.put(name, value);
    }

    /**
     * Returns if the plugin is to be auto started on server init or not.
     * The default value (without previous configuration) is to start the plugin.
     * <b>NOTE</b>: this method is strictly for plugins, not embedded services.
     *
     * @param name the name of the plugin.
     * @return true if the plugin is to be auto started on server init or false if it is not.
     */
    @Override
    public boolean getAutoStartPlugin(String name)
    {
    	Boolean result = autoStartPlugin.get(name);

        // if there is not such setting return the default value
        if (result == null) return true; // by default start the plugin
        return result.booleanValue();
    }

	/**
	 * Returns the current settings for plugin auto start on server init.
	 *
	 * @return the current settings for plugin auto start on server init.
	 */
    @Override
	public ConcurrentHashMap<String, Boolean> getAutoStartPluginsSettings()
	{
		return autoStartPlugin;
	}

	/**
	 * Returns the Remote GUI list of settings (name, value/type pairs).
	 *
	 * @return and HashMap containing the Remote GUI list of settings (name, value/type pairs).
	 */
    @Deprecated
	public HashMap<String, Object> getRGUISettings()
	{
		HashMap<String, Object> result = new HashMap<String, Object>();

		result.put(RGUI_SETTING_EXTERNAL_IP, getRGUIExternalIP());

		return result;
	}

	/**
	 * Validates the new settings for the Remote GUI service.
	 *
	 * @param settings a HashMap containing the new setting values.
	 * @return true if all the values are valid and can be applied, false otherwise.
	 */
    @Deprecated
	public boolean tryRGUISettings(HashMap<String, Object> settings)
	{
		// TODO
		return true;
	}

	/**
	 * Tries to apply the new settings for the Remote GUI service.
	 *
	 * @param settings a HashMap containing the new setting values.
	 * @return true if all the values are valid and were applied successfully, false otherwise.
	 */
    @Deprecated
	public boolean setRGUISettings(HashMap<String, Object> settings)
	{
		if (! tryRGUISettings(settings))
			return false;

		setRGUIExternalIP((String) settings.get(RGUI_SETTING_EXTERNAL_IP));

		return true;
	}

	/**
	 * Returns the Remote GUI list of settings help (name,help pairs).
	 *
	 * @return and HashMap containing the Remote GUI list of settings help (name, help).
	 */
	public HashMap<String, String> getRGUISettingsHelp()
	{
		HashMap<String, String> result = new HashMap<String, String>();
	
		result.put(RGUI_SETTING_EXTERNAL_IP, RGUI_SETTING_EXTERNAL_IP_HELP);

		return result;
	}

	/**
	 * Returns the Query Retrieve list of settings (name, value/type pairs).
	 *
	 * @return and HashMap containing the Query Retrieve list of settings (name, value/type pairs).
	 */
    @Override
	public HashMap<String, Object> getQueryRetrieveSettings()
	{
		HashMap<String, Object> result = new HashMap<>();

		result.put(QUERYRETRIEVE_MAX_ASSOCIATIONS, getMaxClientAssoc());
		result.put(QUERYRETRIEVE_MAX_PDU_RECEIVE, getMaxPDULengthReceive());
		result.put(QUERYRETRIEVE_MAX_PDU_SEND, getMaxPDULenghtSend());
		result.put(QUERYRETRIEVE_IDLE_TIMEOUT, getIdleTimeout());
		result.put(QUERYRETRIEVE_ACCEPT_TIMEOUT, getAcceptTimeout());
		result.put(QUERYRETRIEVE_RESPONSE_TIMEOUT, getRspDelay());
		result.put(QUERYRETRIEVE_CONNECTION_TIMEOUT, getConnectionTimeout());

		return result;
	}

	/**
	 * Validates the new settings for the Query Retrieve service.
	 *
	 * @param settings a HashMap containing the new setting values.
	 * @return true if all the values are valid and can be applied, false otherwise.
	 */
	public boolean tryQueryRetrieveSettings(HashMap<String, Object> settings)
	{
		// TODO
		return true;
	}

	/**
	 * Tries to apply the new settings for the Query Retrieve service.
	 *
	 * @param settings a HashMap containing the new setting values.
	 * @return true if all the values are valid and were applied successfully, false otherwise.
	 */
	public boolean setQueryRetrieveSettings(HashMap<String, Object> settings)
	{
		if (! tryQueryRetrieveSettings(settings))
			return false;

		setMaxClientAssoc(((Integer) settings.get(QUERYRETRIEVE_MAX_ASSOCIATIONS)).intValue());
		setMaxPDULengthReceive(((Integer) settings.get(QUERYRETRIEVE_MAX_PDU_RECEIVE)).intValue());
		setMaxPDULengthSend(((Integer) settings.get(QUERYRETRIEVE_MAX_PDU_SEND)).intValue());
		setIdleTimeout(((Integer) settings.get(QUERYRETRIEVE_IDLE_TIMEOUT)).intValue());
		setAcceptTimeout(((Integer) settings.get(QUERYRETRIEVE_ACCEPT_TIMEOUT)).intValue());
		setRspDelay(((Integer) settings.get(QUERYRETRIEVE_RESPONSE_TIMEOUT)).intValue());
		setConnectionTimeout(((Integer) settings.get(QUERYRETRIEVE_CONNECTION_TIMEOUT)).intValue());

		return true;
	}

	/**
	 * Returns the Query Retrieve list of settings help (name,help pairs).
	 *
	 * @return and HashMap containing the Query Retrieve list of settings help (name, help).
	 */
	public HashMap<String, String> getQueryRetrieveSettingsHelp()
	{
		return null; // no help available
	}

	/**
	 * Returns the Storage list of settings (name, value/type pairs).
	 *
	 * @return and HashMap containing the Storage list of settings (name, value/type pairs).
	 */
    @Override
	public HashMap<String, Object> getStorageSettings()
	{
		HashMap<String, Object> result = new HashMap<>();

		//result.put(STORAGE_SETTING_PATH, new ServerDirectoryPath(getPath()));
		// TODO move some of these new classes onto the SDK, so that plugins can also process option types/fields
		int destCount = dest.size();
		DataTable storageServers = new DataTable(3, destCount);
		storageServers.setColumnName(0, "AETitle");
		storageServers.setColumnName(1, "IP");
		storageServers.setColumnName(2, "Port");
		// if there are no rows, then add an empty one (for reference)
		if (destCount < 1)
		{
			storageServers.addRow();
			storageServers.setCellData(0, 0, "");
			storageServers.setCellData(0, 1, "");
			storageServers.setCellData(0, 2, "");
		}
		else
			for (int i = 0; i < destCount; i++)
			{
				MoveDestination aDest = dest.get(i);
				storageServers.setCellData(i, 0, aDest.getAETitle());
				storageServers.setCellData(i, 1, aDest.getIpAddrs());
				storageServers.setCellData(i, 2, "" + aDest.getPort());
			}
		result.put(STORAGE_SETTING_SERVERS_DESTINATIONS, storageServers);

		return result;
	}

	/**
	 * Validates the new settings for the Storage service.
	 *
	 * @param settings a HashMap containing the new setting values.
	 * @return true if all the values are valid and can be applied, false otherwise.
	 */
	public boolean tryStorageSettings(HashMap<String, Object> settings)
	{
		// TODO
		return true;
	}

	/**
	 * Tries to apply the new settings for the Storage service.
	 *
	 * @param settings a HashMap containing the new setting values.
	 * @return true if all the values are valid and were applied successfully, false otherwise.
	 */
	public boolean setStorageSettings(HashMap<String, Object> settings)
	{
		if (! tryStorageSettings(settings))
			return false;

		//setPath(((ServerDirectoryPath) settings.get(STORAGE_SETTING_PATH)).getPath());
		// TODO set the query retrieve options

		return true;
	}

	/**
	 * Returns the Storage list of settings help (name,help pairs).
	 *
	 * @return and HashMap containing the Storage list of settings help (name, help).
	 */
	public HashMap<String, String> getStorageSettingsHelp()
	{
		return null; // no help available
	}

    public void setStorage(boolean storage)
    {
        this.storage = storage;
    }

    public void setQueryRetrive(boolean queryRetrieve)
    {
        this.queryRetrieve.enabled = queryRetrieve;
    }

    @Override
    public ArrayList<String> getNetworkInterfacesNames()
    {
        ArrayList<String> interfaces = new ArrayList<String>();
        Enumeration<NetworkInterface> nets = null;
        try
        {
            nets = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex)
        {
            ex.printStackTrace();
        }

        
        for (NetworkInterface netint : Collections.list(nets))
        {
            try
            {
                if (!netint.isLoopback())
                {
                    Enumeration<InetAddress> addresses = netint.getInetAddresses();
                    while (addresses.hasMoreElements())
                    {
                        if (Inet4Address.class.isInstance(addresses.nextElement()))
                        {
                            interfaces.add(netint.getDisplayName());
                        }
                    }
                }
            } catch (SocketException ex)
            {
                LoggerFactory.getLogger(ServerSettings.class).error(ex.getMessage(), ex);
            }
        }
        return interfaces;
    }

    @Override
    public String getNetworkInterfaceAddress()
    {
        Enumeration<NetworkInterface> nets = null;
        try
        {
            nets = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex)
        {
            ex.printStackTrace();
        }

        for(NetworkInterface netint : Collections.list(nets))
        {
            if(netint.getDisplayName().compareTo(this.networkInterfaceName) == 0)
            {
                Enumeration<InetAddress> addresses = netint.getInetAddresses();
                while(addresses.hasMoreElements())
                {
                    InetAddress address = addresses.nextElement();
                    if(Inet4Address.class.isInstance(address))
                    {
                        return address.getHostAddress();
                    }
                }
                return null;
            }
        }
        return null;
    }

    @Override
    @Deprecated
    public HashSet<String> getExtensionsAllowed()
    {
        return extensionsAllowed;
    }

    /**
     * @return the maxMessages
     */
    @Override
    @Deprecated
    public int getMaxMessages() {
        return maxMessages;
    }

    /**
     * @param maxMessages the maxMessages to set
     */
    @Deprecated
    public void setMaxMessages(int maxMessages) {
        this.maxMessages = maxMessages;
    }

    @Override
    @Deprecated
	public boolean isWANModeEnabled() {
		return wanmode;
	}

	@Deprecated
	public void setWanmode(boolean wanmode) {
		this.wanmode = wanmode;
	}

}
