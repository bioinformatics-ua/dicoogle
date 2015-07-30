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
package pt.ua.dicoogle.sdk.settings;

import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


/**
 *
 * @author tiago
 */
public class ConfigurationHolder {
    private XMLConfiguration cnf;
    
    public ConfigurationHolder(File settingsFile) throws ConfigurationException{        
        this(settingsFile, true);
    }
    
    public ConfigurationHolder(File settingsFile, boolean autosave) throws ConfigurationException{        

    	if(!settingsFile.exists()){
    		cnf = new XMLConfiguration();
    		cnf.setFile(settingsFile);
    		cnf.save();
    		cnf.setAutoSave(true);
    	}else{
    		cnf = new XMLConfiguration(settingsFile);
	        cnf.setAutoSave(autosave); 
	    }
    }

	public XMLConfiguration getConfiguration() {
		return cnf;
	}    
}
