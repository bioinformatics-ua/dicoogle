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
package pt.ua.dicoogle.core;

import java.io.Serializable;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class MoveDestination implements Serializable
{
    static final long serialVersionUID = 1L;

    private String AETitle = null ;
    private String ipAddrs = null ;
    private int port ;

    public MoveDestination(String AETitle, String ipAddr, int port)
    {
        this.AETitle = AETitle ;
        this.ipAddrs = ipAddr ;
        this.port = port ;
    }

    /**
     * @return the AETitle
     */
    public String getAETitle()
    {
        return AETitle;
    }

    /**
     * @param AETitle the AETitle to set
     */
    public void setAETitle(String AETitle)
    {
        this.AETitle = AETitle;
    }

    /**
     * @return the ipAddrs
     */
    public String getIpAddrs()
    {
        return ipAddrs;
    }

    /**
     * @param ipAddrs the ipAddrs to set
     */
    public void setIpAddrs(String ipAddrs)
    {
        this.ipAddrs = ipAddrs;
    }

    /**
     * @return the port
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    @Override
    public String toString()
    {
        String result = "" ;

        result = "MoveDestination AET: " + this.AETitle + " (" + this.ipAddrs + ":" +
                String.valueOf(this.port) + ")";

        return result ;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null || getClass() != obj.getClass())
            return false;

        if(obj == this)
            return true;

        MoveDestination move = (MoveDestination) obj;

        // To the list of MoveDestinations may not be repeated AETitles
        return move.AETitle.equals(AETitle) && move.ipAddrs.equals(ipAddrs) && move.port == port;

        //return move.AETitle.equals(AETitle) && move.ipAddrs.equals(ipAddrs) && move.port == port;
    }
}
