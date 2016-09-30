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
package pt.ua.dicoogle.rGUI.server.controllers;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.core.settings.ServerSettings;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IQueryRetrieve;

/**
 * Controller of Query/Retrieve Settings
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class QueryRetrieve implements IQueryRetrieve {
    private ServerSettings settings;

    private int MaxClientAssoc;
    private int MaxPDULengthReceive;
    private int MaxPDULengthSend;
    private int IdleTimeout;
    private int AcceptTimeout;
    private int RspDelay;
    private int ConnectionTimeout;


    private static Semaphore sem = new Semaphore(1, true);
    private static QueryRetrieve instance = null;

    public static synchronized QueryRetrieve getInstance()
    {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new QueryRetrieve();
            }
            sem.release();
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(QRServers.class).error(ex.getMessage(), ex);
        }
        return instance;
    }

    private QueryRetrieve(){
        settings = ServerSettings.getInstance();

        loadSettings();
    }

    public void loadSettings(){
        MaxClientAssoc = settings.getMaxClientAssoc();
        MaxPDULengthReceive = settings.getMaxPDULengthReceive();
        MaxPDULengthSend = settings.getMaxPDULenghtSend();
        IdleTimeout = settings.getIdleTimeout();
        AcceptTimeout = settings.getAcceptTimeout();
        RspDelay = settings.getRspDelay();
        ConnectionTimeout = settings.getConnectionTimeout();
    }

    /**
     * Save the Settings related to QueryRetrieve to ServerSettings
     *
     * dont print XML
     */
    public void saveSettings(){
        settings.setMaxClientAssoc(MaxClientAssoc);
        settings.setMaxPDULengthReceive(MaxPDULengthReceive);
        settings.setMaxPDULengthSend(MaxPDULengthSend);
        settings.setIdleTimeout(IdleTimeout);
        settings.setAcceptTimeout(AcceptTimeout);
        settings.setRspDelay(RspDelay);
        settings.setConnectionTimeout(ConnectionTimeout);
    }

    /**
     *
     * @return  true - if there are unsaved settings ( != ServerSettings)
     *          false - not
     */
    public boolean unsavedSettings(){
        if(MaxClientAssoc != settings.getMaxClientAssoc() || MaxPDULengthReceive != settings.getMaxPDULengthReceive()
                || MaxPDULengthSend != settings.getMaxPDULenghtSend() || IdleTimeout != settings.getIdleTimeout()
                || AcceptTimeout != settings.getAcceptTimeout() || RspDelay != settings.getRspDelay()
                || ConnectionTimeout != settings.getConnectionTimeout())
            return true;

        return false;
    }

    @Override
    public int getMaxClientAssoc() throws RemoteException {
        return MaxClientAssoc;
    }
    @Override
    public void setMaxClientAssoc(int value) throws RemoteException {
        MaxClientAssoc = value;
    }

    @Override
    public int getMaxPDULengthReceive() throws RemoteException {
        return MaxPDULengthReceive;
    }
    @Override
    public void setMaxPDULengthReceive(int value) throws RemoteException {
        MaxPDULengthReceive = value;
    }

    @Override
    public int getMaxPDULengthSend() throws RemoteException {
        return MaxPDULengthSend;
    }
    @Override
    public void setMaxPDULengthSend(int value) throws RemoteException {
        MaxPDULengthSend = value;
    }

    @Override
    public int getQRIdleTimeout() throws RemoteException {
        return IdleTimeout;
    }
    @Override
    public void setQRIdleTimeout(int value) throws RemoteException {
        IdleTimeout = value;
    }

    @Override
    public int getQRAcceptTimeout() throws RemoteException {
        return AcceptTimeout;
    }
    @Override
    public void setQRAcceptTimeout(int value) throws RemoteException {
        AcceptTimeout = value;
    }

    @Override
    public int getQRRspDelay() throws RemoteException {
        return RspDelay;
    }
    @Override
    public void setQRRspDelay(int value) throws RemoteException {
        RspDelay = value;
    }

    @Override
    public int getQRConnectionTimeout() throws RemoteException {
        return ConnectionTimeout;
    }
    @Override
    public void setQRConnectionTimeout(int value) throws RemoteException {
        ConnectionTimeout = value;
    }

    @Override
    public HashMap<String, String> getFindModalities() throws RemoteException {
        Map<String, String> map = settings.getModalityFind();
        
        return new HashMap<String, String>(map);
    }

}
