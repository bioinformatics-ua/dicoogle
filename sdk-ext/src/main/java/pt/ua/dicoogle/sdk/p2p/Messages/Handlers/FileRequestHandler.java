/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk-ext.
 *
 * Dicoogle/dicoogle-sdk-ext is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk-ext is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.sdk.p2p.Messages.Handlers;

import java.util.Observable;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageFields;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageXML;
import pt.ua.dicoogle.sdk.p2p.Messages.SearchResultFields;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.ua.dicoogle.sdk.NetworkPluginAdapter;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.Utils.TaskRequestsConstants;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageI;

/**
 *
 * @author Carlos Ferreira
 */
public class FileRequestHandler implements MessageHandler, Observer
{

    private NetworkPluginAdapter NPA;

    public FileRequestHandler(NetworkPluginAdapter NPA)
    {
        this.NPA = NPA;
    }

    public void handleMessage(MessageI message, String address)
    {
        if (!MessageXML.class.isInstance(message))
        {
            return;
        }
        byte[] msg = (byte[]) message.getMessage();

        SAXReader saxReader = new SAXReader();

        Document document = null;
        try
        {
            document = saxReader.read(new ByteArrayInputStream(msg));
        } catch (DocumentException ex)
        {
            ex.printStackTrace(System.out);
        }

        Element root = document.getRootElement();
        if (root == null)
        {
            return;
        }

        Element tmp = root.element(MessageFields.FILE_REQUESTED);
        if (tmp == null)
        {
            return;
        }
        Element temp2 = tmp.element(SearchResultFields.FILE_NAME);
        String Filename = temp2.getText();
        temp2 = tmp.element(SearchResultFields.FILE_HASH);
        String Filehash = temp2.getText();

        //public TaskRequest(int Task, String RequesterPlugin, Map<Integer , Object> Parameters)
        HashMap<Integer, Object> parameters = new HashMap<Integer, Object>();
        parameters.put(TaskRequestsConstants.P_QUERY, "FileHash:" + Filehash + " AND FileName:\"" + Filename+"\"");
        parameters.put(TaskRequestsConstants.P_EXTRAFIELDS, new ArrayList<String>());
        parameters.put(TaskRequestsConstants.P_REQUESTER_ADDRESS, address);

        TaskRequest task = new TaskRequest(TaskRequestsConstants.T_QUERY_LOCALLY, this.NPA.getName(), parameters);
        task.addObserver(this);
        this.NPA.getTaskRequestsList().addTask(task);
        //System.out.println("handle message do FileRequestHandler....\n The query is: " + "FileHash:" + Filehash + " and FileName:" + Filename);

        //List<SearchResult> sr = IndexEngine.getInstance().search("FileHash:"+Filehash+" and FileName:"+Filename, null);
        //String filepath = new String(Base64.decodeBase64(tmp.getText().getBytes()));

    }

    /*  public void update(Observable o, Object arg)
    {
    if (!TaskRequest.class.isInstance(o))
    {
    return;
    }
    
    TaskRequest task = (TaskRequest) o;
    
    if (task.hasChanged())
    {
    List<SearchResult> sr = (List<SearchResult>) task.getResults().get(TaskRequestsConstants.R_SEARCH_RESULTS);
    
    if ((sr == null) || (sr.isEmpty()))
    {
    return;
    }
    this.NPA.sendFile(sr.get(0).getOrigin(), this.NPA.getName());
    }
    }*/
    public void update(Observable o, Object arg)
    {
        //System.out.println("Update do FileRequestHandler.........");
        if (TaskRequest.class.isInstance(o))
        {
            //System.out.println("Update do FileRequestHandler -> 1");
            TaskRequest tr = (TaskRequest) o;
            if (tr.getTask() != TaskRequestsConstants.T_QUERY_LOCALLY)
            {
                //System.out.println("Update do FileRequestHandler -> 2");
                return;
            }
            //System.out.println("Update do FileRequestHandler -> 3");
            Map<Integer, Object> parameters = tr.getParameters();
            Map<Integer, Object> results = tr.getResults();
            if ((parameters.get(TaskRequestsConstants.P_REQUESTER_ADDRESS) != null) && (results.get(TaskRequestsConstants.R_SEARCH_RESULTS) != null)
                    && List.class.isInstance(results.get(TaskRequestsConstants.R_SEARCH_RESULTS)))
            {
                //System.out.println("Update do FileRequestHandler -> 4");
                List srList = (List) results.get(TaskRequestsConstants.R_SEARCH_RESULTS);
                if (srList.isEmpty() || (!SearchResult.class.isInstance(srList.get(0))))
                {
                    //System.out.println("The list has " + srList.size() + " elements");
                    return;
                }
                SearchResult sr = (SearchResult) srList.get(0);

                NPA.sendFile(sr.getURI().toString(), (String) parameters.get(TaskRequestsConstants.P_REQUESTER_ADDRESS));
                //System.out.println("Message Sent?");
            }
        }
    }
}
