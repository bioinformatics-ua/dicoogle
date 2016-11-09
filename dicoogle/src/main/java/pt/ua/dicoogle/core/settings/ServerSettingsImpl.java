package pt.ua.dicoogle.core.settings;


import com.fasterxml.jackson.annotation.*;

import pt.ua.dicoogle.core.settings.part.ArchiveImpl;
import pt.ua.dicoogle.core.settings.part.DicomServicesImpl;
import pt.ua.dicoogle.core.settings.part.WebServerImpl;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;

import java.util.*;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
//@JsonIgnoreProperties(ignoreUnknown = true)
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

    @Override @JsonIgnore
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
}
