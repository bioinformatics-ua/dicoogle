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

package pt.ua.dicoogle.sdk;

import org.restlet.Restlet;

/** The plugin interface for installing Restlet components.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public interface RestletInterface {
    
    /** Access the restlet instance of this plugin.
     * @return the single Restlet contained in this plugin
     */
    public Restlet getRestlet();

    /** Retrieve the relative path where this restlet is to be attached to.
     * 
     * @return a URI string for the relative path (e.g. <tt>"/myplugin"</tt>)
     */
    public String getPath();
}
