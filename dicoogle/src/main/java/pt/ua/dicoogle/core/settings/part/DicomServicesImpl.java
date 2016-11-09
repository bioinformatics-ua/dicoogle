package pt.ua.dicoogle.core.settings.part;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.sdk.datastructs.SOPClass;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;

import java.util.*;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DicomServicesImpl implements ServerSettings.DicomServices {
    public static DicomServicesImpl createDefault() {
        DicomServicesImpl s = new DicomServicesImpl();
        s.aetitle = "DICOOGLE-STORAGE";

        s.allowedAETitles = Collections.EMPTY_LIST;
        s.allowedHosts = Collections.singletonList("any");
        s.allowedLocalInterfaces = Collections.singletonList("any");
        s.defaultTS = Arrays.asList();
        s.sopClasses = Arrays.asList();

        s.storage = StorageImpl.createDefault();
        s.queryRetrieve = QueryRetrieveImpl.createDefault();

        throw new UnsupportedOperationException("not implemented yet!");
    }

    private String aetitle;

    @JsonProperty("device-description")
    private String deviceDescription;

    @JsonSetter("allowed-aetitles")
    private void setAllowedAETitles_(Object o) {
        if (o instanceof Collection) {
            this.allowedAETitles = new ArrayList<>();
            for (Object e : (Collection) o) {
                this.allowedAETitles.add(e.toString());
            }
        } else {
            this.allowedAETitles = Collections.singletonList(o.toString());
        }
    }

    @JsonProperty("allowed-aetitles")
    private Collection<String> allowedAETitles;

    @JsonSetter("allowed-local-interfaces")
    private void setAllowedLocalInterfaces_(Object o) {
        if (o instanceof Collection) {
            this.allowedLocalInterfaces = new ArrayList<>();
            for (Object e : (Collection) o) {
                this.allowedLocalInterfaces.add(e.toString());
            }
        } else {
            this.allowedLocalInterfaces = Collections.singletonList(o.toString());
        }
    }

    @JsonProperty("priority-aetitles")
    private Collection<String> priorityAETitles;

    @JsonProperty("allowed-local-interfaces")
    private Collection<String> allowedLocalInterfaces;

    @JsonProperty("allowed-hostnames")
    private Collection<String> allowedHosts;

    @JsonSetter("allowed-hostnames")
    private void setAllowedHostnames_(Object o) {
        if (o instanceof Collection) {
            this.allowedHosts = new ArrayList<>();
            for (Object e : (Collection) o) {
                this.allowedHosts.add(e.toString());
            }
        } else {
            this.allowedHosts = Collections.singletonList(o.toString());
        }
    }

    @JsonIgnore
    private Collection<String> defaultTS;

    @JsonProperty("sop-classes")
    private List<SOPClass> sopClasses;

    @JsonSetter("sop-classes")
    private void setSOPClasses_(Collection<?> col) {
        this.defaultTS = null;
        this.sopClasses = new ArrayList<>();

        for (Object o : col) {
            if (o instanceof SOPClass) {
                SOPClass c = (SOPClass) o;
                if ("".equals(c.getUID()) || "default".equals(c.getUID())) {
                    this.defaultTS = c.getTransferSyntaxes();
                } else {
                    this.sopClasses.add(c);
                }
            } else if (o instanceof String) {
                this.sopClasses.add(new SOPClass(o.toString()));
            }
        }

        if (this.defaultTS != null) {
            List<SOPClass> l = this.sopClasses;
            this.sopClasses = new ArrayList<>();
            for (SOPClass c : this.sopClasses) {
                if (c.getTransferSyntaxes().isEmpty()) {
                    c = c.withTS(this.defaultTS);
                }
                this.sopClasses.add(c);
            }
        }
    }

    @JsonProperty("move-destinations")
    private List<MoveDestination> moveDestinations;

    private StorageImpl storage;

    @JsonProperty("query-retrieve")
    private QueryRetrieveImpl queryRetrieve;

    @Override
    public String getAETitle() {
        return aetitle;
    }

    @Override
    public void setAETitle(String aetitle) {
        this.aetitle = aetitle;
    }

    @Override
    public String getDeviceDescription() {
        return this.deviceDescription;
    }

    @Override
    public void setDeviceDescription(String description) {
        this.deviceDescription = description;
    }

    @Override
    public Collection<String> getAllowedAETitles() {
        return Collections.unmodifiableCollection(allowedAETitles);
    }

    @Override
    public void setAllowedAETitles(Collection<String> allowedAETitles) {
        this.allowedAETitles = new ArrayList<>(allowedAETitles);
    }

    @Override
    public Collection<String> getPriorityAETitles() {
        return allowedAETitles;
    }

    @Override
    public void setPriorityAETitles(Collection<String> aetitles) {
        this.priorityAETitles = aetitles;
    }

    @Override
    public Collection<String> getAllowedLocalInterfaces() {
        return allowedLocalInterfaces;
    }

    @Override
    public void setAllowedLocalInterfaces(Collection<String> allowedLocalInterfaces) {
        this.allowedLocalInterfaces = allowedLocalInterfaces;
    }

    @Override
    public Collection<String> getAllowedHostnames() {
        return allowedHosts;
    }

    @Override
    public void setAllowedHostnames(Collection<String> allowedHosts) {
        this.allowedHosts = allowedHosts;
    }

    public Collection<String> getDefaultTransferSyntaxes() {
        return Collections.unmodifiableCollection(defaultTS);
    }

    @Override
    public Collection<SOPClass> getSOPClasses() {
        return sopClasses;
    }

    @Override
    public void setSOPClasses(Collection<SOPClass> sopClasses) {
        this.setSOPClasses_(sopClasses);
    }

    @Override
    public List<MoveDestination> getMoveDestinations() {
        return Collections.unmodifiableList(moveDestinations);
    }

    @Override
    public void setMoveDestinations(List<MoveDestination> moveDestinations) {
        this.moveDestinations = new ArrayList<>(moveDestinations);
    }

    public StorageImpl getStorageSettings() {
        return storage;
    }

    public QueryRetrieveImpl getQueryRetrieveSettings() {
        return queryRetrieve;
    }


    @Override
    public void addMoveDestination(MoveDestination moveDestination) {
        this.moveDestinations.add(moveDestination);
    }

    @Override
    public boolean removeMoveDestination(String aetitle) {
        Iterator<MoveDestination> it = this.moveDestinations.iterator();
        boolean removed = false;
        while (it.hasNext()) {
            MoveDestination mov = it.next();
            if (mov.getAETitle().equals(aetitle)) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }
}
