package pt.ua.dicoogle.core.settings.part;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ArchiveImpl implements ServerSettings.Archive {

    /**
     * Create the default server archive configurations
     */
    public static ArchiveImpl createDefault() {
        ArchiveImpl a = new ArchiveImpl();

        a.saveThumbnails = false;
        a.thumbnailSize = 64;
        a.indexerEffort = 100;
        a.dirWatcherEnabled = false;
        a.watchDirectory = "";

        a.dimProviders = Collections.EMPTY_LIST;
        a.defaultStorage = Collections.EMPTY_LIST;

        return a;
    }

    @JsonProperty("save-thumbnails")
    private boolean saveThumbnails;

    @JsonProperty("thumbnail-size")
    private int thumbnailSize;

    @JsonProperty("indexer-effort")
    private int indexerEffort;

    @JsonProperty("dim-providers")
    private List<String> dimProviders;

    @JsonSetter("dim-providers")
    protected void setDIMProviders_(Object o) {
        if (o instanceof Collection) {
            this.defaultStorage = new ArrayList<>();
            for (Object e : (Collection) o) {
                this.defaultStorage.add(e.toString());
            }
        } else {
            this.defaultStorage = Collections.singletonList(o.toString());
        }
    }

    @JsonProperty("default-storage")
    private List<String> defaultStorage;

    @JsonSetter("default-storage")
    protected void setDefaultStorage_(Object o) {
        if (o instanceof Collection) {
            this.defaultStorage = new ArrayList<>();
            for (Object e : (Collection) o) {
                this.defaultStorage.add(e.toString());
            }
        } else {
            this.defaultStorage = Collections.singletonList(o.toString());
        }
    }

    @JsonProperty(value = "enable-watch-directory", defaultValue = "false")
    private boolean dirWatcherEnabled;

    @JsonProperty("watch-directory")
    private String watchDirectory;

    @Override
    public String getMainDirectory() {
        return mainDirectory;
    }

    @Override
    public void setMainDirectory(String mainDirectory) {
        this.mainDirectory = mainDirectory;
    }

    @JsonProperty("main-directory")
    private String mainDirectory;

    public boolean getSaveThumbnails() {
        return saveThumbnails;
    }

    public void setSaveThumbnails(boolean saveThumbnails) {
        this.saveThumbnails = saveThumbnails;
    }

    public int getThumbnailSize() {
        return thumbnailSize;
    }

    public void setThumbnailSize(int thumbnailSize) {
        this.thumbnailSize = thumbnailSize;
    }

    public int getIndexerEffort() {
        return indexerEffort;
    }

    public void setIndexerEffort(int indexerEffort) {
        this.indexerEffort = indexerEffort;
    }

    public List<String> getDIMProviders() {
        return dimProviders;
    }

    public void setDIMProviders(List<String> dimProviders) {
        this.dimProviders = dimProviders;
    }

    public List<String> getDefaultStorage() {
        return defaultStorage;
    }

    public void setDefaultStorage(List<String> defaultStorage) {
        this.defaultStorage = defaultStorage;
    }

    public boolean isDirectoryWatcherEnabled() {
        return dirWatcherEnabled;
    }

    public void setDirectoryWatcherEnabled(boolean dirWatcherEnabled) {
        this.dirWatcherEnabled = dirWatcherEnabled;
    }

    public String getWatchDirectory() {
        return watchDirectory;
    }

    public void setWatchDirectory(String watchDirectory) {
        this.watchDirectory = watchDirectory;
    }
}
