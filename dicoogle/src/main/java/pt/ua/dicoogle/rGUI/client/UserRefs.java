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
package pt.ua.dicoogle.rGUI.client;

import java.rmi.RemoteException;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.rGUI.interfaces.IUser;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IDicomSend;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISearch;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class UserRefs {

    private ISearch search;
    private IDicomSend dicomSend;

    private IUser user;

    private static UserRefs instance;
    private static Semaphore sem = new Semaphore(1, true);

    public static synchronized UserRefs getUserRefs(final IUser user) {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new UserRefs(user);
            }
            sem.release();
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(ClientCore.class).error(ex.getMessage(), ex);
        }
        return instance;
    }

    public static UserRefs getInstance() {
        return instance;
    }
    
    private UserRefs(IUser user){
        this.user = user;

        getRefs();
    }

    /**
     * This function is responsible for obtaining references to Remote user objects
     */
    private void getRefs() {
        class GetReferences  {


            public void run() {
                try {
                    //System.out.println("Starting to adquire User Refs");

                    search = user.getSearch();
                    dicomSend = user.getDicomSend();

                    //System.out.println("User Refs Adquired");
                } catch (RemoteException ex) {
                    LoggerFactory.getLogger(AdminRefs.class).error(ex.getMessage(), ex);

                    //TODO: se falhar, ter?? implica????es... o programa cliente deve parar
                }
            }
        }
        GetReferences getRefs = new GetReferences();

        //starts the Thread that will get the remote administrations object references
        getRefs.run();

    }

    /**
     * @return the search
     */
    public ISearch getSearch() {
        return search;
    }

    /**
     * @return the dicomSend
     */
    public IDicomSend getDicomSend() {
        return dicomSend;
    }
}
