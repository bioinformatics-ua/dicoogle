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
import javax.servlet.http.HttpServletResponse;

/** Since CrossOriginFilter doesn't work, this is a custom implementation of the same thing.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class CORSFilter implements Filter {
    
    public static String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    public static String ACCESS_CONTROL_ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";
    public static String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";

    public static String ALLOWED_ORIGINS_PARAM = "allowedOrigins";
    public static String ALLOWED_HEADERS_PARAM = "allowedHeaders";
    public static String ALLOWED_METHODS_PARAM = "allowedMethods";

    private String allowedOrigins;
    private String allowedHeaders;
    private String allowedMethods;

    @Override
    public void init(FilterConfig fc) throws ServletException {
        allowedOrigins = fc.getInitParameter(ALLOWED_ORIGINS_PARAM);
        allowedHeaders = fc.getInitParameter(ALLOWED_HEADERS_PARAM);
        allowedMethods = fc.getInitParameter(ALLOWED_METHODS_PARAM);
        if (allowedMethods == null) {
            allowedMethods = "GET,POST,HEAD";
        }
        if (allowedHeaders == null) {
            allowedHeaders = "X-Requested-With,Content-Type,Accept,Origin";
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
            resp.addHeader(ACCESS_CONTROL_ALLOW_METHODS_HEADER, allowedMethods);
        }
        fc.doFilter(sreq, sresp);
    }

    @Override
    public void destroy() {
    }

}
