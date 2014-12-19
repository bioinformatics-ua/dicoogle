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
/*
 * DICOM Objects filter
 */

package pt.ua.dicoogle.server.queryretrieve;

import java.util.Iterator;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;

/**
 *
 * @author Joaoffr  <joaoffr@ua.pt>
 * @author DavidP   <davidp@ua.pt>
 *
 * @version $Revision: 002 $ $Date: 2008-11-22 14:25:00  $
 * @since Nov 21, 2008
 *
 */
public class Filter {

     public static boolean applyFilter(DicomObject keyObj, DicomObject sourceObj)
     {
        DicomObject kObj = keyObj;
        DicomObject sObj = sourceObj;

        Iterator<DicomElement> itKeys = kObj.datasetIterator();

        DicomElement dcmElmt;
        int tag;

        //DebugManager.getInstance().debug("Appling a filter to DCMObj");

        for (; itKeys.hasNext();  ) {
            dcmElmt = (DicomElement) itKeys.next();

            if (dcmElmt.isEmpty()) {
                continue;
            }

            if (dcmElmt.hasDicomObjects()) {
                if (dcmElmt.getDicomObject() == null) continue;

                if (!applyFilter(dcmElmt.getDicomObject(), sObj)) {
                    return false;
                }
                continue;
            }

             tag = dcmElmt.tag();

             if ( sObj.get(tag)!= null &&
                  kObj.get(tag)!= null &&
                  !(byte2string(sObj.get(tag).getBytes()).trim().equals(byte2string(dcmElmt.getBytes()).trim()))
                 ) {
                    //System.out.println("-->" + byte2string(sObj.get(tag).getBytes()) );
                    return false;
             }
        }

        return true;
    }


     private static String byte2string(byte[] in) {
        String out = "";

        for (int i = 0; i < in.length; i++) {
            out += (char)in[i];
        }

        return out;
    }
}
