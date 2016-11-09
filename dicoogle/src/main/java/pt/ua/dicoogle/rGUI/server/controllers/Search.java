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

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ua.dicoogle.core.QueryExpressionBuilder;
import pt.ua.dicoogle.plugins.NetworkMember;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.rGUI.RFileBrowser.RemoteFile;
import pt.ua.dicoogle.rGUI.fileTransfer.FileSender;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISearch;
import pt.ua.dicoogle.rGUI.interfaces.signals.ISearchSignal;
import pt.ua.dicoogle.rGUI.server.SearchHelper;
import pt.ua.dicoogle.sdk.utils.DictionaryAccess;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.Utils.TaskRequestsConstants;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.observables.FileObservable;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;
import pt.ua.dicoogle.sdk.utils.TagValue;
import pt.ua.dicoogle.sdk.utils.TagsStruct;

/**
 *  This class is the controller to search in IndexEngine or P2P Network
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class Search implements ISearch
{

    private ISearchSignal searchSignal;
    private ArrayList<String> extrafields;
    private ArrayList<SearchResult> resultList;
    private ArrayList<SearchResult> P2PResultList;
    private ArrayList<SearchResult> pendingP2PThumbnails;
    private ArrayList<SearchResult> exportList;
    private SearchHelper searchHelper;
    private long searchTime;

    public Search()
    {
        extrafields = new ArrayList<String>();

        extrafields.add("PatientName");
        extrafields.add("PatientID");
        extrafields.add("Modality");
        extrafields.add("StudyDate");
        extrafields.add("SOPInstanceUID");
        //extrafields.addMoveDestination("Thumbnail");

        searchHelper = new SearchHelper(this);
    }

    public void setSearchTime(long time)
    {
        searchTime = time;

        /*
        try {
        if(searchSignal != null)
        searchSignal.sendSearchSignal(2);
        } catch (RemoteException ex) {
        searchSignal = null;
        DebugManager.getSettings().debug("Failure to send the signal to the client..");
        LoggerFactory.getLogger(Search.class).error(ex.getMessage(), ex);
        }
         * 
         */
    }

    // SearchHelper sends the local searchResults
    public void setLocalSearchResult(ArrayList<SearchResult> sResult)
    {
        this.resultList = sResult;

        try
        {
            if (searchSignal != null)
            {
                searchSignal.sendSearchSignal(0);
            }
        } catch (RemoteException ex)
        {
            searchSignal = null;
            //DebugManager.getSettings().debug("Failure to send the signal to the client..");
            LoggerFactory.getLogger(Search.class).error(ex.getMessage(), ex);
        }
    }
    
    
    public void queryFinished()
    {

        try
        {
            if (searchSignal != null)
            {
                searchSignal.sendSearchSignal(5);
            }
        } catch (RemoteException ex)
        {
            searchSignal = null;
            //DebugManager.getSettings().debug("Failure to send the signal to the client..");
            LoggerFactory.getLogger(Search.class).error(ex.getMessage(), ex);
        }
    }

    // SearchHelper sends the new P2P searchResults
    public void setP2PSearchResult(ArrayList<SearchResult> sResult)
    {
        //System.out.println("entrou no setP2PSearchResult com " + sResult.size() + " resultados");
        this.P2PResultList = sResult;
        
        try
        {
            if (searchSignal != null)
            {
                //System.out.println("there is searchsignal");
                searchSignal.sendSearchSignal(1);
            }
        } catch (RemoteException ex)
        {
            searchSignal = null;
            //DebugManager.getSettings().debug("Failure to send the signal to the client..");
            LoggerFactory.getLogger(Search.class).error(ex.getMessage(), ex);
        }
    }

    public void setExportSearchResult(ArrayList<SearchResult> sResult)
    {
        if (this.exportList != null)
        {
            exportList.addAll(sResult);
        }
        else
        {
            this.exportList = sResult;
        }

        try
        {
            
            if (searchSignal != null)
            {
                searchSignal.sendSearchSignal(4);
            }
        } catch (RemoteException ex)
        {
            searchSignal = null;
            //DebugManager.getSettings().debug("Failure to send the signal to the client..");
            LoggerFactory.getLogger(Search.class).error(ex.getMessage(), ex);
        }
        
    }

    public void setP2PThumbnails(ArrayList<SearchResult> sResult)
    {
        if (pendingP2PThumbnails != null)
        {
            pendingP2PThumbnails.addAll(sResult);
        } else
        {
            pendingP2PThumbnails = sResult;
        }

        try
        {
            if (searchSignal != null)
            {
                searchSignal.sendSearchSignal(3);
            }

        } catch (RemoteException ex)
        {
            searchSignal = null;
            //DebugManager.getSettings().debug("Failure to send the signal to the client..");
            LoggerFactory.getLogger(Search.class).error(ex.getMessage(), ex);
        }
    }

    
    
    @Override
    public void Search(String query, boolean keywords, HashMap<String,Boolean> plugins) throws RemoteException

    {
        if (query.isEmpty())

        {
            query = "*:*";
        } else
        {
            if (!keywords)
            {
                /**
                 * Write the QueryString respecting BNF grammer
                 * defined regarding Lucene documentation 2.4.X branch
                 */
                QueryExpressionBuilder _expression = new QueryExpressionBuilder(query);
                query = _expression.getQueryString();
            }
            //DebugManager.getSettings().debug(">>> New Query String is:" + query);

        }
        searchTime = 0;
        searchHelper.search(query, extrafields, plugins, false);
    }

    @Override
    public ListObservableSearch<SearchResult> SearchToExport(String query, boolean keywords, HashMap<String, Boolean> plugins, ArrayList<String> eFields, Observer obs) throws RemoteException
    {
        if (query.isEmpty())
        {
            query = "*:*";
        } else
        {
            if (!keywords)
            {
                QueryExpressionBuilder _expression = new QueryExpressionBuilder(query);
                query = _expression.getQueryString();
            }
        }
        
        return searchHelper.search(query, eFields, plugins, true, obs);
    
    }

    @Override
    public void pruneQuery(String id) throws RemoteException
    {
        PluginController.getInstance().addTask(new TaskRequest(TaskRequestsConstants.T_QUERY_PRUNE, null, null));
    }

    class SearchDumper implements Observer
    {

        static final int WAIT_TIME = 10;
        
        ListObservableSearch<SearchResult> result = null;
        
        ArrayList<SearchResult> resultArr = new ArrayList<SearchResult>();
        
        public ArrayList<SearchResult> search(SearchResult r, String query, ArrayList<String> extrafields)
        {
        	return null;
            /*synchronized(this)
            {
            	//TODO: DELETED
                //result = PluginController.getSettings().searchOne(r.getURI().toString(),
                //query , extrafields, r.getURI().toString(), this);
                
            	
            	//System.out.println("Query: " + query);
                //System.out.println("Plugin: " + r.getPluginName());
                //System.out.println("Waiting for results");
                resultArr.addAll(result.getArray());
                try {
                    this.wait(WAIT_TIME);
                } catch (InterruptedException ex) {
                    LoggerFactory.getLogger(Search.class).error(ex.getMessage(), ex);
                }
            }
            //System.out.println("Received the results + "+resultArr.size());
            //System.out.println("Received the results + "+result.getArray());
            return resultArr;*/
        }
        
        public List<SearchResult> getResults()
        {
            //System.out.println("Received the results2 + "+resultArr.size());
            //System.out.println("Received the results2 + "+result.getArray());
            return this.resultArr;
        }
        
        @Override
        public void update(Observable o, Object o1) 
        {
            //System.out.println("Update + "+o);
            if (o1==null)
            {
                synchronized(this)
                {
                    this.notifyAll();
                }
                return;
            }
            ArrayList tmp = ((ListObservableSearch) o1).getArray();
            //System.out.println("Result_: " + tmp);
            //System.out.println("Received a result_");
            synchronized(this)
            {
                resultArr.addAll(tmp);
                this.notifyAll();
            }
            //System.out.println("Received a result - done notification_");
   
        }
    }
    
    @Override
    public ArrayList<SearchResult> SearchIndexedMetaData(SearchResult searchResult) throws RemoteException {
        
        TagsStruct tags = TagsStruct.getInstance();
        
        ArrayList<String> extraFields = new ArrayList<>();
        
        for(TagValue tag : tags.getAllFields())
        	extraFields.add(tag.getAlias());
        
        String query = "FileName:" + searchResult.getURI() + " AND FileHash:"+ searchResult.get("filehash");

        
        SearchDumper sdumper = new SearchDumper();
        sdumper.search(searchResult, query, extraFields);
        List<SearchResult> result = sdumper.getResults();
        //System.out.println("Result + " +result);
        if (result.size() > 0) {
            
            SearchResult r = (SearchResult) result.get(0);
            for (String s:r.getExtraData().keySet())
            {
                if (s.contains("Sequence"))
                {
                    String [] fields = r.getExtraData().get(s).toString().split(" ");
                    for (int i = 0 ; i<fields.length; i++)
                    {
                            extraFields.add(fields[i]);
                    }
                    
                }
            }
            
            sdumper = new SearchDumper();
            sdumper.search(searchResult, query, extraFields);
            result = sdumper.getResults();
         
        }
        
        return (ArrayList<SearchResult>) result;
    }
    
    
    

    @Override
    public long getSearchTime() throws RemoteException
    {
        return searchTime;
    }

    @Override
    public void RegisterSignalBack(ISearchSignal signalBack) throws RemoteException
    {
        //System.out.println("Registring SignalBack");

        //if(signalBack == null)
        //    System.out.println("signalBack == null");
        //else
        //    System.out.println("signalBack != null");

        searchSignal = signalBack;

        //System.out.println("SignalBack Registered");
    }

    @Override
    public FileObservable RequestP2PFile(SearchResult file) throws RemoteException
    {
        //TOSO: fix this
       // return PluginController.getSettings().requestFile(file.getURI(), file.getURI(), file.getURI(), file.get("filehash"));
        //PeerEngine.getSettings().requestFile(file.getAddress(), file.getFileName(), file.getFileHash());
        return null;
    }

    /**
     * Don't work with SearchResultP2P
     * 
     * @param file
     * @return
     * @throws RemoteException
     */
    @Override
    public SimpleEntry<RemoteFile, Integer> downloadFile(SearchResult result) throws RemoteException
    {
        /*if (!PluginController.getSettings().isLocalPlugin(result.getPluginName()))
        {
            return null;
        }*/
        try
        {
            String path = result.getURI().toString().replace('\\', '/');

            File file = new File(path);
            InetAddress client = InetAddress.getByName(RemoteServer.getClientHost());

            FileSender sender = new FileSender(file, client);

            SimpleEntry<RemoteFile, Integer> entry = new SimpleEntry<RemoteFile, Integer>(new RemoteFile(file), sender.getListenerPort());

            //DebugManager.getSettings().debug("Transfering file: " + entry.getKey().getName() + ", listening port: " + entry.getValue());

            Thread tSender = sender;
            tSender.start();

            return entry;

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<NetworkMember> getPeerList() throws RemoteException
    {
    	//TODO: DELETED
        //return PluginController.getSettings().getMembers();
    	return null;
    }

    @Override
    public ArrayList<SearchResult> getSearchResults() throws RemoteException
    {
        ArrayList<SearchResult> returnResultList = resultList;

        resultList = null;

        return returnResultList;
    }

    @Override
    public ArrayList<SearchResult> getP2PSearchResults() throws RemoteException
    {
        ArrayList<SearchResult> returnResultList = P2PResultList;

        P2PResultList = null;

        return returnResultList;
    }

    @Override
    public ArrayList<SearchResult> getExportSearchResults() throws RemoteException
    {
        ArrayList<SearchResult> returnResultList = exportList;

        exportList = null;

        return returnResultList;
    }

    @Override
    public SearchResult getThumbnail(URI FileName, String FileHash) throws RemoteException
    {
        return searchHelper.searchThumbnail(FileName, FileHash);
    }

    @Override
    public void getP2PThumbnail(URI FileName, String FileHash, String addr) throws RemoteException
    {
        searchHelper.searchP2PThumbnail(FileName, FileHash, addr);
    }

    @Override
    public ArrayList<SearchResult> getPendingP2PThumnails() throws RemoteException
    {
        ArrayList<SearchResult> returnResultList = pendingP2PThumbnails;

        pendingP2PThumbnails = null;

        return returnResultList;
    }

    @Override
    public HashMap<String, Integer> getTagList() throws RemoteException
    {
        return DictionaryAccess.getInstance().getTagList();
    }

    @Override
    public HashMap<Integer, TagValue> getDIMFields() throws RemoteException
    {
    	Set<TagValue> fields = TagsStruct.getInstance().getDIMFields();
    	HashMap<Integer, TagValue> map = new HashMap<>(fields.size());
    	for(TagValue tag : fields)
    		map.put(tag.getTagNumber(), tag);
    	
        return map;
    }
}
