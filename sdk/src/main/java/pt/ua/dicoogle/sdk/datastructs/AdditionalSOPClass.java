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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.util.UIDUtils;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author André Almeida <almeida.a@ua.pt>
 */
@JsonRootName("additional-sop-class")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public final class AdditionalSOPClass {

    // @JacksonXmlProperty(isAttribute = true, localName = "uid")
    @JsonProperty("uid")
    private final String uid;
    // @JacksonXmlProperty(isAttribute = true, localName = "alias")
    @JsonProperty("alias")
    private final String alias;

    @JsonCreator
    public AdditionalSOPClass(@JsonProperty("uid") String uid, @JsonProperty("alias") String alias) {
        this.uid = uid;
        this.alias = alias;
    }

    public static boolean isValid(AdditionalSOPClass a) {
        if (a == null || a.uid == null)
            return false;
        return UIDUtils.isValidUID(a.uid) && (a.alias != null && !a.alias.equals(""));
    }

    // Generated functions
    public String getUid() {
        return uid;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AdditionalSOPClass))
            return false;
        AdditionalSOPClass that = (AdditionalSOPClass) o;
        return Objects.equals(getUid(), that.getUid()) && Objects.equals(getAlias(), that.getAlias());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUid(), getAlias());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AdditionalSOPClass.class.getSimpleName() + "[", "]").add("uid='" + uid + "'")
                .add("alias='" + alias + "'").toString();
    }
}