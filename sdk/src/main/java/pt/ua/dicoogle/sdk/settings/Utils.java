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
package pt.ua.dicoogle.sdk.settings;

import java.util.Map;
import java.util.HashMap;
import org.apache.commons.lang3.StringEscapeUtils;
import pt.ua.dicoogle.sdk.settings.types.GenericSetting;

/**
 * Helper for parsing form input values and creating HTML content based on them,
 * server side.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class Utils
{
	/**
	 * This converts and applies the new textual values into the originalSettings HashMap, taking into account their original class/type.
	 * Only the settings that exist on both Maps are updated, this avoid invalid settings and Class type injections from happening.
	 *
	 * @param originalSettings the original advanced/internal settings of a plugin or service.
	 * @param newSettings the new settings to apply.
	 * @return the originalSettings HashMap with the new values in place (you can use the reference passed as originalSettings instead if you want, they will allways be the same).
	 */
	public static HashMap<String, Object> processAdvancedSettings(HashMap<String, Object> originalSettings, HashMap<String, String[]> newSettings)
	{
		for (Map.Entry<String, Object> setting : originalSettings.entrySet())
		{
			String name = getHTMLElementIDFromString(setting.getKey()); // NOTE remmember that the setting name is "kinda encoded" (and not in the URLEncode way, that's handled automagically)
			Object value = setting.getValue();

			if (newSettings.containsKey(name)) // if the setting is found on the request try to update its value (maintaining the same type ofc)
			{
				String[] newValue = newSettings.get(name);

				if (value != null)
				{
					// parse the value depending on its class
					if (value.getClass().equals(Integer.class))
						value = Integer.valueOf(newValue[0]);
					else if (value.getClass().equals(Float.class))
						value = Float.valueOf(newValue[0]);
					else if (value.getClass().equals(Boolean.class))
						value = Boolean.valueOf(parseCheckBoxValue(newValue[0]));
					else if (value instanceof GenericSetting)
						value = ((GenericSetting) value).fromHTTPParams(newSettings, 0, name);
					else // String or unrecognized class
						value = newValue[0];

					setting.setValue(value);
				}
				else // null is treated as String
				{
					value = newValue[0];

					setting.setValue(value);
				}

			}
			else // if the setting was not found check if it's a Boolean one, this is a FIX because browsers omit unchecked checkboxes on form action
			{
				if (value.getClass().equals(Boolean.class))
					setting.setValue(new Boolean(false));
				else if (value instanceof GenericSetting)
				{
					value = ((GenericSetting) value).fromHTTPParams(newSettings, 0, name);
					setting.setValue(value);
				}
			}
		}

		return originalSettings; // just for easier use
	}

	/**
	 * Given the string value of a checkbox obtained from an http request,
	 * returns its boolean value.
	 *
	 * @param value the checkbox value obtained from an http request.
	 * @return the checkbox state in boolean form.
	 */
	public static boolean parseCheckBoxValue(String value)
	{
		// default state is not checked
		boolean result = false;

		// if the value is defined
		if ((value != null) && (! value.isEmpty()))
			result = value.equalsIgnoreCase("On"); // check if it matches the "on" string

		return result;
	}

	/**
	 * Given the string value of a checkbox obtained from an http request,
	 * returns its boolean value.
	 *
	 * @param values an array of Strings where the checkbox value obtained from an http request is.
	 * @param index the index on the array where the wanted checkbox value is.
	 * @return the checkbox state in boolean form.
	 */
	public static boolean parseCheckBoxValue(String[] values, int index)
	{
		// default state is not checked
		boolean result = false;

		// if the value is defined
		if ((values != null) && (values.length > index))
			result = parseCheckBoxValue(values[index]);

		return result;
	}

	/**
	 * Based on a input String, returns a valid HTML element name or ID for it.
	 * <b>NOTE:</b> be aware that repeated calls with the same input String
	 * will return the same result, so, on these cases name/ID collisions will happen!
	 *
	 * @param input the input String.
	 * @return a valid HTML element ID or name.
	 */
	public static String getHTMLElementIDFromString(String input)
	{
		if ((input == null) || input.trim().isEmpty())
			return input;

		input = input.trim();

		// the name must start with a letter, if it doesn't them add an "s" to it
		if (! input.substring(0, 1).matches("[A-Za-z]"))
			input = "s" + input;

		// these are the whitelisted (valid) chars, replace all the others with nothing
		return input.replaceAll("[^A-Za-z0-9-_]", "");
	}

	/**
	 * Based on the "real" type/class of the plugin setting Object returns the appropriate form input for it.
	 *
	 * @param name the Form Name of this HTML input.
	 * @param value a Object.
	 * @param isArrayElement if this value Object is part of a another multiple value Object.
	 * @return the appropriate form input for the supplied Object.
	 */
	public static String getHTMLInputFromType(String name, Object value, boolean isArrayElement)
	{
		String result = "<input name=\"" + name + (isArrayElement ? "[]" : "") + "\" ";

		if (value == null)
		{
			result += "type=\"text\" value=\"\" />";
		}
		else if (value.getClass().equals(Integer.class))
		{
			result += "type=\"number\" value=\"" + ((Integer) value).intValue() + "\" />";
		}
		else if (value.getClass().equals(Float.class))
		{
			result += "type=\"number\" value=\"" + ((Float) value).floatValue() + "\" />";
		}
		else if (value.getClass().equals(Boolean.class))
		{
			result += "type=\"checkbox\" " + (((Boolean) value).booleanValue() ? "checked=\"checked\"" : "") + " />";
		}
		else if (value instanceof GenericSetting)
		{
			result = ((GenericSetting) value).toHTMLString(name + (isArrayElement ? "[]" : ""));
		}
		else
		// NOTE add extra data type classes here, if needed
		if (value.getClass().equals(String.class))
		{
			result += "type=\"text\" value=\"" + StringEscapeUtils.escapeHtml4((String) value) + "\" />";
		}
		else // unrecognized type/class
		{
			//throw new ClassCastException("Unsupported class \"" + value.getClass().getName() + "\"");
			//result += "type=\"text\" value=\"" + StringEscapeUtils.escapeHtml4( value.toString()) + "\" />";
			result += StringEscapeUtils.escapeHtml4( value.toString());			
		}

		return result;
	}

	/**
	 * Based on the "real" type/class of the plugin setting Object returns the appropriate form input for it.
	 * This procedure espects the value Object to not be a part of a another multiple value Object.
	 *
	 * @param id the ID and Name of this HTML input.
	 * @param value a Object.
	 * @return the appropriate form input for the supplied Object.
	 */
	public static String getHTMLInputFromType(String id, Object value)
	{
		return getHTMLInputFromType(id, value, false);
	}

	/**
	 * Converts a String value into the type/class of originalValue.
	 *
	 * @param originalValue the original/target type/class of the value.
	 * @param newValue the new value in String form.
	 * @return a new Object of the same class as orignalValue but it's value updated to newValue.
	 */
	public static Object convertStringValueIntoProperType(Object originalValue, HashMap<String, String[]> params, int index, String htmlElementID)
	{
		Object result = null;

		if (originalValue == null)
		{
			result = null;
		}
		else if (originalValue.getClass().equals(Integer.class))
		{
			String[] values = params.get(htmlElementID);
			if ((values == null) || (values.length <= index) || (params.get(htmlElementID)[index] == null) || (params.get(htmlElementID)[index].isEmpty()))
				result = 0;
			else
				result = Integer.valueOf(params.get(htmlElementID)[index]);
		}
		else if (originalValue.getClass().equals(Float.class))
		{
			String[] values = params.get(htmlElementID);
			if ((values == null) || (values.length <= index) || (params.get(htmlElementID)[index] == null) || (params.get(htmlElementID)[index].isEmpty()))
				result = 0.0F;
			else
				result = Float.valueOf(params.get(htmlElementID)[index]);
		}
		else if (originalValue.getClass().equals(Boolean.class))
		{
			String[] values = params.get(htmlElementID);
			if ((values == null) || (values.length <= index) || (params.get(htmlElementID)[index] == null) || (params.get(htmlElementID)[index].isEmpty()))
				result = false;
			else
				result = Boolean.valueOf(params.get(htmlElementID)[index]);
		}
		else if (originalValue instanceof GenericSetting)
		{
			result = ((GenericSetting) originalValue).fromHTTPParams(params, index, htmlElementID);
		}
		else
		// NOTE add extra data type classes here, if needed
		if (originalValue.getClass().equals(String.class))
		{
			result = params.get(htmlElementID)[index];
		}
		else // unrecognized type/class
		{
			//throw new ClassCastException("Unsupported class \"" + value.getClass().getName() + "\"");
			result = originalValue;
		}

		return result;
	}
}
