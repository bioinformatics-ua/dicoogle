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

import pt.ua.dicoogle.sdk.settings.ConfigurationHolder;
import net.xeoh.plugins.base.Plugin;

/**
 * <p>
 * This class represents a generic plugin. All we can say about this is that
 * it can be enabled or disabled and initialized.
 * </p>
 * <p>
 * Furthermore every plugin must have
 * a name, so we can address it and control it. No further assumptions are made here regarding
 * functionality, any other, more specific functionality must be defined as a class inheriting from
 * this one.
 * </p>
 * 
 * @author Frederico Valente
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */

public interface DicooglePlugin extends Plugin {
    
    /**
     * It returns the name of plugin. 
     * A plugin must have a name for ID, this method enforces that
     * @return String name of plugin
     */
    public abstract String getName();


    
    /**
     * allows us to enable/disable the plugin
     * of course, practical implementations can disregard these commands.
     * If you do that, however, you will burn in hell
     * @return boolean enable plugin
     */
    public abstract boolean enable();
    
    
    /**
     * Disables the plugin. It is responsabile to stop all the services that the 
     * plugin is running 
     * @return a boolean specifying whether the plugin was successfully disabled
     */
    public abstract boolean disable();
    
    
    /**
     * Verify if the plugin is enabled.
     * <b>Just does what it says, useful for graphical display/control</b>
     * @return boolean verify if the plugin is enabled
     */
    public abstract boolean isEnabled();

    /**
     * This method lets the plugin receive its settings
     * 
     * @param settings Settings this settings are the parameters that will be
     * used by the Plugin. It used to be serialized to the disk
     * 
     */
    public abstract void setSettings(ConfigurationHolder settings);
    
    /**
     * Access to the Settings of the Plugin
     * @return Settings plugin settings
     */
    public abstract ConfigurationHolder getSettings();    
}
