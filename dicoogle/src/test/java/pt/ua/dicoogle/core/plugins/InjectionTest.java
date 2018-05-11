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
package pt.ua.dicoogle.core.plugins;

import org.junit.Test;
import pt.ua.dicoogle.plugins.PluginPreparer;
import pt.ua.dicoogle.sdk.DicooglePlugin;
import pt.ua.dicoogle.sdk.annotation.InjectPlatformProxy;
import pt.ua.dicoogle.sdk.core.DicooglePlatformInterface;
import pt.ua.dicoogle.sdk.settings.ConfigurationHolder;
import java.io.IOException;

import static org.junit.Assert.*;

public class InjectionTest {

    public static class MyPlugin implements DicooglePlugin {

        @InjectPlatformProxy
        private DicooglePlatformInterface platform;

        @Override
        public String getName() {
            return "MyPlugin";
        }

        @Override
        public boolean enable() {
            return true;
        }

        @Override
        public boolean disable() {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void setSettings(ConfigurationHolder settings) {
        }

        @Override
        public ConfigurationHolder getSettings() {
            return null;
        }

        public DicooglePlatformInterface getPlatform() {
            return platform;
        }
    }

    private final static DicooglePlatformInterface myproxy = new PlatformInterfaceMock();

    @Test
    public void test() throws IOException {
        MyPlugin myplugin = new MyPlugin();

        assertNull(myplugin.getPlatform());

        PluginPreparer prepare = new PluginPreparer(this.myproxy);

        prepare.setup(myplugin);

        assertEquals(this.myproxy, myplugin.getPlatform());
    }
}
