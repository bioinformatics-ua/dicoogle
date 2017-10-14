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
 * Implements an Integer ranged setting.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class RangeInteger implements GenericSetting
{
	private int min;
	private int max;
	private int value;

	public RangeInteger(int min, int max)
	{
		this.min = min;
		this.max = max;
		this.value = min;
	}

	public RangeInteger(int min, int max, int value)
	{
		this.min = min;
		this.max = max;
		this.value = value;
	}

	/**
	 * @return the min
	 */
	public int getMin()
	{
		return min;
	}

	/**
	 * @return the max
	 */
	public int getMax()
	{
		return max;
	}

	/**
	 * @return the value
	 */
	public int getValue()
	{
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value)
	{
		this.value = value;
	}

	public String toHTMLString(String htmlElementID)
	{
		String result = "";

		result += "<input type=\"range\" id=\"" + htmlElementID + "\" name=\"" + htmlElementID + "\" value=\"" + value + "\" min=\"" + min + "\" max=\"" + max + "\" />";

		return result;
	}

	public RangeInteger fromHTTPParams(HashMap<String, String[]> params, int index, String htmlElementID)
	{
		RangeInteger result = new RangeInteger(this.min, this.max, this.value);

		result.setValue(Integer.parseInt(params.get(htmlElementID)[index]));

		return result;
	}
}
