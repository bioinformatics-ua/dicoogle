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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang3.StringUtils;

import pt.ua.dicoogle.core.settings.ServerSettings;

/**
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class DicomQuerySettingsServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServerSettings ss = ServerSettings.getInstance();

		int responseTimeout = ss.getRspDelay();
		int connectionTimeout = ss.getConnectionTimeout();
		int idleTimeout = ss.getIdleTimeout();
		int acceptTimeout = ss.getAcceptTimeout();
		int maxPduSend = ss.getMaxPDULenghtSend();
		int maxPduReceive = ss.getMaxPDULengthReceive();
		int maxAssociations = ss.getMaxClientAssoc();

		QueryRetrieveSettingsObject queryRetrieveSettings = new QueryRetrieveSettingsObject(
				responseTimeout, connectionTimeout, idleTimeout, acceptTimeout,
				maxPduSend, maxPduReceive, maxAssociations);
        	resp.setContentType("application/json");
		resp.getWriter().write(JSONSerializer.toJSON(queryRetrieveSettings).toString());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String responseTimeout = req.getParameter("responseTimeout");
		String connectionTimeout = req.getParameter("connectionTimeout");
		String idleTimeout = req.getParameter("idleTimeout");
		String acceptTimeout = req.getParameter("acceptTimeout");
		String maxPduSend = req.getParameter("maxPduSend");
		String maxPduReceive = req.getParameter("maxPduReceive");
		String maxAssociations = req.getParameter("maxAssociations");

		ServerSettings ss = ServerSettings.getInstance();

		if (StringUtils.isNotEmpty(responseTimeout)) {
			int intV = Integer.parseInt(responseTimeout);
			ss.setRspDelay(intV);
		}
		if (StringUtils.isNotEmpty(connectionTimeout)) {
			int intV = Integer.parseInt(connectionTimeout);
			ss.setConnectionTimeout(intV);
		}
		if (StringUtils.isNotEmpty(idleTimeout)) {
			int intV = Integer.parseInt(idleTimeout);
			ss.setIdleTimeout(intV);
		}
		if (StringUtils.isNotEmpty(acceptTimeout)) {
			int intV = Integer.parseInt(acceptTimeout);
			ss.setAcceptTimeout(intV);
		}
		if (StringUtils.isNotEmpty(maxPduSend)) {
			int intV = Integer.parseInt(maxPduSend);
			ss.setMaxPDULengthSend(intV);
		}
		if (StringUtils.isNotEmpty(maxPduReceive)) {
			int intV = Integer.parseInt(maxPduReceive);
			ss.setMaxPDULengthReceive(intV);
		}
		if (StringUtils.isNotEmpty(maxAssociations)) {
			int intV = Integer.parseInt(maxAssociations);
			ss.setMaxClientAssoc(intV);
		}

	}

	/*
	 * MODEL FOR QUERY RETRIEVE SETTINGS
	 */
	public static class QueryRetrieveSettingsObject {
        private int responseTimeout, connectionTimeout, idleTimeout, acceptTimeout,
				maxPduSend, maxPduReceive, maxAssociations;

        public QueryRetrieveSettingsObject() {
        }
        
		public QueryRetrieveSettingsObject(int responseTimeout,
				int connectionTimeout, int idleTimeout, int acceptTimeout,
				int maxPduSend, int maxPduReceive, int maxAssociations) {
			super();
			this.responseTimeout = responseTimeout;
			this.connectionTimeout = connectionTimeout;
			this.idleTimeout = idleTimeout;
			this.acceptTimeout = acceptTimeout;
			this.maxPduSend = maxPduSend;
			this.maxPduReceive = maxPduReceive;
			this.maxAssociations = maxAssociations;
		}

        public int getResponseTimeout() {
            return responseTimeout;
        }

        public void setResponseTimeout(int responseTimeout) {
            this.responseTimeout = responseTimeout;
        }

        public int getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public int getIdleTimeout() {
            return idleTimeout;
        }

        public void setIdleTimeout(int idleTimeout) {
            this.idleTimeout = idleTimeout;
        }

        public int getAcceptTimeout() {
            return acceptTimeout;
        }

        public void setAcceptTimeout(int acceptTimeout) {
            this.acceptTimeout = acceptTimeout;
        }

        public int getMaxPduSend() {
            return maxPduSend;
        }

        public void setMaxPduSend(int maxPduSend) {
            this.maxPduSend = maxPduSend;
        }

        public int getMaxPduReceive() {
            return maxPduReceive;
        }

        public void setMaxPduReceive(int maxPduReceive) {
            this.maxPduReceive = maxPduReceive;
        }

        public int getMaxAssociations() {
            return maxAssociations;
        }

        public void setMaxAssociations(int maxAssociations) {
            this.maxAssociations = maxAssociations;
        }

	}
}
