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

import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import pt.ua.dicoogle.plugins.NetworkMember;
import pt.ua.dicoogle.rGUI.RFileBrowser.RemoteFile;
import pt.ua.dicoogle.rGUI.interfaces.signals.ISearchSignal;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.observables.FileObservable;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;
import pt.ua.dicoogle.sdk.utils.TagValue;


/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public interface ISearch extends Remote {

    // Search
    public void Search(String query, boolean keywords, HashMap<String, Boolean> range) throws RemoteException;
    public ListObservableSearch<SearchResult> SearchToExport(String query, boolean keywords, HashMap<String, Boolean> range, ArrayList<String> extraFields, Observer obs) throws RemoteException;
    public List<SearchResult> SearchIndexedMetaData(SearchResult sresult) throws RemoteException;
    public void pruneQuery(String id) throws RemoteException;;
    
    // Thumnails
    public SearchResult getThumbnail(URI FileName, String FileHash) throws RemoteException;
    public void getP2PThumbnail(URI FileName, String FileHash, String addr) throws RemoteException;
    public ArrayList<SearchResult> getPendingP2PThumnails() throws RemoteException;

    //Search Results
    public long getSearchTime() throws RemoteException;
    public List<SearchResult> getSearchResults() throws RemoteException;
    public List<SearchResult> getP2PSearchResults() throws RemoteException;
    public List<SearchResult> getExportSearchResults() throws RemoteException;

    public HashMap<String, Integer> getTagList() throws RemoteException;

    public HashMap<Integer, TagValue> getDIMFields() throws RemoteException;

    //Download a file from a remote GUI Server
    public SimpleEntry<RemoteFile, Integer> downloadFile(SearchResult file) throws RemoteException;

    // Request a File to download from P2P Network
    public FileObservable RequestP2PFile(SearchResult file) throws RemoteException;

    // get the list of P2P Peers
    public List<NetworkMember> getPeerList() throws RemoteException;
    
    public void RegisterSignalBack(ISearchSignal signalBack) throws RemoteException;
}
