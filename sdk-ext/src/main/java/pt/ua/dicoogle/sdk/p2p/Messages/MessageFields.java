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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.dicoogle.sdk.p2p.Messages;

/**
 *
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class MessageFields
{
    /**
     * Root element of every xml messages
     */
    public static final String MESSAGE = "Message";

    /**
     * Definition of the message type
     */
    public static final String MESSAGE_TYPE = "MessageType";
    /**
     * The number of the query
     */
    public static final String QUERY_NUMBER = "QueryNumber";
    /**
     * The field where the query value is declared
     */
    public static final String QUERY = "Query";
    /**
     * The list of search results
     */
    public static final String SEARCH_RESULTS = "SearchResults";
    public static final String EXTRAFIELD = "Extrafield";

    /**
     * One result of the search.
     */
    public static final String SEARCH_RESULT = "SearchResult";

    public static final String FILE_REQUESTED = "FileRequested";
}
