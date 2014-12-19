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
import java.util.ArrayList;

/**
 * Access List Settings interface
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public interface IAccessList extends Remote {
    public String getAETitle() throws RemoteException;
    public void setAETitle(String AETitle) throws RemoteException;

    public ArrayList<String> getAccessList() throws RemoteException;
    public boolean addToAccessList(String AETitle) throws RemoteException;
    public boolean removeFromAccessList(String AETitle) throws RemoteException;

    public void setPermitAllAETitles(boolean value) throws RemoteException;
    public boolean getPermitAllAETitles() throws RemoteException;
}
