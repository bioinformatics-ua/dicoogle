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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.util.UIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author André Almeida <almeida.a@ua.pt>
 */
@JsonRootName("additional-transfer-syntax")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public final class AdditionalTransferSyntax {

    @JacksonXmlProperty(isAttribute = true, localName = "uid")
    private final String uid;

    @JacksonXmlProperty(isAttribute = true, localName = "alias")
    private final String alias;

    @JacksonXmlProperty(isAttribute = true, localName = "bigEndian")
    private final boolean bigEndian;

    @JacksonXmlProperty(isAttribute = true, localName = "explicitVR")
    private final boolean explicitVR;

    @JacksonXmlProperty(isAttribute = true, localName = "encapsulated")
    private final boolean encapsulated;

    @JacksonXmlProperty(isAttribute = true, localName = "deflated")
    private final boolean deflated;


    public AdditionalTransferSyntax(@JsonProperty("uid") String uid, @JsonProperty("alias") String alias,
            @JsonProperty("bigEndian") Boolean bigEndian, @JsonProperty("explicitVR") Boolean explicitVR,
            @JsonProperty("encapsulated") Boolean encapsulated, @JsonProperty("deflated") Boolean deflated) {
        this.uid = uid;
        this.alias = alias;
        this.bigEndian = bigEndian != null ? bigEndian : false;
        this.explicitVR = explicitVR != null ? explicitVR : true;
        this.encapsulated = encapsulated != null ? encapsulated : true;
        this.deflated = deflated != null ? deflated : false;
    }

    /**
     * Checks whether the transfer syntax is valid in a general sense
     */
    public static boolean isValid(AdditionalTransferSyntax ats, Logger logger) {
        Objects.requireNonNull(ats);
        if (ats.uid == null) {
            if (ats.alias == null || ats.alias.equals(""))
                logger.warn("An additional Transfer Syntax is undefined.");
            else
                logger.warn("Additional Transfer Syntax \"{}\": UID not set.", ats.alias);
            return false;
        }
        if (ats.alias == null || ats.alias.equals("")) {
            if (!UIDUtils.isValidUID(ats.uid))
                logger.warn("Additional Transfer syntax: uid \"{}\" is not valid.", ats.uid);
            else
                logger.warn("Additional Transfer Syntax with uid \"{}\": alias not set.", ats.uid);
            return false;
        }
        if (!UIDUtils.isValidUID(ats.uid)) {
            logger.warn("Additional Transfer syntax \"{}\": UID not valid.", ats.alias);
            return false;
        }
        return true;
    }

    /**
     * Overload isValid
     */
    public static boolean isValid(AdditionalTransferSyntax ats) {
        Logger logger = LoggerFactory.getLogger(AdditionalTransferSyntax.class);
        return isValid(ats, logger);
    }

    /**
     * Creates a dcm4che2 TransferSyntax object equivalent to the referenced object
     * */
    public TransferSyntax toTransferSyntax() {
        return new TransferSyntax(uid, explicitVR, bigEndian, deflated, encapsulated);
    }

    // Generated functions
    @JsonIgnore
    public String getUid() {
        return uid;
    }

    @JsonIgnore
    public String getAlias() {
        return alias;
    }

    @JsonIgnore
    public boolean isBigEndian() {
        return this.bigEndian;
    }

    @JsonIgnore
    public boolean isExplicitVR() {
        return this.explicitVR;
    }

    @JsonIgnore
    public boolean isEncapsulated() {
        return this.encapsulated;
    }

    @JsonIgnore
    public boolean isDeflated() {
        return this.deflated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AdditionalTransferSyntax))
            return false;
        AdditionalTransferSyntax that = (AdditionalTransferSyntax) o;
        return isBigEndian() == that.isBigEndian() && isExplicitVR() == that.isExplicitVR()
                && isEncapsulated() == that.isEncapsulated() && isDeflated() == that.isDeflated()
                && getUid().equals(that.getUid()) && getAlias().equals(that.getAlias());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUid(), getAlias(), isBigEndian(), isExplicitVR(), isEncapsulated(), isDeflated());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AdditionalTransferSyntax.class.getSimpleName() + "[", "]")
                .add("uid='" + uid + "'").add("alias='" + alias + "'").add("bigEndian=" + bigEndian)
                .add("explicitVR=" + explicitVR).add("encapsulated=" + encapsulated).add("deflated=" + deflated)
                .toString();
    }
}
