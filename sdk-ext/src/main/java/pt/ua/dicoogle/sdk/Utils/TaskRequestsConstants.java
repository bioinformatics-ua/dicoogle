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
package pt.ua.dicoogle.sdk.Utils;

/**
 *
 * @author Carlos Ferreira
 */
public class TaskRequestsConstants
{
    //Task Types
    public static final int T_QUERY_LOCALLY = 1;
    public static final int T_INDEX_FILE = 2;
    //public static final int T_INDEX_UPDATE = 3;
    public static final int T_LOGGER_MESSAGE_ALREADY_INDEXED = 4;
    public static final int T_RESET_LOCAL_INDEX = 5;
    public static final int T_LOCAL_DELETE_FILE = 6;
    public static final int T_QUERY_PRUNE = 7;
    public static final int T_BLOCK_SIGNAL = 8;
    
    //Task Parameters
    public static final int P_QUERY = 1;
    public static final int P_EXTRAFIELDS = 2;
    public static final int P_REQUESTER_ADDRESS = 3;
    public static final int P_QUERY_NUMBER = 4;
    public static final int P_FILE_PATH = 5;
    public static final int P_MESSAGE = 6;
    public static final int P_TO_REMOVE_FILE = 7;
    
    
    
    //Task Results
    public static final int R_SEARCH_RESULTS = 1;
}
