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
package pt.ua.dicoogle.core.settings.part;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.sdk.datastructs.SOPClass;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;
import pt.ua.dicoogle.server.SOPList;

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

        s.allowedHosts = Collections.singletonList("any");
        s.allowedLocalInterfaces = Collections.singletonList("any");
        s.moveDestinations = new ArrayList<>();

        s.defaultTS = Arrays.asList(
                "1.2.840.10008.1.2", "1.2.840.10008.1.2.1", "1.2.840.10008.1.2.2",
                "1.2.840.10008.1.2.4.70", "1.2.840.10008.1.2.4.80", "1.2.840.10008.1.2.4.57",
                "1.2.840.10008.1.2.4.90", "1.2.840.10008.1.2.4.91", "1.2.840.10008.1.2.5",
                "1.2.840.10008.1.2.4.100", "1.2.840.10008.1.2.4.51");

        s.sopClasses = SOPList.getInstance().asSOPClassList();

        s.storage = StorageImpl.createDefault();
        s.queryRetrieve = QueryRetrieveImpl.createDefault();

        return s;
    }

    private String aetitle;

    @JsonProperty("device-description")
    private String deviceDescription;

    @JsonSetter("allowed-aetitles")
    private void setAllowedAETitles_(Object o) {
        if (o == null) {
            this.allowedAETitles = Collections.emptyList();
        } else if (o instanceof Collection) {
            this.allowedAETitles = new ArrayList<>();
            for (Object e : (Collection) o) {
                this.allowedAETitles.add(e.toString());
            }
        } else {
            this.allowedAETitles = Collections.singletonList(o.toString());
        }
    }

    @JacksonXmlElementWrapper(useWrapping = false, localName = "allowed-aetitles")
    @JacksonXmlProperty(localName = "allowed-aetitles")
    private Collection<String> allowedAETitles = Collections.emptyList();

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

    @JacksonXmlElementWrapper(useWrapping = false, localName = "priority-aetitles")
    @JacksonXmlProperty(localName = "priority-aetitles")
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

    @JacksonXmlElementWrapper(localName = "default-ts")
    @JacksonXmlProperty(localName = "ts")
    private Collection<String> defaultTS = null;

    @JsonProperty("sop-classes")
    private Collection<SOPClass> sopClasses = Collections.emptyList();

    @JacksonXmlElementWrapper(localName = "move-destinations")
    private List<MoveDestination> moveDestinations;

    @JsonProperty("storage")
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
        this.allowedAETitles = allowedAETitles;
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
        return defaultTS;
    }

    @JsonGetter("sop-classes")
    public Collection<SOPClass> getRawSOPClasses() {
        return this.sopClasses;
    }

    @Override
    @JsonIgnore
    public Collection<SOPClass> getSOPClasses() {
        List<SOPClass> l = new ArrayList<>();
        for (SOPClass c : this.sopClasses) {
            l.add(c.withDefaultTS(this.defaultTS));
        }
        return l;
    }

    @Override
    @JsonSetter("sop-classes")
    public void setSOPClasses(Collection<SOPClass> sopClasses) {
        this.sopClasses = sopClasses;
    }

    @Override
    @JsonGetter("move-destinations")
    public List<MoveDestination> getMoveDestinations() {
        return Collections.unmodifiableList(moveDestinations);
    }

    @Override
    @JsonSetter("move-destinations")
    public void setMoveDestinations(List<MoveDestination> moveDestinations) {
        if (moveDestinations == null) {
            this.moveDestinations = new ArrayList<>();
        } else {
            this.moveDestinations = new ArrayList<>(moveDestinations);
        }
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

    @Override
    public String toString() {
        return "DicomServicesImpl{" +
                "aetitle='" + aetitle + '\'' +
                ", deviceDescription='" + deviceDescription + '\'' +
                ", allowedAETitles=" + allowedAETitles +
                ", priorityAETitles=" + priorityAETitles +
                ", allowedLocalInterfaces=" + allowedLocalInterfaces +
                ", allowedHosts=" + allowedHosts +
                ", defaultTS=" + defaultTS +
                ", sopClasses=" + sopClasses +
                ", moveDestinations=" + moveDestinations +
                ", storage=" + storage +
                ", queryRetrieve=" + queryRetrieve +
                '}';
    }
}
