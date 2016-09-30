/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk.
 *
 * Dicoogle/dicoogle-sdk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */

package pt.ua.dicoogle.sdk.core;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;

/** A read-only interface for accessing server settings.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public interface ServerSettingsReader {

    public WebSettingsReader getWeb();
    
    public String getAE();
    
    public int getAcceptTimeout();
    
    public String getAccessListFileName();
    
    public boolean getAutoStartPlugin(String name);

    public ConcurrentMap<String,Boolean> getAutoStartPluginsSettings();
    
    public String[] getCAET();
    
    public int getConnectionTimeout();
    
    public int getDIMSERspTimeout();
    
    public String getDeviceDescription();
    
    public String getDicoogleDir();

    @Deprecated
    public Set<String> getExtensionsAllowed();
    
    public boolean getFullContentIndex();
    
    public String getID();
    
    public int getIdleTimeout();

    @Deprecated
    public String getIndexer();

    @Deprecated
    public String getNodeName();

    @Deprecated
    public boolean isNodeNameDefined();
    
    public String getNetworkInterfaceName();
    
    public int getIndexerEffort();
    
    public boolean isEncryptUsersFile();
    
    public boolean isIndexZIPFiles();
    
    public boolean isMonitorWatcher();
    
    public boolean isIndexAnonymous();
    
    public boolean isGzipStorage();
    
    public boolean getPermitAllAETitles();
    
    public String getPath();
    
    public int getStoragePort();
    
    public boolean getSaveThumbnails();
    
    public String getThumbnailsMatrix();
    
    public int getWlsPort();
    
    public int getRspDelay();
    
    public String[] getSOPClasses();
    
    public String getSOPClass();
    
    public String getTransfCap();
    
    public int getMaxClientAssoc();
    
    public int getMaxPDULengthReceive();
    
    public int getMaxPDULenghtSend();
    
    public String getLocalAETName();
    
    public String getPermitedLocalInterfaces();
    
    public String getPermitedRemoteHostnames();
    
    public boolean isStorage();
    
    public boolean isQueryRetrive();

    @Deprecated
    public boolean isWANModeEnabled();
    
    public int getMaxMessages();
    
    public String getNetworkInterfaceAddress();
    
    public List<String> getNetworkInterfacesNames();
    
    public Map<String, Object> getStorageSettings();
    
    public Map<String, Object> getQueryRetrieveSettings();
    
    public Map<String, String> getModalityFind();

    public List<MoveDestination> getMoves();

    public Set<String> getPriorityAETitles();

}

