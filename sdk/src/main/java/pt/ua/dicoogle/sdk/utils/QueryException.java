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
 * Thrown to indicate that a query attempt to a provider has failed.
 */
public class QueryException extends RuntimeException {
    private static final long serialVersionUID = 450205731642750093L;

	public QueryException() {
        super();
    }

    public QueryException(String message) {
        super(message);
    }

    public QueryException(Exception cause) {
        super(cause);
    }

    public QueryException(String message, Exception cause) {
        super(message, cause);
    }
}