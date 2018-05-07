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


import pt.ua.dicoogle.sdk.datastructs.dim.Patient;
import pt.ua.dicoogle.sdk.datastructs.dim.Serie;
import pt.ua.dicoogle.sdk.datastructs.dim.Study;

/**
 *
 * Query Interface provides methods to query at different levels in DICOM Information Model.
 *
 * Created by bastiao on 02-02-2017.
 */
public interface QueryDimInterface extends QueryInterface {




    /**
     * Performs a search on the database.
     *
     * The consumer of the results would either request an iterator or use a for-each loop. The underlying
     * iterator implementation can be redefined to wait for more results at the caller. Furthermore, the
     * resulting iterable is expected to be traversed only once.
     *
     * @param query a string describing the query. The underlying plugin is currently free to follow any
     * query format, but only those based on Lucene with work with the search user interface.
     * @param parameters A variable list of parameters of the query. The plugin can use them to establish
     * their own API's, which may require more complex data structures (e.g. images).
     *
     * @return the results of the query as a (possibly lazy) iterable with <b>Patient</b>
     */
    public Iterable<Patient> queryPatient(String query, Object ... parameters);

    /**
     *
     * Performs a search on the database.
     *
     * @param query a string describing the query. The underlying plugin is currently free to follow any
     *      * query format
     * @param parameters
     * @return the results of the query as a (possibly lazy) iterable with <b>Study</b>
     */
    public Iterable<Study> queryStudy(String query, Object ... parameters);

    /**
     *
     * Performs a search on the database.
     *
     * @param query a string describing the query. The underlying plugin is currently free to follow any
     *      * query format, but only those based on Lucene with work with the search user interface.
     * @param parameters
     * @return the results of the query as a (possibly lazy) iterable with <b>Series</b>
     */
    public Iterable<Serie> querySeries(String query, Object ... parameters);
    
}
