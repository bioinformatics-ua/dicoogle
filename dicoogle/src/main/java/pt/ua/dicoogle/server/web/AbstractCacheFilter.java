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
/**
 */

package pt.ua.dicoogle.server.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/** A filter that can deal with non-static content caching.
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public abstract class AbstractCacheFilter implements Filter {
    
    public static String CACHE_CONTROL_HEADER = "Cache-Control";
    public static String IF_NONE_MATCH_HEADER = "If-None-Match";
    public static String ETAG_HEADER = "ETag";

    public static String CACHE_CONTROL_PARAM = "cacheControl";

    private String cacheControl;

    @Override
    public void init(FilterConfig fc) throws ServletException {
        this.cacheControl = fc.getInitParameter(CACHE_CONTROL_PARAM);
    }

    @Override
    public void doFilter(ServletRequest sreq, ServletResponse sresp, FilterChain fc) throws IOException, ServletException {
        if (sresp instanceof HttpServletResponse && sreq instanceof HttpServletRequest) {
            HttpServletResponse resp = (HttpServletResponse) sresp;
            HttpServletRequest req = (HttpServletRequest)sreq;

            if (cacheControl != null) {
                resp.addHeader(CACHE_CONTROL_HEADER, cacheControl);
            }
            String ifNoneMatchParam = req.getHeader(IF_NONE_MATCH_HEADER);
            String thisETag = this.etag(req);
            if (thisETag != null) {
                resp.setHeader(ETAG_HEADER, thisETag);
                if (ifNoneMatchParam != null) {
                    List<String> matches = Arrays.asList(ifNoneMatchParam.split(" *, *"));
                    if (matches.contains(thisETag)) {
                        // intercept request, send 304
                        resp.setStatus(304);
                        return;
                    }
                }
            }
        }
        fc.doFilter(sreq, sresp);
    }

    protected abstract String etag(HttpServletRequest req);

    @Override
    public void destroy() {
    }
}
