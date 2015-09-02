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
package pt.ua.dicoogle.server.web.servlets.management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.RandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.sdk.Utils.Platform;

/**
 * @author Frederico Silva <fredericosilva@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class LoggerServlet extends HttpServlet {
    private static final Logger classLogger = LoggerFactory.getLogger(LoggerServlet.class);

    private String logFilename = null;
    
    protected String logFilename() {
        if (this.logFilename == null) {
            org.apache.logging.log4j.Logger logger = LogManager.getLogger();
            Map<String, Appender> appenderMap = ((org.apache.logging.log4j.core.Logger) logger).getAppenders();
            for (Map.Entry<String,Appender> e : appenderMap.entrySet()) {
                String filename = null;
                Appender appender = e.getValue();
                if (appender instanceof FileAppender) {
                    filename = ((FileAppender)appender).getFileName();
                } else if (appender instanceof RollingFileAppender) {
                    filename = ((RollingFileAppender)appender).getFileName();
                } else if (appender instanceof RandomAccessFileAppender) {
                    filename = ((RandomAccessFileAppender)appender).getFileName();
                } else if (appender instanceof RollingRandomAccessFileAppender) {
                    filename = ((RollingRandomAccessFileAppender)appender).getFileName();
                }
                if (filename != null) {
                    classLogger.debug("Using \"{}\" as the file for the server log.", filename);
                    this.logFilename = filename;
                    return this.logFilename;
                }
            }
            // no file appender found, use default DICOMLOG.log
            classLogger.debug("No file appender found, using \"DICOMLOG.log\" as the default logger");
            this.logFilename = Platform.homePath() + "DICOMLOG.log";
        }
        return this.logFilename;
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try (BufferedReader fis = new BufferedReader(new FileReader(logFilename()))) {
            PrintWriter respWriter = resp.getWriter();
            String l;
            while ((l = fis.readLine()) != null) {
                respWriter.println(l);
            }
        }
	}
}
