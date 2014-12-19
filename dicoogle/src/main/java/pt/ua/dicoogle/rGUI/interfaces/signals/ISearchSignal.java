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

package pt.ua.dicoogle.rGUI.interfaces.signals;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public interface ISearchSignal extends Remote {
    /**
     *
     * @param flag
     *              0 - new ArrayList<SearchResult> with the local results
     *              1 - new ArrayList<SearchResultP2P> with the P2P results
     *              2 - new SearchTime
     *              3 - new ArrayList<SearchResultP2P> with P2P Thumbnails requested
     *              4 - new ArrayList<SearchResult> with the results
     * @throws RemoteException
     */
    public void sendSearchSignal(int flag) throws RemoteException;

}
