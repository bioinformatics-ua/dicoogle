/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk.
 *
 * Dicoogle/dicoogle-sdk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.sdk;

import pt.ua.dicoogle.sdk.datastructs.QueryReport;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;

/**
 *  Base class for all plugins able to handle queries
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author fmvalente
 */

public interface QueryInterface extends DicooglePlugin 
{
    
    /**
     * Search in the database
     * 
     * @param query Query string based on Lucene
     * @param parameters Object parameters of the query. The plugin can use 
     * the generic parameters to communicate between each other.
     * 
     * The consumer of the results will use Iterator (which a QueryReport is) and get the results until
     * has next. The iterator can be redefined, and wait until results.
     * 
     * @return Iterator returns the results.
     */
    public QueryReport query(String query, Object ... parameters) ;
}
