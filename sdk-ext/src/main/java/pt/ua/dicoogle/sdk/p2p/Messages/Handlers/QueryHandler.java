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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import org.slf4j.LoggerFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.ua.dicoogle.sdk.NetworkPluginAdapter;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.Utils.TaskRequestsConstants;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.p2p.Messages.Builders.MessageBuilder;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageFields;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageI;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageXML;

/**
 *
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class QueryHandler implements MessageHandler, Observer
{

    private int maxResultsPerMessage;
    //private ListObservable<TaskRequest> tasks;
    private NetworkPluginAdapter plugin;

    public QueryHandler(int maxResultsPerMessage, NetworkPluginAdapter plugin)
    {
        this.plugin = plugin;
        this.maxResultsPerMessage = maxResultsPerMessage;
    }

    public void handleMessage(MessageI message, String sender)
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

        /**
         * Reading the message XML, getting the necessary fields.
         */
        Element root = document.getRootElement();
        String query;
        Element queryNumber = root.element(MessageFields.QUERY_NUMBER);
        List<String> extra = new ArrayList();
        Element queryE = root.element(MessageFields.QUERY);
        /**
         * If the message has not the query element, then it is an invalid message
         * ignore it.
         */
        if (queryE == null)
        {
            return;
        }
        query = queryE.getText();
        List<Element> extrafields = root.elements(MessageFields.EXTRAFIELD);
        if (extrafields == null)
        {
            extrafields = new ArrayList();
        }
        for (Element extrafield : extrafields)
        {
            extra.add(extrafield.getText());
        }

        /**
         * Local search.
         */
        Map<Integer, Object> parameters = new HashMap<Integer, Object>();
        parameters.put(TaskRequestsConstants.P_QUERY, query);
        parameters.put(TaskRequestsConstants.P_EXTRAFIELDS, extra);
        parameters.put(TaskRequestsConstants.P_REQUESTER_ADDRESS, sender);
        parameters.put(TaskRequestsConstants.P_QUERY_NUMBER, queryNumber.getText());

        TaskRequest task = new TaskRequest(TaskRequestsConstants.T_QUERY_LOCALLY,
                this.plugin.getName(), parameters);
        task.addObserver(this);

        this.plugin.getTaskRequestsList().addTask(task);


        // ArrayList<SearchResult> resultsAll = IndexEngine.getInstance().search(query, extra);
        //
    }

    public void update(Observable o, Object arg)
    {

        if (!TaskRequest.class.isInstance(o))
        {
            return;
        }

        TaskRequest task = (TaskRequest) o;


        Map<Integer, Object> res = task.getResults();
        Object SR = res.get(TaskRequestsConstants.R_SEARCH_RESULTS);
        if ((SR == null) || (!ArrayList.class.isInstance(SR)))
        {
            return;
        }

        ArrayList<SearchResult> resultsAll = (ArrayList<SearchResult>) SR;
        List<SearchResult> results;
        /**
         * Build of the response for the query
         */
        MessageBuilder builder = new MessageBuilder();
        MessageI newMessage = null;
        for (int i = 0; i < resultsAll.size(); i += maxResultsPerMessage)
        {
            int size = maxResultsPerMessage;
            if (i + maxResultsPerMessage > resultsAll.size())
            {
                size = resultsAll.size() - i;
            }

            results = resultsAll.subList(i, i + size);
            try
            {
                newMessage = builder.buildQueryResponse(results, (String) task.getParameters().get(TaskRequestsConstants.P_QUERY_NUMBER), this.plugin.getName());
            } catch (IOException ex)
            {
                LoggerFactory.getLogger(QueryHandler.class).error(ex.getMessage(), ex);
            }

            /**
             * Sending the response
             */
            if (newMessage != null)
            {
                
                this.plugin.send(newMessage, (String) task.getParameters().get(TaskRequestsConstants.P_REQUESTER_ADDRESS));
            }
        }
    }
}
