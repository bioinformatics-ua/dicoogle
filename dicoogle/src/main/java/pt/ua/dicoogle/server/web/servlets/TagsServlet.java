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
package pt.ua.dicoogle.server.web.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletOutputStream;
import pt.ua.dicoogle.server.web.dicom.Information;

/**
 * Handles the requests for DICOM file tags information, returning the information as a XML document.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class TagsServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String sopInstanceUID = request.getParameter("SOPInstanceUID");
		if (sopInstanceUID == null || sopInstanceUID.trim().isEmpty()) // make sure that the SOPInstanceUID param is supplied
		{
			response.sendError(400, "Invalid SOP Instance UID!");
			return;
		}
        
        String[] providerArray = request.getParameterValues("provider");
        List<String> providers = providerArray == null ? null : Arrays.asList(providerArray);

		// get the XML document containing the tags for that SOP Instance UID file
		String xml = Information.getXMLTagListFromFile(sopInstanceUID, providers);

		// if no xml was retrieved, tell the client
		if (xml == null)
		{
			response.sendError(404, "SOP Instance UID not indexed/found!");
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
