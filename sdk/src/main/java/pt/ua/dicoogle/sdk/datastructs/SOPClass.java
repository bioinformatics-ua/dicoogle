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
package pt.ua.dicoogle.sdk.datastructs;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 * @author Luís A. Bastião Silva <bastiao@bmd-software.com>
 */
@JsonRootName("sop-class")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public final class SOPClass {

    @JsonProperty("uid")
    private final String uid;

    @JsonProperty("ts")
    @JacksonXmlProperty(localName = "transfer-syntaxes")
    private final Collection<String> ts;



    public SOPClass(String uid) {
        this.uid = uid;
        this.ts = Collections.EMPTY_LIST;
    }

    @JsonCreator
    public SOPClass(@JsonProperty("uid") String uid, @JsonProperty("transfer-syntaxes") Collection<String> ts) {
        this.uid = uid;
        this.ts = ts != null ? ts : Collections.EMPTY_LIST;
    }

    public String getUID() {
        return this.uid;
    }

    public Collection<String> getTransferSyntaxes() {
        return this.ts;
    }

    /** Create a new, independent SOP class with the transfer syntax collection replaced.
     */
    public SOPClass withTS(Collection<String> ts) {
        return new SOPClass(this.uid, ts);
    }

    /** Retrieve an SOP class with the transfer syntax collection replaced only if this SOP class
     * has no transfer syntaxes specified, return itself otherwise.
     */
    public SOPClass withDefaultTS(Collection<String> ts) {
        return this.ts.isEmpty() ? this.withTS(ts) : this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SOPClass sopClass = (SOPClass) o;
        return Objects.equals(uid, sopClass.uid) && Objects.equals(ts, sopClass.ts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, ts);
    }

    @Override
    public String toString() {
        return "SOPClass{" + "uid='" + uid + '\'' + ", ts=" + ts + '}';
    }
}
