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
package pt.ua.dicoogle.rGUI.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.rGUI.server.controllers.Search;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.Utils.TaskRequestsConstants;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class SearchHelper implements Observer
{

    private ListObservableSearch<SearchResult> SearchResList = null;
    private boolean export = false; // indicates whether the search is to export or do not
    private long time;
    private Search searchControler;

    public SearchHelper(Search search)
    {
        this.searchControler = search;


    }

    public void setList(ListObservableSearch<SearchResult> SearchResList)
    {
        this.SearchResList = SearchResList;
    }
    
    
    public ListObservableSearch<SearchResult> search(String query, ArrayList<String> extrafields,
            HashMap<String, Boolean> plugins, boolean export, Observer obs)
    {
    
        this.export = export;
        this.time = System.nanoTime();

    ArrayList<String> plugins2 = new ArrayList<String>();    
        for (String pi : plugins.keySet())
        {
            Boolean value = plugins.get(pi);
            if (value)
            {
                    plugins2.add(pi);

            }
            
        }
        
        
        Thread seachThread = new SearchLocal(query, extrafields, plugins2, obs);
        seachThread.start();
        return SearchResList;
        
        
    }
    
    
    public void search(String query, ArrayList<String> extrafields,
            HashMap<String, Boolean> plugins, boolean export)
    {
        search(query, extrafields, plugins, export, this);
    
    }

    public SearchResult searchThumbnail(URI FileName, String FileHash)
    {
        ArrayList<SearchResult> queryResultListLocal;

        ArrayList<String> extrafields = new ArrayList<String>();
        extrafields.add("Thumbnail");

        String query = "FileName:" + FileName + " AND FileHash:" + FileHash;
        // TODO: Implement it.
        return null;
    }

    public void searchP2PThumbnail(URI FileName, String FileHash, String addr)
    {

        ArrayList<String> extrafields = new ArrayList<String>();
        extrafields.add("Thumbnail");

        String query = "FileName:" + FileName + " AND FileHash:" + FileHash;
        // TODO: Implement it.
    }

    @Override
    public void update(Observable o, Object arg)
    {
        
        ArrayList tmp = ((ListObservableSearch) o).getArray();
        Boolean finished = ((ListObservableSearch) o).isFinish();
        System.out.println("SearchHelper...recebeu os resultados: " + tmp.size());
        ArrayList<SearchResult> resultsList;
        
        if (tmp.isEmpty())
        {
            return;
                  
        } else
        {
            
            ((ListObservableSearch) o).resetArray();
            
            if (SearchResult.class.isInstance(tmp.get(0)))
            {
                
                if (finished)
                {
                    searchControler.queryFinished();
                }
                
                    resultsList = tmp;

                    if (resultsList.size() == 1 && resultsList.get(0).getExtraData().size() == 0)
                    {
                       return;
                    }

                    //if the result is just the requested Thumbnail
                    if (resultsList.size() == 1 && resultsList.get(0).getExtraData().size() == 1
                            && resultsList.get(0).getExtraData().get("Thumbnail") != null)
                    {
                        searchControler.setP2PThumbnails(resultsList);
                        
                    } else
                    {
                        ((ListObservableSearch) o).resetArray();
                        long timeEnd = System.nanoTime();

                        if (!export)
                        {
                            searchControler.setSearchTime(((int) ((timeEnd - time) / 1000000L)));
                            searchControler.setP2PSearchResult(resultsList);
                        } else
                        {
                            searchControler.setExportSearchResult(resultsList);
                        }
                        
                        PluginController.getInstance().addTask(new TaskRequest(TaskRequestsConstants.T_BLOCK_SIGNAL, null, null));
                    }
           
            }
            
            
            // Memory monitoring 
             long freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024;
             long totalMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
             long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
             
             double percentage = freeMemory / totalMemory;
             
             if (percentage>0.8)
             {
                 // Prune query!!! 
                 PluginController.getInstance().addTask(new TaskRequest(TaskRequestsConstants.T_QUERY_PRUNE, null, null));
             }
        }
        
    }

    /**
     * Private class that implements one Thread to search in IndexEngine
     */
    private class SearchLocal extends Thread
    {

        private String query;
        private ArrayList<String> extrafields;
        private ArrayList<String> pluginsLocals;
        private Observer searchHelper;

        public SearchLocal(String query, ArrayList<String> extrafields, ArrayList<String> pluginsLocals , Observer searchHelper)
        {
            this.query = query;
            this.extrafields = extrafields;
            this.pluginsLocals = pluginsLocals;
            this.searchHelper = searchHelper;
        }

        @Override
        public void run()
        {
            //TODO: DELETED
        	//SearchResList = PluginController.getSettings().search(pluginsLocals, query, extrafields, searchHelper);
        }
    }
}
