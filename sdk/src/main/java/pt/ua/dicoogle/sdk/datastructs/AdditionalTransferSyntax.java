package pt.ua.dicoogle.sdk.datastructs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Objects;
import java.util.StringJoiner;

import static java.util.regex.Pattern.matches;

@JsonRootName("additional-transfer-syntax")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public final class AdditionalTransferSyntax {

    @JacksonXmlProperty(isAttribute = true, localName = "uid")
    private final String uid;
    @JacksonXmlProperty(isAttribute = true, localName = "alias")
    private final String alias;
    /**
     * This field is to contain coded data to be parsed into
     * useful information for the TS declaration constructor of org.dcm4che2.data.TransferSyntax.
     * Digits:
     *  1. ExplicitVR
     *  2. Big Endian
     *  3. Deflated
     *  4. Encapsulated
     *  For example, 1000 would mean: explicitVR, little endian, not deflated or encapsulated
     * */
    @JacksonXmlProperty(isAttribute = true, localName = "format")
    private final String format;



    /**
     * Checks whether the transfer syntax's format field is valid
     * */
    public static boolean validFormat(String format) {

        // Valid: length=4 and binary characters (either 0 or 1)
        // Examples: 0010, 1011.

        return matches("[01]{4}", format);
    }

    // Generated functions
    public AdditionalTransferSyntax(String uid, String alias, String format) {
        this.uid = uid;
        this.alias = alias;
        this.format = format;
    }

    public String getUid() {
        return uid;
    }

    public String getAlias() {
        return alias;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AdditionalTransferSyntax))
            return false;
        AdditionalTransferSyntax that = (AdditionalTransferSyntax) o;
        return getUid().equals(that.getUid()) && getAlias().equals(that.getAlias());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUid(), getAlias());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AdditionalTransferSyntax.class.getSimpleName() + "[", "]")
                .add("uid='" + uid + "'").add("alias='" + alias + "'").add("format='" + format + "'").toString();
    }
}
