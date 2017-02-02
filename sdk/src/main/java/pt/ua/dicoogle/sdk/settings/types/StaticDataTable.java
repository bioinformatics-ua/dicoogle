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
import pt.ua.dicoogle.sdk.settings.Utils;

/**
 * Handles a data (type) that is suposed to be on a table form.
 * Supports multiple fields per row.
 * The contents of this table cannot be modified by the end user on the HTML interface.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class StaticDataTable extends DataTable
{
	/**
	 * Creates a DataTable with no columns nor cells.
	 */
	public StaticDataTable()
	{
		super();
	}

	/**
	 * Creates a DataTable with (unamed) columnCount columns.
	 *
	 * @param columnCount the initial number of columns.
	 */
	public StaticDataTable(int columnCount)
	{
		super(columnCount);
	}

	/**
	 * Creates a DataTable with (unamed) columnCount columns and (empty) rowCount rows.
	 *
	 * @param columnCount the initial number of columns.
	 * @param rowCount the initial number of rows.
	 */
	public StaticDataTable(int columnCount, int rowCount)
	{
		super(columnCount, rowCount);
	}

	// Output formats ------------------------------------------------------

	@Override
	public synchronized String toHTMLString(String htmlElementID)
	{
		String result = "";

		result += "<div id=\"" + htmlElementID + "\" class=\"data-table\">";

		// loop through all the rows and add their cell data and column name to the result
		for (int row = 0; row < this.getRowCount(); row++)
		{
			result += "<div class=\"data-table-row\">";

			for (int column = 0; column < this.getColumnCount(); column++)
			{
				String cellID = Utils.getHTMLElementIDFromString(htmlElementID + this.getColumnName(column) + row);

				result +=		Utils.getHTMLInputFromType(cellID, this.getCellData(row, column), false);
			}

			result += "</div>";
		}

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
	 * @return a new DataTable with the updated cell information.
	 */
	@Override
	public StaticDataTable fromHTTPParams(HashMap<String, String[]> params, int index, String htmlElementID)
	{
		// create a new DataTable with the same column size as the original one and the same row count as the new params
		StaticDataTable result = new StaticDataTable(this.getColumnCount(), this.getRowCount());

		// add all the columns titles/names
		for (int i = 0; i < result.getColumnCount(); i++)
			result.setColumnName(i, this.getColumnName(i));

		// add all the cells information available on the original DataTable
		for (int column = 0; column < result.getColumnCount(); column++)
		{
			for (int row = 0; row < result.getRowCount(); row++)
			{
				String cellID = Utils.getHTMLElementIDFromString(htmlElementID + result.getColumnName(column)) + row;
				Object cellData = this.getCellData(row, column);

				cellData = Utils.convertStringValueIntoProperType(cellData, params, 0, cellID);

				result.setCellData(row, column, cellData);
			}
		}

		// return the newly formed DataTable
		return result;
	}
}
