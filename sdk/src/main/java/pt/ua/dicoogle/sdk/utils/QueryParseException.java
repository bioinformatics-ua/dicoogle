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
package pt.ua.dicoogle.sdk.utils;

/**
 * Thrown to indicate that the given query text and parameters could not be
 * properly interpreted as a query. This is made into its own class in order to
 * distinguish client-sided issues from server-sided issues.
 * 
 * When possible, it is recommended that the message argument provides an
 * explanation to why the query is not valid (e.g. "unexpected token
 * '}' at position 13").
 */
public class QueryParseException extends QueryException {
    private static final long serialVersionUID = 2082328278641651574L;

	public QueryParseException() {
        super();
    }

    public QueryParseException(String message) {
        super(message);
    }

    public QueryParseException(Exception cause) {
        super(cause);
    }

    public QueryParseException(String message, Exception cause) {
        super(message, cause);
    }
}