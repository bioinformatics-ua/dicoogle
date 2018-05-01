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
package pt.ua.dicoogle.rGUI.client.windows;

import java.rmi.RemoteException;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IServices;
import pt.ua.dicoogle.rGUI.client.AdminRefs;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPluginControllerAdmin;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class Services extends javax.swing.JFrame
{
    private ArrayList<buttonNlabel> listbuttons;
    private static Semaphore sem = new Semaphore(1, true);
    private static Services instance = null;
    private static IServices serv;
    private static IPluginControllerAdmin plugin;
    //private static INetworkInterfaces networkInterfaces;
    private ImageIcon startIcon = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("playSmall.png"));
    private ImageIcon stopIcon = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("stopSmall.png"));

    
    
     public static Image getImage(final String pathAndFileName) {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(pathAndFileName);
        return Toolkit.getDefaultToolkit().getImage(url);
    }
    
    private class buttonNlabel
    {

        private JButton button;
        private JLabel label;
        private JLabel labelName;
        private IPluginControllerAdmin pluginController;

        
        
        public buttonNlabel(boolean isRunning, String labelName)
        {
            this.pluginController = AdminRefs.getInstance().getPluginController();

            this.labelName = new JLabel();
            this.labelName.setText(labelName);

            this.label = new JLabel();

            this.button = new JButton();

            if (isRunning)
            {
                label.setText("Running");
                button.setText("Stop");
                button.setIcon(stopIcon);
            } else
            {
                label.setText("Stopped");
                button.setText("Start");
                button.setIcon(startIcon);
            }
            button.addActionListener(new java.awt.event.ActionListener()
            {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    buttonActionPerformed(evt);
                }
            });
        }

        private void buttonActionPerformed(java.awt.event.ActionEvent evt)
        {
            try
            {
                if (this.pluginController.isRunning(this.labelName.getText()))
                {
                    this.pluginController.StopPlugin(this.labelName.getText());
                } else
                {
                    this.pluginController.InitiatePlugin(this.labelName.getText());
                }
                refreshInterface();
            } catch (RemoteException ex)
            {
                LoggerFactory.getLogger(Services.class).error(ex.getMessage(), ex);
            }
        }

        /* public void setIsRunning(boolean isRunning)
        {
        if (isRunning)
        {
        label.setText("Running");
        button.setText("Stop");
        button.setIcon(stopIcon);
        } else
        {
        label.setText("Stopped");
        button.setText("Start");
        button.setIcon(startIcon);
        }
        }*/
        public void refresh()
        {
            try
            {
                if (this.pluginController.isRunning(this.labelName.getText()))
                {
                    label.setText("Running");
                    button.setText("Stop");
                    button.setIcon(stopIcon);
                } else
                {
                    label.setText("Stopped");
                    button.setText("Start");
                    button.setIcon(startIcon);
                }
            } catch (RemoteException ex)
            {
                LoggerFactory.getLogger(Services.class).error(ex.getMessage(), ex);
            }
        }

        public JButton getButton()
        {
            return button;
        }

        public JLabel getLabel()
        {
            return label;
        }

        public JLabel getLabelName()
        {
            return labelName;
        }


    }
    

    public static synchronized Services getInstance()
    {
        try
        {
            sem.acquire();
            if (instance == null)
            {
                instance = new Services();
            }
            sem.release();
        } catch (InterruptedException ex)
        {
//            LoggerFactory.getLogger(MainWindow.class.getName()).log(Level.FATAL, null, ex);
        }
        return instance;
    }

    /** Creates new form Services */
    private Services()
    {
        plugin = AdminRefs.getInstance().getPluginController();
        List<String> names = null;
        try
        {
            names = plugin.getPluginNames();
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Services.class).error(ex.getMessage(), ex);
        }

        this.listbuttons = new ArrayList<buttonNlabel>();
        if(names != null)
        {
            for(String pluginName: names)
            {
                try
                {
                    this.listbuttons.add(new buttonNlabel(plugin.isRunning(pluginName), pluginName));
                } catch (RemoteException ex)
                {
                    LoggerFactory.getLogger(Services.class).error(ex.getMessage(), ex);
                }
            }
        }

        initComponents();

        javax.swing.GroupLayout jPanel4Layout = (GroupLayout) jPanel4.getLayout();
        javax.swing.GroupLayout jPanel5Layout = (GroupLayout) jPanel5.getLayout();
        javax.swing.GroupLayout jPanel2Layout = (GroupLayout) jPanel2.getLayout();

        ParallelGroup buttonsH = jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonQR)
                    .addComponent(jButtonStorage)
                    .addComponent(jButtonWeb)
                    .addComponent(jButtonWebServices);
        SequentialGroup buttonsV = jPanel4Layout.createSequentialGroup()
                .addComponent(jButtonStorage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonQR)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonWeb)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonWebServices);

        ParallelGroup namesH = jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7);
        SequentialGroup namesV = jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jLabel7);

        ParallelGroup statesH = jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE);
        SequentialGroup statesV = jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(jLabel11);

        for(buttonNlabel bNl : this.listbuttons)
        {
            buttonsH.addComponent(bNl.getButton());
            buttonsV.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(bNl.getButton());

            namesH.addComponent(bNl.getLabelName());
            namesV.addGap(18, 18, 18).addComponent(bNl.getLabelName());

            statesH.addComponent(bNl.getLabel());
            statesV.addGap(18, 18, 18).addComponent(bNl.getLabel());
        }

        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonsH)
                .addContainerGap(16, Short.MAX_VALUE)));
        
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsV.addContainerGap(51, Short.MAX_VALUE)));

        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(namesH)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(namesV.addContainerGap(71, Short.MAX_VALUE))
        );

        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statesH)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statesV.addContainerGap(71, Short.MAX_VALUE))
        );

        Services.serv = AdminRefs.getInstance().getServices();
      //  Services.networkInterfaces = AdminRefs.getSettings().getNetworkInterfaces();
        Services.plugin = AdminRefs.getInstance().getPluginController();
        Image image = Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("trayicon.gif"));
        this.setIconImage(image);

        refreshInterface();
    }

    /**
     * Refresh buttons and labels
     * with current running services
     *
     */
    private void refreshInterface()
    {
        try
        {
            if (serv.storageIsRunning())
            {
                jLabel8.setText("Running");
                jButtonStorage.setText("Stop");
                jButtonStorage.setIcon(stopIcon);
            } else
            {
                jLabel8.setText("Stopped");
                jButtonStorage.setText("Start");
                jButtonStorage.setIcon(startIcon);
            }
            if (serv.queryRetrieveIsRunning())
            {
                jLabel9.setText("Running");
                jButtonQR.setText("Stop");
                jButtonQR.setIcon(stopIcon);
            } else
            {
                jLabel9.setText("Stopped");
                jButtonQR.setText("Start");
                jButtonQR.setIcon(startIcon);
            }
            if (serv.webServerIsRunning())
            {
                jLabel10.setText("Running");
                jButtonWeb.setText("Stop");
                jButtonWeb.setIcon(stopIcon);
            } else
            {
                jLabel10.setText("Stopped");
                jButtonWeb.setText("Start");
                jButtonWeb.setIcon(startIcon);
            }
            if (serv.webServicesIsRunning())
            {
                jLabel11.setText("Running");
                jButtonWebServices.setText("Stop");
                jButtonWebServices.setIcon(stopIcon);
            } else
            {
                jLabel11.setText("Stopped");
                jButtonWebServices.setText("Start");
                jButtonWebServices.setIcon(startIcon);
            }

            for (buttonNlabel b : this.listbuttons)
            {
                b.refresh();
            }
            /*            if (serv.p2PIsRunning()) {
            jLabel13.setText("Running");
            jButtonP2P.setText("Stop");
            jButtonP2P.setIcon(stopIcon);
            } else {
            jLabel13.setText("Stopped");
            jButtonP2P.setText("Start");
            jButtonP2P.setIcon(startIcon);
            }*/
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Services.class).error(ex.getMessage(), ex);
        }
        //Updates allways the members list.
    /*    jComboBox1.removeAllItems();
        try
        {
            for (String networkInterface : networkInterfaces.getNetworkInterfaces())
            {
                jComboBox1.addItem(networkInterface);
            }
            if (networkInterfaces.getNetworkInterface() != null)
            {
                this.jComboBox1.setSelectedItem(networkInterfaces.getNetworkInterface());
            }
               if(networkInterfaces.getNetworkInterface() == null)
            networkInterfaces.setNetworkInterface((String) this.jComboBox1.getSelectedItem());
            else
            {
            System.out.println(networkInterfaces.getNetworkInterface());
            this.jComboBox1.setSelectedItem(networkInterfaces.getNetworkInterface());
            }
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Services.class).error(ex.getMessage(), ex);
        }*/
    }

    private void showOptions()
    {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButtonStorage = new javax.swing.JButton();
        jButtonQR = new javax.swing.JButton();
        jButtonWeb = new javax.swing.JButton();
        jButtonWebServices = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jButtonQRStorageServers = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Dicoogle Services");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel8.setText("status");

        jLabel9.setText("status");

        jLabel10.setText("status");

        jLabel11.setText("status");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addContainerGap(76, Short.MAX_VALUE))
        );

        jLabel8.getAccessibleContext().setAccessibleName("storageStatusLabel");

        jLabel2.setText("Service");

        jLabel3.setText("Status");

        jLabel4.setText("Control");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addGap(90, 90, 90)
                .addComponent(jLabel3)
                .addGap(72, 72, 72)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("DICOM Storage:");

        jLabel5.setText("DICOM Query/Retrieve:");

        jLabel6.setText("Dicoogle Web:");

        jLabel7.setText("Dicoogle WebServices:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addContainerGap(76, Short.MAX_VALUE))
        );

        jButtonStorage.setIcon(new ImageIcon(getImage("playSmall.png")));
        jButtonStorage.setText("Start");
        jButtonStorage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStorageActionPerformed(evt);
            }
        });

        jButtonQR.setIcon(new ImageIcon(getImage("playSmall.png")));
        jButtonQR.setText("Start");
        jButtonQR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonQRActionPerformed(evt);
            }
        });

        jButtonWeb.setIcon(new ImageIcon(getImage("playSmall.png")));
        jButtonWeb.setText("Start");
        jButtonWeb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonWebActionPerformed(evt);
            }
        });

        jButtonWebServices.setIcon(new ImageIcon(getImage("playSmall.png")));
        jButtonWebServices.setText("Start");
        jButtonWebServices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonWebServicesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonQR)
                    .addComponent(jButtonStorage)
                    .addComponent(jButtonWeb)
                    .addComponent(jButtonWebServices))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButtonStorage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonQR)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonWeb)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonWebServices)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        jButtonQRStorageServers.setIcon(new ImageIcon(getImage("data-server.png")));
        jButtonQRStorageServers.setText("Storage Servers");
        jButtonQRStorageServers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonQRStorageServersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonQRStorageServers)
                .addContainerGap(111, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jButtonQRStorageServers)
                .addContainerGap(113, Short.MAX_VALUE))
        );

        jLabel12.setText("To define Dicoogle Inicial Services go to: Preferences -> Inicial Services");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel12)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addGap(9, 9, 9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        MainWindow main = MainWindow.getInstance();

        main.toFront();
        main.setEnabled(true);

        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonStorageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStorageActionPerformed
        try
        {
            if (serv.storageIsRunning())
            {
                serv.stopStorage();
            } else
            {
                try
                {
                    if (serv.startStorage() == -1)
                    {
                        int choice = JOptionPane.showConfirmDialog(null, "The server's storage path is not defined. Do you wish to define it?", "Error: Undefined Storage Path", JOptionPane.YES_NO_OPTION);
                        if (choice == 0)
                        {
                            showOptions();
                        }
                    }
                } catch (IOException ex)
                {
                    LoggerFactory.getLogger(Services.class).error(ex.getMessage(), ex);
                }
            }
            refreshInterface();
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Services.class).error(ex.getMessage(), ex);
        }
    }//GEN-LAST:event_jButtonStorageActionPerformed

    private void jButtonQRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQRActionPerformed
        try
        {
            if (serv.queryRetrieveIsRunning())
            {
                serv.stopQueryRetrieve();
            } else
            {
                serv.startQueryRetrieve();
            }
            refreshInterface();
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Services.class).error(ex.getMessage(), ex);
        }
    }//GEN-LAST:event_jButtonQRActionPerformed

    private void jButtonWebActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonWebActionPerformed
        try
        {
            if (serv.webServerIsRunning())
            {
                serv.stopWebServer();
            } else
            {
                serv.startWebServer();
            }
            refreshInterface();
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Services.class).error(ex.getMessage(), ex);
        }       
        
    }//GEN-LAST:event_jButtonWebActionPerformed

    private void jButtonWebServicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonWebServicesActionPerformed
        try
        {
            if (serv.webServicesIsRunning())
            {
                serv.stopWebServices();
            } else
            {
                serv.startWebServices();
            }

        } catch (Exception ex)
        {
            LoggerFactory.getLogger(Services.class).error(ex.getMessage(), ex);
        }
        refreshInterface();
    }//GEN-LAST:event_jButtonWebServicesActionPerformed

    private void jButtonQRStorageServersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQRStorageServersActionPerformed
        QRServers QRservers = QRServers.getInstance();
        QRservers.setVisible(true);
        QRservers.toFront();
    }//GEN-LAST:event_jButtonQRStorageServersActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonQR;
    private javax.swing.JButton jButtonQRStorageServers;
    private javax.swing.JButton jButtonStorage;
    private javax.swing.JButton jButtonWeb;
    private javax.swing.JButton jButtonWebServices;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
