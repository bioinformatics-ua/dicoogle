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
package pt.ua.dicoogle.plugins.webui;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/** A POJO data type containing the full description of a Web UI plugin.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class WebUIPlugin implements Cloneable {
    private String name;
    private String description;
    private String version;
    private String slotId;
    private String slotType;
    private String moduleFile;
    private JSONObject settings;
    private boolean enabled = true;

    public WebUIPlugin() {}

    @Override
    protected WebUIPlugin clone() throws CloneNotSupportedException {
        return (WebUIPlugin)super.clone();
    }

    public WebUIPlugin copy() {
        try {
            return clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(WebUIPlugin.class.getName()).log(Level.SEVERE, null, ex);
            return new WebUIPlugin();
        }
    }
    
    public static WebUIPlugin fromPackageJSON(String jsonText) {
        JSONSerializer.toJSON(jsonText);
        WebUIPlugin plugin = new WebUIPlugin();
        plugin.setName(jsonText);
        return plugin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getSlotType() {
        return slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public String getModuleFile() {
        return moduleFile;
    }

    public void setModuleFile(String moduleFile) {
        this.moduleFile = moduleFile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSettings(JSONObject holder) {
        this.settings = holder;
    }
    
    /** Getter for the plugin's settings.
     * 
     * @return a JSON object containing the settings, empty object if no settings are stored
     */
    public JSONObject getSettings() {
        return this.settings != null ? this.settings : new JSONObject(false);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
