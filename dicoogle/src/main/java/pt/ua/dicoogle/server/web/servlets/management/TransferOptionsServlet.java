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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONSerializer;
import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.server.SOPList;
import pt.ua.dicoogle.server.TransfersStorage;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;


/**
*
* @author Frederico Silva<fredericosilva@ua.pt>
*/
public class TransferOptionsServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uid = req.getParameter("uid");
		
        resp.setContentType("application/json");
		String soplist = SOPList.getInstance().getSOPList();
		resp.getWriter().write(soplist);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String UID = req.getParameter("uid");
		String option = req.getParameter("option");
		String valueS = req.getParameter("value");
		boolean value = Boolean.parseBoolean(req.getParameter("value"));

		SOPList.getInstance().updateTSField(UID, option, value);
        ServerSettingsManager.saveSettings();
		ResponseUtil.simpleResponse(resp, "success", true);
	}

    public static class TransferenceOptionsResponse {
        List<Option> options;

        public TransferenceOptionsResponse() {
            options = new ArrayList<>();

        }

        public List<Option> getOptions() {
            return options;
        }

        public void setOptions(List<Option> options) {
            this.options = options;
        }

        public static TransferenceOptionsResponse fromBooleanList(boolean[] tsList) {
            TransferenceOptionsResponse tor = new TransferenceOptionsResponse();
            for (int i = 0; i < tsList.length; i++) {
                tor.options.add(new Option(
                        TransfersStorage.globalTransferMap.get(i), tsList[i]));
            }
            return tor;
        }
        
        public static String getJSON(boolean[] tsList) {
            TransferenceOptionsResponse tor = fromBooleanList(tsList);
            return JSONSerializer.toJSON(tor).toString();
        }

        public static class Option {
            String name;
            boolean value;

            public Option(String name, boolean value) {
                this.name = name;
                this.value = value;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public boolean isValue() {
                return value;
            }

            public void setValue(boolean value) {
                this.value = value;
            }
        }
    }
}

