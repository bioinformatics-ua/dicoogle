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



/*
 * Dicom Network is a singletone class to handler the services
 * Each Device have a Application Entity and a Device can have multiple
 * connections. Hence for an Application Entity we have multiple connections 
 * associated too. 
 * 
 * Dicoogle is available to keep some DICOM services up,
 * but services need to be handled and forward to correct entity 
 * 
 * 
 */

package pt.ua.dicoogle.server;


import pt.ua.dicoogle.server.callbacks.LogEvent;
import pt.ua.dicoogle.server.callbacks.LogEventAfter;
import pt.ua.dicoogle.server.callbacks.LogEventBefore;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public abstract class DicomNetwork
{    
    

    private NetworkApplicationEntity remoteAE = new NetworkApplicationEntity();
    private NetworkConnection remoteConn = new NetworkConnection();
    private Device device = null ;
    private NetworkApplicationEntity localAE = new NetworkApplicationEntity();
    private NetworkConnection localConn = new NetworkConnection();


    // AETitle of Service
    private String AETitle = null ;


    /** Event Interface Calls */ 
    LogEvent eventService = null ;
    LogEventBefore eventBefore = null ;
    LogEventAfter eventAfter = null ;

    private String deviceName = null ;
    
    public DicomNetwork(String DeviceName)
    {
        // Starts Device
        device = new Device(DeviceName);
        this.deviceName = DeviceName; 
    }



    /**
     * Connect to the service after happen
     * For example in the case of c-move it will happen after send all images
     *
     * @param e An class that implement methods to be call
     * @return a boolean to know if event will be triggered or not
     */
    public boolean connectAfter(LogEventAfter e)
    {
        boolean result = false ;
        if (e != null && e instanceof LogEventAfter  )
        {
            result = true ;
        }
        this.eventAfter = e ;
        return result ; 
    }


    /**
     * Connect to the service before happen
     * For example in the case of c-move it will happen before send images
     *
     * @param e An class that implement methods to be call
     * @return a boolean to know if event will be triggered or not
     */
    public boolean connectBefore(LogEventBefore e)
    {
        boolean result = false ;
        if (e != null && e instanceof LogEventBefore  )
        {
            result = true ;
        }
        this.eventBefore = e ;
        return result ;
    }

    /**
     * When a service is started or stopped it will be called after service
     * start/stop
     * @param e the class that have necessary callbacks implemented
     * @return
     */

    public boolean connectServices(LogEvent e)
    {
        boolean result = false ;
        if (e != null && e instanceof LogEvent )
        {
            result = true ;
        }
        this.eventService = e ;
        return result ;
    }




    public boolean startListening()
    {
        boolean result = false;

        result = doStartService();
        if (this.eventService!=null)
            this.eventService.startService(this.deviceName +  " was started " +
                    "QueryRetrieve");
        return result ; 
          
    }


    public boolean stopListening()
    {
        boolean result = false;

        result = doStopService();
        if (this.eventService!=null)
            this.eventService.stopService(this.deviceName +  " was stoppped" +
                    " QueryRetrieve");
        return result ;
    }

    public abstract boolean doStartService();
    public abstract boolean doStopService();
    


    /**
     * @return the remoteAE
     */
    public NetworkApplicationEntity getRemoteAE() {
        return remoteAE;
    }

    /**
     * @param remoteAE the remoteAE to set
     */
    public void setRemoteAE(NetworkApplicationEntity remoteAE) {
        this.remoteAE = remoteAE;
    }

    /**
     * @return the remoteConn
     */
    public NetworkConnection getRemoteConn() {
        return remoteConn;
    }

    /**
     * @param remoteConn the remoteConn to set
     */
    public void setRemoteConn(NetworkConnection remoteConn) {
        this.remoteConn = remoteConn;
    }

    /**
     * @return the device
     */
    public Device getDevice() {
        return device;
    }

    /**
     * @param device the device to set
     */
    public void setDevice(Device device) {
        this.device = device;
    }

    /**
     * @return the localAE
     */
    public NetworkApplicationEntity getLocalAE() {
        return localAE;
    }

    /**
     * @param localAE the localAE to set
     */
    public void setLocalAE(NetworkApplicationEntity localAE) {
        this.localAE = localAE;
    }

    /**
     * @return the localConn
     */
    public NetworkConnection getLocalConn() {
        return localConn;
    }

    /**
     * @param localConn the localConn to set
     */
    public void setLocalConn(NetworkConnection localConn) {
        this.localConn = localConn;
    }

    /**
     * @return the AETitle
     */
    public String getAETitle() {
        return AETitle;
    }

    /**
     * @param AETitle the AETitle to set
     */
    public void setAETitle(String AETitle) {
        this.AETitle = AETitle;
    }

}
