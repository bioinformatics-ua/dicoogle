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

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Since CrossOriginFilter doesn't work, this is a custom implementation of the same thing.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class CORSFilter implements Filter {
    
    public static String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    public static String ACCESS_CONTROL_ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";
    public static String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";
    public static String ACCESS_CONTROL_ALLOW_AUTHORIZATION_HEADER = "Authorization";

    public static String ALLOWED_ORIGINS_PARAM = "allowedOrigins";
    public static String ALLOWED_HEADERS_PARAM = "allowedHeaders";
    public static String ALLOWED_METHODS_PARAM = "allowedMethods";
    public static String ALLOWED_AUTHORIZATION_PARAM = "allowedAuthorization";

    private String allowedOrigins;
    private String allowedHeaders;
    private String allowedMethods;
    private String allowedAuthorization;

    @Override
    public void init(FilterConfig fc) throws ServletException {
        allowedOrigins = fc.getInitParameter(ALLOWED_ORIGINS_PARAM);
        allowedHeaders = fc.getInitParameter(ALLOWED_HEADERS_PARAM);
        allowedMethods = fc.getInitParameter(ALLOWED_METHODS_PARAM);
        allowedMethods = fc.getInitParameter(ALLOWED_METHODS_PARAM);
        allowedAuthorization = fc.getInitParameter(ALLOWED_AUTHORIZATION_PARAM);
        if (allowedMethods == null) {
            allowedMethods = "GET,POST,HEAD";
        }
        if (allowedHeaders == null) {
            allowedHeaders = "X-Requested-With,Content-Type,Accept,Origin,Authorization,Content-Length";
        }

    }

    @Override
    public void doFilter(ServletRequest sreq, ServletResponse sresp, FilterChain fc) throws IOException, ServletException {
        if (sresp instanceof HttpServletResponse) {
            HttpServletResponse resp = (HttpServletResponse) sresp;
            if (allowedOrigins != null) {
                resp.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, allowedOrigins);
            }
            if (allowedHeaders!= null) {
                resp.addHeader(ACCESS_CONTROL_ALLOW_HEADERS_HEADER, allowedHeaders);
            }
            if (allowedAuthorization!=null)
                resp.addHeader(ACCESS_CONTROL_ALLOW_AUTHORIZATION_HEADER, allowedAuthorization);
            resp.addHeader(ACCESS_CONTROL_ALLOW_METHODS_HEADER, allowedMethods);
        }
        if (sreq instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest)sreq;
            if (req.getMethod().equalsIgnoreCase("OPTIONS")) {
                return;
            }
        }
        fc.doFilter(sreq, sresp);
    }

    @Override
    public void destroy() {
    }

}
