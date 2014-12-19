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
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.rGUI.interfaces.IUser;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IDicomSend;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISearch;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
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
            Logger.getLogger(ClientCore.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(AdminRefs.class.getName()).log(Level.SEVERE, null, ex);

                    //TODO: se falhar, terá implicações... o programa cliente deve parar
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
