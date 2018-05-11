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
package pt.ua.dicoogle.sdk.utils;

import java.util.Iterator;
import org.apache.commons.lang3.math.NumberUtils;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.PersonName;
import org.dcm4che2.data.SpecificCharacterSet;
import org.dcm4che2.data.VR;

import pt.ua.dicoogle.sdk.utils.DICOMObject.Path;

/**
 * Provides two static methods to convert dcm4che's DicomObject representation into regular java Objects.
 * 
 * Very inspired in dcm4che3 dcm2json implementation.
 * 
 * @author Tiago Marques Godinho, tmgodinho@ua.pt
 * @author Luís A. Bastião Silva <bastiao@bmd-software.com>
 */
public class DicomObjectFactory {

  private static TagsStruct tagStruct = TagsStruct.getInstance();
  private static boolean cache = false;

  public static MappedDICOMObject toMap(DicomObject obj) {
    if(obj == null)
      return null;
    
    return toMap(obj, Path.NULL_PATH);
  }
  
  public static MappedDICOMObject toMap(DicomObject obj, Path parent) {
    if(obj == null)
      return null;
    
    MappedDICOMObject map = new MappedDICOMObject(parent);
    
    SpecificCharacterSet charSet = obj.getSpecificCharacterSet();
    Iterator<DicomElement> it = obj.iterator();
    while(it.hasNext()){
      DicomElement e = it.next();
      Object o = toObject(parent , e, charSet);     
      TagValue tag = tagStruct.getTagValue(e.tag());
      String t = (tag != null) ? tag.getAlias() : Integer.toString(e.tag());
      map.put(t, o);
    }
    return map;
  }
  
  public static Object toObject(Path parentPath, DicomElement elem, SpecificCharacterSet cs) {
    VR vr = elem.vr();
    Object ret = null;
    
    if (vr == VR.AE || vr == VR.AE || vr == VR.AS || vr == VR.AT || vr == VR.CS || vr == VR.DA
        || vr == VR.DS || vr == VR.DT || vr == VR.IS || vr == VR.LO || vr == VR.LT || vr == VR.PN
        || vr == VR.SH || vr == VR.ST || vr == VR.TM || vr == VR.UI || vr == VR.UT) {
      Object[] tmp = writeStringValues(elem, cs);
      switch(tmp.length){
        case 0:
          ret = "";
          break;
        case 1:
          ret = tmp[0];
          break;
        default:
          ret = tmp;
      }      
    } else if (vr == VR.FL || vr == VR.FD) {
      double[] tmp = writeDoubleValues(elem);
      switch(tmp.length){
        case 0:
          ret = "";
          break;
        case 1:
          ret = tmp[0];
          break;
        default:
          ret = tmp;
      }     
    } else if (vr == VR.SL || vr == VR.SS || vr == VR.UL || vr == VR.US) {
      int[] tmp = writeIntValues(elem);
      switch(tmp.length){
        case 0:
          ret = "";
          break;
        case 1:
          ret = tmp[0];
          break;
        default:
          ret = tmp;
      }     
    } else if (vr == VR.OB || vr == VR.OF || vr == VR.OW || vr == VR.UN) {
      ret = writeInlineBinary(elem);
    } else if (vr == VR.SQ) {

      DicomObject d = elem.getDicomObject();
      TagValue tag = tagStruct.getTagValue(elem.tag());
      String t = (tag != null) ? tag.getAlias() : Integer.toString(elem.tag());
      MappedDICOMObject map = DicomObjectFactory.toMap(d,parentPath.getAttributePath(t));

      ret = map;
    }
    
    return ret;
  }

  private static Object[] writeStringValues(DicomElement elem, SpecificCharacterSet cs) {
    String[] ss = elem.getStrings(cs, cache);
    
    Object[] ret = new Object[ss.length];
    VR vr = elem.vr();
    
    for(int i = 0; i< ss.length ;i++){
      String s = ss[i];
      
      if (s == null || s.isEmpty())
          ret[i] = null;
      else if( vr == VR.DS){
          ret[i] = parseDS(s);
      }else if(vr == VR.IS){
          ret[i] = parseIS(s);
      }else if(vr == VR.PN){
          ret[i] = writePersonName(s);
      }else{
          ret[i] = s;
      }     
    }
    return ret;
  }

  private static double[] writeDoubleValues(DicomElement elem) {
    double[] d = elem.getDoubles(cache);
    return d;
  }

  private static int[] writeIntValues(DicomElement elem) {
    int[] ints = elem.getInts(cache);
    return ints;
  }

  private static String writePersonName(String s) {
    PersonName pn = new PersonName(s);
    return pn.toString();
  }

  private static byte[] writeInlineBinary(DicomElement elem) {
    return elem.getBytes();
  }

  private static int parseIS(String s) {
    int x = NumberUtils.toInt(s);
    return x;
  }

  private static double parseDS(String s) {
    return s != null && s.length() != 0 ? Double.parseDouble(s.replace(',', '.')) : 0;
  }

}
