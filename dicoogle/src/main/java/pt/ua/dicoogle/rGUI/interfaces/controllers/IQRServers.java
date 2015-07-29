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

import java.util.ArrayList;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Methods avaliable for Query/Retrive Servers window
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public interface IQRServers extends Remote {

    /**
     * Add one Query/Retrieve Server to the list
     *
     * @param move
     * @return
     */
    boolean AddEntry(MoveDestination move) throws RemoteException;

    boolean RemoveEntry(MoveDestination move) throws RemoteException;

    ArrayList<MoveDestination> getMoves() throws RemoteException;

}
