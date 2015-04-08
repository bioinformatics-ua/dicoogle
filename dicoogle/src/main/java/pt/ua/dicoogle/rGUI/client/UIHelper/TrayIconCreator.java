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

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ua.dicoogle.rGUI.client.ClientCore;
import pt.ua.dicoogle.rGUI.client.windows.About;
import pt.ua.dicoogle.rGUI.client.windows.ClientOptions;
import pt.ua.dicoogle.rGUI.client.windows.MainWindow;
import pt.ua.dicoogle.rGUI.client.windows.ServerOptions;

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
    
    
    private TrayIconCreator() {
        setTrayIcon();
    }


    private void setTrayIcon(){
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = getImage("trayicon.gif");

            MouseListener mouseListener = new MouseListener() {

                public void mouseClicked(MouseEvent e) {
                    MainWindow m = MainWindow.getInstance();

                    if (e.getClickCount() == 2 && m.getExtendedState() == MainWindow.ICONIFIED) {
                        m.setExtendedState(MainWindow.NORMAL);
                        m.setVisible(true);
                    }

                    m.toFront();
                }

                public void mouseEntered(MouseEvent e) {
                    //System.out.println("Tray Icon - Mouse entered!");
                }

                public void mouseExited(MouseEvent e) {
                    //System.out.println("Tray Icon - Mouse exited!");
                }

                public void mousePressed(MouseEvent e) {
                    //System.out.println("Tray Icon - Mouse pressed!");
                }

                public void mouseReleased(MouseEvent e) {
                    //System.out.println("Tray Icon - Mouse released!");
                }
            };


            PopupMenu popup = new PopupMenu();


            MenuItem mainItem = new MenuItem("Show Dicoogle Window");
            mainItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    MainWindow m = MainWindow.getInstance();
                    
                    m.setExtendedState(MainWindow.NORMAL);
                    m.setVisible(true);
                    m.toFront();
                }
            });

            popup.add(mainItem);
            popup.addSeparator();


            ClientCore core = ClientCore.getInstance();

            if(core.isAdmin()){
                MenuItem settingsItem = new MenuItem("Server Preferences");
                settingsItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        ServerOptions s = ServerOptions.getInstance();
                        s.setReturnToMain(false);
                        s.setVisible(true);
                    }
                });


                popup.add(settingsItem);
            }

            if(core.isUser()){
                MenuItem settingsItem = new MenuItem("Client Preferences");
                settingsItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        ClientOptions s = ClientOptions.getInstance();
                        s.setVisible(true);
                    }
                });


                popup.add(settingsItem);
            }

            popup.addSeparator();

            MenuItem aboutItem = new MenuItem("About");
            aboutItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    About a = About.getInstance();
                    a.setReturnToMain(false);
                    a.setVisible(true);
                }
            });

            popup.add(aboutItem);
            popup.addSeparator();

            MenuItem defaultItem = new MenuItem("Exit");
            defaultItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    MainWindow m = MainWindow.getInstance();
                    m.dispatchEvent(new java.awt.event.WindowEvent(m, java.awt.Event.WINDOW_DESTROY));
                }
            });

            
            
            popup.add(defaultItem);

            trayIcon = new TrayIcon(image, "Dicoogle PACS", popup);

            ActionListener actionListener = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                }
            };

            trayIcon.addActionListener(actionListener);
            trayIcon.setImageAutoSize(true);

            trayIcon.addMouseListener(mouseListener);

            try {
                tray.add(trayIcon);

            } catch (AWTException e) {
                //System.err.println("TrayIcon could not be added.");
            }

        } else {
            //DebugManager.getInstance().debug("System Tray is not supported");
        }
    }

    public void distroyTrayIcon(){
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            
            tray.remove(trayIcon);
            trayIcon = null;
        }
    }
    
}
