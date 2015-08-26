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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

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
import pt.ua.dicoogle.server.FileWatcher;

/**
 * @author Frederico Silva <fredericosilva@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class LoggerServlet extends HttpServlet {
    private static final Logger classLogger = LoggerFactory.getLogger(LoggerServlet.class);
            
	private final String logfilename;
	private String serverLog;

	public LoggerServlet() {
        this.logfilename = getLogFilename();
        this.serverLog = "";
        File f = new File(logfilename);
        reloadtext(f);
		TimerTask task = new FileWatcher(f) {

			@Override
			protected void onChange(File file) {
				reloadtext(file);
			}
		};

		java.util.Timer timer = new java.util.Timer();
		// repeat the check every second
		timer.schedule(task, new Date(), 5000);
	}
    
    private static String getLogFilename() {
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
                return filename;
            }
        }
        // no file appender found, use default DICOMLOG.log
        classLogger.debug("No file appender found, using \"DICOMLOG.log\" as the default logger");
        return Platform.homePath() + "DICOMLOG.log";
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// resp.getWriter().write(JSONSerializer.toJSON(LogDICOM.getInstance().getLl()).toString());
		resp.getWriter().write(serverLog);
	}

	public final void reloadtext(File file) {
        // TODO this method could be optimized by seeking the file
        // to the line last read and only read the new lines
		try (BufferedReader fis = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String l;
            while ((l = fis.readLine()) != null) {
                content.append(l);
                content.append('\n');
            }
            serverLog = content.toString();
		} catch (IOException ex) {
			//TODO
            classLogger.warn("Failed to read log file, logger content not updated", ex);
		}
	}

}
