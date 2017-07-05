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
package pt.ua.dicoogle.rGUI.client.UIHelper;

import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.rGUI.client.windows.MainWindow;

import java.awt.*;
import java.net.URL;
import java.util.concurrent.Semaphore;

/**
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class TrayIconCreator {

    private static TrayIconCreator instance = null;
    private static Semaphore sem = new Semaphore(1, true);

    private TrayIcon trayIcon;

    public static synchronized TrayIconCreator getInstance() {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new TrayIconCreator();
            }
            sem.release();
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(MainWindow.class).error(ex.getMessage(), ex);
        }
        return instance;
    }

    public static Image getImage(final String pathAndFileName) {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(pathAndFileName);
        return Toolkit.getDefaultToolkit().getImage(url);
    }

}
