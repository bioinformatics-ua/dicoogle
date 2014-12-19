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

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.VR;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class TagValue implements Serializable {

    static final long serialVersionUID = 1L;
    
    /** tag value - (group, subgroup) in hex coding */ 
    private int tag = 0  ;
    /** Public name like Patient Name */
    private String name = null ;
    /** Alias to the name like PatientName */
    private String alias = null ;

    /**  ValueRepresentation */
    private String tgVR = null ;
    /** RetiredStatus - Deprecated mode */
    private int   ret  = 0 ;

    /** Value Multiciply */
    private int VM = 0 ;

    public TagValue(int tag, String alias)
    {
        this.tag = tag ;
        this.name = alias;
        this.alias = alias ;
        
        VR tagVR = (new BasicDicomObject()).vrOf(tag);
        
        if(tagVR != null)
            tgVR = tagVR.toString();
    }
    /**
     * Get The Tag Number (group, subgroup)
     * @return int
     */
    public int getTagNumber()
    {
        return this.tag  ;
    }
    
    public String getTagID(){
    	return StringUtils.leftPad(Integer.toHexString(tag), 8, '0');
    }
    /**
     * A formal name
     * @return String
     */
    public String getName()
    {
        return this.name ;
    }
    /**
     * Alias
     * @return String
     */
    public String getAlias()
    {
        return this.alias ;
    }
    /**
     * Verify if it is deprecated
     * @return int
     */
    public int getRet()
    {
        return this.ret ;
    }

    public String getGroup()
    {
        return TagValue.getGroup(tag);
    }

    public String getSubgroup()
    {
        return TagValue.getSubgroup(tag);
    }
    
    public static String getGroup(int tag)
    {
        String result = Integer.toHexString(
                (tag & 0xFFFF0000 )>> 16) ;
        return StringUtils.leftPad(result, 4, '0');
    }

    public static String getSubgroup(int tag)
    {
        String result =  Integer.toHexString(tag & 0x0000FFFF ) ;
        return StringUtils.leftPad(result, 4, '0');
    }

    /**
     * @return the VR
     */
    public String getVR() {
        return tgVR;
    }

    /**
     * @param VR the VR to set
     */
    public void setVR(String VR) {
        this.tgVR = VR;
    }

    /**
     * @return the VM
     */
    public int getVM() {
        return VM;
    }

    /**
     * @param VM the VM to set
     */
    public void setVM(int VM) {
        this.VM = VM;
    }
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public void updateTagInformation(TagValue newTag){
		if(!this.equals(newTag))
			return;
		
		if(newTag.alias != null)
			this.alias = newTag.alias;
		
		if(newTag.name != null)
			this.name = newTag.name;
		
		if(newTag.ret != this.ret)
			this.ret = newTag.ret;
		
		if(this.VM != newTag.VM)
			this.VM = newTag.VM;
		
		if(newTag.tgVR != null)
			this.tgVR = newTag.tgVR;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + tag;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TagValue other = (TagValue) obj;
		if (tag != other.tag)
			return false;
		return true;
	}
    
    public boolean isBinary(){
    	return this.tgVR.equals("AT");
    }

}
