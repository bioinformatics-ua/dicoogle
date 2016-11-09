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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Objects;

/** An immutable data structure for describing a DICOM node (potential C-MOVE destinations).
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class MoveDestination implements Serializable
{
    static final long serialVersionUID = 2L;

    @JsonProperty("aetitle")
    private final String AETitle;
    @JsonProperty("address")
    private final String ipAddrs;
    @JsonProperty("port")
    private final int port;

    @JsonProperty("description")
    private final String description;
    @JsonProperty("public")
    private final boolean isPublic;

    @JsonCreator
    public MoveDestination(@JsonProperty("aetitle") String AETitle, @JsonProperty("address") String ipAddr,
                           @JsonProperty("port") int port, @JsonProperty("public") boolean isPublic,
                           @JsonProperty("description") String description) {
        this.AETitle = AETitle;
        this.ipAddrs = ipAddr;
        this.port = port;
        this.isPublic = isPublic;
        this.description = description;
    }

    public MoveDestination(String AETitle, String ipAddr, int port, boolean isPublic) {
        this(AETitle, ipAddr, port, isPublic, "");
    }

    public MoveDestination(String AETitle, String ipAddr, int port) {
        this(AETitle, ipAddr, port, false, "");
    }

    /**
     * @return the AETitle
     */
    public String getAETitle()
    {
        return AETitle;
    }

    /**
     * @return the ipAddrs
     */
    public String getIpAddrs()
    {
        return ipAddrs;
    }

    /**
     * @return the port
     */
    public int getPort()
    {
        return port;
    }

    @Override
    public String toString()
    {
        String result;
        result = "MoveDestination AET: " + this.AETitle + " (" + this.ipAddrs + ":" +
                String.valueOf(this.port) + ")";
        if (!description.isEmpty()) {
            result += " - " + this.description;
        }
        result += "(" + (isPublic ? "Public)" : "Private)");
        
        return result ;
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the isPublic
     * @todo rename method name
     */
    @Deprecated
    public boolean isIsPublic() {
        return isPublic;
    }

    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveDestination that = (MoveDestination) o;
        return port == that.port &&
                isPublic == that.isPublic &&
                Objects.equals(AETitle, that.AETitle) &&
                Objects.equals(ipAddrs, that.ipAddrs) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(AETitle, ipAddrs, port, description, isPublic);
    }
}
