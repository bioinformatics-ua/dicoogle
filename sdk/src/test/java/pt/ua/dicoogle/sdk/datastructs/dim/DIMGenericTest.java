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
package pt.ua.dicoogle.sdk.datastructs.dim;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import java.util.HashMap;

public class DIMGenericTest {

    /** Create a search result with the given URI and
     * the key-value pairs as a flat array.
     * 
     * @param uri the URI of the instance, that will be converted to a URI
     * @param part var-args of key-value pairs with a string
     * of the tag alias followed by an object
     */
    private static SearchResult createResult(String uri, Object... parts) {
        assertTrue(parts.length % 2 == 0);
        HashMap<String, Object> data = new HashMap<>();
        for (int i = 0; i < parts.length; i += 2) {
            String key = (String) parts[i];
            Object value = parts[i + 1];
            data.put(key, value);
        }
        return new SearchResult(URI.create(uri), 1.0, data);
    }

    private static final String STUID1 = "1.2.345.777.1";
    private static final String STUID2 = "1.2.345.777.2";
    private static final String STUID3 = "1.2.345.777.3";
    private static final String STUID4 = "1.2.345.777.4";

    private static DIMGeneric example() throws Exception {

        return new DIMGeneric(new ArrayList<>(Arrays.asList(
                createResult("file:/CR/P001/A1/1", "PatientID", "P001", "PatientName", "Patient^Anonymous",
                        "AccessionNumber", "A1", "StudyInstanceUID", STUID1, "SeriesInstanceUID", STUID1 + ".1",
                        "Modality", "CR", "SOPInstanceUID", STUID1 + ".1.1", "InstanceNumber", "1"),
                createResult("file:/CR/P001/A2/1", "PatientID", "P001", "PatientName", "Patient^Anonymous",
                        "AccessionNumber", "A2", "StudyInstanceUID", STUID2, "SeriesInstanceUID", STUID2 + ".9",
                        "Modality", "CR", "SOPInstanceUID", STUID2 + ".9.1", "InstanceNumber", "1"),
                createResult("file:/CR/P002/B2/1", "PatientID", "P002", "PatientName", "Patient^Anonymouse",
                        "AccessionNumber", "B2", "StudyInstanceUID", STUID3, "SeriesInstanceUID", STUID3 + ".1",
                        "Modality", "CR", "SOPInstanceUID", STUID3 + ".1.1", "InstanceNumber", "1"),
                createResult("file:/MG/P003/C5/1.dcm", "PatientID", "P003", "PatientName", "Patient^Anonymousse",
                        "AccessionNumber", "C5", "StudyInstanceUID", STUID4, "SeriesInstanceUID", STUID4 + ".1",
                        "Modality", "MG", "SOPInstanceUID", STUID4 + ".1.1", "InstanceNumber", "1"),
                createResult("file:/MG/P003/C5/2.dcm", "PatientID", "P003", "PatientName", "Patient^Anonymousse",
                        "AccessionNumber", "C5", "StudyInstanceUID", STUID4, "SeriesInstanceUID", STUID4 + ".1",
                        "Modality", "MG", "SOPInstanceUID", STUID4 + ".1.1", "InstanceNumber", "2"))));
    }

    @Test
    public void testDimDepth2() throws Exception {
        DIMGeneric dim = example();
        JSONObject obj = dim.getJSONObject(2, 0, 0);

        assertEquals("numResults should be the number of patient records", 3, obj.getInt("numResults"));

        JSONArray patients = obj.getJSONArray("results");
        assertNotNull("results should not be null (depth > 0)", patients);
        assertEquals("results should have the expected size", 3, patients.size());

        JSONObject patient1 = patients.getJSONObject(0);
        assertEquals("P001", patient1.getString("id"));
        assertEquals("Patient^Anonymous", patient1.getString("name"));

        JSONArray studies1 = patient1.getJSONArray("studies");
        assertEquals("studies should have 2 studies", 2, studies1.size());
        JSONObject study1 = studies1.getJSONObject(0);
        assertEquals(STUID1, study1.getString("studyInstanceUID"));

        assertFalse(study1.has("series"));
    }

    @Test
    public void testDimDepth4() throws Exception {
        DIMGeneric dim = example();
        JSONObject obj = dim.getJSONObject(4, 0, 0);

        assertEquals("numResults should be the number of patient records", 3, obj.getInt("numResults"));

        JSONArray patients = obj.getJSONArray("results");
        assertNotNull("results should not be null (depth > 0)", patients);
        assertEquals("results should have 3 patients", 3, patients.size());

        JSONObject patient1 = patients.getJSONObject(0);
        assertEquals("P001", patient1.getString("id"));
        assertEquals("Patient^Anonymous", patient1.getString("name"));

        JSONArray studies1 = patient1.getJSONArray("studies");
        assertEquals("studies should have 2 studies", 2, studies1.size());
        JSONObject study1 = studies1.getJSONObject(0);
        assertEquals(STUID1, study1.getString("studyInstanceUID"));

        JSONArray seriesArr = study1.getJSONArray("series");
        JSONObject series1 = seriesArr.getJSONObject(0);
        assertEquals(STUID1 + ".1", series1.getString("serieInstanceUID"));

        JSONArray instances = series1.getJSONArray("images");
        assertEquals("instances should have 1 instance", 1, instances.size());
        JSONObject instance1 = instances.getJSONObject(0);
        assertEquals(STUID1 + ".1.1", instance1.getString("sopInstanceUID"));
        assertEquals("file:/CR/P001/A1/1", instance1.getString("uri"));
    }

    @Test
    public void testDimDepth2Paged() throws Exception {
        DIMGeneric dim = example();
        JSONObject obj = dim.getJSONObject(2, 2, 1);

        assertEquals("numResults should be the number of patient records", 3, obj.getInt("numResults"));

        JSONArray patients = obj.getJSONArray("results");
        assertNotNull("results should not be null (depth > 0)", patients);
        assertEquals("results should have only one patient (2 were skipped)", 1, patients.size());

        JSONObject patient3 = patients.getJSONObject(0);
        assertEquals("P003", patient3.getString("id"));
        assertEquals("Patient^Anonymousse", patient3.getString("name"));

        JSONArray studies = patient3.getJSONArray("studies");
        assertEquals("studies should have 1 study", 1, studies.size());
        JSONObject study = studies.getJSONObject(0);
        assertEquals(STUID4, study.getString("studyInstanceUID"));

        assertFalse(study.has("series"));
    }

}

