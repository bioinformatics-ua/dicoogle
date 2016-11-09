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
package pt.ua.dicoogle.server.web.utils.types;

import java.util.Arrays;
import java.util.HashMap;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import pt.ua.dicoogle.sdk.settings.Utils;

/**
 * Handles a data (type) that is suposed to be on a table form.
 * Supports multiple fields, in a String format.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class DataTable
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

		// addMoveDestination the new data
		newColumns[columns.length] = name;

		// set the new data as the current one
		columns = newColumns;
		data = newData;
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

	// Output formats ------------------------------------------------------

	public synchronized String toHTMLString(String htmlElementID)
	{
		String result = "";

		result += "<div id=\"" + htmlElementID + "\">";

		// loop through all the rows and addMoveDestination their cell data and column name to the result
		for (int row = 0; row < data.length; row++)
		{
			result += "<div class=\"data-table-row\">";

			for (int column = 0; column < columns.length; column++)
			{
				String columnID = Utils.getHTMLElementIDFromString(columns[column]);

				result += escapeHtml4(columns[column]) + " " + Utils.getHTMLInputFromType(columnID, data[row][column], true);

				result += "&nbsp;&nbsp;&nbsp;";
			}

			// addMoveDestination a button to remove each element, except the first one
			result += 	"<button type=\"button\" class=\"removeButton\" onclick=\"removeDataTableRow(this.parentNode.parentNode, this.parentNode);\" " + ((row == 0) ? "hidden" : "") + ">Remove</button>";

			result += "</div>";
		}

		result += "</div>";

		// addMoveDestination one button to addMoveDestination another element to the table
		result += "<button type=\"button\" onclick=\"addDataTableRow(document.getElementById('" + htmlElementID + "'));\">Add</button><br />"; // FIXME replace the getelementbyid with this.previoussibling ?!? http://www.w3schools.com/dom/prop_element_previoussibling.asp

		return result;
	}
	
	

	// Input formats -------------------------------------------------------

	@Override
	public String toString() {
		return toHTMLString("PID---");
				}

	/**
	 * Based on a list of http request form (names & values) and a original value object,
	 * parse the first ones to match the second ones format with the new values.
	 *
	 * @param original the original DataTable.
	 * @param params the new params, and their values, to put into the DataTable.
	 * @return a new DataTable with the updated cell information.
	 */
	public static DataTable fromHTTPParams(DataTable original, HashMap<String, String[]> params)
	{
		// find how many rows the new params have list has
		int paramsCountMin = Integer.MAX_VALUE;
		int paramsCountMax = Integer.MIN_VALUE;
		for (String[] list : params.values())
		{
			if (list.length < paramsCountMin)
				paramsCountMin = list.length;
			if (list.length > paramsCountMax)
				paramsCountMax = list.length;
		}

		// create a new DataTable with the same column size as the original one and the same row count as the new params
		DataTable result = new DataTable(original.getColumnCount(), paramsCountMax);

		// addMoveDestination all the columns titles/names
		for (int i = 0; i < result.getColumnCount(); i++)
			result.setColumnName(i, original.columns[i]);

		// addMoveDestination all the cells information present on the original DataTable
		for (int j = 0; j < result.getColumnCount(); j++)
		{
			String columnID = Utils.getHTMLElementIDFromString(result.columns[j]);
			boolean hasNewValues = params.containsKey(columnID);

			for (int i = 0; i < result.getRowCount(); i++)
			{
				Object cellData = original.data[i][j];

				// if there is a new value for this cell then use it
				if (hasNewValues)
				{
					cellData = Utils.convertStringValueIntoProperType(cellData, params, i, columnID);
					// FIMXE if paramsCountMax is different of paramsCountMin them some column might not have all param values
					//	this is true if boolean column are used, but browsers don't usually report back the uncheked boxes
					//	so extra logic must be applied to fix this issue with boolean values
					//	the problem relies on how do we know in what row is the value missing?
				}

				result.setCellData(j, j, cellData);
			}
		}

		// return the newly formed DataTable
		return result;
	}
}
