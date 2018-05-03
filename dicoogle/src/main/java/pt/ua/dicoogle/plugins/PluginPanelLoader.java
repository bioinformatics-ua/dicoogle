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
package pt.ua.dicoogle.plugins;

/**
 * Dummie Class Loader, only to get the Class Name in a simple way
 * @author Carlos Ferreira
 */
public class PluginPanelLoader extends ClassLoader
{

    private static PluginPanelLoader instance = null;

    private PluginPanelLoader(ClassLoader parent)
    {
        super(parent);
    }

    private PluginPanelLoader()
    {
        super(getSystemClassLoader());
    }

    private static synchronized PluginPanelLoader getInstance()
    {
        if (instance == null)
        {
            instance = new PluginPanelLoader();
        }
        return instance;
    }

    private static synchronized PluginPanelLoader getInstance(ClassLoader cl)
    {
        if (instance == null)
        {
            instance = new PluginPanelLoader(cl);
        }
        return instance;
    }

   /* @Override
    public Class<?> loadClass(String name)
    {
        try
        {
            IPluginControllerAdmin plugins = AdminRefs.getSettings().getPluginController();
            HashMap<String, byte[]> panelClasses = plugins.getPanelClasses();
            Set<String> keys = panelClasses.keySet();
            for (String key : keys)
            {
                byte[] pp = panelClasses.get(key);

                ClassLoader cl = PluginPanel.class.getClassLoader();
                Class c = defineClass(pp);

                return c;
            }
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(PluginPanelLoader.class).error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve)
    {
        try
        {
            IPluginControllerAdmin plugins = AdminRefs.getSettings().getPluginController();
            HashMap<String, byte[]> panelClasses = plugins.getPanelClasses();
            Set<String> keys = panelClasses.keySet();
            for (String key : keys)
            {
                byte[] pp = panelClasses.get(key);

                //ClassLoader cl = PluginPanel.class.getClassLoader();
                Class c = defineClass(pp);

                return c;
            }
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(PluginPanelLoader.class).error(ex.getMessage(), ex);
        }
        return null;
    }*/

    public Class defineClass(byte[] b)
    {
        Class c = defineClass(null, b, 0, b.length);
        //System.out.println(c.getName().substring(0, c.getName().lastIndexOf('.')));
        //super.definePackage(c.getPackage().getName(), c.getPackage().getSpecificationTitle(), c.getPackage().getSpecificationVersion(), c.getPackage().getSpecificationVendor(),
        //        c.getPackage().getImplementationTitle(), c.getPackage().getImplementationVersion(), c.getPackage().getImplementationVendor(), null);
        super.resolveClass(c);
        //System.out.println(c.getClassLoader().toString());
        return c;
        /*     try
        {
        this.loadClass(c.getName());
        System.out.println(c.getName());
        } catch (ClassNotFoundException ex)
        {
        LoggerFactory.getLogger(ServerOptions.class).error(ex.getMessage(), ex);
        }*/
    }
}
