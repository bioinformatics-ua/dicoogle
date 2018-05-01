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
/**
 * 
 * The Goal of this class is based on DicomObject 
 * send FindRSP to destination entity 
 * 
 * It will search data at Index of Lucene 
 * 
 */
package pt.ua.dicoogle.server.queryretrieve;

import aclmanager.core.LuceneQueryACLManager;
import aclmanager.models.Principal;

import java.io.IOException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.Status;


import pt.ua.dicoogle.core.exceptions.CFindNotSupportedException;
import pt.ua.dicoogle.server.SearchDicomResult;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class FindRSP implements DimseRSP 
{
    
    private DicomObject rsp;
    private DicomObject keys;
    
    
    
    /** 
     * Each moment in timeline we're getting a item
     */
    private DicomObject mwl = null;

    SearchDicomResult search = null ; 
    
    private String callingAET;
    private LuceneQueryACLManager luke;
    
    public FindRSP(DicomObject keys, DicomObject rsp, String callingAET, LuceneQueryACLManager luke)
    {
        this.rsp = rsp ; 
        this.keys = keys ;
        
        this.callingAET = callingAET;
        this.luke = luke;
        
        
        /** Debug - show keys, rsp, index */ 
        if (keys!=null)
        {
            //DebugManager.getSettings().debug("keys object: ");
            //DebugManager.getSettings().debug(keys.toString());
        }
        if (rsp!=null)
        {
            //DebugManager.getSettings().debug("Rsp object");
            //DebugManager.getSettings().debug(rsp.toString());
        }
        
        /** 
         * Get object to search
         */
        ArrayList<String> extrafields  = null ;
        extrafields = new ArrayList<String>();

        extrafields.add("PatientName");
        extrafields.add("PatientSex");
        extrafields.add("PatientID");
        extrafields.add("Modality");
        extrafields.add("StudyDate");
        extrafields.add("StudyTime");
        extrafields.add("AccessionNumber");
        extrafields.add("StudyID");
        extrafields.add("StudyDescription");
        extrafields.add("SeriesNumber");
        extrafields.add("SeriesInstanceUID");
        extrafields.add("SeriesDescription");
        extrafields.add("ReferringPhysicianName");
        extrafields.add("PatientBirthDate");
        extrafields.add("ModalitiesInStudy");
        extrafields.add("StudyInstanceUID");
        extrafields.add("InstitutionName");
        extrafields.add("AcquisitionDeviceProcessingDescription");
        extrafields.add("ProtocolName");

        extrafields.add("SeriesDate");

        extrafields.add("OperatorsName");
        extrafields.add("RequestingPhysician");
        extrafields.add("BodyPartThickness");
        extrafields.add("PatientOrientation");
        extrafields.add("CompressionForce");

        extrafields.add("ViewPosition");
        extrafields.add("ImageLaterality");
        extrafields.add("AcquisitionDeviceProcessingDescription");


        extrafields.add("ViewCodeSequence_CodeValue");
        extrafields.add("ViewCodeSequence_CodingSchemeDesignator");
        extrafields.add("ViewCodeSequence_CodingSchemeVersion");
        extrafields.add("ViewCodeSequence_CodeMeaning");


        extrafields.add("SOPInstanceUID");
        
        String query = getQueryString(keys, rsp);
        //System.out.println("OLD Query: "+query);
        query = applyQueryFilter(query);
        
        System.out.println("NEW Query: "+query);

        /** 
         * Search results
         * TODO: 
         * - Get Search String Query?
         * - Is A Network Search?
         * - How works extrafields?
         */

        SearchDicomResult.QUERYLEVEL level = null ;
        /*
        if (CFindBuilder.isPatientRoot(rsp))
        {
            level = SearchDicomResult.QUERYLEVEL.PATIENT;
        }
        else if (CFindBuilder.isStudyRoot(rsp))
        {
            level = SearchDicomResult.QUERYLEVEL.STUDY;
        }*/
        DicomElement elem = keys.get(Integer.parseInt("00080052", 16));
        String levelStr = new String(elem.getBytes());

        if (levelStr.contains("PATIENT"))
        {
            level = SearchDicomResult.QUERYLEVEL.PATIENT;
        }
        else if(levelStr.contains("STUDY"))
        {
            level = SearchDicomResult.QUERYLEVEL.STUDY;
        }
        else if (levelStr.contains("SERIES"))
        {
            level = SearchDicomResult.QUERYLEVEL.SERIE;
        }
        else if (levelStr.contains("IMAGE"))
        {
            level = SearchDicomResult.QUERYLEVEL.IMAGE;
        }
        System.out.println("Charset: "+ this.keys.get(Tag.SpecificCharacterSet));
        search = new SearchDicomResult(query,
                 true, extrafields, level);



         if (search == null)
         {
            //DebugManager.getSettings().debug(">> Search is null, so" +
            //        " somethig is wrong ");
         }
         
        // always return Specific Character Set
        if (!keys.contains(Tag.SpecificCharacterSet)) {
            this.keys.putNull(Tag.SpecificCharacterSet, VR.CS);
            keys.putNull(Tag.SpecificCharacterSet, VR.CS);
            this.rsp.putNull(Tag.SpecificCharacterSet, VR.CS);
        }

    }




    private String getQueryString(DicomObject keys, DicomObject rsp)
    {
        String result = "";
        try
        {
            CFindBuilder c = new CFindBuilder(keys, rsp);
            result = c.getQueryString() ;
        } catch (CFindNotSupportedException ex)
        {
            LoggerFactory.getLogger(FindRSP.class).error(ex.getMessage(), ex);
        }

        return result ;
    }

     private String applyQueryFilter(String normalQuery){
        if(luke == null)
            return normalQuery;
       //LuceneQueryACLManager luke = new LuceneQueryACLManager(manager);
       
       String query = luke.produceQueryFilter(new Principal("AETitle", callingAET));
         if(query.length() > 0 )
             return normalQuery + query;
               
        return normalQuery;
    }



    /**
     * 
     * Verify if have a next DicomObject and set 
     * the pointer of DicomObject with correct paraments
     * It also apply the filter to verify if the DicomObject matches
     * with query and if it is not search for the next DicomObject.
     * 
     * @return true if there is a next DicomObject
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */

    @Override
    public boolean next() throws IOException, InterruptedException 
    {
        if (search!=null)
        {
            if (search.hasNext())
            {
                    //DebugManager.getSettings().debug("We have next, so get it");
                    mwl = search.next();
                    //if (mwl.matches(this.keys, true))
                    //{

                        // always return Specific Character Set
                        if (!this.mwl.contains(Tag.SpecificCharacterSet))
                            this.mwl.putNull(Tag.SpecificCharacterSet, VR.CS);
                        this.rsp.putInt(Tag.Status, VR.US, mwl.containsAll(keys) ? Status.Pending : Status.PendingWarning);
                        return true;
                    //}
            }

            /** Sucess */
            this.rsp.putInt(Tag.Status, VR.US, Status.Success);
            /** Clean pointers */
            this.mwl = null;
            this.search = null;
            return true ;
            
        }
        else
        {
            this.rsp.putInt(Tag.Status, VR.US, Status.Cancel);
        }
        return false;
    }
    
    /**
     * 
     * @return
     */
    @Override
    public DicomObject getCommand() 
    {
        return this.rsp;
    }
    
    
    /**
     * This method see the current DicomObject and return it
     * @return null or DicomObject
     */
    @Override
    public DicomObject getDataset() 
    {
        //DebugManager.getSettings().debug("Get Data Set");
        return  this.mwl != null ? this.mwl.subSet(this.keys) : null;
    }

    @Override
    public void cancel(Association arg0) throws IOException 
    {

        search = null ;
        try
        {
            arg0.release(true);
            
        } catch (InterruptedException ex)
        {
            LoggerFactory.getLogger(FindRSP.class).error(ex.getMessage(), ex);
        }
    }

   @Override
    protected void finalize() throws Throwable
   {

        super.finalize();


    }

}
    
