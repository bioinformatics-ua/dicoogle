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


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import pt.ua.dicoogle.core.settings.part.ArchiveImpl;
import pt.ua.dicoogle.core.settings.part.DicomServicesImpl;
import pt.ua.dicoogle.core.settings.part.WebServerImpl;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;

import java.util.Objects;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonRootName("config")
public class ServerSettingsImpl implements ServerSettings {

    public ServerSettingsImpl() {
        this.webServer = new WebServerImpl();
        this.archive = new ArchiveImpl();
        this.dicomServices = new DicomServicesImpl();
    }

    public ServerSettingsImpl(WebServerImpl webServer, ArchiveImpl archive, DicomServicesImpl dicomServices) {
        Objects.requireNonNull(webServer);
        Objects.requireNonNull(archive);
        Objects.requireNonNull(dicomServices);

        this.webServer = webServer;
        this.archive = archive;
        this.dicomServices = dicomServices;
    }

    /** Create a new server settings instance with default configurations.
     */
    public static ServerSettingsImpl createDefault() {
        return new ServerSettingsImpl(
                WebServerImpl.createDefault(),
                ArchiveImpl.createDefault(),
                DicomServicesImpl.createDefault());
    }

    @JsonProperty("web-server")
    private WebServerImpl webServer;

    @JsonProperty("archive")
    private ArchiveImpl archive;

    @JsonProperty("dicom-services")
    private DicomServicesImpl dicomServices;

    @Override
    public WebServer getWebServerSettings() {
        return this.webServer;
    }

    @Override
    public Archive getArchiveSettings() {
        return this.archive;
    }

    @Override
    public DicomServices getDicomServicesSettings() {
        return this.dicomServices;
    }

    @Override
    public String toString() {
        return "ServerSettingsImpl{" +
                "webServer=" + webServer +
                ", archive=" + archive +
                ", dicomServices=" + dicomServices +
                '}';
    }
}
