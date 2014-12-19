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
package pt.ua.dicoogle.sdk.core;

/**
 * Plugin interface to communicate with core and other external plugins (loaded)
 *
 * 
 * When a plugin is loaded, if inherits from this interface,
 * the loader will call this method with a proxy to the core.
 * It is up to the plugin maintainer to store that proxy, and use it.
 * 
 * @author Fredeiro Valente
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @see DicooglePlatformInterface
 */
public interface PlatformCommunicatorInterface 
{
    void setPlatformProxy(DicooglePlatformInterface core);
}
