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
package pt.ua.dicoogle.server.queryretrieve;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import pt.ua.dicoogle.core.exceptions.CFindNotSupportedException;

import java.util.Iterator;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;

import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.sdk.utils.TagValue;
import pt.ua.dicoogle.sdk.utils.TagsStruct;

/**
 *
 *  [SOP Class name] -  [SOP Class UID]
 *   Patient Root Q/R Find - 1.2.840.10008.5.1.4.1.2.1.1
 *   Study Root Q/R Find - 1.2.840.10008.5.1.4.1.2.2.1
 *   NOT SUPPORTED: @depracated
 *   Patient-Study Root Q/R Find (Retired) - 1.2.840.10008.5.1.4.1.2.3.1
 *
 *
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class CFindBuilder
{

    private boolean patientRoot = false ;
    private boolean studyRoot = false ;

    private String query = "";
    

    public CFindBuilder(DicomObject key, DicomObject rsp) throws CFindNotSupportedException
    {

        //if (!setRoot(rsp))
        //    throw new CFindNotSupportedException() ;

              
        /**
         * Sample output
        (0008,0005) CS #10 [ISO_IR 100] Specific Character Set
        (0008,0020) DA #18 [20090531-20090531] Study Date
        (0008,0030) TM #22 [000000.000-120000.000] Study Time
        (0008,0050) SH #0 [] Accession Number
        (0008,0052) CS #6 [STUDY] Query/Retrieve Level
        (0008,0061) CS #2 [XA] Modalities in Study
        (0008,1030) LO #0 [] Study Description
        (0010,0010) PN #0 [] Patient's Name
        (0010,0020) LO #0 [] Patient ID
        (0010,0030) DA #0 [] Patient's Birth Date
        (0020,000D) UI #0 [] Study Instance UID
        (0020,0010) SH #0 [] Study ID
        (0020,1208) IS #0 [] Number of Study Related Instances
         */

        /** Search by required fields

         *
         *
        */

        // Get Affected SOP Classs UID
        DicomElement elem = rsp.get(Integer.parseInt("00000002", 16));
        String affectedSOP = new String(elem.getBytes());

        TagsStruct tagstruct = TagsStruct.getInstance();
        
        //TagsStruct.getSettings().toStringNew();
        boolean all=false ;
        String append = "" ;
        query="";
        System.out.println(tagstruct.getDIMFields().size());
        Iterator<TagValue> it = tagstruct.getDIMFields().iterator();
        while(it.hasNext())
        {
            /** Verify if this tags exists
            */
        	TagValue tag = it.next();
            int k = tag.getTagNumber() ;
            DicomElement e = key.get(k);
            //DebugManager.getSettings().debug("get key::"+ k );
            if (e!=null)
            {
                String value = new String(e.getBytes());
                //DebugManager.getSettings().debug("Value getted in CFIND RP:<"+t.get(k).getAlias() + "> "+ value  +".");
                if (value.equals(""))
                {
                    continue ;

                }
                else
                {
                    value=value.trim();
                    boolean modified = false ;
                    // TODO :: Study Date need to be rewritted
                    // and others weird tags like // should be mapped to lucene!
                    if (k == Tag.ModalitiesInStudy)
                    {
                        String [] modality = value.split("\\\\");
                        int i = 0 ;
                        String modalityQuery = "";
                        for (String mod : modality )
                        {

                            //DebugManager.getSettings().debug(mod);

                            if (modified)
                            {
                                // There is already exist a modality
                                modalityQuery += " OR ";
                            }

                            i++ ; 
                            modified = true ;
                            modalityQuery += "Modality" + ":" + mod;
                        }
                        if (!modalityQuery.equals(""))
                        {
                            System.err.println(modalityQuery);
                            query = query + append +  " (" + modalityQuery + ")" ;
                        }
                        
                    }

                    else if(k == Tag.StudyDate)
                    {
                        System.out.println("Value :@" +value+".");
                        
                        String [] date = value.split("-");
                        int i = 0 ;
                        if (date.length==1)
                        {
                            
                            if (!value.contains("-"))
                            {
                                query = query + append +tag.getAlias() + ":[" + date[0] + " TO " +
                                date[0] + "]" ;
                            }
                            else
                            {
                                
                                 String s;
                                 Format formatter;
                                 Date date2 = new Date();

                                 // 01/09/02
                                 formatter = new SimpleDateFormat("YYYYMMdd");
                                 s = formatter.format(date2);

                                 query = query + append +tag.getAlias() + ":[" + date[0] + " TO " +
                                 s + "]" ;
                            }
                            
                            
                            //query = query +append + t.get(k).getAlias() + ":[" + date[0] + " TO " +
                            //    date[0] + "]" ;
                        }
                        else if(date.length==2)
                        {
                            query = query + append + tag.getAlias() + ":[" + date[0] + " TO " +
                                date[1] + "]" ;
                        }
                        modified = true ;
                    }
                    
                    
                    else if(k == Tag.StudyTime)
                    {
                        String [] date = value.split("-");
                        int i = 0 ;
                        if (date.length==1)
                        {
                            query = query + append +tag.getAlias() + ":[" + date[0] + " TO " +
                                date[0] + "]" ;
                            /*if (!value.contains("-"))
                            {
                                query = query + append +t.get(k).getAlias() + ":[" + date[0] + " TO " +
                                date[0] + "]" ;
                            }
                            else
                            {
                                
                                 String s;
                                 Format formatter;
                                 Date date2 = new Date();

                                 // 01/09/02
                                 formatter = new SimpleDateFormat("YYYYMMdd");
                                 s = formatter.format(date2);

                                 query = query + append +t.get(k).getAlias() + ":[" + date[0] + " TO " +
                                 s + "]" ;
                            }*/
                        }
                        else if(date.length==2)
                        {
                            query = query + append + tag.getAlias() + ":[" + date[0] + " TO " +
                                date[1] + "]" ;
                        }
                        modified = true ;
                    }
                    
                    

                    else
                    {
                        modified = true ; 
                        query += append + tag.getAlias() + ":" + value;
                    }

                    if (modified && it.hasNext())
                    {
                        append = " AND ";
                    }

                }

            }
        }
        
        if (query.equals(""))
            query="*:*"; 
        //DebugManager.getSettings().debug(">> Query String DICOM: "+ query);


    }

    public String getQueryString()
            {
        return this.query;
    }

    private synchronized  boolean setRoot(DicomObject rsp)
    {
        /**
         * Verify if it is inside of:
         * Affected SOP Class UID (0000,0002)
         * Checked in C-FIND RQ in DICOM
         * It is one of three ([P|S|PS] Root )
         */

        DicomElement elem = rsp.get(Integer.parseInt("00000002", 16));
        String affectedSOP = new String(elem.getBytes());



        //DebugManager.getSettings().debug(">" + affectedSOP);
        //DebugManager.getSettings().debug(">> "+ServerSettingsManager.getSettings().getSOPClasses());
        


        boolean found = false;

        for (String i : ServerSettingsManager.getSettings().getDicomServicesSettings().getQueryRetrieveSettings().getSOPClass())
        {
            //DebugManager.getSettings().debug("It have in settings:>: " + i);

            if (affectedSOP.equals(i))
            {
                /**
                1.2.840.10008.5.1.4.1.2.1.1 (Patient)
                1.2.840.10008.5.1.4.1.2.2.1 (Study)
                 */
                //DebugManager.getSettings().debug(">>> Affected SOPs in ");
                if (affectedSOP.equals("1.2.840.10008.5.1.4.1.2.1.1"))
                {
                    this.patientRoot = true ;
                    found = true ;
                }
                else if (affectedSOP.equals("1.2.840.10008.5.1.4.1.2.2.1"))
                {
                    this.studyRoot = true ;
                    found = true  ;
                }
                break ;
            }
        }

        return found ;

    }



    /**
     * @return the patientRoot
     */
    public boolean isPatientRoot()
    {
        return patientRoot;
    }

    /**
     * @param patientRoot the patientRoot to set
     */
    public void setPatientRoot(boolean patientRoot)
    {
        this.patientRoot = patientRoot;
    }

    /**
     * @return the studyRoot
     */
    public boolean isStudyRoot()
    {
        return studyRoot;
    }

    /**
     * @param studyRoot the studyRoot to set
     */
    public void setStudyRoot(boolean studyRoot)
    {
        this.studyRoot = studyRoot;
    }

    /**
     * @return the query
     */
    public String getQuery()
    {
        return query;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query)
    {
        this.query = query;
    }


    public static boolean isPatientRoot(DicomObject rsp)
    {
        DicomElement elem = rsp.get(Integer.parseInt("00000002", 16));
        String affectedSOP = new String(elem.getBytes());

        if (affectedSOP.equals("1.2.840.10008.5.1.4.1.2.2.1"))
        {
            return true;
        }
        return false;
    }

    public static boolean isStudyRoot(DicomObject rsp)
    {
        DicomElement elem = rsp.get(Integer.parseInt("00000002", 16));
        String affectedSOP = new String(elem.getBytes());
        if (affectedSOP.equals("1.2.840.10008.5.1.4.1.2.1.1"))
        {
            return true;
        }
        return false;
    }
    

}
