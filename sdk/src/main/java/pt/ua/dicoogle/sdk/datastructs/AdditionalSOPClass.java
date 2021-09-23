package pt.ua.dicoogle.sdk.datastructs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Collection;
import java.util.Objects;
import java.util.StringJoiner;

@JsonRootName("additional-sop-class")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public final class AdditionalSOPClass {

    @JacksonXmlProperty(isAttribute = true, localName = "uid")
    private final String uid;
    @JacksonXmlProperty(isAttribute = true, localName = "alias")
    private final String alias;

//    // Imported Functions (adapted from SOPClass')
//    /** Create a new, independent SOP class with the transfer syntax collection replaced.
//     */
//    public AdditionalSOPClass withTS(Collection<String> ts) {
//        return new SOPClass(this.uid, this.alias, ts);
//    }
//
//    /** Retrieve an SOP class with the transfer syntax collection replaced only if this SOP class
//     * has no transfer syntaxes specified, return itself otherwise.
//     */
//    public AdditionalSOPClass withDefaultTS(Collection<String> ts) {
//        return this.ts.isEmpty() ? this.withTS(ts) : this;
//    }


    // Generated functions
    public AdditionalSOPClass(String uid, String alias) {
        this.uid = uid;
        this.alias = alias;
    }

    public String getUid() {
        return uid;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdditionalSOPClass)) return false;
        AdditionalSOPClass that = (AdditionalSOPClass) o;
        return Objects.equals(getUid(), that.getUid()) && Objects.equals(getAlias(), that.getAlias());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUid(), getAlias());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AdditionalSOPClass.class.getSimpleName() + "[", "]")
                .add("uid='" + uid + "'")
                .add("alias='" + alias + "'")
                .toString();
    }
}
