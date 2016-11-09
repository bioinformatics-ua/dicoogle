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
 * Class SearchResult is responsible to get results from Lucene indexer
 * But no text results are returned instead we use names of DICOM file and 
 * instance a new DicomObject, so in theory this is an abstract to a list of 
 * DicomObject, neither list of *names* of DICOM file.
 */
package pt.ua.dicoogle.server;

import java.io.*;

import org.dcm4che2.data.*;
import org.slf4j.Logger;
import pt.ua.dicoogle.core.settings.ServerSettingsManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.slf4j.LoggerFactory;

import org.dcm4che2.io.DicomInputStream;


import pt.ua.dicoogle.core.dim.*;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;
/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @since 17 Fev 2009
 */
public class SearchDicomResult implements Iterator<DicomObject>
{

    public enum QUERYLEVEL { PATIENT, STUDY, SERIE, IMAGE}

    private final QUERYLEVEL queryLevel; 


    /**
    * Get IndexCore
    */
    
    Collection<SearchResult> list = null ;
    List<Patient> patientList = new ArrayList<>();
    List<Study> studyList = new ArrayList<>();
    List<Serie> seriesList = new ArrayList<>();
    Iterator it = null ;
    
    String currentFile ;



    private static ConcatTags concatTags = null;
    private static  boolean concatTagsCheck = true;


    private static final Logger logger = LoggerFactory.getLogger(SearchDicomResult.class);

    public SearchDicomResult(String searchQuery, boolean isNetwork,
			ArrayList<String> extrafields, QUERYLEVEL level) {

		queryLevel = level;

		/**
		 * Get the array list of resulst match searchQuery
		 */

        logger.info("QUERY: " + searchQuery);
        logger.info("QUERYLEVEL: " + queryLevel);

        if (concatTags==null&& concatTagsCheck)
        {
            concatTags = new ConcatTags();
            try {
                concatTags.parseConfig(ConcatTags.FILENAME);
            } catch (FileNotFoundException ex) {
                logger.info("There is not file: " + ConcatTags.FILENAME);
                concatTags = null;
                concatTagsCheck = false;
            }
        }

		HashMap<String, String> extraFields = new HashMap<String, String>();
		for (String s : extrafields) {
			extraFields.put(s, s);
		}

		JointQueryTask holder = new JointQueryTask() {

			@Override
			public void onReceive(Task<Iterable<SearchResult>> e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCompletion() {
				// TODO Auto-generated method stub

			}
		};
		holder = PluginController.getInstance().queryAll(holder, searchQuery,
				extraFields);

		try {
			it = holder.get().iterator();
			
			list = new ArrayList<SearchResult>();
			while (it.hasNext()) {
				list.add((SearchResult) it.next());
			}
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(list != null)
			it = list.iterator();
		
		if (level == QUERYLEVEL.PATIENT || level == QUERYLEVEL.STUDY) {
			DIMGeneric dimModel = null;
            try
            {
                if (concatTags==null)
                    dimModel = new DIMGeneric(list);
                else
                    dimModel = new DIMGeneric(concatTags, list);

            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

			ArrayList<Patient> listPatients = dimModel.getPatients();

			for (Patient p : listPatients) {
				studyList.addAll(p.getStudies());
			}

			it = studyList.iterator();

		} else if (level == QUERYLEVEL.SERIE) {

            DIMGeneric dimModel = null;
            try
            {
                if (concatTags==null)
                    dimModel = new DIMGeneric(list);
                else
                    dimModel = new DIMGeneric(concatTags, list);
            } catch (Exception ex)
            {
            }
            

			ArrayList<Patient> listPatients = dimModel.getPatients();
			for (Patient p : listPatients) {
				studyList.addAll(p.getStudies());
				for (Study s : p.getStudies()) {
					seriesList.addAll(s.getSeries());
				}
			}
			it = seriesList.iterator();

		}

	}

    @Override
    public boolean hasNext()
    {
      if (it!=null)
      {
        return it.hasNext();
      }
      else
      {
        return false;
      }
    }

    public String getCurrentFile()
    {
        return this.currentFile ; 
    }

    @Override
    public DicomObject next()
    {

        // TODO: this code need to be refactored
        // C-FIND RSP should be builded based on Search Result,
        // instead opening the file to build DicomObject.

        /** 
         * Get the fullpath of images 
         */
        String path = ServerSettingsManager.getSettings().getArchiveSettings().getMainDirectory();

        //DebugManager.getSettings().debug("Path of DICOM: "+path);


        if (it != null &&  it.hasNext())
        {
            Object next = it.next();
            if (queryLevel==QUERYLEVEL.IMAGE )
            {

                SearchResult sR = (SearchResult)next;
                
                path = sR.getURI().toString();
                currentFile = path ;
                //DebugManager.getSettings().debug("-> Next::: " + next.toString());
                DicomInputStream din = null;
                /*try
                {
                    if (path.endsWith(".gz"))
                        din = new DicomInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(new File(path)), 256)));
                    else
                        din = new DicomInputStream(new File(path));
                   
                    
                    URI uri = new URI(path);
                    //System.out.println("Trying to find Plugin for: "+uri.toString());
                    StorageInterface plug = PluginController.getSettings().getStorageForSchema(uri);
                    
                    if(plug != null){
                        //System.out.println("Found Plugin For: "+uri.toString());
                        
                        Iterable<StorageInputStream> stream = plug.at(uri);
                        for(StorageInputStream str : stream)
                            
                            try {
                                din = new DicomInputStream(str.getInputStream());
                            } catch (IOException ex) {
                                LoggerFactory.getLogger(SearchDicomResult.class).error(ex.getMessage(), ex);
                            }
                    }
                    
                    //DebugManager.getSettings().debug("Imagem: "+path+"..."+next);
                } catch (URISyntaxException ex) {
                    LoggerFactory.getLogger(SearchDicomResult.class).error(ex.getMessage(), ex);
                }
                */
                
                /** This code is refactored in a experimental branch
                 * Building a BasicDicomObject based on Indexing
                 * It will increase the performace
                 */
                BasicDicomObject result = new BasicDicomObject();

                // Fill fields of study now


                //System.out.println("Serie : "+ serieTmp);
                result.putString(Tag.InstitutionName, VR.CS,(String)sR.get("InstitutionName"));

                result.putString(Tag.StudyInstanceUID, VR.UI, (String)sR.get("StudyInstanceUID"));
                result.putString(Tag.SeriesInstanceUID, VR.UI, (String)sR.get("SeriesInstanceUID"));
                result.putString(Tag.SOPInstanceUID, VR.UI, (String)sR.get("SOPInstanceUID"));
                result.putString(Tag.SeriesDescription, VR.LO, (String)sR.get("SeriesDescription"));
                result.putString(Tag.SeriesDate, VR.TM, (String)sR.get("SeriesDate"));
                result.putString(Tag.SeriesTime, VR.TM, (String)sR.get("SeriesTime"));
                result.putString(Tag.QueryRetrieveLevel, VR.LO, "IMAGE");

                result.putString(Tag.Modality, VR.CS,(String)sR.get("Modality"));

                result.putString(Tag.SeriesNumber, VR.IS, "" + (String)sR.get("SeriesNumber"));


                return result;

            }
            else if (queryLevel == QUERYLEVEL.STUDY||queryLevel == QUERYLEVEL.PATIENT)
            {

                Study studyTmp = (Study)next;
                BasicDicomObject result = new BasicDicomObject();
                String patientName =studyTmp.getParent().getPatientName() ;
                
                try {
                    patientName = new String(studyTmp.getParent().getPatientName().getBytes("ISO-8859-1"), "ISO-8859-1");
                } catch (Exception ex) {
                    LoggerFactory.getLogger(SearchDicomResult.class).error(ex.getMessage(), ex);
                }
                try {
                    result.putBytes(Tag.PatientName, VR.PN, patientName.getBytes("ISO-8859-1"));
                } catch (Exception ex) {
                    LoggerFactory.getLogger(SearchDicomResult.class).error(ex.getMessage(), ex);
                }
                
                //System.out.println("PatientName:"+patientName);
                result.putString(Tag.SpecificCharacterSet, VR.CS, "ISO_IR 100");
                result.putString(Tag.PatientSex, VR.LO, studyTmp.getParent().getPatientSex());
                result.putString(Tag.PatientID, VR.LO, studyTmp.getParent().getPatientID());
                result.putString(Tag.PatientBirthDate, VR.DA, studyTmp.getParent().getPatientBirthDate());
                result.putString(Tag.StudyDate, VR.DA, studyTmp.getStudyData());
                result.putString(Tag.StudyID, VR.SH, studyTmp.getStudyID());
                result.putString(Tag.StudyTime, VR.TM, studyTmp.getStudyTime());
                result.putString(Tag.AccessionNumber, VR.SH, studyTmp.getAccessionNumber());
                result.putString(Tag.StudyInstanceUID, VR.UI, studyTmp.getStudyInstanceUID());
                result.putString(Tag.StudyDescription, VR.LO, studyTmp.getStudyDescription());
                String modality = studyTmp.getSeries().get(0).getModality(); // Point of Failure, fix me
                result.putString(Tag.ModalitiesInStudy, VR.CS,modality);
                result.putString(Tag.Modality, VR.CS,modality);
                result.putString(Tag.InstitutionName, VR.CS, studyTmp.getInstitutuionName());

                int instances = 0;
                for (Serie serieTmp : studyTmp.getSeries())
                {
                    instances+=serieTmp.getImageList().size();
                }

                result.putString(Tag.NumberOfStudyRelatedInstances, VR.IS,""+instances);
                result.putString(Tag.NumberOfSeriesRelatedInstances, VR.IS,""+studyTmp.getSeries().size());


                return result;
                
            }
            else if (queryLevel == QUERYLEVEL.SERIE)
            {
                // Serie

                Serie serieTmp = (Serie)next;
                BasicDicomObject result = new BasicDicomObject();
                //System.out.println("Serie : "+ serieTmp);
                result.putString(Tag.InstitutionName, VR.CS,serieTmp.getParent().getInstitutuionName());
                
                result.putString(Tag.StudyInstanceUID, VR.UI, serieTmp.getParent().getStudyInstanceUID());
                result.putString(Tag.SeriesInstanceUID, VR.UI, serieTmp.getSerieInstanceUID());
                result.putString(Tag.SeriesDescription, VR.LO, serieTmp.getSeriesDescription());
                result.putString(Tag.SeriesDate, VR.TM, serieTmp.getSeriesDate());
                result.putString(Tag.QueryRetrieveLevel, VR.LO, "SERIES");
                String modality = serieTmp.getModality(); // Point of Failure, fix me
                result.putString(Tag.Modality, VR.CS,modality);
                
                result.putString(Tag.SeriesNumber, VR.IS, "" + serieTmp.getSerieNumber());


                result.putString(Tag.Modality, VR.CS,modality);
                if (serieTmp.getModality().equals("MG")||serieTmp.getModality().equals("CR"))
                {

                    result.putString(Tag.ViewPosition, null, serieTmp.getViewPosition());
                    result.putString(Tag.ImageLaterality, null, serieTmp.getImageLaterality());
                    result.putString(Tag.AcquisitionDeviceProcessingDescription, VR.AE, serieTmp.getAcquisitionDeviceProcessingDescription());
                    DicomElement viewCodeSequence = result.putSequence(Tag.ViewCodeSequence);
                    DicomObject viewCodeSequenceObj = new BasicDicomObject();
                    viewCodeSequenceObj.setParent(result);
                    viewCodeSequenceObj.putString(Tag.CodeValue, null, serieTmp.getViewCodeSequence_CodeValue());
                    viewCodeSequenceObj.putString(Tag.CodingSchemeDesignator, null, serieTmp.getViewCodeSequence_CodingSchemeDesignator());
                    viewCodeSequenceObj.putString(Tag.CodingSchemeVersion, null, serieTmp.getViewCodeSequence_CodingSchemeVersion());
                    viewCodeSequenceObj.putString(Tag.CodeMeaning, null, serieTmp.getViewCodeSequence_CodeMeaning());

                    viewCodeSequence.addDicomObject(viewCodeSequenceObj);
                    result.putNestedDicomObject(Tag.ViewCodeSequence, viewCodeSequenceObj);
                }
                result.putString(Tag.NumberOfSeriesRelatedInstances, VR.IS,""+serieTmp.getImageList().size());


                result.putString(Tag.SeriesNumber, VR.IS, "" + serieTmp.getSerieNumber());
                result.putString(Tag.ProtocolName, VR.LO, "" + serieTmp.getProtocolName());
                result.putString(Tag.BodyPartThickness, VR.LO, "" + serieTmp.getBodyPartThickness());


                return result;

            }
            else
            {
                System.err.println("ERROR: WRONG QUERY LEVEL!");
            }
            

        }    
        return null ; 
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported. Nobody use it.");
    }
}
