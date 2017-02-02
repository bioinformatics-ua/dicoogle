/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk.
 *
 * Dicoogle/dicoogle-sdk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.sdk.settings.types;

import java.util.HashMap;

/**
 * Handles a data (type) that is suposed to contain a directory path located on the server.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class ServerDirectoryPath implements GenericSetting
{
	private String path;

	public ServerDirectoryPath()
	{
		this.path = null;
	}

	public ServerDirectoryPath(String path)
	{
		this.path = path;
	}

	/**
	 * @return the path
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	// Output formats ------------------------------------------------------

	public synchronized String toHTMLString(String htmlElementID)
	{
		String result = "";

		result += "<select id=\"" + htmlElementID + "ds\" size=\"5\" class=\"span5\" style=\"display: none;\">";
		result += "</select>";
		result += "<div class=\"input-append\">";
			result += "<input class=\"span4\" type=\"text\" id=\"" + htmlElementID + "\" name=\"" + htmlElementID + "\" value=\"" + path + "\" />";
			result += "<button type=\"button\" class=\"btn\" onclick=\"showDirList(document.getElementById('" + htmlElementID + "ds'), document.getElementById('" + htmlElementID + "'), this);\">";
				result += "<i class=\"icon-folder-open\"></i> Browse...";
			result += "</button>";
		result += "</div>";

		return result;
	}

	// Input formats -------------------------------------------------------

	/**
	 * Based on a list of http request form (names & values) and a original value object,
	 * parse the first ones to match the second ones format with the new values.
	 *
	 * @param original the original DataTable.
	 * @param params the new params, and their values, to put into the DataTable.
	 * @param index the param index where the value is.
	 * @return a new DataTable with the updated cell information.
	 */
	public ServerDirectoryPath fromHTTPParams(HashMap<String, String[]> params, int index, String htmlElementID)
	{
		// create a new DataTable with the same column size as the original one and the same row count as the new params
		ServerDirectoryPath result = new ServerDirectoryPath(this.path);

		result.setPath(params.get(htmlElementID)[index]);

		// return the newly formed DataTable
		return result;
	}
}
