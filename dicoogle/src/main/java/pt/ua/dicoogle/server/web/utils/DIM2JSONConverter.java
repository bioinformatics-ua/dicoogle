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
package pt.ua.dicoogle.server.web.utils;

import java.net.URI;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import pt.ua.dicoogle.sdk.datastructs.dim.DIMGeneric;
import pt.ua.dicoogle.sdk.datastructs.dim.Patient;
import pt.ua.dicoogle.sdk.datastructs.dim.Series;
import pt.ua.dicoogle.sdk.datastructs.dim.Study;

/**
 * Helper Class to tranlate DIMGeneric objects into JSON.
 * Usefull for web environments
 * 
 * TODO: Insert proper documentation.
 * 
 * @author Tiago Marques Godinho.
 *
 */
public class DIM2JSONConverter {
	
	/**
	 * Converts a DIMGeneric object to JSON.
	 * 
	 * @param dim DIM Object
	 * @return The resulting JSON Objects, or null if dim is null.
	 */
	public static JSONArray convertToJSON(DIMGeneric dim){
		JSONArray arr = new JSONArray();
		
		Iterator<Patient> it = dim.getPatients().iterator();
		while(it.hasNext()){
			Patient p = it.next();
				
			arr.add(convertToJSON(p));
		}
		
		return arr;
	}
	
	/**
	 * Converts a Patient Object to JSON.
	 * 
	 * @param patient Patient to be Converted
	 * @return The resulting JSON Object, or null if patient is null.
	 */
	public static JSONObject convertToJSON(Patient patient){
		
		JSONObject obj = new JSONObject();
		
		obj.put("id", StringUtils.trimToNull(patient.getPatientID()));
		obj.put("name", StringUtils.trimToNull(patient.getPatientName()));
		obj.put("sex", StringUtils.trimToNull(patient.getPatientSex()));
		
		JSONArray studies = new JSONArray();
		for(Study s : patient.getStudies()){
			studies.add(convertToJSON(s));
		}
		obj.put("studies", studies);
		
		return obj;		
	}

		
	/**
	 * Converts a Study Object to JSON.
	 * 
	 * @param study Study to be converted.
	 * @return The resulting JSON Object, or null if study is null. 
	 */
	private static JSONObject convertToJSON(Study study) {
		
		JSONObject obj = new JSONObject();
		
		obj.put("id",StringUtils.trimToNull( study.getStudyID()));
		obj.put("uid", StringUtils.trimToNull(study.getStudyInstanceUID()));
		obj.put("data", StringUtils.trimToNull(study.getStudyData()));
		obj.put("time", StringUtils.trimToNull(study.getStudyTime()));
		obj.put("descr", StringUtils.trimToNull(study.getStudyDescription()));
		obj.put("iname", StringUtils.trimToNull(study.getInstitutuionName()));
		
		JSONArray series = new JSONArray();
		for(Series serie : study.getSeries()){
			series.add(convertToJSON(serie));
		}
		obj.put("series", series);
		
		return obj;
	}
	
	/**
	 * Converts a Series Model to JSON. 
	 * 
	 * @param series The DICOM Series model
	 * @return The resulting JSON object, or null if series is null.
	 */
	private static JSONObject convertToJSON(Series series) {
		
		JSONObject obj = new JSONObject();
		
		obj.put("uid", StringUtils.trimToNull(series.getSeriesInstanceUID()));
		obj.put("mod", StringUtils.trimToNull(series.getModality()));
		obj.put("number", series.getSeriesNumber());
		obj.put("descr", StringUtils.trimToNull(series.getSeriesDescription()));
	
		JSONArray images = new JSONArray();
		Iterator<String> itUID = series.getSOPInstanceUIDList().iterator();
		Iterator<URI> itURI = series.getImageList().iterator();
		while(itUID.hasNext() && itURI.hasNext()){
			String uid = itUID.next();
			URI uri = itURI.next();
			
			JSONObject imgObj = new JSONObject();
			imgObj.put("uid", uid);
			imgObj.put("uri", uri.toString());
			
			images.add(imgObj);
		}
		obj.put("images", images);
		
		return obj;
	}
}
