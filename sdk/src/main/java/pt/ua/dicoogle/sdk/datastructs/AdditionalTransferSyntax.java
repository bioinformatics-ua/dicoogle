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

    private static int counter = 1;
    private final int id;
    @JacksonXmlProperty(isAttribute = true, localName = "uid")
    private final String uid;
    @JacksonXmlProperty(isAttribute = true, localName = "alias")
    private final String alias;
    /**
     * This field is to contain coded data to be parsed into
     *  useful information for the TS declaration constructor of org.dcm4che2.data.TransferSyntax.
     * Digits:
     *  1. ExplicitVR
     *  2. Big Endian
     *  3. Deflated
     *  4. Encapsulated
     *  For example, 1000 would mean: explicitVR, little endian, not deflated nor encapsulated
     * */
    @JacksonXmlProperty(isAttribute = true, localName = "bigEndian")
    private final Boolean bigEndian;
    @JacksonXmlProperty(isAttribute = true, localName = "explicitVR")
    private final Boolean explicitVR;
    @JacksonXmlProperty(isAttribute = true, localName = "encapsuled")
    private final Boolean encapsulated;
    @JacksonXmlProperty(isAttribute = true, localName = "deflated")
    private final Boolean deflated;



    public AdditionalTransferSyntax(@JsonProperty("uid") String uid, @JsonProperty("alias") String alias,
            @JsonProperty("bigEndian") Boolean bigEndian, @JsonProperty("explicitVR") Boolean explicitVR,
            @JsonProperty("encapsulated") Boolean encapsulated, @JsonProperty("deflated") Boolean deflated) {
        this.id = counter++;
        this.uid = uid;
        this.alias = alias;
        this.bigEndian = bigEndian != null ? bigEndian : false;
        this.explicitVR = explicitVR != null ? explicitVR : false;
        this.encapsulated = encapsulated != null ? encapsulated : false;
        this.deflated = deflated != null ? deflated : false;
    }

    /**
     * Checks whether the transfer syntax is valid in a general sense
     */
    public static boolean isValid(AdditionalTransferSyntax ats, Logger logger) {
        boolean returnVal = true;
        Objects.requireNonNull(ats);
        if (ats.uid == null) {
            if (ats.alias == null || ats.alias.equals(""))
                logger.warn("Additional Transfer Syntax no.{} is undefined.", ats.id);
            else
                logger.warn("Additional Transfer Syntax no.{}'s UID not set.", ats.id);
            return false;
        }
        if (ats.alias == null || ats.alias.equals("")) {
            logger.warn("Additional Transfer syntax no.{}'s alias not set.", ats.id);
            returnVal = false;
        }
        if (!UIDUtils.isValidUID(ats.uid)) {
            logger.warn("Additional Transfer syntax no.{}'s UID not valid.", ats.id);
            returnVal = false;
        }

        return returnVal;
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
    public int getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getAlias() {
        return alias;
    }

    public Boolean getBigEndian() {
        return bigEndian;
    }

    public Boolean getExplicitVR() {
        return explicitVR;
    }

    public Boolean getEncapsulated() {
        return encapsulated;
    }

    public Boolean getDeflated() {
        return deflated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AdditionalTransferSyntax))
            return false;
        AdditionalTransferSyntax that = (AdditionalTransferSyntax) o;
        return Objects.equals(getUid(), that.getUid()) && Objects.equals(getAlias(), that.getAlias())
                && getBigEndian().equals(that.getBigEndian()) && getExplicitVR().equals(that.getExplicitVR())
                && getEncapsulated().equals(that.getEncapsulated()) && getDeflated().equals(that.getDeflated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUid(), getAlias(), getBigEndian(), getExplicitVR(), getEncapsulated(),
                getDeflated());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AdditionalTransferSyntax.class.getSimpleName() + "[", "]").add("id=" + id)
                .add("uid='" + uid + "'").add("alias='" + alias + "'").add("bigEndian=" + bigEndian)
                .add("explicitVR=" + explicitVR).add("encapsuled=" + encapsulated).add("deflated=" + deflated)
                .toString();
    }
}
