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
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

/**
 * SOP Class Settings interface
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public interface ISOPClass extends Remote {

    public ArrayList<SimpleEntry<String, String>> getTransferSyntax() throws RemoteException;

    public ArrayList<String> getSOPClassList() throws RemoteException;

    public String getUID(String sopClass) throws RemoteException;

    public boolean[] getTS(String UID) throws RemoteException;
    public boolean getAccepted(String UID) throws RemoteException;

    public boolean[] setDefault() throws RemoteException;
    public boolean[] clearAll() throws RemoteException;
    public boolean[] setAll() throws RemoteException;
    public void setTS(String UID, boolean status, int number) throws RemoteException;

    public void setAccepted(String UID, boolean value) throws RemoteException;

    public void saveLocalTS(String UID) throws RemoteException;
    public void saveAllTS() throws RemoteException;
}
