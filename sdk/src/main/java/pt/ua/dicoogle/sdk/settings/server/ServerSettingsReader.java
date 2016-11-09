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

package pt.ua.dicoogle.sdk.settings.server;

import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.sdk.datastructs.SOPClass;

import java.util.Collection;
import java.util.List;

/** A read-only interface for accessing server settings.
 *
 * When requesting the server's settings from a plugin, an instance with this API is returned.
 * Plugins should not implement this interface.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public interface ServerSettingsReader {

    public interface ServiceBase {
        public boolean isAutostart();

        public int getPort();
    }

    public interface WebServer extends ServiceBase {

        public String getAllowedOrigins();

    }
    public WebServer getWebServerSettings();

    public Archive getArchiveSettings();
    public interface Archive {

        public boolean getSaveThumbnails();
        public int getThumbnailSize();
        public int getIndexerEffort();
        public String getMainDirectory();
        public boolean isDirectoryWatcherEnabled();
        public String getWatchDirectory();
        public List<String> getDIMProviders();
        public List<String> getDefaultStorage();
    }

    public DicomServices getDicomServicesSettings();
    public interface DicomServices {

        public String getAETitle();

        public String getDeviceDescription();

        public Collection<String> getAllowedAETitles();

        public Collection<String> getPriorityAETitles();

        public Collection<String> getAllowedLocalInterfaces();

        public Collection<String> getAllowedHostnames();

        public Collection<SOPClass> getSOPClasses();

        public List<MoveDestination> getMoveDestinations();

        public ServiceBase getStorageSettings();

        public interface QueryRetrieve extends ServiceBase {
            public Collection<String> getSOPClass();

            public Collection<String> getTransferCapabilities();

            public int getRspDelay();

            public int getIdleTimeout();

            public int getAcceptTimeout();

            public int getConnectionTimeout();

            public int getDIMSERspTimeout();

            public int getMaxClientAssoc();

            public int getMaxPDULengthReceive();

            public int getMaxPDULengthSend();
        }
        public QueryRetrieve getQueryRetrieveSettings();
    }
}

