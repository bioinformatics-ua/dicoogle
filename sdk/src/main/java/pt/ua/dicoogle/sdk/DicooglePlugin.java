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

/** Class interface representing a generic plugin.
 * <p>
 * These plugins can have an initialization process, and can be enabled or disabled by the system.
 * Furthermore, every plugin must have a name, so that Dicoogle can address it and control it. No further
 * assumptions are made here regarding functionality. Any other more specific functionality must be defined
 * as a class inheriting from this one.</p>
 * 
 * @author Frederico Valente
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public interface DicooglePlugin extends Plugin {
    
    /**
     * Obtains the unique name of plugin. 
     * A plugin must have a name to serve as an ID, and this is the method from where it is retrieved. This
     * name must never change.
     * 
     * @return the name of the plugin
     */
    public String getName();
    
    /**
     * Issues the plugin to become enabled. It is expected that an enabled plugin, once configured, is ready
     * to perform its main tasks.
     * 
     * @return whether the plugin was successfully enabled
     */
    public boolean enable();
    
    /**
     * Issues the plugin to become disabled. When called, the plugin is responsible
     * for stopping all services that the plugin is running. 
     * @return whether the plugin was successfully disabled
     */
    public boolean disable();
    
    
    /**
     * Verifies if the plugin is enabled.
     *
     * @return whether the plugin is enabled
     */
    public boolean isEnabled();

    /** Sets the settings of this plugin.
     * This method lets the plugin receive its settings from the core Dicoogle system.
     * 
     * @param settings the parameters that will be used by the plugin
     */
    public void setSettings(ConfigurationHolder settings);
    
    /**
     * Obtains access to the settings of the plugin.
     * @return the plugin's settings
     */
    public ConfigurationHolder getSettings();
}
