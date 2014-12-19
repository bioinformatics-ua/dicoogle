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
package pt.ua.dicoogle.rGUI.client.signals;

import java.rmi.RemoteException;
import pt.ua.dicoogle.rGUI.client.UIHelper.Result2Tree;
import pt.ua.dicoogle.rGUI.interfaces.signals.ISearchSignal;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class SearchSignal implements ISearchSignal {
    private Result2Tree result;

    public SearchSignal(Result2Tree result) throws NullPointerException{
        if (result == null)
            throw new NullPointerException("main can't be null");

        this.result = result;
    }

    /**
     *
     * @param flag
     *              0 - new ArrayList<SearchResult> with the local results
     *              1 - new ArrayList<SearchResultP2P> with the P2P results
     *              2 - new SearchTime
     *              3 - new ArrayList<SearchResultP2P> with P2P Thumbnails requested
     *              4 - new ArrayList<SearchResult> with the results
     *              5 - Finish search
     * @throws RemoteException
     */
    @Override
    public void sendSearchSignal(int flag) throws RemoteException {
        switch(flag){
            case 0:
                result.getLocalSearchResults();
                break;

            case 1:
                result.getP2PSearchResults();
                break;

            case 2:
                result.getSearchTime();
                break;

            case 3:
                result.getPendingP2PThumbnails();
                break;

            case 4:
                result.getExportSearchResults();
                break;
                
            case 5:
                result.finishSearch();
                break;
     
                

        }
    }

}
