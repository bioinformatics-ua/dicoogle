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
package pt.ua.dicoogle.server;
import java.util.*;

import org.dcm4che2.data.UID;

/**
 *
 * @author Marco Pereira
 * @author Frederico Silva
 */
public class TransfersStorage {
    private boolean accepted;
    private boolean [] TS;
    
    /*  [0] ImplicitVRLittleEndian
     *  [1] ExplicitVRLittleEndian
     *  [2] DeflatedExplicitVRLittleEndian
     *  [3] ExplicitVRBigEndian
     *  [4] JPEGLossless
     *  [5] JPEGLSLossless
     *  [6] JPEGLosslessNonHierarchical14
     *  [7] JPEG2000LosslessOnly
     *  [8] JPEGBaseline1
     *  [9] JPEGExtended24
     * [10] JPEGLSLossyNearLossless
     * [11] JPEG2000
     * [12] RLELossless
     * [13] MPEG2
     */
    
    public static final Map<Integer, String> globalTransferMap;
    static {
        Map<Integer, String> aMap = new HashMap<>();
        aMap.put(0, "ImplicitVRLittleEndian");
        aMap.put(1, "ExplicitVRLittleEndian");
        aMap.put(2, "DeflatedExplicitVRLittleEndian");
        aMap.put(3, "ExplicitVRBigEndian");
        aMap.put(4, "JPEGLossless");
        aMap.put(5, "JPEGLSLossless");
        aMap.put(6, "JPEGLosslessNonHierarchical14");
        aMap.put(7, "JPEG2000LosslessOnly");
        aMap.put(8, "JPEGBaseline1");
        aMap.put(9, "JPEGExtended24");
        aMap.put(10, "JPEGLSLossyNearLossless");
        aMap.put(11, "JPEG2000");
        aMap.put(12, "RLELossless");
        aMap.put(13, "MPEG2");
        globalTransferMap = Collections.unmodifiableMap(aMap);
    }
    public TransfersStorage()
    {
        int i;
        accepted = false;
        TS = new boolean [14];
        for(i = 0; i< TS.length; i++)
        {
            TS[i] = false;
        }        
    }

    public void setAccepted(boolean status)
    {
        accepted = status;
    }
    
    public boolean getAccepted()
    {
        return accepted;
    }
    
    public int setTS(boolean [] status)
    {
        int i;
       
        if (status.length != 14)
        {
            return -1;
        }
        
        for(i=0; i<status.length; i++)
        {
            TS[i] = status[i];
        }
        
        return 0;
    }
    
    public int setTS(boolean status, int index)
    {
        int i;
       
        if(index < 0 || index > 13)
        {
            return -1;
        }     
        TS[index] = status;
        
        return 0;
    }
            
    public boolean[] getTS()
    {
        return TS;
    }
    
    public void setDefaultSettings()
    {
        TS[0] = true;
        TS[1] = true;
        TS[4] = true;
        TS[5] = true;
        TS[8] = true;
        accepted = true;
        
    }
    
    public String [] getVerboseTS()
    {
        int i, count =0;
        String [] return_value = null;
        for(i= 0; i<14; i++)
        {
            if(TS[i])
            {
                count++;
            }
        }
        if(count > 0)
        {
            i = 0;
            return_value = new String[count];
            if (TS[0])
            {
                return_value[i] = UID.ImplicitVRLittleEndian;
                i++;
            }
            if (TS[1])
            {
                return_value[i] = UID.ExplicitVRLittleEndian;
                i++;
            }
            if (TS[2])
            {
                return_value[i] = UID.DeflatedExplicitVRLittleEndian;
                i++;
            }
            if (TS[3])
            {
                return_value[i] = UID.ExplicitVRBigEndian;
                i++;
            }
            if (TS[4])
            {
                return_value[i] = UID.JPEGLossless;
                i++;
            }
            if (TS[5])
            {
                return_value[i] = UID.JPEGLSLossless;
                i++;
            }
            if (TS[6])
            {
                return_value[i] = UID.JPEGLosslessNonHierarchical14;
                i++;
            }
            if (TS[7])
            {
                return_value[i] = UID.JPEG2000LosslessOnly;
                i++;
            }
            if (TS[8])
            {
                return_value[i] = UID.JPEGBaseline1;
                i++;
            }
            if (TS[9])
            {
                return_value[i] = UID.JPEGExtended24;
                i++;
            }
            if (TS[10])
            {
                return_value[i] = UID.JPEGLSLossyNearLossless;
                i++;
            }
            if (TS[11])
            {
                return_value[i] = UID.JPEG2000;
                i++;
            }
            if (TS[12])
            {
                return_value[i] = UID.RLELossless;
                i++;
            }
            if (TS[13])
            {
                return_value[i] = UID.MPEG2;
                i++;
            }
        }        
        return return_value;        
    }

    public List<String> asList() {
        return Arrays.asList(this.getVerboseTS());
    }
}
