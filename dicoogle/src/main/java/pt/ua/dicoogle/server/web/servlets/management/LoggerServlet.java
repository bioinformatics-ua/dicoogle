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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONSerializer;
import pt.ua.dicoogle.sdk.Utils.Platform;
import pt.ua.dicoogle.server.FileWatcher;

/**
 *
 * @author Frederico Silva<fredericosilva@ua.pt>
 */
public class LoggerServlet extends HttpServlet {
	private final String logfilename = Platform.homePath() + "DICOMLOG.log";
	private String serverLog;

	public LoggerServlet() {
		TimerTask task = new FileWatcher(new File(logfilename)) {

			@Override
			protected void onChange(File file) {
				reloadtext(file);
			}
		};

		java.util.Timer timer = new java.util.Timer();
		// repeat the check every second
		timer.schedule(task, new Date(), 5000);

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.addHeader("Access-Control-Allow-Origin", "*");

		// resp.getWriter().write(JSONSerializer.toJSON(LogDICOM.getInstance().getLl()).toString());
		resp.getWriter().write(serverLog);
	}

	public void reloadtext(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			int x = fis.available();
			byte b[] = new byte[x];
			fis.read(b);
			String content = new String(b);
			if (!content.equals(serverLog)) {
				serverLog = content;

			}
		} catch (IOException ex) {
			//TODO
		}
	}

}
