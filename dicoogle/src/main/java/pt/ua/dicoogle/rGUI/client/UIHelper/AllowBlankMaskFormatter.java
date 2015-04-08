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

/**
 * Getted from
 * http://www.javalobby.org/java/forums/t48652.html
 */

package pt.ua.dicoogle.rGUI.client.UIHelper;

import java.text.ParseException;

import javax.swing.text.MaskFormatter;

/**
 * A special version of the {@link javax.swing.text.MaskFormatter} for
 * {@link javax.swing.JFormattedTextField formatted text fields} that supports
 * the field being emptied/left blank.
 *
 * @author R.J. Lorimer
 * @author Luis Silva
 */
@Deprecated
public class AllowBlankMaskFormatter extends MaskFormatter {

	private boolean allowBlankField = true;
	private String blankRepresentation;

	public AllowBlankMaskFormatter() {
		super();
	}
	public AllowBlankMaskFormatter(String mask) throws ParseException {
		super(mask);
	}

	public void setAllowBlankField(boolean allowBlankField) {
		this.allowBlankField = allowBlankField;
	}

	public boolean isAllowBlankField() {
		return allowBlankField;
	}

	/**
	 * Update our blank representation whenever the mask is updated.
	 */
	@Override public void setMask(String mask) throws ParseException {
		super.setMask(mask);
		updateBlankRepresentation();
	}

	/**
	 * Update our blank representation whenever the mask is updated.
	 */
	@Override public void setPlaceholderCharacter(char placeholder) {
		super.setPlaceholderCharacter(placeholder);
		updateBlankRepresentation();
	}

	/**
	 * Override the stringToValue method to check the blank representation.
	 */
	@Override public Object stringToValue(String value) throws ParseException {
		Object result = value;
		if(isAllowBlankField() && blankRepresentation != null && blankRepresentation.equals(value)) {
			// an empty field should have a 'null' value.
			result = null;
		}
		else {
			result = super.stringToValue(value);
		}
		return result;
	}

	private void updateBlankRepresentation() {
		try {
			// calling valueToString on the parent class with a null attribute will get the 'blank'
			// representation.
			blankRepresentation = valueToString(null);
		}
		catch(ParseException e) {
			blankRepresentation = null;
		}
	}
}
