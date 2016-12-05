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

import aclmanager.core.LuceneQueryACLManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Executor;

import org.dcm4che2.data.*;


import javax.xml.transform.TransformerConfigurationException;

import org.dcm4che2.net.Association;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.service.CFindService;


import pt.ua.dicoogle.DicomLog.LogDICOM;
import pt.ua.dicoogle.DicomLog.LogLine;


import pt.ua.dicoogle.DicomLog.LogXML;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.rGUI.server.controllers.Logs;



import pt.ua.dicoogle.server.DicomNetwork;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class CFindServiceSCP extends CFindService {

    private ServerSettings s = ServerSettings.getInstance();
    private int rspdelay = ServerSettings.getInstance().getRspDelay();
    
    private DicomNetwork service = null;
    private LuceneQueryACLManager luke = null;

    private boolean superSpeed = false;
    

    public CFindServiceSCP(String[] multiSop, Executor e) {
        super(multiSop, e);
        this.luke = null;        
    }

    public CFindServiceSCP(String[] multiSop, Executor e, LuceneQueryACLManager luke) {
        super(multiSop, e);
        this.luke = luke;        
    }
    
    /*** CFIND */
    @Override
    protected synchronized DimseRSP doCFind(Association as, int pcid,
            DicomObject cmd, DicomObject keys, DicomObject rsp)
            throws DicomServiceException {

        //DebugManager.getInstance().debug("doCFind? -- > working on it");


        DimseRSP replay = null;

        /**
         * ///  How create a new Connection? Detect new connections..
        if (MainWindow.getMw() != null) {
        MainWindow.getMw().newClientConnection(as);
        }
         */
        /**
         * Verify Permited AETs
         */
        //DebugManager.getInstance().debug(":: Verify Permited AETs @??C-FIND Action ");
        boolean permited = false;

        if (s.getPermitAllAETitles()) {
            permited = true;
        } else {
            String permitedAETs[] = s.getCAET();

            for (int i = 0; i < permitedAETs.length; i++) {
                if (permitedAETs[i].equals(as.getCallingAET())) {
                    permited = true;
                    break;
                }
            }
        }


        if (!permited) {
            //DebugManager.getInstance().debug("Client association NOT permited: " + as.getCallingAET() + "!");
            //as.abort();

            //return new FindRSP(keys, rsp, null);
        } else {
            //DebugManager.getInstance().debug("Client association permited: " + as.getCallingAET() + "!");
        }


        if (this.rspdelay > 0) {
            try {
                this.wait(this.rspdelay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * Search information at Lucene Indexer
         * So the FindRSP will fill the DimRSP
         */
        replay = new FindRSP(keys, rsp,  as.getCallingAET(), luke);


        DicomElement e = keys.get(Tag.PatientName);
        String add = "";
        if (e != null) {
            add = new String(e.getBytes());
        }

        String queryParams = "";

        for (Iterator<DicomElement> iterator = keys.iterator(); iterator.hasNext();)
        {
            DicomElement element = iterator.next();

            if (!element.isEmpty())
            {
                if (!ElementDictionary.getDictionary().nameOf(element.tag()).contains("Sequence"))
                    queryParams += ElementDictionary.getDictionary().nameOf(element.tag()) + " - " + element.getValueAsString(new SpecificCharacterSet("UTF-8"), 0) + " ";
            }
        }
        if (!superSpeed)
        {
            LogLine ll = new LogLine("cfind", getDateTime(), as.getCallingAET(),
                    as.toString() + " -- " + add, queryParams);
            LogDICOM.getInstance().addLine(ll);

            synchronized (LogDICOM.getInstance()) {
                LogXML l = new LogXML();
                try {
                    l.printXML();
                } catch (TransformerConfigurationException ex) {
                    ex.printStackTrace();
                }
            }

        }
        //Logs.getInstance().addLog(ll);


        return replay;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * @return the service
     */
    public DicomNetwork getService() {
        return service;
    }

    /**<
     * @param service the service to set
     */
    public void setService(DicomNetwork service) {
        this.service = service;
    }
}
