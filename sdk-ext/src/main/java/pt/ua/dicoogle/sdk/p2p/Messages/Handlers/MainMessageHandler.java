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

//import pt.ua.dicoogle.p2p.jgroups.sockets.MulticastSocketHandler;
import pt.ua.dicoogle.sdk.NetworkPluginAdapter;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.observables.ListObservable;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageI;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageType;

/**
 * Class that chooses which message handler will process the message received.
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class MainMessageHandler implements MessageHandler
{
    private NetworkPluginAdapter NPA;
    private ListObservable<SearchResult> results = null;

    public MainMessageHandler(NetworkPluginAdapter NPA)
    {
        this.NPA = NPA;
        results = NPA.getSearchResults();
    }

    public void handleMessage(MessageI message, String address)
    {
        MessageHandler handler = null;
        if (message.getType().equals(MessageType.QUERY))
        {
            if (address.equals(this.NPA.getLocalAddress()))
            {
                return;
            }
            handler = new QueryHandler(1000, this.NPA);
        }
        if (message.getType().equals(MessageType.QUERY_RESP))
        {
            handler = new QueryResponseHandler(this.NPA, this.results);
        }

        if (message.getType().equals(MessageType.FILE_REQ))
        {
            handler = new FileRequestHandler(this.NPA);
        }
        if (message.getType().equals(MessageType.FILE_RESP))
        {
            handler = new FileResponseHandler("received", this.NPA);
        }
        handler.handleMessage(message, address);
    }
}
