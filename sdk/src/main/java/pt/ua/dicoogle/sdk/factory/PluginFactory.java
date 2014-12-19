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
package pt.ua.dicoogle.sdk.factory;

import java.io.File;
import java.util.Collection;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import pt.ua.dicoogle.sdk.PluginSet;

/**
 * 
 * @author psytek
 */

public class PluginFactory {
    public static Collection<PluginSet> getPlugins(){
        File path = new File("Plugins");
        return PluginFactory.getPlugins(path);
    }
    
    public static Collection<PluginSet> getPlugins(File pluginDirectory){
        PluginManager pm = PluginManagerFactory.createPluginManager();
        //System.err.println(pluginDirectory.getAbsolutePath());
        pm.addPluginsFrom(pluginDirectory.toURI());
        PluginManagerUtil pmu = new PluginManagerUtil(pm);
        return pmu.getPlugins(PluginSet.class);
    }
}
