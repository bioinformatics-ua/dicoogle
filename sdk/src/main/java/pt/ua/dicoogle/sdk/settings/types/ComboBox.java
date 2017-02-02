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
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Implements a combobox, multiple elements, with different values, may be added.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class ComboBox implements GenericSetting
{
	private HashMap<String, String> elements;
	private String current;

	public ComboBox()
	{
		elements = new LinkedHashMap<String, String>();
		current = "";
	}

	public synchronized void addElement(String name, String value)
	{
		elements.put(name, value);
	}

	public synchronized void setCurrent(String name)
	{
		current = name;
	}

	public String getCurrent()
	{
		return current;
	}

	/**
	 * Returns the value of the currently selected element.
	 *
	 * @return the value of the currently selected element, null if the
	 * element is invalid or no element is currently selected.
	 */
	public String getCurrentValue()
	{
		for (Map.Entry<String, String> elem : elements.entrySet())
		{
			if (current.equalsIgnoreCase(elem.getKey()))
				return elem.getValue();
		}

		return null;
	}

	public void setCurrentByValue(String value)
	{
		if (value == null)
			current = "";

		for (Map.Entry<String, String> elem : elements.entrySet())
		{
			if (value.equalsIgnoreCase(elem.getValue()))
			{
				current = elem.getKey();
				return;
			}
		}

		// if value not found, no change is made
	}

	public String toHTMLString(String htmlElementID)
	{
		String result = "";

		result += "<select id=\"" + htmlElementID + "\" name=\"" + htmlElementID + "\">";
		for (Map.Entry<String, String> elem : elements.entrySet())
		{
			result += "<option value=\"" + StringEscapeUtils.escapeHtml4(elem.getValue()) + "\" "+ ((current.equalsIgnoreCase(elem.getKey())) ? "selected=\"selected\"" : "" ) + ">";
			result += StringEscapeUtils.escapeHtml4(elem.getKey());
			result += "</option>";
		}
		result += "</select>";

		return result;
	}

	@Override
	public synchronized Object clone()
	{
		ComboBox result = new ComboBox();

		result.current = this.current;
		result.elements.putAll(this.elements);

		return result;
	}

	public ComboBox fromHTTPParams(HashMap<String, String[]> params, int index, String htmlElementID)
	{
		ComboBox result = (ComboBox) this.clone();

		String[] values = params.get(htmlElementID);
		if ((values != null) && (values.length > index))
			result.setCurrentByValue(values[index]);

		return result;
	}
}
