/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk-ext.
 *
 * Dicoogle/dicoogle-sdk-ext is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk-ext is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.sdk.index;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class DicomByteArrField implements IDicomField
{
 
    private String name;
    private byte [] value;
    

    public DicomByteArrField(String name, byte [] value)
    {
        this.name = name;
        this.value = value;
        
    }
    
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public  byte [] getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(byte [] value) {
        this.value = value;
    }

}
