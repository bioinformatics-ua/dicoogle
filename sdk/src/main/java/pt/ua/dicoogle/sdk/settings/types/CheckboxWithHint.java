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
 * Implements a regular checkbox with the addition of a mouse hint.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class CheckboxWithHint implements GenericSetting
{
	private String id;
	private boolean checked;
	private String text;
	private String hint;

	public CheckboxWithHint(String id, boolean checked, String text) {
		super();
		this.id = id;
		this.checked = checked;
		this.text = text;
	}

	public CheckboxWithHint(boolean checked, String text, String hint)
	{
		this.checked = checked;
		this.text = text;
		this.hint = hint;
	}

	/**
	 * @return the checked
	 */
	public boolean isChecked()
	{
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

	/**
	 * @return the text
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * @return the hint
	 */
	public String getHint()
	{
		return hint;
	}

	public String toHTMLString(String htmlElementID)
	{
		String result = "";

		result += "<label class=\"checkbox\" title=\"" + StringEscapeUtils.escapeHtml4(hint) + "\">";
		result +=	"<input type=\"checkbox\" id=\"" + htmlElementID + "\" name=\"" + htmlElementID + "\" " + (checked ? "checked=\"checked\"" : "") + " /> " + StringEscapeUtils.escapeHtml4(text);
		result += "</label>";

		return result;
	}

	public CheckboxWithHint fromHTTPParams(HashMap<String, String[]> params, int index, String htmlElementID)
	{
		CheckboxWithHint result = new CheckboxWithHint(this.checked, this.text, this.hint);

		result.setChecked(Utils.parseCheckBoxValue(params.get(htmlElementID), index));

		return result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
