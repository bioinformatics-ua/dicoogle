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
package pt.ua.dicoogle.server.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import pt.ua.dicoogle.server.web.dicom.Search;

/**
 * Handles the requests for DICOM file tags information, returning the information as a XML document.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class SearchServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		Search search = new Search(request);

		if (! search.hasQuery()) // make sure that its a valid search query
		{
			response.sendError(400, "No search query supplied!");
			return;
		}

		// get the XML document containing the search results
		String xml ="";

		// if no xml was retrieved, tell the client
		if (xml == null)
		{
			response.sendError(500, "Could generate the resulting XML document!");
			return;
		}

		// get the returned xml as a UTF-8 byte array
		byte[] data = xml.getBytes("UTF-8");
		if (data == null)
		{
			response.sendError(500, "Could generate the resulting XML document!");
			return;
		}

		response.setContentType("application/xml"); // set the appropriate type for the XML file
		response.setContentLength(data.length); // set the document size
		//response.setCharacterEncoding("UTF-8"); // set the apropriate encoding type

		// write the XML data to the response output
		ServletOutputStream out = response.getOutputStream();
		out.write(data);
		out.close();
	}
}
