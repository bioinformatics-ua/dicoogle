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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.core.XMLSupport;
import pt.ua.dicoogle.server.TransfersStorage;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;


/**
*
* @author Frederico Silva<fredericosilva@ua.pt>
*/
public class TransferenceOptionsServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		XMLSupport xmlSupport = new XMLSupport();
		xmlSupport.printXML();
		TransfersStorage ts = xmlSupport.getLocalTS();

		resp.getWriter().print(TransferenceOptionsResponse.getJSON(ts.getTS()));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String option = req.getParameter("option");
		String valueS = req.getParameter("value");
		boolean value = Boolean.parseBoolean(req.getParameter("value"));

		XMLSupport xmlSupport = new XMLSupport();
		xmlSupport.printXML();
		TransfersStorage ts = xmlSupport.getLocalTS();

		int index = -1;
		for (int i = 0; i < TransfersStorage.globalTransferMap.size(); i++) {
			if (TransfersStorage.globalTransferMap.get(i).equals(option)) {
				index = i;
				break;
			}
		}
		// TODO: VERIFY INDEX
		ts.setTS(value, index);
		xmlSupport.printXML();

		ResponseUtil.simpleResponse(resp, "success", true);
	}

}

class TransferenceOptionsResponse {
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

	public static String getJSON(boolean[] tsList) {
		TransferenceOptionsResponse tor = new TransferenceOptionsResponse();
		for (int i = 0; i < tsList.length; i++) {
			tor.options.add(tor.new Option(TransfersStorage.globalTransferMap
					.get(i), tsList[i]));
		}

		Gson gson = new Gson();
		String json = gson.toJson(tor);

		return json;
	}

	class Option {
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
