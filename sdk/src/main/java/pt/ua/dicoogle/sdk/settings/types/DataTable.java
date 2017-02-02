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
import org.apache.commons.lang3.StringEscapeUtils;
import pt.ua.dicoogle.sdk.settings.Utils;

/**
 * Handles a data (type) that is suposed to be on a table form.
 * Supports multiple fields per row, and allows row coppies of the first row to
 * be made and appended to the table. The user can also rows (except the first one).
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class DataTable implements GenericSetting
{
	/**
	 * Column names.
	 */
	private String[] columns;
	/**
	 * Cell data.
	 */
	private Object[][] data;

	/**
	 * Creates a DataTable with no columns nor cells.
	 */
	public DataTable()
	{
		columns = new String[0];
		data = new Object[0][0];
	}

	/**
	 * Creates a DataTable with (unamed) columnCount columns.
	 *
	 * @param columnCount the initial number of columns.
	 */
	public DataTable(int columnCount)
	{
		columns = new String[columnCount];
		data = new String[0][columnCount];
	}

	/**
	 * Creates a DataTable with (unamed) columnCount columns and (empty) rowCount rows.
	 *
	 * @param columnCount the initial number of columns.
	 * @param rowCount the initial number of rows.
	 */
	public DataTable(int columnCount, int rowCount)
	{
		columns = new String[columnCount];
		data = new Object[rowCount][columnCount];
	}

	/**
	 * Returns the column count on this DataTable.
	 *
	 * @return the column count on this DataTable.
	 */
	public synchronized int getColumnCount()
	{
		return columns.length;
	}

	/**
	 * Returns the row count on this DataTable.
	 *
	 * @return the row count on this DataTable.
	 */
	public synchronized int getRowCount()
	{
		return data.length;
	}

	/**
	 * Validates a column index for later use.
	 *
	 * @param index the index (zero based) of the target column.
	 */
	public synchronized boolean isValidColumnIndex(int index)
	{
		return ((index >= 0) && (index < columns.length));
	}

	/**
	 * Validates a cell index for later use.
	 *
	 * @param row the row index (zero based) of the target cell.
	 * @param column the column index (zero based) of the target cell.
	 */
	public synchronized boolean isValidCellIndex(int row, int column)
	{
		return (isValidColumnIndex(column) && ((row >= 0) && (row < data.length)));
	}

	/**
	 * Adds a new (unamed) column at the end of the column list.
	 */
	public synchronized void addColumn()
	{
		// create a bigger sized array
		String[] newColumns = new String[columns.length + 1];
		Object[][] newData = new Object[data.length][columns.length + 1];

		// copy all the previous data
		System.arraycopy(columns, 0, newColumns, 0, columns.length);
		for (int i = 0; i < data.length; i++)
			System.arraycopy(data[i], 0, newData[i], 0, columns.length);

		// set the new data as the current one
		columns = newColumns;
		data = newData;
	}

	/**
	 * Adds a new column at the end of the column list.
	 *
	 * @param name the name/title of the new column.
	 */
	public synchronized void addColumn(String name)
	{
		// create a bigger sized array
		String[] newColumns = new String[columns.length + 1];
		Object[][] newData = new Object[data.length][columns.length + 1];

		// copy all the previous data
		System.arraycopy(columns, 0, newColumns, 0, columns.length);
		for (int i = 0; i < data.length; i++)
			System.arraycopy(data[i], 0, newData[i], 0, columns.length);

		// add the new data
		newColumns[columns.length] = name;

		// set the new data as the current one
		columns = newColumns;
		data = newData;
	}

	/**
	 * Returns the selected column name to the desired one.
	 * Returns null if the the column index is not valid.
	 *
	 * @param column the index (zero based) of the target column.
	 * @return the selected column name to the desired one.
	 */
	public synchronized String getColumnName(int column)
	{
		if (! isValidColumnIndex(column))
			return null;

		return columns[column];
	}

	/**
	 * Changes the selected column name to the desired one.
	 * Gracefully aborts if the the column index is not valid.
	 *
	 * @param column the index (zero based) of the target column.
	 * @param name the desired new name for the column.
	 */
	public synchronized void setColumnName(int column, String name)
	{
		if (! isValidColumnIndex(column))
			return;

		columns[column] = name;
	}

	/**
	 * Adds a new (empty) row at the end of the table.
	 */
	public synchronized void addRow()
	{
		// create a bigger sized array
		Object[][] newData = new Object[data.length + 1][columns.length];

		// copy all the previous data
		for (int i = 0; i < data.length; i++)
			System.arraycopy(data[i], 0, newData[i], 0, columns.length);

		// set the new data as the current one
		data = newData;
	}

	/**
	 * Changes the selected cell data to the desired one.
	 * Gracefully aborts if the the cell location is not valid.
	 *
	 * @param row the row index (zero based) of the target cell.
	 * @param column the column index (zero based) of the target cell.
	 * @param data the new data for the target cell.
	 */
	public synchronized void setCellData(int row, int column, Object data)
	{
		if (! isValidCellIndex(row, column))
			return;

		this.data[row][column] = data;
	}

	/**
	 * Returns the selected cell data.
	 *
	 * @param row the row index (zero based) of the wanted cell.
	 * @param column the column index (zero based) of the wanted cell.
	 * @return null if the wanted cell is invalid, an otherwise Object.
	 */
	public synchronized Object getCellData(int row, int column)
	{
		if (! isValidCellIndex(row, column))
			return null;

		return this.data[row][column];
	}

	// Output formats ------------------------------------------------------

	@Override
	public synchronized String toHTMLString(String htmlElementID)
	{
		String result = "";

		result += "<div id=\"" + htmlElementID + "\" class=\"data-table\">";

		// loop through all the rows and add their cell data and column name to the result
		for (int row = 0; row < data.length; row++)
		{
			result += "<div class=\"data-table-row\">";

			for (int column = 0; column < columns.length; column++)
			{
				String columnID = Utils.getHTMLElementIDFromString(htmlElementID + " " + columns[column]);

				result += "<div style=\"margin-right: 6px; display: inline-block;\">";
				result +=	"<span>";
				result +=		StringEscapeUtils.escapeHtml4(columns[column]);
				result +=	"</span>";
				result +=	"<span style=\"padding-left: 4px;\">";
				result +=		Utils.getHTMLInputFromType(columnID, data[row][column], true);
				result +=	"</span>";
				result += "</div>";
			}

			// add a button to remove each element, except the first one
			result += 	"<button type=\"button\" class=\"btn btn-small btn-danger removeButton\" onclick=\"removeDataTableRow(this.parentNode.parentNode, this.parentNode);\" " + ((row == 0) ? "hidden style=\"display: none !important;\"" : "") + ">Remove</button>";

			result += "</div>";
		}

		result += "</div>";

		// add one button to add another element to the table
		result += "<button type=\"button\" class=\"btn btn-small btn-success\" onclick=\"addDataTableRow(document.getElementById('" + htmlElementID + "'));\">Add</button><br />"; // FIXME replace the getelementbyid with this.previoussibling ?!? http://www.w3schools.com/dom/prop_element_previoussibling.asp

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
	public DataTable fromHTTPParams(HashMap<String, String[]> params, int index, String htmlElementID)
	{
		// find how many rows the new params list has
		// NOTE see the FIXME bellow to understand the need for two counters
		int paramsCountMin = Integer.MAX_VALUE;
		int paramsCountMax = Integer.MIN_VALUE;
		for (String[] list : params.values()) // list all the params, and find the one that has the bigger and smaller amount of values
		{
			if (list.length < paramsCountMin)
				paramsCountMin = list.length;
			if (list.length > paramsCountMax)
				paramsCountMax = list.length;
		}

		// create a new DataTable with the same column size as the original one and the same row count as the new params
		DataTable result = new DataTable(this.getColumnCount(), paramsCountMax);

		// add all the columns titles/names
		for (int i = 0; i < result.getColumnCount(); i++)
			result.setColumnName(i, this.columns[i]);

		// add all the cells information available on the original DataTable
		for (int j = 0; j < result.getColumnCount(); j++)
		{
			String columnID = Utils.getHTMLElementIDFromString(htmlElementID + " " + result.columns[j]) + "[]";
			boolean hasNewValues = params.containsKey(columnID);

			for (int i = 0; i < result.getRowCount(); i++)
			{
				Object cellData = this.data[0][j];

				// if there is a new value for this cell then use it
				if (hasNewValues)
				{
					cellData = Utils.convertStringValueIntoProperType(cellData, params, i, columnID);
					// FIMXE if paramsCountMax is different of paramsCountMin them some column might not have all param values
					//	this is true if boolean column are used, but browsers don't usually report back the uncheked boxes
					//	so extra logic must be applied to fix this issue with boolean values
					//	the problem relies on how do we know in what row is the value missing?
				}

				result.setCellData(i, j, cellData);
			}
		}

		// return the newly formed DataTable
		return result;
	}
}
