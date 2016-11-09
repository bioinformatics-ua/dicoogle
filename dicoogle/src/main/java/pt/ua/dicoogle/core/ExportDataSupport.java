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
package pt.ua.dicoogle.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Semaphore;
import org.slf4j.LoggerFactory;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISearch;
import pt.ua.dicoogle.rGUI.server.controllers.Search;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.Utils.TaskRequestsConstants;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;


/**
 * This class is used to export query results to .csv file format with the selected tags
 *
 * @author Samuel da Costa Campos <samuelcampos@ua.pt>
 *
 */
public class ExportDataSupport extends Observable implements Observer, Serializable {
    private String query;
    private HashMap<String, Boolean> origin;
    //private boolean network;
    private boolean keywords;
    private ArrayList<String> tags;
    private String filePath;

    private static Semaphore semFile = new Semaphore(1, true);
    private ListObservableSearch obsAux = null;

    
    public ExportDataSupport(String query, HashMap<String, Boolean> origin, boolean keywords, ArrayList<String> tags, String filePath) throws Exception
    {
        
        //System.out.println("Plugins: " + origin);
        
        //System.out.println("Entenring...");
        this.query = query;
        this.origin = origin;
        this.keywords = keywords;
        this.tags = tags;
        this.filePath = filePath;

        if(!this.filePath.endsWith(".csv"))
            this.filePath = this.filePath + ".csv";

    }

    public void InitiateExport(Observer obs) throws Exception
    {
        this.addObserver(obs);
        //this.obsAux = obs;
        //System.out.println("Count observables: "+this.countObservers());
        //System.out.println("1 - initiateExport");
        printFirstLine();
        //System.out.println("2 - initiateExport");
        //Result2Tree.getSettings().searchToExport(query, keywords, origin, tags, this);
        ISearch search = new Search();
        obsAux  = search.SearchToExport(query, keywords, origin, tags, this);
        //System.out.println("3 - initiateExport"); 
    }

    /**
     * Print the first line of the .csv file
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void printFirstLine() throws IOException, InterruptedException{
        StringBuffer st = new StringBuffer();
        //System.out.println("Write the First Line");
        for(String tag: tags)
            st.append(tag).append(';');

        st.append('\n');
        //System.out.println("Count observables: "+this.countObservers());
        printToFile(st);
    }

    private void printLines(ArrayList<SearchResult> results) throws InterruptedException, IOException{
        //System.out.println("Count observables: "+this.countObservers());
        StringBuffer st = new StringBuffer();
        long i = 0 ; 
        //System.out.println("Calling");
        for(SearchResult result : results){
            
            //System.out.println("Export: " + i );
            //System.out.println("Count observables: "+this.countObservers());
            
            HashMap<String, Object> extraFields = result.getExtraData();
            
            for(String tag: tags){
                Object temp1 = extraFields.get(tag);
                String temp = null;
                if(temp1 instanceof String)
                    temp = (String) temp1;
                
                if(temp == null)
                {
                    st.append(';');
                }
                else{
                    temp = temp.replace("\n", "");
                    temp = temp.replace(";", ",");
                    st.append(temp).append(';');
                }
            }
            st.append('\n');
            if (i%1000==0)
            {
                BufferedWriter out = new BufferedWriter(new FileWriter(filePath, true));
                out.write(st.toString());
                out.close();
                st = new StringBuffer();
            }
            i++;
            
        }
        //System.out.println("Out of loop");
        // Print last buffer (that is not yet printed)
        printToFile(st);

        // only intended to notify the observers of the fist time
        //System.out.println("Calling observables: "+this.countObservers());
        if(this.countObservers() > 0)
        {
            //System.out.println("Calling observables. There is one, at least.");
            this.setChanged();
            
            if (obsAux!=null)
            {
                LoggerFactory.getLogger(ExportDataSupport.class).error("obsAux " + obsAux.isFinish() );
                this.notifyObservers(obsAux.isFinish());
            }
            else
            {
                LoggerFactory.getLogger(ExportDataSupport.class).error("not obsAux " );
                this.notifyObservers(false);
            }
            //this.deleteObservers();
        }
    }

    private void printToFile(StringBuffer st) throws InterruptedException, IOException{
        
        BufferedWriter out = new BufferedWriter(new FileWriter(filePath, true));
        out.write(st.toString());
        out.close();

    }

    private int count = 0 ; 
    
    @Override
    public synchronized  void update(Observable o, Object arg) {
        count++ ;
        
        if (obsAux==null)
        {

            obsAux = ((ListObservableSearch) o);
        }
        LoggerFactory.getLogger(ExportDataSupport.class).error("Update @ obs");
        ArrayList tmp = ((ListObservableSearch) o).getArray();
        
        if (tmp==null)
        {
            tmp = ((ListObservableSearch) obsAux).getArray();
        }
        
        if (tmp==null)
        {
            LoggerFactory.getLogger(ExportDataSupport.class).error("Update is null, sending block signal");
            PluginController.getInstance().addTask(new TaskRequest(TaskRequestsConstants.T_BLOCK_SIGNAL, null, null));
            return;
        }
        
        boolean finish = ((ListObservableSearch) obsAux).isFinish();
        LoggerFactory.getLogger(ExportDataSupport.class).error("Finished @ " + finish);
        if (finish)
        {
            // only intended to notify the observers of the fist time
            if(this.countObservers() > 0)
            {
                this.setChanged();
                this.notifyObservers((Boolean)finish);
                obsAux = null;
                this.deleteObservers();
            }
        }   
        
        if(tmp instanceof ArrayList) 
        {
            try
            {
                
                ArrayList<SearchResult> results = (ArrayList<SearchResult>) tmp;
                if (results.isEmpty())
                {
                    PluginController.getInstance().addTask(new TaskRequest(TaskRequestsConstants.T_BLOCK_SIGNAL, null, null));
                    return;
                }
                LoggerFactory.getLogger(ExportDataSupport.class).error("Print lines");
                printLines(results);
                PluginController.getInstance().addTask(new TaskRequest(TaskRequestsConstants.T_BLOCK_SIGNAL, null, null));

            } catch (Exception ex) 
            {
                LoggerFactory.getLogger(ExportDataSupport.class).error(ex.getMessage(), ex);
            }
        }
    }
}
