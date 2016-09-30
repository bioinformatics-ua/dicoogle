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

import pt.ua.dicoogle.rGUI.interfaces.controllers.IQRServers;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.core.settings.ServerSettings;

/**
 * Controller of Query/Retrieve Servers Settings
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class QRServers implements IQRServers {

    private ServerSettings settings = null;

    private static Semaphore sem = new Semaphore(1, true);
    private static QRServers instance = null;


    private ArrayList<MoveDestination> moves;
    
    public static synchronized QRServers getInstance()
    {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new QRServers();
            }
            sem.release();
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(QRServers.class).error(ex.getMessage(), ex);
        }
        return instance;
    }

    private QRServers(){
        settings = ServerSettings.getInstance();

        loadSettings();
    }

    public void loadSettings(){
        moves = new ArrayList<MoveDestination>();
        
        moves.addAll(settings.getMoves());
    }


    /**
     * Save the settings related to Startup Services
     *
     * not write the settings in XML
     */
    public void saveSettings(){
        settings.setMoves(moves);
    }

    /**
     *
     * @return  true - if there are unsaved settings ( != ServerSettings)
     *          false - not
     */
    public boolean unsavedSettings(){
        if(!moves.equals(settings.getMoves()))
            return true;

        return false;
    }

    /**
     * Add one Query/Retrieve Server to the list
     *
     * @param move
     * @return
     */
    @Override
    public boolean AddEntry(MoveDestination move){

        if(!moves.contains(move))
        {
            moves.add(move);
            return true;
        }

        return false;
    }

    @Override
    public boolean RemoveEntry(MoveDestination move){
        return moves.remove(move);
    }

    @Override
    public ArrayList<MoveDestination> getMoves(){
        return moves;
    }
}
