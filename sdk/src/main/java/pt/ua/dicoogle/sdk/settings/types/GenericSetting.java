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
 * A generic data/setting type/class.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public interface GenericSetting
{
	// Output formats ------------------------------------------------------

	/**
	 * Outputs the current setting onto an HTML formated string, that can be
	 * embeded onto a webpage.
	 *
	 * @param htmlElementID the ID name of the this HTML element.
	 * @return an HTML formated string, that can be embeded onto a webpage.
	 */
	public String toHTMLString(String htmlElementID);

	// Input formats -------------------------------------------------------

	/**
	 * Based on a list of HTTP request form (names & values) and the original
	 * value object (of same type/class), parse the later to match the former
	 * format with the new values. The original setting will be the object where
	 * this function gets called at.
	 *
	 * @param params the new params, and their values, to put into the return object.
	 * @param index the param index where the value is.
	 * @return a new GenericSetting with the updated information.
	 */
	public GenericSetting fromHTTPParams(HashMap<String, String[]> params, int index, String htmlElementID);
}
