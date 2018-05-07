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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class PathUtils {

    /**
     * Splits a request's path by "/", ignoring empty tokens.
     *
     * @param req the request
     * @return the list of tokens from the split path
     */
    public static List<String> sanitizedSubpathParts(HttpServletRequest req) {
        String subpath = req.getRequestURI().substring(req.getServletPath().length());

        String[] subpathParts = subpath.split("/");

        List<String> l = new ArrayList<>();
        for (String s : subpathParts) {
            if (!s.isEmpty()) {
                l.add(s);
            }
        }
        return l;
    }
}
