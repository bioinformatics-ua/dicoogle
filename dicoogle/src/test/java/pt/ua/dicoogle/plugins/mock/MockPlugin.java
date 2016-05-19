package pt.ua.dicoogle.plugins.mock;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;
import org.eclipse.jetty.server.handler.HandlerList;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.sdk.*;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.settings.ConfigurationHolder;
import pt.ua.dicoogle.sdk.task.Task;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class MockPlugin implements DicooglePlugin, QueryInterface, IndexerInterface, StorageInterface, JettyPluginInterface {
    private final String name;
    private ConfigurationHolder settings;
    private boolean enabled = true;

    public MockPlugin(String name) {
        this.name = name;
    }

    public Iterable<SearchResult> query(String query, Object... params) {
        return Collections.singleton(
                new SearchResult(URI.create("dummy://somewhere"), 1.0, new HashMap<String, Object>()));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean enable() {
        this.enabled = true;
        return true;
    }

    @Override
    public boolean disable() {
        this.enabled = false;
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setSettings(ConfigurationHolder configurationHolder) {
        this.settings = configurationHolder;
    }

    @Override
    public ConfigurationHolder getSettings() {
        return this.settings;
    }

    @Override
    public Task<Report> index(StorageInputStream storageInputStream, Object... objects) {
        return new Task<>(new Callable<Report>() {
            @Override
            public Report call() throws Exception {
                return new Report();
            }
        });
    }

    @Override
    public Task<Report> index(Iterable<StorageInputStream> iterable, Object... objects) {
        return new Task<>(new Callable<Report>() {
            @Override
            public Report call() throws Exception {
                return new Report();
            }
        });
    }

    @Override
    public String getScheme() {
        return "dummy";
    }

    @Override
    public boolean handles(URI uri) {
        return true;
    }

    @Override
    public Iterable<StorageInputStream> at(URI uri, Object... objects) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public URI store(DicomObject dicomObject, Object... objects) {
        return null;
    }

    @Override
    public URI store(DicomInputStream dicomInputStream, Object... objects) throws IOException {
        return null;
    }

    @Override
    public void remove(URI uri) {
    }

    @Override
    public boolean unindex(URI uri) {
        return false;
    }

    @Override
    public HandlerList getJettyHandlers() {
        return new HandlerList();
    }
}
