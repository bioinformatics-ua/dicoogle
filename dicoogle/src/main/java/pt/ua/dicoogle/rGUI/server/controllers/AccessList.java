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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.core.settings.ServerSettings;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IAccessList;

/**
 * Controller of Access List to DICOM services
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class AccessList implements IAccessList {
    private ServerSettings settings;

    private String AETitle;
    private ArrayList<String> list;
    private boolean permitAllAETitles;

    private static Semaphore sem = new Semaphore(1, true);
    private static AccessList instance = null;

    public static synchronized AccessList getInstance()
    {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new AccessList();
            }
            sem.release();
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(QRServers.class).error(ex.getMessage(), ex);
        }
        return instance;
    }

    private AccessList(){
        settings = ServerSettings.getInstance();

        loadSettings();
    }

    /**
     * Load settings from ServerSettings
     */
    public void loadSettings(){
        AETitle = settings.getAE();
        permitAllAETitles = settings.getPermitAllAETitles();

        list = new ArrayList<String>();

        String[] CAET = settings.getCAET();
        
        if(CAET != null)
            for(String AET : CAET)
                list.add(AET);
    }

    /**
     * Save the settings related to Access List
     *
     * not write the settings in XML
     */
    public void saveSettings(){
        settings.setAE(AETitle);
        settings.setPermitAllAETitles(permitAllAETitles);

        String[] CAET = new String[list.size()];
        CAET = list.toArray(CAET);

        settings.setCAET(CAET);
    }

    /**
     *
     * @return  true - if there are unsaved settings ( != ServerSettings)
     *          false - not
     */
    public boolean unsavedSettings(){
        if(!AETitle.equals(settings.getAE()) || settings.getPermitAllAETitles() != permitAllAETitles)
            return true;
        else{
            ArrayList<String> tmp = new ArrayList<String>();

            String[] CAET = settings.getCAET();

            if(CAET != null)
                for(String AET : CAET)
                    tmp.add(AET);
            else if(list.size() != 0)
                return true;

            if (!tmp.equals(list))
                return true;
        }

        return false;
    }

    @Override
    public String getAETitle() throws RemoteException {
        return AETitle;
    }

    @Override
    public void setAETitle(String AETitle) throws RemoteException {
        if(AETitle == null)
            this.AETitle = " ";
        else
            this.AETitle = AETitle;
    }

    @Override
    public ArrayList<String> getAccessList() throws RemoteException {

        list.trimToSize(); //trim size of ArrayList

        return list;
    }

    @Override
    public boolean addToAccessList(String AETitle) throws RemoteException {
        if(!list.contains(AETitle))
            return list.add(AETitle);
        else
            return false;
    }

    @Override
    public boolean removeFromAccessList(String AETitle) throws RemoteException {
           
        return list.remove(AETitle);
    }

    @Override
    public void setPermitAllAETitles(boolean value) throws RemoteException {
        permitAllAETitles = value;
    }

    @Override
    public boolean getPermitAllAETitles() throws RemoteException {
        return permitAllAETitles;
    }

}
