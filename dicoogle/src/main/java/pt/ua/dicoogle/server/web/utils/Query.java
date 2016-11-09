/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.server.web.utils;

/**
 * Provides some functions for handling query builds easier.
 *
 * @author Antonio
 */
public class Query
{
	/**
	 * For an input query adds the extra param to it, correctly.
	 *
	 * @param query the "up-to-now" query that we want to addMoveDestination the extra param to.
	 * @param param the param to addMoveDestination to the query.
	 * @param operator the logical operator to addMoveDestination between params.
	 * @return the new query that has the new/extra param on it.
	 */
	public static String addExtraQueryParam(String query, String param, String operator)
	{
		String result = query;

		// check if this is not the only param in the query
		if (! result.isEmpty())
			result += " " + operator + " ";

		// addMoveDestination the extra param
		result += param;

		return result;
	}

	/**
	 * Same as the overloaded version, but the operator is AND.
	 *
	 * @param query the "up-to-now" query that we want to addMoveDestination the extra param to.
	 * @param param the param to addMoveDestination to the query.
	 * @return the new query that has the new/extra param on it.
	 */
	public static String addExtraQueryParam(String query, String param)
	{
		return addExtraQueryParam(query, param, "AND");
	}
}
