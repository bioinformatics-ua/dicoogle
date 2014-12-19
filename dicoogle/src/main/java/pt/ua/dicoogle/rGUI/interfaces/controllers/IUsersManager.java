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
 * Users Manager Settings interface
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public interface IUsersManager extends Remote {

    public boolean getEncryptUsersFile() throws RemoteException;
    public void setEncryptUsersFile(boolean value) throws RemoteException;

    public ArrayList<String> getUsernames() throws RemoteException;
    public boolean isAdmin(String username) throws RemoteException;

    public boolean deleteUser(String username) throws RemoteException;
    public boolean addUser(String username, String passwordHash, boolean admin) throws RemoteException;

    public boolean resetPassword(String username, String passwordHash) throws RemoteException;
}
