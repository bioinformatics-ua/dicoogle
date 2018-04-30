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
package pt.ua.dicoogle.rGUI.server.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import org.slf4j.LoggerFactory;

import pt.ua.dicoogle.rGUI.interfaces.controllers.ILogs;
import pt.ua.dicoogle.DicomLog.LogDICOM;
import pt.ua.dicoogle.DicomLog.LogLine;
import pt.ua.dicoogle.rGUI.interfaces.signals.ILogsSignal;
import pt.ua.dicoogle.server.users.UserSessionsLog;
import pt.ua.dicoogle.server.FileWatcher;
import pt.ua.dicoogle.sdk.Utils.Platform;

/**
 * Controller of Logs
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class Logs implements ILogs {
    // Server Log (activities in server)

    private static String logfilename;
    private static File logfile;
    private ILogsSignal signalBack;

    private String serverLog;
    private static Semaphore sem = new Semaphore(1, true);
    private static Logs instance = null;

    // saves the Loglines that are pending to send to GUI client
    private ArrayList<LogLine> partialDICOMLog;
    private String partialServerLog;

    public static synchronized Logs getInstance() {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new Logs();
            }
            sem.release();

        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(Logs.class).error(ex.getMessage(), ex);
        }
        return instance;
    }

    private Logs() {

        // defaul log file
        logfilename = Platform.homePath() + "DICOMLOG.log";
        logfile = new File(logfilename);

        partialDICOMLog = new ArrayList<LogLine>();
        partialServerLog = "";

        /**
         * File watcher of Log File, just to keep updated the
         * interface log
         */
        TimerTask task = new FileWatcher(logfile) {

            @Override
            protected void onChange(File file) {
                reloadtext(file);
            }
        };

        java.util.Timer timer = new java.util.Timer();
        // repeat the check every second
        timer.schedule(task, new Date(), 1000);
    }

    public void resetSignalBack(){
        signalBack = null;
    }
    
    @Override
    public void RegisterSignalBack(ILogsSignal signalBack) throws RemoteException {
        this.signalBack = signalBack;
        
        try {
            partialDICOMLog.addAll(LogDICOM.getInstance().getLl());
            partialServerLog = UserSessionsLog.getInstance().readLog();

            if(signalBack != null){
                signalBack.sendLogSignal(0);
                signalBack.sendLogSignal(2);
            }
        } catch (RemoteException ex) {
            //Logger.getLogger(Logs.class.getName()).log(Level.SEVERE, null, ex);
            //DebugManager.getSettings().debug("Problem sending signal to log: 4");
        }
        
        serverLog = null;
        reloadtext(logfile);
    }


    public void reloadtext(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            int x = fis.available();
            byte b[] = new byte[x];
            fis.read(b);
            String content = new String(b);
            if (!content.equals(serverLog)) {
                serverLog = content;

                if(signalBack != null)
                    signalBack.sendLogSignal(1);
            }
        } catch (IOException ex) {
            //Logger.getLogger(Logs.class.getName()).log(Level.SEVERE, null, ex);
            //DebugManager.getSettings().debug("Problem sending signal to log: 1");
        }
    }

    public void addLog(LogLine line) {
        partialDICOMLog.add(line);

        try {
            if(signalBack != null)
                signalBack.sendLogSignal(0);
        } catch (RemoteException ex) {
            //Logger.getLogger(Logs.class.getName()).log(Level.SEVERE, null, ex);
            //DebugManager.getSettings().debug("Problem sending signal to log: 2");
        }
    }

    public void addServerLog(String text){
        FileOutputStream fos = null;
        PrintStream pos = null;
        
        try {
            fos = new FileOutputStream(logfilename, true);
            pos = new PrintStream(fos);
            pos.print(getDateTime().concat(": ").concat(text).concat("\n"));

            pos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSessionsLog(String line){
        partialServerLog = partialServerLog + line;

        try {
            if(signalBack != null)
                signalBack.sendLogSignal(2);
        } catch (RemoteException ex) {
            //Logger.getLogger(Logs.class.getName()).log(Level.SEVERE, null, ex);
            //DebugManager.getSettings().debug("Problem sending signal to log: 3");
        }
    }

    @Override
    public void clearDICOMLog() throws RemoteException {
        partialDICOMLog.clear();
        LogDICOM.getInstance().clearLog();

        //DebugManager.getSettings().debug("Clear DICOM Log");
    }

    @Override
    public void clearServerLog() throws RemoteException {
        FileOutputStream fos = null;
        PrintStream pos = null;

        /*
         * To clear the log, writes an empty string to the logfile
         */
        try {
            fos = new FileOutputStream(logfilename);
            pos = new PrintStream(fos);
            pos.print("");

            pos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearSessionsLog() throws RemoteException {
        partialServerLog = "";
        UserSessionsLog.getInstance().cleanLog();
    }

    @Override
    public ArrayList<LogLine> getPendingDICOMLog() throws RemoteException {
        ArrayList<LogLine> log = new ArrayList<LogLine>(partialDICOMLog);

        partialDICOMLog.clear();
        
        return log;
    }

    @Override
    public String getServerLog() throws RemoteException {
        return serverLog;
    }

    @Override
    public String getPendingSessionsLog() throws RemoteException {
        String tmp =  partialServerLog;

        partialServerLog = "";

        return tmp;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
