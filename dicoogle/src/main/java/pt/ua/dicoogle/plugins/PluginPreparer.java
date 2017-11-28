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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.sdk.DicooglePlugin;
import pt.ua.dicoogle.sdk.PluginSet;
import pt.ua.dicoogle.sdk.annotation.InjectPlatformProxy;
import pt.ua.dicoogle.sdk.core.DicooglePlatformInterface;
import pt.ua.dicoogle.sdk.core.PlatformCommunicatorInterface;

import java.lang.reflect.Field;
import java.util.List;
import org.apache.commons.lang3.reflect.FieldUtils;

/** Class type which prepares plugins for use.
 */
public class PluginPreparer {
    private static final Logger logger = LoggerFactory.getLogger(PluginPreparer.class);

    private final DicooglePlatformInterface platform;

    public PluginPreparer(DicooglePlatformInterface platform) {
        this.platform = platform;
    }

    /** Prepares a plugin for use. Currently, it only injects the platform proxy. */
    public <P extends DicooglePlugin> void setup(P plugin) {
        this.injectPlatform(plugin);
    }

    /** Prepares a plugin set for use. Currently, it only injects the platform proxy to the set object. */
    public void setup(PluginSet pluginSet) {
        this.injectPlatform(pluginSet);
    }

    /** Inject the Dicoogle platform interface proxy to the given object. This is attempted in two ways, inclusively:
     *
     * 1. If the object contains at least one non-static field annotated with {@link InjectPlatformProxy}, the proxy is assigned to each annotated field.
     * 2. If the object implements the interface {@link PlatformCommunicatorInterface}, the method
     * {@link PlatformCommunicatorInterface#setPlatformProxy(DicooglePlatformInterface)} is called.
     *
     * @param o the object to inject the platform proxy to
     */
    public void injectPlatform(Object o) {
        // inject platform with annotations
        logger.debug("Looking for annotations");
        Field[] fields = FieldUtils.getFieldsWithAnnotation(o.getClass(), InjectPlatformProxy.class);
        for (Field f: fields) {
            try {
                boolean a = f.isAccessible();
                f.setAccessible(true);
                f.set(o, this.platform);
                f.setAccessible(a);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        // inject with PlatformCommunicatorInterface
        if (o instanceof PlatformCommunicatorInterface) {
            ((PlatformCommunicatorInterface) o).setPlatformProxy(this.platform);
        }
    }
}
