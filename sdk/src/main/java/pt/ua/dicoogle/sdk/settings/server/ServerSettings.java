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

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public interface ServerSettings extends ServerSettingsReader {

    public interface ServiceBase extends ServerSettingsReader.ServiceBase {
        public void setAutostart(boolean autostart);

        public void setPort(int port);
    }

    @Override
    public WebServer getWebServerSettings();

    public interface WebServer extends ServiceBase, ServerSettingsReader.WebServer {
        public void setAllowedOrigins(String allowedOrigins);
    }

    public Archive getArchiveSettings();

    public interface Archive extends ServerSettingsReader.Archive {
        public void setSaveThumbnails(boolean saveThumbnails);

        public void setThumbnailSize(int thumbnailSize);

        public void setMainDirectory(String directory);

        public void setIndexerEffort(int effort);

        public void setWatchDirectory(String dir);

        public void setDirectoryWatcherEnabled(boolean watch);

        public void setDIMProviders(List<String> providers);

        public void setDefaultStorage(List<String> storages);
    }

    public DicomServices getDicomServicesSettings();

    public interface DicomServices extends ServerSettingsReader.DicomServices {
        public void setAETitle(String aetitle);

        public void setDeviceDescription(String description);

        public void setAllowedAETitles(Collection<String> AETitles);

        public void setPriorityAETitles(Collection<String> AETitles);

        public void setMoveDestinations(List<MoveDestination> destinations);

        public void addMoveDestination(MoveDestination destination);

        public boolean removeMoveDestination(String aetitle);

        public void setAllowedLocalInterfaces(Collection<String> localInterfaces);

        public void setAllowedHostnames(Collection<String> hostnames);

        public void setSOPClasses(Collection<SOPClass> sopClasses);

        public ServiceBase getStorageSettings();

        public interface QueryRetrieve extends ServiceBase, ServerSettingsReader.DicomServices.QueryRetrieve {
            public void setSOPClass(Collection<String> sopClasses);

            public void setTransferCapabilities(Collection<String> tcap);

            public void setRspDelay(int timeout);

            public void setIdleTimeout(int timeout);

            public void setAcceptTimeout(int timeout);

            public void setConnectionTimeout(int timeout);

            public void setDIMSERspTimeout(int timeout);

            public void setMaxClientAssoc(int max);

            public void setMaxPDULengthReceive(int pdu);

            public void setMaxPDULengthSend(int pdu);
        }
        public QueryRetrieve getQueryRetrieveSettings();
    }
}
