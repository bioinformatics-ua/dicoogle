/*
 * Copyright (C) 2015 Eduardo Pinho <eduardopinho@ua.pt>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.plugins.webui;

/** An exception for describing an issue when reading a web UI plugin descriptor.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class PluginFormatException extends Exception {

    public PluginFormatException() {
    }

    public PluginFormatException(String string) {
        super(string);
    }

    public PluginFormatException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public PluginFormatException(Throwable thrwbl) {
        super(thrwbl);
    }
}
