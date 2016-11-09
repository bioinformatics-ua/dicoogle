package pt.ua.dicoogle.core.settings.part;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dcm4che2.data.UID;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
public class QueryRetrieveImpl implements ServerSettings.DicomServices.QueryRetrieve {
    private boolean autostart;
    private int port;

    public QueryRetrieveImpl() {}

    public static QueryRetrieveImpl createDefault() {
        QueryRetrieveImpl qr = new QueryRetrieveImpl();

        qr.autostart = true;
        qr.port = 1045;
        qr.rspDelay = 0;
        qr.dimseRspTimeout = 60;
        qr.idleTimeout = 60;
        qr.acceptTimeout = 60;
        qr.connectionTimeout = 60;
        qr.transferCapabilities = Arrays.asList(UID.ImplicitVRLittleEndian, UID.ExplicitVRLittleEndian, UID.ExplicitVRBigEndian);
        qr.sopClasses = Arrays.asList(UID.StudyRootQueryRetrieveInformationModelFIND, UID.PatientRootQueryRetrieveInformationModelFIND);
        qr.maxClientAssocs = 20;
        qr.maxPduLengthReceive = 16364;
        qr.maxPduLengthSend = 16364;

        return qr;
    }

    @JsonProperty("rsp-delay")
    private int rspDelay;

    @JsonProperty("dimse-rsp-timeout")
    private int dimseRspTimeout;

    @JsonProperty("idle-timeout")
    private int idleTimeout;

    @JsonProperty("accept-timeout")
    private int acceptTimeout;

    @JsonProperty("connection-timeout")
    private int connectionTimeout;

    @JsonProperty("transfer-capabilities")
    private Collection<String> transferCapabilities;

    @JsonProperty("sop-classes")
    private Collection<String> sopClasses;

    @JsonProperty("max-client-assocs")
    private int maxClientAssocs;

    @JsonProperty("max-pdu-length-receive")
    private int maxPduLengthReceive;

    @JsonProperty("max-pdu-length-send")
    private int maxPduLengthSend;

    public boolean isAutostart() {
        return autostart;
    }

    public void setAutostart(boolean autostart) {
        this.autostart = autostart;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getRspDelay() {
        return rspDelay;
    }

    public void setRspDelay(int rspDelay) {
        this.rspDelay = rspDelay;
    }

    public int getDIMSERspTimeout() {
        return dimseRspTimeout;
    }

    public void setDIMSERspTimeout(int dimseRspTimeout) {
        this.dimseRspTimeout = dimseRspTimeout;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public int getAcceptTimeout() {
        return acceptTimeout;
    }

    public void setAcceptTimeout(int acceptTimeout) {
        this.acceptTimeout = acceptTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Collection<String> getTransferCapabilities() {
        return transferCapabilities;
    }

    public void setTransferCapabilities(Collection<String> transferCapabilities) {
        this.transferCapabilities = transferCapabilities;
    }

    public Collection<String> getSOPClass() {
        return sopClasses;
    }

    public void setSOPClass(Collection<String> sopClasses) {
        this.sopClasses = sopClasses;
    }

    public int getMaxClientAssoc() {
        return maxClientAssocs;
    }

    public void setMaxClientAssoc(int maxClientAssocs) {
        this.maxClientAssocs = maxClientAssocs;
    }

    public int getMaxPDULengthReceive() {
        return maxPduLengthReceive;
    }

    public void setMaxPDULengthReceive(int maxPduLengthReceive) {
        this.maxPduLengthReceive = maxPduLengthReceive;
    }

    public int getMaxPDULengthSend() {
        return maxPduLengthSend;
    }

    public void setMaxPDULengthSend(int maxPduLengthSend) {
        this.maxPduLengthSend = maxPduLengthSend;
    }
}
