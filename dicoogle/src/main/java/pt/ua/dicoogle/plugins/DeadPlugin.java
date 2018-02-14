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

import java.util.Objects;

/** Data type containing information about a dead plugin. Plugins are considered dead when they perform
 * some kind of irrecoverable misbehaviour, such as throwing exceptions during the configuration phase.
 * Information about the plugin set's name and the exception that "killed" the plugin are kept here.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public final class DeadPlugin {
    private final String name;
    private final Exception cause;

    public DeadPlugin(String name, Exception cause) {
        this.name = name;
        this.cause = cause;
    }

    public String getName() {
        return name;
    }

    public Exception getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return "DeadPlugin{" +
                "name='" + name + '\'' +
                ", cause=" + cause +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeadPlugin that = (DeadPlugin) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(cause, that.cause);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cause);
    }
}
