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
package pt.ua.dicoogle.rGUI.interfaces.controllers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * Query Retrieve Settings interface
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public interface IQueryRetrieve extends Remote {

    public int getMaxClientAssoc() throws RemoteException;
    public void setMaxClientAssoc(int value) throws RemoteException;

    public int getMaxPDULengthReceive() throws RemoteException;
    public void setMaxPDULengthReceive(int value) throws RemoteException;

    public int getMaxPDULengthSend() throws RemoteException;
    public void setMaxPDULengthSend(int value) throws RemoteException;

    public int getQRIdleTimeout() throws RemoteException;
    public void setQRIdleTimeout(int value) throws RemoteException;

    public int getQRAcceptTimeout() throws RemoteException;
    public void setQRAcceptTimeout(int value) throws RemoteException;

    public int getQRRspDelay() throws RemoteException;
    public void setQRRspDelay(int value) throws RemoteException;

    public int getQRConnectionTimeout() throws RemoteException;
    public void setQRConnectionTimeout(int value) throws RemoteException;

    public HashMap<String, String> getFindModalities() throws RemoteException;

    /*
     * Setters ?
     * QR port P2P?
     */
}
