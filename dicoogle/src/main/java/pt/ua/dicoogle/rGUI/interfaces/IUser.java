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

package pt.ua.dicoogle.rGUI.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IDicomSend;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPluginControllerUser;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISearch;

/**
 * This interface contains methods that are features to normal users
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public interface IUser extends Remote {

    public String getUsername() throws RemoteException;
    
    public boolean changePassword(String oldPassHash, String newPassHash) throws RemoteException;

    public ISearch getSearch() throws RemoteException;

    public IDicomSend getDicomSend() throws RemoteException;

    public void logout() throws RemoteException;

    public void KeepAlive() throws RemoteException;

    public IPluginControllerUser getPluginController() throws RemoteException;
    /**
     * If the number is equal to the number randomly generated
     * in Main class, the timetout stops.
     *
     * @param number
     * @return if the number is equal or not
     * @throws RemoteException
     */
    public boolean shtudownTimeout(int number) throws RemoteException;
}
