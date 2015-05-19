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

package pt.ua.dicoogle.sdk.datastructs;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import pt.ua.dicoogle.sdk.utils.Multirator;

/**
 *
 * @author Frederico Valente <fmvalente@ua.pt>
 */
public class QueryReport  extends Report implements Iterable<SearchResult> {

    public static final QueryReport EmptyReport = new QueryReport(Collections.EMPTY_LIST);        
    
    ArrayList<Iterable<SearchResult>> elementSources = new ArrayList<>();    
    
    public QueryReport(){}
    public QueryReport(Iterable<SearchResult> results){this.elementSources.add(results);}
    public QueryReport(Collection<SearchResult> results){this.elementSources.add(results);}

    @Override
    public Iterator<SearchResult> iterator() {return new Multirator<>(elementSources);}

    @Override
    public String toString(){
        String retString = "";
            
        int i=0;
        for(SearchResult searchResult:this){
            retString += searchResult.toString();
            retString += "\n";
            i++;
        }
        retString += "Obtained "+i+" results...";
        return retString;
    }
    
    /**
     *
     * @return
     */
    @Override
    public Iterable<SearchResult> results(){
        return this;
    }
}
