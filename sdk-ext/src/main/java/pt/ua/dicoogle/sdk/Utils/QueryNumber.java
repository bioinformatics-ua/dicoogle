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
 * Class to provides a unique ID to a query.
 * This class solves the problem, caused when the user search for two different things
 * without receive every responses of the other peers. There is only one query active at a time.
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class QueryNumber
{
    private static Integer queryNumber;
    private static QueryNumber instance = null;

    public static QueryNumber getInstance()
    {
        if (instance == null)
        {
            instance = new QueryNumber();
        }
        return instance;
    }

    private QueryNumber()
    {
        queryNumber = new Integer(0);
    }

    public Integer getNewQueryNumber()
    {
        return ++queryNumber;
    }

    public Integer getQueryNumber()
    {
        return queryNumber;
    }
}
