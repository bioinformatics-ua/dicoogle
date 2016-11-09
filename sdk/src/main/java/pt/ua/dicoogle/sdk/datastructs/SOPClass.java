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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public final class SOPClass {

    @JsonProperty("uid")
    private final String uid;

    @JsonProperty("ts")
    private final Collection<String> ts;

    @JsonCreator
    public SOPClass(@JsonProperty("uid") String uid) {
        this.uid = uid;
        this.ts = Collections.EMPTY_LIST;
    }

    @JsonCreator
    public SOPClass(@JsonProperty("uid")String uid, @JsonProperty("ts") Collection<String> ts) {
        this.uid = uid;
        this.ts = ts;
    }

    public String getUID() {
        return this.uid;
    }

    public Collection<String> getTransferSyntaxes() {
        return this.ts;
    }

    public SOPClass withTS(Collection<String> ts) {
        return new SOPClass(this.uid, ts);
    }
}
