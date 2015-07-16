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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.dicoogle.server.web.rest.elements;

import java.io.File;

/**
 *
 * @author samuelcampos
 */
public class ExamTimeCore {

    private static final String examTimeFile = "ExamTime.csv";
    private static ExamTimeCore instance;
    private ExamTimeState actualState;
    private File file;
    private TExamTime thread = null;
    private int total;
    private int i;

    public synchronized static ExamTimeCore getInstance() {
        if (instance == null) {
            instance = new ExamTimeCore();
        }

        return instance;
    }

    private ExamTimeCore() {
        file = new File(examTimeFile);

        if (file.exists()) {
            actualState = ExamTimeState.READY;
        } else {
            actualState = ExamTimeState.EMPTY;
        }
    }
    
    
    public synchronized File getFile(){
        if(actualState.equals(ExamTimeState.READY))
            return file;
        
        return null;
    }
    
    public synchronized void startThread(){
        if(thread != null){
            thread.stopThread();
        }
        
        i = 0;
        
        thread = new TExamTime(file);
//        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
        
        actualState = ExamTimeState.RUNNING;
    }
    
    public synchronized void stopThread(){
        if(thread != null){
            thread.stopThread();
            
            file.delete(); // Apaga o ficheiro pois ele não está acabado
        }
        
        thread = null;
        
        actualState = ExamTimeState.EMPTY;
    }
    
    protected synchronized void threadFinished(){        
        thread = null;
        actualState = ExamTimeState.READY;
    }
    
    protected synchronized void setTotal(int total){
        this.total = total;
    }
    protected synchronized void increment(){
        i++;
    }
    public synchronized float getPercentage(){
        return ((float) i) / ((float) total) * 100;
    }
    

    public synchronized ExamTimeState getState() {
        return actualState;
    }

    public enum ExamTimeState {

        EMPTY,
        RUNNING,
        READY
    }
}
