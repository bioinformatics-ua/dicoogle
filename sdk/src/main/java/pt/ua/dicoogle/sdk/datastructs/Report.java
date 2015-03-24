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

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author psytek
 * 
 * This interface is meant to return data from the tasks
 * since a single task (such as indexing) may handle multiple sub tasks
 * we may need a particularly complex return type (which will be inheriting from this class)
 * TODO: actually enforce some restrictions or functionality, otherwise we may as well use Object
 */
public class Report {
    ArrayList<Report> childs = new ArrayList<>();
       
    
    public void addChild(Report r){childs.add(r);}
    public Iterable<Report> childs(){return childs;}
    
    
    public static ErrorReport error(String error, Exception ex) {
        return new ErrorReport(error,ex);        
    }

    
    public Iterable<SearchResult> results(){return Collections.EMPTY_LIST;}
    public Iterable<ErrorReport> errors(){return Collections.EMPTY_LIST;}
 }
