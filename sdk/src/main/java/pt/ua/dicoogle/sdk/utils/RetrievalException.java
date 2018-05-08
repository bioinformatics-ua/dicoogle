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
 * Thrown to indicate that an error occurred during the retrieval of search
 * results from a query provider. Unlike {@link QueryException}, this
 * exception only emerges after the initial query method is successful, and
 * will usually be thrown upon consumption of the search result iterator.
 */
public class RetrievalException extends RuntimeException {
    private static final long serialVersionUID = -1186512939075390164L;

	public RetrievalException() {
        super();
    }

    public RetrievalException(String message) {
        super(message);
    }

    public RetrievalException(Exception cause) {
        super(cause);
    }

    public RetrievalException(String message, Exception cause) {
        super(message, cause);
    }
}