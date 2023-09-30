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

package pt.ua.dicoogle.webui;

import org.junit.Test;

import net.sf.json.JSONObject;
import pt.ua.dicoogle.plugins.webui.PluginFormatException;
import pt.ua.dicoogle.plugins.webui.WebUIPlugin;
import pt.ua.dicoogle.plugins.webui.WebUIPluginManager;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

public class WebUIPluginTest {

    /**
     * Test that the Web UI plugin manager
     * can successfully load test plugin 1.
     * @throws PluginFormatException
     * @throws IOException
     */
    @Test
    public void testLoadPlugin1() throws IOException, PluginFormatException {
        WebUIPluginManager webui = new WebUIPluginManager();

        String test1Path =
                WebUIPluginTest.class.getClassLoader().getResource("pt/ua/dicoogle/webui/WebPlugins/test1").getFile();
        webui.load(new File(test1Path));

        WebUIPlugin plugin = webui.get("test1");
        assertNotNull(plugin);
        assertEquals("test1", plugin.getName());
        assertEquals("menu", plugin.getSlotId());
        assertEquals("Test Plugin 1", plugin.getCaption());
        assertEquals("module.js", plugin.getModuleFile());

        // retrieve package.json as JSON object
        JSONObject packageJson = webui.retrieveJSON("test1");

        // check that the package.json object contains the correct values
        assertEquals("test1", packageJson.get("name"));
        JSONObject dicoogleJson = packageJson.getJSONObject("dicoogle");
        assertEquals("menu", dicoogleJson.get("slot-id"));
        assertEquals("Test Plugin 1", dicoogleJson.get("caption"));
        assertEquals("module.js", dicoogleJson.get("module-file"));

        // also retrieve module.js
        String moduleJs = webui.retrieveModuleJS("test1");
        assertFalse("should not be empty", moduleJs.isEmpty());
    }
}
