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
package pt.ua.dicoogle.server.web.rest.elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author samuelcampos
 */
public class TExamTime extends Thread {

    private File file;
    private boolean stop = false;

    public TExamTime(File file) {
        if (file.isDirectory()) {
            throw new IllegalArgumentException("Invalid FILE");
        }

        this.file = file;
    }

    public synchronized void stopThread() {
        if(stop)
            return; // A Thread j?? parou ou est?? a parar
        
        stop = true;

        try {
            while (stop == true) {
                wait();
            }
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(TExamTime.class).error(ex.getMessage(), ex);
        }
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException();/*
        long beginTime = System.currentTimeMillis();
        long endTime;
        ExamTimeCore examTime = ExamTimeCore.getSettings();
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file, false));


            IndexEngine core = IndexEngine.getSettings();

            Set<String> enumList = core.enumField("AccessionNumber", false);
            
            examTime.setTotal(enumList.size());

            List<String> extrafields = new ArrayList<String>();
            extrafields.addMoveDestination("SeriesInstanceUID");
            extrafields.addMoveDestination("AcquisitionDateTime");
            extrafields.addMoveDestination("AcquisitionTime");
            extrafields.addMoveDestination("AcquisitionDate");


            int i = 0, j = 0, k = 0;
            for (String accessionNumber : enumList) {

                List<SearchResult> results = core.searchSync("AccessionNumber:" + accessionNumber, extrafields);

                HashMap<String, List<String>> DateTimes = new HashMap<String, List<String>>();

                for (SearchResult result : results) {
                    synchronized (this) {
                        if (stop) {
                            stop = false;
                            out.close();
                            
                            notify();
                            return;
                        }
                    }


                    Hashtable<String, String> fields = result.getExtrafields();

                    String SeriesInstanceUID = fields.get("SeriesInstanceUID");

                    if (SeriesInstanceUID == null) {
                        // Its not possible to calculate the Series Time!
                        throw new NullPointerException("SeriesInstanceUID IS NULL!!!!");
                    }

                    String time = fields.get("AcquisitionDateTime");

                    if (time != null) {
                        List<String> lt = DateTimes.get(SeriesInstanceUID);

                        if (lt == null) {
                            lt = new ArrayList<String>();
                            DateTimes.put(SeriesInstanceUID, lt);
                        }

                        lt.addMoveDestination(time);

                    } else {
                        time = fields.get("AcquisitionTime");
                        String date = fields.get("AcquisitionDate");

                        if (time != null) {
                            if (date == null) {
                                throw new NullPointerException("DATE IS NULL!!!!");
                            }

                            List<String> lt = DateTimes.get(SeriesInstanceUID);

                            if (lt == null) {
                                lt = new ArrayList<String>();
                                DateTimes.put(SeriesInstanceUID, lt);
                            }

                            lt.addMoveDestination(date + time);
                        } else {
                            k++;
                        }
                    }

                    j++;
                }

                if (!DateTimes.isEmpty()) {
                    HashMap<String, SimpleEntry<Integer, Integer>> times = getSeriesDateTimes(DateTimes);

                    writeFileLine(out, accessionNumber, times.values());
                }
                
                
                examTime.increment();

//                i++;
//
//                if (i == 50) {
//                    break;
//                }
            }
            
            examTime.threadFinished();
            
        } catch (Exception ex) {
            LoggerFactory.getLogger(TExamTime.class).error(ex.getMessage(), ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                LoggerFactory.getLogger(TExamTime.class).error(ex.getMessage(), ex);
            }
        }

        synchronized (this) {
            stop = true;
            notify();
        }
        
        endTime = System.currentTimeMillis();
        
        System.out.println("SpentTime (ms): " + (endTime - beginTime));*/
    }

    private void writeFileLine(BufferedWriter out, String accessionNumber, Collection<SimpleEntry<Integer, Integer>> times) throws IOException {
        StringBuilder st = new StringBuilder();

        st.append(accessionNumber).append(";");

        for (SimpleEntry<Integer, Integer> time : times) {
            st.append(time.getKey()).append(";").append(time.getValue()).append(";");
        }
        st.deleteCharAt(st.length()-1); 
        st.append("\n");


        out.write(st.toString());
    }

    /**
     * Calcula o tempo de dura????o de cada uma das S??ries, dado tempos em "Ano,
     * M??s, Dia, Hora, Minuto, Segundo"
     *
     * @param dateTime
     * @return
     */
    private HashMap<String, SimpleEntry<Integer, Integer>> getSeriesDateTimes(HashMap<String, List<String>> dateTime) {
        HashMap<String, SimpleEntry<Integer, Integer>> result = new HashMap<String, SimpleEntry<Integer, Integer>>();

        for (String SeriesInstanceUID : dateTime.keySet()) {
            List<String> times = dateTime.get(SeriesInstanceUID);

            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;


            for (String time : times) {
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.valueOf(time.substring(0, 4)), Integer.valueOf(time.substring(4, 6)), Integer.valueOf(time.substring(6, 8)), Integer.valueOf(time.substring(8, 10)), Integer.valueOf(time.substring(10, 12)), Integer.valueOf(time.substring(12, 14)));

                int actual = (int) (cal.getTimeInMillis() / 1000);

                if (actual < min) {
                    min = actual;
                }

                if (actual > max) {
                    max = actual;
                }
            }

            result.put(SeriesInstanceUID, new SimpleEntry<Integer, Integer>((max - min), times.size()));
        }

        return result;
    }
}
