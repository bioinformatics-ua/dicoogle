package pt.ua.dicoogle.core.settings.part;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        isGetterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        setterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class StorageImpl implements ServerSettings.ServiceBase {
    private boolean autostart;
    private int port;

    public StorageImpl() {}

    public StorageImpl(boolean autostart, int port) {
        this.autostart = autostart;
        this.port = port;
    }

    public static StorageImpl createDefault() {
        return new StorageImpl(true, 6666);
    }

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
        return port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }
}
