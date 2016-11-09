package pt.ua.dicoogle.core.settings.part;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class WebServerImpl implements ServerSettings.WebServer {
    private boolean autostart;
    private int port;

    public WebServerImpl() {
    }

    public WebServerImpl(boolean autostart, int port, String allowedOrigins) {
        this.autostart = autostart;
        this.port = port;
        this.allowedOrigins = allowedOrigins;
    }

    public static WebServerImpl createDefault() {
        return new WebServerImpl(true, 8080, null);
    }

    @JsonProperty("allowed-origins")
    private String allowedOrigins;

    @Override
    public boolean isAutostart() {
        return autostart;
    }

    @Override
    public void setAutostart(boolean autostart) {
        this.autostart = autostart;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public String getAllowedOrigins() {
        return this.allowedOrigins;
    }
}
