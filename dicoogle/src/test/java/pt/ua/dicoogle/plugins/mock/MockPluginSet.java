package pt.ua.dicoogle.plugins.mock;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.sdk.*;
import pt.ua.dicoogle.sdk.settings.ConfigurationHolder;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class MockPluginSet implements PluginSet {
    private final String name;
    private final QueryInterface query;
    private final IndexerInterface indexer;
    private final StorageInterface store;
    private final JettyPluginInterface servlets;
    private final ServerResource restletResource;
    private ConfigurationHolder settings;

    private MockPluginSet(String name, QueryInterface qi, IndexerInterface ii, StorageInterface str, JettyPluginInterface srv, ServerResource rest) {
        this.name = name;
        this.query = qi;
        this.indexer = ii;
        this.store = str;
        this.servlets = srv;
        this.restletResource = rest;
    }

    public static MockPluginSet withQueryAndIndexer(String name) {
        return new MockPluginSet(name, new MockPlugin(name), new MockPlugin(name), null, null, null);
    }

    public static MockPluginSet withStorage(String name) {
        return new MockPluginSet(name, null, null, new MockPlugin(name), null, null);
    }

    public static MockPluginSet withJettyPlugin(String name) {
        return new MockPluginSet(name, null, null, null, new MockPlugin(name), null);
    }

    public static MockPluginSet withRestletPlugin(final String name) {
        return new MockPluginSet(name, null, null, null, null, new ServerResource() {
            @Override
            protected Representation get() throws ResourceException {
                return new StringRepresentation("Hello Dicoogle");
            }

            @Override
            public String toString() {
                return name;
            }
        });
    }

    @Override
    public Collection<IndexerInterface> getIndexPlugins() {
        return indexer != null ? Collections.singleton(indexer) : Collections.EMPTY_LIST;
    }

    /**
     * @deprecated
     */
    @Override
    public Collection<GraphicalInterface> getGraphicalPlugins() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<QueryInterface> getQueryPlugins() {
        return query != null ? Collections.singleton(query) : Collections.EMPTY_LIST;
    }

    @Override
    public Collection<StorageInterface> getStoragePlugins() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<ServerResource> getRestPlugins() {
        return restletResource != null ? Collections.singleton(restletResource) : Collections.EMPTY_LIST;
    }

    @Override
    public Collection<JettyPluginInterface> getJettyPlugins() {
        return servlets != null ? Collections.singleton(servlets) : Collections.EMPTY_LIST;
    }

    @Override
    public String getName() {
        return this.name;
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
    public void shutdown() {
    }

}
