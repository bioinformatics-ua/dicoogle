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
import java.util.HashMap;

import pt.ua.dicoogle.sdk.utils.TagValue;

/**
 * Index Options interface
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public interface IIndexOptions extends Remote {

    public HashMap<String, ArrayList<TagValue>> getDIMFields() throws RemoteException;

    public boolean isIndexAllModalities() throws RemoteException;
    public void setIndexAllModalities(boolean value) throws RemoteException;
    
    public ArrayList<String> getModalities() throws RemoteException;
    public boolean addModality(String modality) throws RemoteException;
    public boolean removeModality(String modality) throws RemoteException;
    
    public boolean removeDictionary(String dic) throws RemoteException;
    public boolean addDictionary(String dic) throws RemoteException;

    public HashMap<String, ArrayList<TagValue>> getManualFields() throws RemoteException;
    public boolean addManualField(int group, int subGroup, String name) throws RemoteException;
    public boolean removeManualField(int group, int subGroup) throws RemoteException;

    
    
    public void index(String path, boolean resume) throws RemoteException;

    /**
     * Indexes even if thes files are already indexed
     *
     * @param list
     * @throws RemoteException
     */
    public void reIndex(ArrayList<String> list) throws RemoteException;

    public void removeFilesFromIndexer(ArrayList<String> files, boolean deleteFiles) throws RemoteException;
    
}
