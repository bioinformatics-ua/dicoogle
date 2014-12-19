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
/*
 * GUI.java
 *
 * Created on 07 November 2007, 17:01
 */
package pt.ua.dicoogle.rGUI.client.windows;

import java.awt.Component;
import java.rmi.RemoteException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;


import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;





import pt.ua.dicoogle.rGUI.RFileBrowser.FileAction;
import pt.ua.dicoogle.rGUI.RFileBrowser.RemoteFileChooser;
import pt.ua.dicoogle.rGUI.client.AdminRefs;
import pt.ua.dicoogle.rGUI.client.ClientCore;
import pt.ua.dicoogle.rGUI.client.UIHelper.PanelPluginsController;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IAccessList;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IDirectory;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IIndexOptions;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPluginControllerAdmin;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IQueryRetrieve;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISOPClass;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IStartupServ;
import pt.ua.dicoogle.sdk.Utils.PluginPanel;
import pt.ua.dicoogle.sdk.utils.TagValue;


/**
 * Server Options Configurations form
 * @author  Marco Pereira
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class ServerOptions extends javax.swing.JFrame {

    private DefaultComboBoxModel m;
    private DefaultListModel lm;
    private DefaultListModel lmDic;
    private boolean returnToMain = true;
    private static ServerOptions instance = null;
    
    //#######################################
    private IStartupServ startupserv;
    private IQueryRetrieve queryRetrieve;
    private IAccessList accessList;
    private IIndexOptions indexOptions;
    private ISOPClass SOPClass;
    private IDirectory directorySet;
    private HashMap<String, PluginPanel> panels = new HashMap<String, PluginPanel>();

    public static synchronized ServerOptions getInstance() {
            
            if (instance == null) {
                instance = new ServerOptions();
            }
         
        return instance;
    }

    
     public static Image getImage(final String pathAndFileName) 
     {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(pathAndFileName);
        return Toolkit.getDefaultToolkit().getImage(url);
    }
    
    /** Creates new form Server Options 
     *  @param win Parent form
     */
    private ServerOptions() {
        initComponents();

        
        IPluginControllerAdmin pluginController = AdminRefs.getInstance().getPluginController();
        /**
         * Transferrence of the plugins from the server into the client...
         */
        List<String> pluginNames = null;
        try
        {
            /*pluginNames = pluginController.getPluginNames();
            for (String pName : pluginNames)
            {
                byte[] b = pluginController.getJarFile(pName);
                File newFile = new File("pluginClasses/" + pName + ".jar");
                try
                {
                    newFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    fos.write(b);
                    fos.close();
                    newFile.deleteOnExit();
                } catch (IOException ex)
                {
                    Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
                }
            }*/

            /**
             * Loading of the plugins and initialization of the panels
             */
            PanelPluginsController ppc = PanelPluginsController.getInstance();
            Collection<PluginPanel> panelsTmp = ppc.getPanels();
            HashMap<String, ArrayList> initializeParams = pluginController.getInitializeParams();

            for (PluginPanel panel : panelsTmp)
            {
                ArrayList params = initializeParams.get(panel.getPluginName());
                if (params != null)
                {
                    panel.initialize(params);
                    this.panels.put(panel.getPluginName(), panel);
                    JScrollPane pane = new javax.swing.JScrollPane();
                    pane.setViewportView((Component) panel);
                    this.jTabbedPane1.addTab(panel.getPluginName(), new javax.swing.JScrollPane().add((Component) panel));
                }
            }
        } catch (RemoteException ex)
        {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }

        Image image = Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("trayicon.gif"));
        this.setIconImage(image);

        AdminRefs refs = AdminRefs.getInstance();

        startupserv = refs.getStartupServices();
        queryRetrieve = refs.getQueryRetrieve();
        accessList = refs.getAccessList();
        indexOptions = refs.getIndexOptions();
        SOPClass = refs.getSOPClass();
        directorySet = refs.getDirectorySettings();

        loadDirectorySettings();
        loadInicialServices();
        loadQueryRetrieve();
        loadAccessList();

        //Create the nodes with DIM Fields
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("DIM Fields");
        loadTagsDIMFields(top);
        jTreeTagsDIM.setModel(new DefaultTreeModel(top));

        //Modalities
        loadTagsModalities();

        DefaultMutableTreeNode topOthers = new DefaultMutableTreeNode("Other Fields");
        loadTagsOthers(topOthers);
        jTreeTagsManual.setModel(new DefaultTreeModel(topOthers));


        initLabels();

        m = new DefaultComboBoxModel();
        initCB();
        jComboBoxSOP.setModel(m);


        convert();

        filllocalTS();


        /*
         * Index Options Start
         */
        jPanelTagsDIMFields.setVisible(true);
        jPanelTagsModalities.setVisible(false);
        jPanelTagsManual.setVisible(false);

    }

    private void loadDirectorySettings() {
        try {
            jLabelPath.setText(directorySet.getStoragePath());
            jLabelDicoogleDirPath.setText(directorySet.getDicoogleDir());
            
            jCheckBoxIndexThumbnails.setSelected(directorySet.getSaveThumbnails());

            String size = directorySet.getThumbnailsMatrix();
            if (size!=null)
            {
                if (size.equals("64")) {
                    jComboBoxMatrixThumbnails.setSelectedIndex(0);
                } else if (size.equals("96")) {
                    jComboBoxMatrixThumbnails.setSelectedIndex(1);
                } else if (size.equals("128")) {
                    jComboBoxMatrixThumbnails.setSelectedIndex(2);
                } else if (size.equals("256")) {
                    jComboBoxMatrixThumbnails.setSelectedIndex(3);
                }
            }
            jSlider1.setValue(directorySet.getIndexerEffort());

            jCheckBoxIndexZIPFiles.setSelected(directorySet.isIndexZip());
            jCheckBoxGZip.setSelected(directorySet.isGZipStorage());
            jCheckBoxAnonymous.setSelected(directorySet.isIndexAnonymous());
            
            jCheckBoxDirectoryWatcher.setSelected(directorySet.isMonitorWatcher());

        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadAccessList() {
        lm = new DefaultListModel();
        jListAET.setModel(lm);

        try {
            jTextFieldAETitle.setText(accessList.getAETitle());

            ArrayList<String> AList = accessList.getAccessList();

            String[] CAET = new String[AList.size()];
            CAET = AList.toArray(CAET);

            if (CAET != null) {
                for (int i = 0; i < CAET.length; i++) {
                    lm.addElement(CAET[i]);
                }
            }

            boolean permitAll = accessList.getPermitAllAETitles();
            jCheckBoxPermitAllAETitles.setSelected(permitAll);

            if(permitAll){
                jListAET.setEnabled(false);
                jTextFieldClientAET.setEnabled(false);
                jButtonAdd.setEnabled(false);
                jButtonRemove.setEnabled(false);
                jLabel19.setEnabled(false);
                jLabel4.setEnabled(false);
                jLabel22.setEnabled(false);
            }

        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Load Query/Retrive tab with the settings
     */
    private void loadQueryRetrieve() {
        try {
            /**
             * Fill query & retrieve settings
             *
             */
            jTextFieldMaxAssoc.setText(String.valueOf(queryRetrieve.getMaxClientAssoc()));
            jTextFieldMaxPDUSend.setText(String.valueOf(queryRetrieve.getMaxPDULengthSend()));
            jTextFieldMaxPDUReceive.setText(String.valueOf(queryRetrieve.getMaxPDULengthReceive()));

            jComboBoxModalityFind.removeAllItems();

            HashMap<String, String> table = queryRetrieve.getFindModalities();
            Collection<String> en = table.values();

            for(String s : en){
                jComboBoxModalityFind.addItem(s);
            }

            jTextFieldQRAcceptTimeout.setText(String.valueOf(queryRetrieve.getQRAcceptTimeout()));
            jTextFieldQRConnectionTimeout.setText(String.valueOf(queryRetrieve.getQRConnectionTimeout()));
            jTextFieldQRTimeout.setText(String.valueOf(queryRetrieve.getQRIdleTimeout()));
            jTextFieldQRResponseTimeout.setText(String.valueOf(queryRetrieve.getQRRspDelay()));

        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Load Inicial Services
     */
    private void loadInicialServices() {
        try {
            /**
             * P2P is enable?
             */
            jCheckBoxP2P.setSelected(startupserv.getP2P());

            /**
             *
             * Services
             */
            jCheckBoxDICOMStorage.setSelected(startupserv.getDICOMStorage());
            jTextFieldDICOMStoragePort.setText(String.valueOf(startupserv.getDICOMStoragePort()));
            jCheckBoxDICOMQR.setSelected(startupserv.getDICOMQR());
            jCheckBoxWebServer.setSelected(startupserv.getWebServer());
            jTextFieldWebPort.setText(String.valueOf(startupserv.getWebServerPort()));
            jCheckBoxWebServices.setSelected(startupserv.getWebServices());
            jTextFieldWebServicesPort.setText(String.valueOf(startupserv.getWebServicesPort()));
            jTextFieldRGUIPort.setText(String.valueOf(startupserv.getRemoteGUIPort()));
            jTextFieldQRPort.setText(String.valueOf(startupserv.getDICOMQRPort()));
            jTextFieldRGUIExtIP.setText(startupserv.getRemoteGUIExtIP());
            
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadTagsDIMFields(DefaultMutableTreeNode top) {
        try {
            //DebugManager.getInstance().debug("Getting DIM Fields");

            DefaultMutableTreeNode group = null;
            DefaultMutableTreeNode subGroup = null;

            HashMap<String, ArrayList<TagValue>> groupTable = indexOptions.getDIMFields();

            /** Create nodes */
            for (String g : groupTable.keySet()) {
                group = new DefaultMutableTreeNode(g);
                top.add(group);
                //DebugManager.getInstance().debug("Adding new group: " + g);
                for (TagValue t : groupTable.get(g)) {
                    //DebugManager.getInstance().debug("Adding sub-group: " + t.getAlias());
                    subGroup = new DefaultMutableTreeNode("(" + TagValue.getSubgroup(t.getTagNumber()) + ")  " + t.getAlias());
                    group.add(subGroup);
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadTagsModalities() {
        try {
            ArrayList<String> Modalities = indexOptions.getModalities();
            if (Modalities == null) {
                return;
            }

            DefaultListModel model = (DefaultListModel) jListTagsModalities.getModel();
            for (String struct : Modalities) {
                int pos = jListTagsModalities.getModel().getSize();
                model.add(pos, struct);
            }

            jCheckBoxIndexAllModalities.setSelected(indexOptions.isIndexAllModalities());
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadTagsOthers(DefaultMutableTreeNode top) {
        try {
            //DebugManager.getInstance().debug(">> Filling the Others Tags in MainWindow Notebook");

            DefaultMutableTreeNode group = null;
            DefaultMutableTreeNode subGroup = null;

            HashMap<String, ArrayList<TagValue>> groupTable = indexOptions.getManualFields();

            /** Finally Create nodes */
            for (String g : groupTable.keySet()) {
                //DebugManager.getInstance().debug(">> Adding the Others Tags in MainWindow Notebook");
                group = new DefaultMutableTreeNode(g);
                top.add(group);
                for (TagValue t : groupTable.get(g)) {
                    subGroup = new DefaultMutableTreeNode("(" + TagValue.getSubgroup(t.getTagNumber()) + ")  " + t.getAlias());
                    group.add(subGroup);
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setReturnToMain(Boolean ret) {
        returnToMain = ret;
    }

    /**
     * Convert ComboBox Item into UID
     */
    private void convert() {
        String text = jComboBoxSOP.getSelectedItem().toString();

        try {
            jLabelName.setText(SOPClass.getUID(text));

        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Fill labels with UID data
     */
    private void initLabels() {
        try {
            ArrayList<SimpleEntry<String, String>> TS = SOPClass.getTransferSyntax();


            jLabel5.setText(TS.get(0).getKey());
            jLabel6.setText(TS.get(1).getKey());
            jLabel7.setText(TS.get(2).getKey());
            jLabel8.setText(TS.get(3).getKey());
            jLabel9.setText(TS.get(4).getKey());
            jLabel10.setText(TS.get(5).getKey());
            jLabel11.setText(TS.get(6).getKey());
            jLabel12.setText(TS.get(7).getKey());
            jLabel13.setText(TS.get(8).getKey());
            jLabel14.setText(TS.get(9).getKey());
            jLabel15.setText(TS.get(10).getKey());
            jLabel16.setText(TS.get(11).getKey());
            jLabel17.setText(TS.get(12).getKey());
            jLabel18.setText(TS.get(13).getKey());

            jCheckBox2.setText(TS.get(0).getValue());
            jCheckBox3.setText(TS.get(1).getValue());
            jCheckBox4.setText(TS.get(2).getValue());
            jCheckBox5.setText(TS.get(3).getValue());
            jCheckBox6.setText(TS.get(4).getValue());
            jCheckBox7.setText(TS.get(5).getValue());
            jCheckBox8.setText(TS.get(6).getValue());
            jCheckBox9.setText(TS.get(7).getValue());
            jCheckBox10.setText(TS.get(8).getValue());
            jCheckBox11.setText(TS.get(9).getValue());
            jCheckBox12.setText(TS.get(10).getValue());
            jCheckBox13.setText(TS.get(11).getValue());
            jCheckBox14.setText(TS.get(12).getValue());
            jCheckBox15.setText(TS.get(13).getValue());

        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Syncronize shown data with configuration data
     */
    private void filllocalTS() {
        try {
            String UID = jLabelName.getText();
            boolean[] TS = SOPClass.getTS(UID);

            jCheckBoxAccepted.setSelected(SOPClass.getAccepted(UID));

            jCheckBox2.setSelected(TS[0]);
            jCheckBox3.setSelected(TS[1]);
            jCheckBox4.setSelected(TS[2]);
            jCheckBox5.setSelected(TS[3]);
            jCheckBox6.setSelected(TS[4]);
            jCheckBox7.setSelected(TS[5]);
            jCheckBox8.setSelected(TS[6]);
            jCheckBox9.setSelected(TS[7]);
            jCheckBox10.setSelected(TS[8]);
            jCheckBox11.setSelected(TS[9]);
            jCheckBox12.setSelected(TS[10]);
            jCheckBox13.setSelected(TS[11]);
            jCheckBox14.setSelected(TS[12]);
            jCheckBox15.setSelected(TS[13]);
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Adds items to Combo Box
     */
    private void initCB() {
        try {
            ArrayList<String> classList = SOPClass.getSOPClassList();
            Iterator<String> it = classList.iterator();

            while (it.hasNext()) {
                m.addElement(it.next());
            }

        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Save changes made to a SOPClass Option 
     * to the global configuration object
     */
    private void savelocalTS() {
        try {
            String UID = jLabelName.getText();

            SOPClass.saveLocalTS(UID);
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Save made to all SOP Classes 
     * to the global configuration object
     */
    private void saveallTS() {
        try {
            SOPClass.saveAllTS();

        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonClose = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane8 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jCheckBoxIndexThumbnails = new javax.swing.JCheckBox();
        jComboBoxMatrixThumbnails = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel25 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel32 = new javax.swing.JLabel();
        jButtonRebuildIndex = new javax.swing.JButton();
        jButtonRebuildDicomDir = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabelPath = new javax.swing.JLabel();
        jButtonStoragePath = new javax.swing.JButton();
        jLabelDicoogleDirPath = new javax.swing.JLabel();
        jButtonDiccogleDir = new javax.swing.JButton();
        jCheckBoxIndexZIPFiles = new javax.swing.JCheckBox();
        jCheckBoxDirectoryWatcher = new javax.swing.JCheckBox();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jCheckBoxAffectAll = new javax.swing.JCheckBox();
        jComboBoxSOP = new javax.swing.JComboBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabelTSInfo = new javax.swing.JLabel();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox11 = new javax.swing.JCheckBox();
        jCheckBox12 = new javax.swing.JCheckBox();
        jCheckBox13 = new javax.swing.JCheckBox();
        jCheckBox14 = new javax.swing.JCheckBox();
        jCheckBox15 = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabelName = new javax.swing.JLabel();
        jButtonClear = new javax.swing.JButton();
        jButtonDefault = new javax.swing.JButton();
        jButtonAll = new javax.swing.JButton();
        jCheckBoxAccepted = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel8 = new javax.swing.JPanel();
        jPanelTagsDIMFields = new javax.swing.JPanel();
        jLabelTagsDIMStatus = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTreeTagsDIM = new javax.swing.JTree();
        jLabel21 = new javax.swing.JLabel();
        jCheckBoxAnonymous = new javax.swing.JCheckBox();
        jCheckBoxGZip = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        jButtonTagsManual = new javax.swing.JButton();
        jButtonTagsDIM = new javax.swing.JButton();
        jButtonTagsModalities = new javax.swing.JButton();
        jPanelTagsModalities = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jButtonTagsModalityAdd = new javax.swing.JButton();
        jButtonTagsModalityRemove = new javax.swing.JButton();
        jTextFieldTagsModality = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        DefaultListModel modelTagsModalities = new DefaultListModel();
        jListTagsModalities = new JList(modelTagsModalities);
        jLabel51 = new javax.swing.JLabel();
        jCheckBoxIndexAllModalities = new javax.swing.JCheckBox();
        jPanelTagsManual = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jTextFieldTagsMGroup = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jTextFieldTagsMSubG = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jTextFieldTagsMName = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        jButtonTagsAdd = new javax.swing.JButton();
        jButtonTagsRemove = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTreeTagsManual = new javax.swing.JTree();
        jScrollPane11 = new javax.swing.JScrollPane();
        lmDic = new DefaultListModel();
        jListDics = new javax.swing.JList();
        jButtonTagsRemoveDic = new javax.swing.JButton();
        jButtonTagsAddDic = new javax.swing.JButton();
        jLabel52 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jListAET = new javax.swing.JList();
        jButtonAdd = new javax.swing.JButton();
        jButtonRemove = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jTextFieldClientAET = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldAETitle = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jCheckBoxPermitAllAETitles = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabelModalityFind = new javax.swing.JLabel();
        jComboBoxModalityFind = new javax.swing.JComboBox();
        jButton6 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jTextFieldQRConnectionTimeout = new javax.swing.JTextField();
        jTextFieldQRResponseTimeout = new javax.swing.JTextField();
        jTextFieldQRAcceptTimeout = new javax.swing.JTextField();
        jTextFieldMaxAssoc = new javax.swing.JTextField();
        jTextFieldQRTimeout = new javax.swing.JTextField();
        jTextFieldMaxPDUReceive = new javax.swing.JTextField();
        jTextFieldMaxPDUSend = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabelMaxAssoc = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabelMaxPDUReceive = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabelMaxPDUSend = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel7 = new javax.swing.JPanel();
        jCheckBoxP2P = new javax.swing.JCheckBox();
        jLabel42 = new javax.swing.JLabel();
        jCheckBoxDICOMStorage = new javax.swing.JCheckBox();
        jCheckBoxDICOMQR = new javax.swing.JCheckBox();
        jSeparator4 = new javax.swing.JSeparator();
        jCheckBoxWebServer = new javax.swing.JCheckBox();
        jLabel43 = new javax.swing.JLabel();
        jTextFieldWebPort = new javax.swing.JTextField();
        jCheckBoxWebServices = new javax.swing.JCheckBox();
        jLabel44 = new javax.swing.JLabel();
        jTextFieldWebServicesPort = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jTextFieldDICOMStoragePort = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel31 = new javax.swing.JLabel();
        jTextFieldRGUIPort = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        jTextFieldQRPort = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jTextFieldRGUIExtIP = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jButtonWrite = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Dicoogle Settings");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(500, 350));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(0, 0));

        jPanel4.setMaximumSize(new java.awt.Dimension(400, 400));
        jPanel4.setPreferredSize(new java.awt.Dimension(700, 447));

        jCheckBoxIndexThumbnails.setText("Store Thumbnails, Matrix Size:");
        jCheckBoxIndexThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxIndexThumbnailsActionPerformed(evt);
            }
        });

        jComboBoxMatrixThumbnails.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "64", "96", "128", "256" }));
        jComboBoxMatrixThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxMatrixThumbnailsActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel23.setText("Storage Path:");

        jLabel25.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel25.setText("Dicoogle Directory Monitorization:");

        jLabel32.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel32.setText("Search Indexer - Monitoring Directory");

        jButtonRebuildIndex.setText("Rebuild Search Index");
        jButtonRebuildIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRebuildIndexActionPerformed(evt);
            }
        });

        jButtonRebuildDicomDir.setText("Rebuild DICOM.DIR");
        jButtonRebuildDicomDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRebuildDicomDirActionPerformed(evt);
            }
        });

        jLabel33.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel33.setText("Dicom Directory Builder");

        jLabel28.setIcon(new ImageIcon(getImage("aboutico.gif")));
        jLabel28.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel28MouseClicked(evt);
            }
        });

        jLabel29.setIcon(new ImageIcon(getImage("aboutico.gif")));
        jLabel29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel29MouseClicked(evt);
            }
        });

        jLabel30.setIcon(new ImageIcon(getImage("aboutico.gif")));
        jLabel30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel30MouseClicked(evt);
            }
        });

        jLabel34.setIcon(new ImageIcon(getImage("aboutico.gif")));
        jLabel34.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel34MouseClicked(evt);
            }
        });

        jSlider1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider1MouseReleased(evt);
            }
        });
        jSlider1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jSlider1KeyReleased(evt);
            }
        });

        jLabel53.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel53.setText("Index Effort");

        jLabel54.setText("Intensive");

        jLabel55.setText("Lower");

        jLabelPath.setText("<Storage Path>");

        jButtonStoragePath.setText("Change Storage Path");
        jButtonStoragePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStoragePathActionPerformed(evt);
            }
        });

        jLabelDicoogleDirPath.setText("<Dicoogle Directory Path>");

        jButtonDiccogleDir.setText("Dicoogle Dir - Monitor");
        jButtonDiccogleDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiccogleDirActionPerformed(evt);
            }
        });

        jCheckBoxIndexZIPFiles.setText("Index ZIP Files");
        jCheckBoxIndexZIPFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxIndexZIPFilesActionPerformed(evt);
            }
        });

        jCheckBoxDirectoryWatcher.setText("Enable directory watcher");
        jCheckBoxDirectoryWatcher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxDirectoryWatcherActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabelPath, javax.swing.GroupLayout.PREFERRED_SIZE, 532, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jButtonStoragePath)))
                                    .addComponent(jLabel23)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
                                .addGap(9, 9, 9))
                            .addComponent(jLabel53)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(jLabel55)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(108, 108, 108)
                                        .addComponent(jLabel54))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel32)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3)
                                        .addComponent(jButtonRebuildIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(62, 62, 62)
                                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonRebuildDicomDir, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(96, 96, 96)
                                        .addComponent(jLabel33))))
                            .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jCheckBoxIndexThumbnails)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxMatrixThumbnails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(96, 96, 96)
                                .addComponent(jCheckBoxDirectoryWatcher))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(jLabelDicoogleDirPath, javax.swing.GroupLayout.PREFERRED_SIZE, 502, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonDiccogleDir, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jCheckBoxIndexZIPFiles)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabelPath, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonStoragePath)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25)
                .addGap(8, 8, 8)
                .addComponent(jLabelDicoogleDirPath)
                .addGap(4, 4, 4)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDiccogleDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxIndexThumbnails)
                    .addComponent(jComboBoxMatrixThumbnails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxDirectoryWatcher))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jLabel53)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel55)
                        .addComponent(jLabel54))
                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonRebuildDicomDir)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonRebuildIndex)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxIndexZIPFiles)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jScrollPane8.setViewportView(jPanel4);

        jTabbedPane1.addTab("Directory Settings", jScrollPane8);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("SOP Class");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 550, -1));

        jCheckBoxAffectAll.setText("Affect All SOPs");
        jPanel1.add(jCheckBoxAffectAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 390, 140, 30));

        jComboBoxSOP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxSOP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSOPActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBoxSOP, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 340, -1));

        jCheckBox2.setText("ImplicitVRLittleEndian ");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 310, -1));

        jLabelTSInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTSInfo.setText("Accepted Transfer Syntax");
        jPanel1.add(jLabelTSInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 530, -1));

        jCheckBox3.setText("ExplicitVRLittleEndian");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 310, -1));

        jCheckBox4.setText("DeflatedExplicitVRLittleEndian");
        jCheckBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox4ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 310, -1));

        jCheckBox5.setText("ExplicitVRBigEndian");
        jCheckBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox5ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 310, -1));

        jCheckBox6.setText("JPEG Lossless");
        jCheckBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox6ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 310, -1));

        jCheckBox7.setText("JPEG Lossless LS");
        jCheckBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox7ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 310, -1));

        jCheckBox8.setText("JPEG Lossless, Non-Hierarchical (Process 14) ");
        jCheckBox8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox8ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 310, -1));

        jCheckBox9.setText("JPEG2000 Lossless Only");
        jCheckBox9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox9ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 310, -1));

        jCheckBox10.setText("JPEG Baseline 1");
        jCheckBox10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox10ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, 310, -1));

        jCheckBox11.setText("JPEG Extended (Process 2 & 4)");
        jCheckBox11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox11ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, 310, -1));

        jCheckBox12.setText("JPEG LS Lossy Near Lossless");
        jCheckBox12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox12ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 310, -1));

        jCheckBox13.setText("JPEG2000");
        jCheckBox13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox13ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, 310, -1));

        jCheckBox14.setText("RLE Lossless");
        jCheckBox14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox14ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 310, -1));

        jCheckBox15.setText("MPEG2");
        jCheckBox15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox15ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 360, 310, -1));

        jLabel5.setText("L5");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 100, 190, 20));

        jLabel6.setText("L6");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 120, 190, 20));

        jLabel7.setText("L7");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 140, 190, 20));

        jLabel8.setText("L8");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 160, 190, 20));

        jLabel9.setText("L9");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 180, 190, 20));

        jLabel10.setText("L10");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 200, 190, 20));

        jLabel11.setText("L11");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 220, 190, 20));

        jLabel12.setText("L12");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 240, 190, 20));

        jLabel13.setText("L13");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 260, 190, 20));

        jLabel14.setText("L14");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 280, 190, 20));

        jLabel15.setText("L15");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 300, 190, 20));

        jLabel16.setText("L16");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 320, 190, 20));

        jLabel17.setText("L17");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 340, 190, 20));

        jLabel18.setText("L18");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 360, 190, 20));

        jLabelName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelName.setText("<SOP NAME>");
        jPanel1.add(jLabelName, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 340, -1));

        jButtonClear.setText("Clear All");
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 390, 110, -1));

        jButtonDefault.setText("Select Defaults");
        jButtonDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDefaultActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonDefault, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 390, 110, -1));

        jButtonAll.setText("Select All");
        jButtonAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAllActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 390, 110, -1));

        jCheckBoxAccepted.setText("Accepted");
        jCheckBoxAccepted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAcceptedActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBoxAccepted, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, 140, 50));

        jScrollPane5.setViewportView(jPanel1);

        jTabbedPane1.addTab("SOP Class Settings", jScrollPane5);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(757, 300));

        jPanelTagsDIMFields.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DIM Fields", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 18))); // NOI18N

        jLabelTagsDIMStatus.setText("It is the minimium fields enabled. Defined in the DICOM Standard.");

        jScrollPane6.setViewportView(jTreeTagsDIM);

        jLabel21.setText("Available fields:");

        jCheckBoxAnonymous.setText("Anonymous");
        jCheckBoxAnonymous.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAnonymousActionPerformed(evt);
            }
        });

        jCheckBoxGZip.setText("Storage in Compressed file (GZip)");
        jCheckBoxGZip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxGZipActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelTagsDIMFieldsLayout = new javax.swing.GroupLayout(jPanelTagsDIMFields);
        jPanelTagsDIMFields.setLayout(jPanelTagsDIMFieldsLayout);
        jPanelTagsDIMFieldsLayout.setHorizontalGroup(
            jPanelTagsDIMFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTagsDIMFieldsLayout.createSequentialGroup()
                .addGroup(jPanelTagsDIMFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTagsDIMFieldsLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanelTagsDIMFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelTagsDIMStatus)
                            .addComponent(jLabel21)))
                    .addGroup(jPanelTagsDIMFieldsLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanelTagsDIMFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxAnonymous)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBoxGZip))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelTagsDIMFieldsLayout.setVerticalGroup(
            jPanelTagsDIMFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTagsDIMFieldsLayout.createSequentialGroup()
                .addComponent(jLabelTagsDIMStatus)
                .addGap(7, 7, 7)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jCheckBoxAnonymous)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBoxGZip)
                .addContainerGap())
        );

        jPanel9.setFocusCycleRoot(true);

        jButtonTagsManual.setIcon(new ImageIcon(getImage("tags_manual.jpg")));
        jButtonTagsManual.setText("Oth. Fields");
        jButtonTagsManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTagsManualActionPerformed(evt);
            }
        });

        jButtonTagsDIM.setIcon(new ImageIcon(getImage("icon_patient.jpg")));
        jButtonTagsDIM.setText("DIM Fields");
        jButtonTagsDIM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTagsDIMActionPerformed(evt);
            }
        });

        jButtonTagsModalities.setIcon(new ImageIcon(getImage("search_icon.jpg")));
        jButtonTagsModalities.setText("Modalities");
        jButtonTagsModalities.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTagsModalitiesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonTagsDIM, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTagsModalities, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTagsManual, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonTagsDIM)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonTagsModalities)
                .addGap(1, 1, 1)
                .addComponent(jButtonTagsManual)
                .addContainerGap(877, Short.MAX_VALUE))
        );

        jPanelTagsModalities.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Modalities", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 18))); // NOI18N
        jPanelTagsModalities.setRequestFocusEnabled(false);

        jLabel24.setText("Available Modalities:");

        jButtonTagsModalityAdd.setIcon(new ImageIcon(getImage("add.png")));
        jButtonTagsModalityAdd.setText("Add");
        jButtonTagsModalityAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTagsModalityAddActionPerformed(evt);
            }
        });

        jButtonTagsModalityRemove.setIcon(new ImageIcon(getImage("remove.png")));
        jButtonTagsModalityRemove.setText("Remove");
        jButtonTagsModalityRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTagsModalityRemoveActionPerformed(evt);
            }
        });

        jLabel35.setText("Modality:");

        jScrollPane7.setViewportView(jListTagsModalities);

        jLabel51.setText("Set the Modalities in which you want to index all the Fields:");

        jCheckBoxIndexAllModalities.setText("Index all fields from all modalities");
        jCheckBoxIndexAllModalities.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxIndexAllModalitiesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelTagsModalitiesLayout = new javax.swing.GroupLayout(jPanelTagsModalities);
        jPanelTagsModalities.setLayout(jPanelTagsModalitiesLayout);
        jPanelTagsModalitiesLayout.setHorizontalGroup(
            jPanelTagsModalitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTagsModalitiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTagsModalitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTagsModalitiesLayout.createSequentialGroup()
                        .addGroup(jPanelTagsModalitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addGroup(jPanelTagsModalitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jButtonTagsModalityAdd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonTagsModalityRemove, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)))
                        .addGap(27, 27, 27)
                        .addGroup(jPanelTagsModalitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel35)
                            .addComponent(jTextFieldTagsModality, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(62, 62, 62)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel51)
                    .addComponent(jCheckBoxIndexAllModalities))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanelTagsModalitiesLayout.setVerticalGroup(
            jPanelTagsModalitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTagsModalitiesLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel51)
                .addGap(18, 18, 18)
                .addGroup(jPanelTagsModalitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelTagsModalitiesLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(21, 21, 21)
                        .addGroup(jPanelTagsModalitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelTagsModalitiesLayout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldTagsModality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelTagsModalitiesLayout.createSequentialGroup()
                                .addComponent(jButtonTagsModalityAdd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonTagsModalityRemove)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBoxIndexAllModalities))
        );

        jPanelTagsManual.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Manual Fields", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 18))); // NOI18N

        jLabel47.setText("Group:");
        jLabel47.setAlignmentY(0.0F);
        jLabel47.setFocusable(false);

        jLabel49.setText("Subgroup:");
        jLabel49.setAlignmentY(0.0F);
        jLabel49.setFocusable(false);

        jLabel50.setText("Tag Name:");
        jLabel50.setAlignmentY(0.0F);
        jLabel50.setFocusable(false);
        jLabel50.setRequestFocusEnabled(false);

        jButtonTagsAdd.setIcon(new ImageIcon(getImage("add.png")));
        jButtonTagsAdd.setText("Add");
        jButtonTagsAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTagsAddActionPerformed(evt);
            }
        });

        jButtonTagsRemove.setIcon(new ImageIcon(getImage("remove.png")));
        jButtonTagsRemove.setText("Remove");
        jButtonTagsRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTagsRemoveActionPerformed(evt);
            }
        });

        jScrollPane9.setViewportView(jTreeTagsManual);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonTagsRemove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonTagsAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jButtonTagsAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonTagsRemove))
            .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jListDics.setModel(lmDic);
        jScrollPane11.setViewportView(jListDics);

        jButtonTagsRemoveDic.setIcon(new ImageIcon(getImage("remove.png")));
        jButtonTagsRemoveDic.setText("Remove");
        jButtonTagsRemoveDic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTagsRemoveDicActionPerformed(evt);
            }
        });

        jButtonTagsAddDic.setIcon(new ImageIcon(getImage("add.png")));
        jButtonTagsAddDic.setText("Add Dictionary");
        jButtonTagsAddDic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTagsAddDicActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldTagsMGroup, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldTagsMSubG, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel50)
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldTagsMName, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 135, Short.MAX_VALUE))))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(174, 174, 174)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonTagsRemoveDic, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jButtonTagsAddDic, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32))))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel47)
                            .addComponent(jTextFieldTagsMGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel49)
                            .addComponent(jTextFieldTagsMSubG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel50)
                            .addComponent(jTextFieldTagsMName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jButtonTagsAddDic)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTagsRemoveDic)))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jLabel52.setText("Set the isolated Fields that you want to index:");

        javax.swing.GroupLayout jPanelTagsManualLayout = new javax.swing.GroupLayout(jPanelTagsManual);
        jPanelTagsManual.setLayout(jPanelTagsManualLayout);
        jPanelTagsManualLayout.setHorizontalGroup(
            jPanelTagsManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTagsManualLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel52))
            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanelTagsManualLayout.setVerticalGroup(
            jPanelTagsManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTagsManualLayout.createSequentialGroup()
                .addComponent(jLabel52)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelTagsManual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelTagsDIMFields, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelTagsModalities, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanelTagsDIMFields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelTagsModalities, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelTagsManual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 20, Short.MAX_VALUE)))
                .addGap(26, 26, 26))
        );

        jScrollPane2.setViewportView(jPanel8);

        jTabbedPane1.addTab("Index Options", jScrollPane2);

        jLabel4.setText("Client AE Title:");

        jScrollPane4.setViewportView(jListAET);

        jButtonAdd.setText("Add to Permit List");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jButtonRemove.setText("Remove From List");
        jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel19.setText("Client Permit Access Control List:");

        jLabel20.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N

        jTextFieldClientAET.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldClientAETKeyPressed(evt);
            }
        });

        jLabel22.setText("To remove from list, select the Client AE Title from the list and click:");

        jLabel27.setIcon(new ImageIcon(getImage("aboutico.gif")));
        jLabel27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel27MouseClicked(evt);
            }
        });

        jLabel3.setText("Server AE Title:");

        jTextFieldAETitle.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldAETitleFocusLost(evt);
            }
        });

        jLabel26.setIcon(new ImageIcon(getImage("aboutico.gif")));
        jLabel26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel26MouseClicked(evt);
            }
        });

        jCheckBoxPermitAllAETitles.setText("Permit All AETitles");
        jCheckBoxPermitAllAETitles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxPermitAllAETitlesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 681, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldAETitle, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jCheckBoxPermitAllAETitles)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldClientAET, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel22))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jButtonAdd))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jButtonRemove)))))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(jLabel20))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldAETitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxPermitAllAETitles)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldClientAET, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAdd))
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jButtonRemove))
                .addContainerGap(85, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel26, jLabel3, jTextFieldAETitle});

        jScrollPane10.setViewportView(jPanel2);

        jTabbedPane1.addTab("Access List", jScrollPane10);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Query/Retrieve Local"));

        jLabelModalityFind.setText("Modality");

        jComboBoxModalityFind.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton6.setIcon(new ImageIcon(getImage("data-server.png")));
        jButton6.setText("Storage Servers");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jTextFieldQRConnectionTimeout.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldQRConnectionTimeoutFocusLost(evt);
            }
        });

        jTextFieldQRResponseTimeout.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldQRResponseTimeoutFocusLost(evt);
            }
        });

        jTextFieldQRAcceptTimeout.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldQRAcceptTimeoutFocusLost(evt);
            }
        });

        jTextFieldMaxAssoc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldMaxAssocFocusLost(evt);
            }
        });

        jTextFieldQRTimeout.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldQRTimeoutFocusLost(evt);
            }
        });

        jTextFieldMaxPDUReceive.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldMaxPDUReceiveFocusLost(evt);
            }
        });

        jTextFieldMaxPDUSend.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldMaxPDUSendFocusLost(evt);
            }
        });

        jLabel39.setText("Connection Timeout:");

        jLabelMaxAssoc.setText("Max Associations:");

        jLabel38.setText("IdleTimeout:");

        jLabelMaxPDUReceive.setText("Max PDU Receive:");

        jLabel37.setText("Response Timeout:");

        jLabelMaxPDUSend.setText("Max PDU Send:");

        jLabel36.setText("Accept Timeout:");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelMaxAssoc)
                    .addComponent(jLabelMaxPDUReceive)
                    .addComponent(jLabelMaxPDUSend)
                    .addComponent(jLabel39)
                    .addComponent(jLabel37)
                    .addComponent(jLabel36)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldQRConnectionTimeout, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldQRResponseTimeout, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldQRAcceptTimeout, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldQRTimeout, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldMaxPDUSend, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldMaxPDUReceive, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldMaxAssoc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldMaxAssoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMaxAssoc))
                .addGap(2, 2, 2)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldMaxPDUReceive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMaxPDUReceive))
                .addGap(4, 4, 4)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldMaxPDUSend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMaxPDUSend))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldQRTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldQRAcceptTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldQRResponseTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldQRConnectionTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelModalityFind)
                    .addComponent(jComboBoxModalityFind, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6))
                .addGap(20, 20, 20))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                .addComponent(jLabelModalityFind)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxModalityFind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(93, 93, 93))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(95, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel1.setText("Note: AETitle of DICOM Services can be changed in \"Access List\"");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addContainerGap(99, Short.MAX_VALUE))
        );

        jScrollPane3.setViewportView(jPanel3);

        jTabbedPane1.addTab("Query/Retrieve", jScrollPane3);

        jCheckBoxP2P.setText("P2P (Peer-to-Peer)");
        jCheckBoxP2P.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxP2PActionPerformed(evt);
            }
        });

        jLabel42.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel42.setText("Services will be start automatically when you start Dicoogle:");

        jCheckBoxDICOMStorage.setText("DICOM Storage ");
        jCheckBoxDICOMStorage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxDICOMStorageActionPerformed(evt);
            }
        });

        jCheckBoxDICOMQR.setText("DICOM Query/Retrieve");
        jCheckBoxDICOMQR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxDICOMQRActionPerformed(evt);
            }
        });

        jCheckBoxWebServer.setText("Dicoogle Web");
        jCheckBoxWebServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxWebServerActionPerformed(evt);
            }
        });

        jLabel43.setText("Port:");

        jTextFieldWebPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldWebPortFocusLost(evt);
            }
        });

        jCheckBoxWebServices.setText("Dicoogle Webservices");
        jCheckBoxWebServices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxWebServicesActionPerformed(evt);
            }
        });

        jLabel44.setText("Port:");

        jTextFieldWebServicesPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldWebServicesPortFocusLost(evt);
            }
        });

        jLabel45.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel45.setText("Note: After save configurations you should restart Dicoogle.");

        jLabel46.setText("Using port values below 1024 may require root/administrator privileges");

        jLabel48.setText("Port:");

        jLabel56.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel56.setText("Note: Need restart the services to take effect");

        jTextFieldDICOMStoragePort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldDICOMStoragePortFocusLost(evt);
            }
        });

        jLabel31.setText("Port:");

        jTextFieldRGUIPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldRGUIPortFocusLost(evt);
            }
        });

        jLabel58.setText("Note: Need restart Dicoogle (Server) to take effect");

        jTextFieldQRPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldQRPortFocusLost(evt);
            }
        });

        jLabel59.setText("Port:");

        jTextFieldRGUIExtIP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldRGUIExtIPFocusLost(evt);
            }
        });

        jLabel40.setText("External IP:");

        jLabel41.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel41.setText("Remote GUI");

        jLabel60.setText("Note: Need restart Dicoogle (Server) to take effect");

        jLabel57.setIcon(new ImageIcon(getImage("aboutico.gif")));
        jLabel57.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel57MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel56))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator8, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
                            .addComponent(jSeparator4, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
                            .addComponent(jSeparator7, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jCheckBoxP2P)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jCheckBoxDICOMStorage)
                                        .addGap(82, 82, 82)
                                        .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextFieldDICOMStoragePort, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel7Layout.createSequentialGroup()
                                                .addComponent(jCheckBoxDICOMQR)
                                                .addGap(39, 39, 39)
                                                .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel7Layout.createSequentialGroup()
                                                .addComponent(jCheckBoxWebServer)
                                                .addGap(94, 94, 94)
                                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel44)
                                                    .addComponent(jLabel43)))
                                            .addComponent(jCheckBoxWebServices))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jTextFieldQRPort)
                                                .addComponent(jTextFieldWebPort, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jTextFieldWebServicesPort, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jLabel46)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel31)
                                            .addComponent(jLabel40))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTextFieldRGUIExtIP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                            .addComponent(jTextFieldRGUIPort, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel58)
                                            .addComponent(jLabel60))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 274, Short.MAX_VALUE))))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel41)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel42)
                .addGap(7, 7, 7)
                .addComponent(jCheckBoxP2P)
                .addGap(8, 8, 8)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxDICOMStorage)
                    .addComponent(jLabel48)
                    .addComponent(jTextFieldDICOMStoragePort, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxDICOMQR)
                    .addComponent(jLabel59)
                    .addComponent(jTextFieldQRPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBoxWebServer)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel43)
                        .addComponent(jTextFieldWebPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxWebServices)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel44)
                        .addComponent(jTextFieldWebServicesPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel56)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel41)
                .addGap(5, 5, 5)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(jTextFieldRGUIPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel58))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldRGUIExtIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel40)
                        .addComponent(jLabel60))
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel46)
                .addContainerGap(78, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel7);

        jTabbedPane1.addTab("Inicial Services", jScrollPane1);

        jButtonWrite.setText("Save Configurations");
        jButtonWrite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonWriteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButtonWrite)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonClose)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 768, Short.MAX_VALUE)
                        .addGap(17, 17, 17))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonClose)
                    .addComponent(jButtonWrite))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("Server Settings");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxSOPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSOPActionPerformed
        convert();
        filllocalTS();
}//GEN-LAST:event_jComboBoxSOPActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox2.isSelected(), 0);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox3.isSelected(), 1);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jCheckBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox4ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox4.isSelected(), 2);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox4ActionPerformed

    private void jCheckBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox5ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox5.isSelected(), 3);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox5ActionPerformed

    private void jCheckBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox6ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox6.isSelected(), 4);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox6ActionPerformed

    private void jCheckBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox7ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox7.isSelected(), 5);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox7ActionPerformed

    private void jCheckBox8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox8ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox8.isSelected(), 6);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox8ActionPerformed

    private void jCheckBox9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox9ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox9.isSelected(), 7);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox9ActionPerformed

    private void jCheckBox10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox10ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox10.isSelected(), 8);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox10ActionPerformed

    private void jCheckBox11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox11ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox11.isSelected(), 9);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox11ActionPerformed

    private void jCheckBox12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox12ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox12.isSelected(), 10);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox12ActionPerformed

    private void jCheckBox13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox13ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox13.isSelected(), 11);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox13ActionPerformed

    private void jCheckBox14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox14ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox14.isSelected(), 12);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox14ActionPerformed

    private void jCheckBox15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox15ActionPerformed
        try {
            String UID = jLabelName.getText();

            SOPClass.setTS(UID, jCheckBox15.isSelected(), 13);
            savelocalTS();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox15ActionPerformed

    private void updateCheckBoxes(boolean[] def) {
        jCheckBox2.setSelected(def[0]);
        jCheckBox3.setSelected(def[1]);
        jCheckBox4.setSelected(def[2]);
        jCheckBox5.setSelected(def[3]);
        jCheckBox6.setSelected(def[4]);
        jCheckBox7.setSelected(def[5]);
        jCheckBox8.setSelected(def[6]);
        jCheckBox9.setSelected(def[7]);
        jCheckBox10.setSelected(def[8]);
        jCheckBox11.setSelected(def[9]);
        jCheckBox12.setSelected(def[10]);
        jCheckBox13.setSelected(def[11]);
        jCheckBox14.setSelected(def[12]);
        jCheckBox15.setSelected(def[13]);
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.setVisible(false);
        if (returnToMain) {
            MainWindow main = MainWindow.getInstance();
            main.setEnabled(true);
            main.toFront();
        }
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

private void jButtonWriteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonWriteActionPerformed
     IPluginControllerAdmin pluginController = AdminRefs.getInstance().getPluginController();
    Collection<PluginPanel> pps = this.panels.values();
    HashMap<String, ArrayList> params = new HashMap<String, ArrayList>();
    for (PluginPanel pp : pps)
    {
        params.put(pp.getPluginName(), pp.getProperties());
    }
    try
    {
        pluginController.setSettings(params);
    } catch (RemoteException ex)
    {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
    AdminRefs.getInstance().saveSettings();
}//GEN-LAST:event_jButtonWriteActionPerformed

private void jCheckBoxIndexThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxIndexThumbnailsActionPerformed
    try {
        directorySet.setSaveThumbnails(jCheckBoxIndexThumbnails.isSelected());
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jCheckBoxIndexThumbnailsActionPerformed

private void jComboBoxMatrixThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxMatrixThumbnailsActionPerformed
    if (jComboBoxMatrixThumbnails.getSelectedItem() != null) {
        try {
            directorySet.setThumbnailsMatrix(jComboBoxMatrixThumbnails.getSelectedItem().toString());
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}//GEN-LAST:event_jComboBoxMatrixThumbnailsActionPerformed

private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
    if (!jTextFieldClientAET.getText().equals("") && jTextFieldClientAET.getText() != null && !lm.contains(jTextFieldClientAET.getText())) {
        lm.addElement(jTextFieldClientAET.getText());

        try {
            accessList.addToAccessList(jTextFieldClientAET.getText());
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    jTextFieldClientAET.setText("");
}//GEN-LAST:event_jButtonAddActionPerformed

private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveActionPerformed
    try {
        Object[] val = jListAET.getSelectedValues();

        for (int i = 0; i < val.length; i++) {
            if (lm.contains(val[i])) {
                lm.removeElement(val[i]);

                accessList.removeFromAccessList((String) val[i]);
            }
        }
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButtonRemoveActionPerformed

private void jLabel26MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel26MouseClicked
    JOptionPane.showMessageDialog(this, "Setting an AE Title will force the server to only accept connections adressed to the given AE Title.", "Did you know?", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_jLabel26MouseClicked

private void jLabel27MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel27MouseClicked
    JOptionPane.showMessageDialog(this, "Adding an AE Title to the Access Control List will force the server to only accept connections from clients with those AE Titles.", "Did you know?", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_jLabel27MouseClicked

private void jLabel28MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel28MouseClicked
    JOptionPane.showMessageDialog(this, "Rebuilding the search index will force Dicoogle to (re)index all of your DICOM files", "Did you know?", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_jLabel28MouseClicked

private void jLabel29MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel29MouseClicked
    JOptionPane.showMessageDialog(this, "The DICOM Directory Builder is very useful to export a set of DICOM files to another computer.", "Did you know?", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_jLabel29MouseClicked

private void jLabel30MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel30MouseClicked
    JOptionPane.showMessageDialog(this, "The Directory Monitorization automatically indexes new or recently edited DICOM files", "Did you know?", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_jLabel30MouseClicked

private void jLabel34MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel34MouseClicked
    JOptionPane.showMessageDialog(this, "The storage path sets the directory that Dicoogle will use to store incoming (transferred) DICOM files", "Did you know?", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_jLabel34MouseClicked

private void jCheckBoxP2PActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxP2PActionPerformed
    try {
        /**
         * Enable or disable peer to peer on  Dicoogle Server
         */
        startupserv.setP2P(jCheckBoxP2P.isSelected());
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jCheckBoxP2PActionPerformed

private void jCheckBoxWebServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxWebServerActionPerformed
    try {
        /**
         * Enable / Disable Web Server
         */
        startupserv.setWebServer(jCheckBoxWebServer.isSelected());
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }

}//GEN-LAST:event_jCheckBoxWebServerActionPerformed

private void jCheckBoxWebServicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxWebServicesActionPerformed
    try {
        /**
         * Enable / Disable Webservices
         */
        startupserv.setWebServices(jCheckBoxWebServices.isSelected());
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jCheckBoxWebServicesActionPerformed

private void jCheckBoxDICOMStorageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxDICOMStorageActionPerformed
    try {
        startupserv.setDICOMStorage(jCheckBoxDICOMStorage.isSelected());
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jCheckBoxDICOMStorageActionPerformed

private void jCheckBoxDICOMQRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxDICOMQRActionPerformed
    try {
        startupserv.setDICOMQR(jCheckBoxDICOMQR.isSelected());
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jCheckBoxDICOMQRActionPerformed

private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
    QRServers QRservers = QRServers.getInstance();
    QRservers.setVisible(true);
    QRservers.toFront();

    //this.setEnabled(false);
}//GEN-LAST:event_jButton6ActionPerformed

private void jButtonTagsManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTagsManualActionPerformed
    // Customized tags
    jPanelTagsDIMFields.setVisible(false);
    jPanelTagsModalities.setVisible(false);
    jPanelTagsManual.setVisible(true);
}//GEN-LAST:event_jButtonTagsManualActionPerformed

private void jButtonTagsDIMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTagsDIMActionPerformed
    // Customized tags
    jPanelTagsDIMFields.setVisible(true);
    jPanelTagsModalities.setVisible(false);
    jPanelTagsManual.setVisible(false);
}//GEN-LAST:event_jButtonTagsDIMActionPerformed

private void jButtonTagsModalitiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTagsModalitiesActionPerformed
    // Customized tags

    jPanelTagsDIMFields.setVisible(false);
    jPanelTagsModalities.setVisible(true);
    jPanelTagsManual.setVisible(false);
}//GEN-LAST:event_jButtonTagsModalitiesActionPerformed

    /**
     * Adds one modality to be indexed by the index peer
     * @param evt
     */
private void jButtonTagsModalityAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTagsModalityAddActionPerformed
    String Modality = jTextFieldTagsModality.getText();

    if (Modality.equals("")) {
        JOptionPane.showMessageDialog(this, "Please enter a modality",
                "No selected item", JOptionPane.WARNING_MESSAGE);
    } else {
        DefaultListModel model = (DefaultListModel) jListTagsModalities.getModel();

        int pos = jListTagsModalities.getModel().getSize();
        model.add(pos, Modality);

        try {
            indexOptions.addModality(Modality);
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }

        jTextFieldTagsModality.setText("");
    }
}//GEN-LAST:event_jButtonTagsModalityAddActionPerformed

private void jButtonTagsModalityRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTagsModalityRemoveActionPerformed

    if (jListTagsModalities.getSelectedIndex() == -1) {
        JOptionPane.showMessageDialog(this, "Please select a modality",
                "No selected item", JOptionPane.WARNING_MESSAGE);
    } else {
        DefaultListModel model = (DefaultListModel) jListTagsModalities.getModel();

        int pos = jListTagsModalities.getSelectedIndex();

        String modalitySelected = (String) model.get(pos);
        model.remove(pos);

        try {
            indexOptions.removeModality(modalitySelected);
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}//GEN-LAST:event_jButtonTagsModalityRemoveActionPerformed

private void jButtonTagsAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTagsAddActionPerformed
    try {
        //System.out.println("Adding manual tag..");

        int GP = Integer.valueOf(jTextFieldTagsMGroup.getText());
        int SGP = Integer.valueOf(jTextFieldTagsMSubG.getText());

        indexOptions.addManualField(GP, SGP, jTextFieldTagsMName.getText());

        // Add TagsManual
        DefaultTreeModel model = (DefaultTreeModel) jTreeTagsManual.getModel();

        DefaultMutableTreeNode group = null;
        DefaultMutableTreeNode subGroup = null;

        group = new DefaultMutableTreeNode(jTextFieldTagsMGroup.getText());

        TreePath path = jTreeTagsManual.getNextMatch("", 0, Position.Bias.Forward);

        MutableTreeNode node = (MutableTreeNode) path.getLastPathComponent();

        model.insertNodeInto(group, node, 0);
        subGroup = new DefaultMutableTreeNode("(" + jTextFieldTagsMSubG.getText() + ") " + jTextFieldTagsMName.getText());
        group.add(subGroup);

    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Group and Subgroup shoud be integers",
                "Invalid Input!", JOptionPane.ERROR_MESSAGE);
    }

}//GEN-LAST:event_jButtonTagsAddActionPerformed

    private int subGroup2Int(String subGroup) {
        int start = subGroup.indexOf('(') + 1;
        int end = subGroup.indexOf(')');

        String substring = subGroup.substring(start, end);

        return Integer.valueOf(substring);
    }
private void jButtonTagsRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTagsRemoveActionPerformed
    try {

        DefaultMutableTreeNode nNode;
        MutableTreeNode mNode;

        DefaultTreeModel model = (DefaultTreeModel) jTreeTagsManual.getModel();

        TreePath path = jTreeTagsManual.getSelectionPath();
        //System.out.println("Path: " + path.toString());

        // removes the tags selected
        if (path.getPathCount() == 1) {
            return;
        } else if (path.getPathCount() == 2) {
            JOptionPane.showMessageDialog(this, "Please select one field (not a group)!",
                    "Group Selected", JOptionPane.WARNING_MESSAGE);
        } else {

            int group = Integer.valueOf(path.getPathComponent(1).toString());
            int subGroup = subGroup2Int(path.getPathComponent(2).toString());
            indexOptions.removeManualField(group, subGroup);

            mNode = (MutableTreeNode) path.getLastPathComponent();
            model.removeNodeFromParent(mNode);

            /*
            if(mNode == null)
            System.out.println("nMode == null");

            if(mNode.getParent().getChildCount() == 0)
            model.removeNodeFromParent((MutableTreeNode) mNode.getParent());
             *
             */

        }

    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }

    //JOptionPane.showMessageDialog(this, "Tag deleted sucessfully!");
}//GEN-LAST:event_jButtonTagsRemoveActionPerformed

private void jTextFieldDICOMStoragePortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldDICOMStoragePortFocusLost
    //System.out.println("perdeu focus");
    try {
        startupserv.setDICOMStoragePort(Integer.valueOf(jTextFieldDICOMStoragePort.getText()));
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        //System.out.println("Excepção");
        JOptionPane.showMessageDialog(null, "Only numbers are accepted!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);

        jTextFieldDICOMStoragePort.grabFocus();
    }
}//GEN-LAST:event_jTextFieldDICOMStoragePortFocusLost

private void jTextFieldWebServicesPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldWebServicesPortFocusLost
    try {
        startupserv.setWebServicesPort(Integer.valueOf(jTextFieldWebServicesPort.getText()));
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Only numbers are accepted!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);

        jTextFieldWebServicesPort.grabFocus();
    }
}//GEN-LAST:event_jTextFieldWebServicesPortFocusLost

private void jTextFieldWebPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldWebPortFocusLost
    try {
        startupserv.setWebServerPort(Integer.valueOf(jTextFieldWebPort.getText()));
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Only numbers are accepted!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);

        jTextFieldWebPort.grabFocus();
    }
}//GEN-LAST:event_jTextFieldWebPortFocusLost

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
    this.dispatchEvent(new java.awt.event.WindowEvent(this, java.awt.Event.WINDOW_DESTROY));
}//GEN-LAST:event_jButtonCloseActionPerformed

private void jTextFieldClientAETKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldClientAETKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        jButtonAdd.doClick();
    }
}//GEN-LAST:event_jTextFieldClientAETKeyPressed

private void jTextFieldAETitleFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldAETitleFocusLost
    try {
        accessList.setAETitle(jTextFieldAETitle.getText());
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jTextFieldAETitleFocusLost

private void jTextFieldMaxAssocFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldMaxAssocFocusLost
    try {
        queryRetrieve.setMaxClientAssoc(Integer.valueOf(jTextFieldMaxAssoc.getText()));
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Only numbers are accepted!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);

        jTextFieldMaxAssoc.grabFocus();
    }
}//GEN-LAST:event_jTextFieldMaxAssocFocusLost

private void jTextFieldMaxPDUReceiveFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldMaxPDUReceiveFocusLost
    try {
        queryRetrieve.setMaxPDULengthReceive(Integer.valueOf(jTextFieldMaxPDUReceive.getText()));
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Only numbers are accepted!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);

        jTextFieldMaxPDUReceive.grabFocus();
    }
}//GEN-LAST:event_jTextFieldMaxPDUReceiveFocusLost

private void jTextFieldMaxPDUSendFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldMaxPDUSendFocusLost
    try {
        queryRetrieve.setMaxPDULengthSend(Integer.valueOf(jTextFieldMaxPDUSend.getText()));
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Only numbers are accepted!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);

        jTextFieldMaxPDUSend.grabFocus();
    }
}//GEN-LAST:event_jTextFieldMaxPDUSendFocusLost

private void jTextFieldQRTimeoutFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldQRTimeoutFocusLost
    try {
        queryRetrieve.setQRIdleTimeout(Integer.valueOf(jTextFieldQRTimeout.getText()));
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Only numbers are accepted!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);

        jTextFieldQRTimeout.grabFocus();
    }
}//GEN-LAST:event_jTextFieldQRTimeoutFocusLost

private void jTextFieldQRAcceptTimeoutFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldQRAcceptTimeoutFocusLost
    try {
        queryRetrieve.setQRAcceptTimeout(Integer.valueOf(jTextFieldQRAcceptTimeout.getText()));
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Only numbers are accepted!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);

        jTextFieldQRAcceptTimeout.grabFocus();
    }
}//GEN-LAST:event_jTextFieldQRAcceptTimeoutFocusLost

private void jTextFieldQRResponseTimeoutFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldQRResponseTimeoutFocusLost
    try {
        queryRetrieve.setQRRspDelay(Integer.valueOf(jTextFieldQRResponseTimeout.getText()));
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Only numbers are accepted!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);

        jTextFieldQRResponseTimeout.grabFocus();
    }
}//GEN-LAST:event_jTextFieldQRResponseTimeoutFocusLost

private void jTextFieldQRConnectionTimeoutFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldQRConnectionTimeoutFocusLost
    try {
        queryRetrieve.setQRConnectionTimeout(Integer.valueOf(jTextFieldQRConnectionTimeout.getText()));
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Only numbers are accepted!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);

        jTextFieldQRConnectionTimeout.grabFocus();
    }
}//GEN-LAST:event_jTextFieldQRConnectionTimeoutFocusLost

private void jTextFieldQRPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldQRPortFocusLost
    try {
        startupserv.setDICOMQRPort(Integer.valueOf(jTextFieldQRPort.getText()));
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Only numbers are accepted!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);

        jTextFieldQRPort.grabFocus();
    }
}//GEN-LAST:event_jTextFieldQRPortFocusLost

private void jCheckBoxAcceptedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxAcceptedActionPerformed
    try {
        String UID = jLabelName.getText();

        SOPClass.setAccepted(UID, jCheckBoxAccepted.isSelected());
        savelocalTS();
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jCheckBoxAcceptedActionPerformed

private void jButtonDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDefaultActionPerformed
    try {
        boolean[] def = SOPClass.setDefault();

        jCheckBoxAccepted.setSelected(true);

        updateCheckBoxes(def);

        if (!jCheckBoxAffectAll.isSelected()) {
            savelocalTS();
        } else {
            saveallTS();
        }
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButtonDefaultActionPerformed

private void jButtonAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAllActionPerformed
    try {
        boolean[] def = SOPClass.setAll();

        jCheckBoxAccepted.setSelected(true);

        updateCheckBoxes(def);

        if (!jCheckBoxAffectAll.isSelected()) {
            savelocalTS();
        } else {
            saveallTS();
        }
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButtonAllActionPerformed

private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
    try {
        boolean[] def = SOPClass.clearAll();

        jCheckBoxAccepted.setSelected(false);

        updateCheckBoxes(def);

        if (!jCheckBoxAffectAll.isSelected()) {
            savelocalTS();
        } else {
            saveallTS();
        }
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButtonClearActionPerformed

private void jButtonRebuildIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRebuildIndexActionPerformed
    try {
        int ret = directorySet.rebuildIndex();

        if (ret == 0)
        {
            /*TaskList tasks = TaskList.getInstance();
            tasks.setVisible(true);
            tasks.toFront();*/
        }
        else if(ret == 1) {
            JOptionPane.showMessageDialog(this, "Defined storage path is invalid!", "Error : storage path invalid", JOptionPane.ERROR_MESSAGE);
        } else if (ret == 2) {
            JOptionPane.showMessageDialog(this, "Please stop the storage server!", "Error : storage server running", JOptionPane.ERROR_MESSAGE);
        }

    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButtonRebuildIndexActionPerformed

private void jButtonRebuildDicomDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRebuildDicomDirActionPerformed
    try {
        int ret = directorySet.rebuildDICOMDir();

        if (ret == 1) {
            JOptionPane.showMessageDialog(this, "Defined storage path is invalid!", "Error : storage path invalid", JOptionPane.ERROR_MESSAGE);
        } else if (ret == 2) {
            JOptionPane.showMessageDialog(this, "Please stop the storage server!", "Error : storage server running", JOptionPane.ERROR_MESSAGE);
        }

    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButtonRebuildDicomDirActionPerformed

private void jButtonStoragePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoragePathActionPerformed
    try {
        if (!ClientCore.getInstance().isLocalServer()){
            class Action1 extends FileAction {

                @Override
                public void setFileChoosed(String filePath) {
                    try {
                        setStoragePath(filePath);
                    } catch (RemoteException ex) {
                        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            FileAction action = new Action1();

            RemoteFileChooser chooser = new RemoteFileChooser(AdminRefs.getInstance().getRFS(), directorySet.getStoragePath(), action);
            chooser.setTitle("Storage Path");
            chooser.setFileSelectionMode(RemoteFileChooser.DIRECTORIES_ONLY);

            chooser.setVisible(true);
        }
        else{
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(directorySet.getStoragePath()));
            chooser.setDialogTitle("Storage Path");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                setStoragePath(chooser.getSelectedFile().toString());
                
        }
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButtonStoragePathActionPerformed

private void jButtonDiccogleDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiccogleDirActionPerformed
    try {
        if (!ClientCore.getInstance().isLocalServer()){
        class Action1 extends FileAction {

            @Override
            public void setFileChoosed(String filePath) {
                try {
                    setDicoogleDir(filePath);

                } catch (RemoteException ex) {
                    Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        FileAction action = new Action1();

        RemoteFileChooser chooser = new RemoteFileChooser(AdminRefs.getInstance().getRFS(), directorySet.getDicoogleDir(), action);
        chooser.setTitle("DicoogleDir Path");
        chooser.setFileSelectionMode(RemoteFileChooser.DIRECTORIES_ONLY);

        chooser.setVisible(true);
        }
        else{
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(directorySet.getDicoogleDir()));
            chooser.setDialogTitle("DicoogleDir Path");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                setDicoogleDir(chooser.getSelectedFile().toString());
        }
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButtonDiccogleDirActionPerformed

private void jSlider1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider1MouseReleased
    try {
        directorySet.setIndexerEffort(jSlider1.getValue());
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jSlider1MouseReleased

private void jSlider1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jSlider1KeyReleased
    try {
        directorySet.setIndexerEffort(jSlider1.getValue());
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jSlider1KeyReleased

private void jCheckBoxIndexAllModalitiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxIndexAllModalitiesActionPerformed
        try {
            indexOptions.setIndexAllModalities(jCheckBoxIndexAllModalities.isSelected());
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jCheckBoxIndexAllModalitiesActionPerformed

private void jTextFieldRGUIPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldRGUIPortFocusLost
    try {
        startupserv.setRemoteGUIPort(Integer.valueOf(jTextFieldRGUIPort.getText()));
        
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Only numbers are accepted!",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);

        jTextFieldRGUIPort.grabFocus();
    }
}//GEN-LAST:event_jTextFieldRGUIPortFocusLost

private void jCheckBoxPermitAllAETitlesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxPermitAllAETitlesActionPerformed
        try {
            accessList.setPermitAllAETitles(jCheckBoxPermitAllAETitles.isSelected());

            boolean NOT_isSelected = !jCheckBoxPermitAllAETitles.isSelected();

            jListAET.setEnabled(NOT_isSelected);
            jTextFieldClientAET.setEnabled(NOT_isSelected);
            jButtonAdd.setEnabled(NOT_isSelected);
            jButtonRemove.setEnabled(NOT_isSelected);
            jLabel19.setEnabled(NOT_isSelected);
            jLabel4.setEnabled(NOT_isSelected);
            jLabel22.setEnabled(NOT_isSelected);
            
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jCheckBoxPermitAllAETitlesActionPerformed

private void jCheckBoxIndexZIPFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxIndexZIPFilesActionPerformed

    try
    {
        directorySet.setIndexZip(jCheckBoxIndexZIPFiles.isSelected());
    }
    catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }



}//GEN-LAST:event_jCheckBoxIndexZIPFilesActionPerformed

private void jTextFieldRGUIExtIPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldRGUIExtIPFocusLost
    try {
        startupserv.setRemoteGUIExtIP(jTextFieldRGUIExtIP.getText());

    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jTextFieldRGUIExtIPFocusLost

private void jLabel57MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel57MouseClicked
    JOptionPane.showMessageDialog(this, "If your Dicoogle GUI Server is behind a router, you need to provide your external IP address to have access from outside of the router.\n"
            + "Besides that, you net to configure your router to open the Remote GUI Port!", "Did you know?", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_jLabel57MouseClicked

private void jCheckBoxDirectoryWatcherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxDirectoryWatcherActionPerformed

      try {
        directorySet.setMonitorWatcher(jCheckBoxDirectoryWatcher.isSelected());
    } catch (RemoteException ex) {
        Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jCheckBoxDirectoryWatcherActionPerformed

private void jCheckBoxAnonymousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxAnonymousActionPerformed
        try {
            directorySet.setIndexAnonymous(jCheckBoxAnonymous.isSelected());
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    
}//GEN-LAST:event_jCheckBoxAnonymousActionPerformed

    private void jButtonTagsRemoveDicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTagsRemoveDicActionPerformed

        
        String value = (String) jListDics.getSelectedValue();
        try {
            indexOptions.removeDictionary(value);
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
        jListDics.remove(jListDics.getSelectedIndex());
        


    }//GEN-LAST:event_jButtonTagsRemoveDicActionPerformed
    private  String value;
    private void jButtonTagsAddDicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTagsAddDicActionPerformed

        
        
        ClientCore clientCore = ClientCore.getInstance();
        value = "";
        if (!clientCore.isLocalServer())
        {
            class Action1 extends FileAction
            {

                private boolean resume = false;

                public void setResume(boolean resume)
                {

                    this.resume = resume;
                }

                @Override
                public void setFileChoosed(String filePath)
                {
                    value = filePath;
                }
            }

            Action1 action = new Action1();
            

            RemoteFileChooser chooser = new RemoteFileChooser(AdminRefs.getInstance().getRFS(), AdminRefs.getInstance().getDefaultFilePath(), action);

            chooser.setTitle("Dictionary file");
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            chooser.setVisible(true);
            // TODO:  put showTaskList = false; -- somewhere...


        } else
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(AdminRefs.getInstance().getDefaultFilePath()));
            chooser.setDialogTitle("Dicoogle Scan Directory");
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                value = chooser.getSelectedFile().toString();

            }

        }
        
        
        
        
        
        
        try {
            indexOptions.addDictionary(value);
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        lmDic.addElement(value);
        

    }//GEN-LAST:event_jButtonTagsAddDicActionPerformed

    private void jCheckBoxGZipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxGZipActionPerformed
        try {
            directorySet.setGZipStorage(jCheckBoxGZip.isSelected());
        } catch (RemoteException ex) {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jCheckBoxGZipActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonAll;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonDefault;
    private javax.swing.JButton jButtonDiccogleDir;
    private javax.swing.JButton jButtonRebuildDicomDir;
    private javax.swing.JButton jButtonRebuildIndex;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JButton jButtonStoragePath;
    private javax.swing.JButton jButtonTagsAdd;
    private javax.swing.JButton jButtonTagsAddDic;
    private javax.swing.JButton jButtonTagsDIM;
    private javax.swing.JButton jButtonTagsManual;
    private javax.swing.JButton jButtonTagsModalities;
    private javax.swing.JButton jButtonTagsModalityAdd;
    private javax.swing.JButton jButtonTagsModalityRemove;
    private javax.swing.JButton jButtonTagsRemove;
    private javax.swing.JButton jButtonTagsRemoveDic;
    private javax.swing.JButton jButtonWrite;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox12;
    private javax.swing.JCheckBox jCheckBox13;
    private javax.swing.JCheckBox jCheckBox14;
    private javax.swing.JCheckBox jCheckBox15;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JCheckBox jCheckBoxAccepted;
    private javax.swing.JCheckBox jCheckBoxAffectAll;
    private javax.swing.JCheckBox jCheckBoxAnonymous;
    private javax.swing.JCheckBox jCheckBoxDICOMQR;
    private javax.swing.JCheckBox jCheckBoxDICOMStorage;
    private javax.swing.JCheckBox jCheckBoxDirectoryWatcher;
    private javax.swing.JCheckBox jCheckBoxGZip;
    private javax.swing.JCheckBox jCheckBoxIndexAllModalities;
    private javax.swing.JCheckBox jCheckBoxIndexThumbnails;
    private javax.swing.JCheckBox jCheckBoxIndexZIPFiles;
    private javax.swing.JCheckBox jCheckBoxP2P;
    private javax.swing.JCheckBox jCheckBoxPermitAllAETitles;
    private javax.swing.JCheckBox jCheckBoxWebServer;
    private javax.swing.JCheckBox jCheckBoxWebServices;
    private javax.swing.JComboBox jComboBoxMatrixThumbnails;
    private javax.swing.JComboBox jComboBoxModalityFind;
    private javax.swing.JComboBox jComboBoxSOP;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelDicoogleDirPath;
    private javax.swing.JLabel jLabelMaxAssoc;
    private javax.swing.JLabel jLabelMaxPDUReceive;
    private javax.swing.JLabel jLabelMaxPDUSend;
    private javax.swing.JLabel jLabelModalityFind;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelPath;
    private javax.swing.JLabel jLabelTSInfo;
    private javax.swing.JLabel jLabelTagsDIMStatus;
    private javax.swing.JList jListAET;
    private javax.swing.JList jListDics;
    private javax.swing.JList jListTagsModalities;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelTagsDIMFields;
    private javax.swing.JPanel jPanelTagsManual;
    private javax.swing.JPanel jPanelTagsModalities;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextFieldAETitle;
    private javax.swing.JTextField jTextFieldClientAET;
    private javax.swing.JTextField jTextFieldDICOMStoragePort;
    private javax.swing.JTextField jTextFieldMaxAssoc;
    private javax.swing.JTextField jTextFieldMaxPDUReceive;
    private javax.swing.JTextField jTextFieldMaxPDUSend;
    private javax.swing.JTextField jTextFieldQRAcceptTimeout;
    private javax.swing.JTextField jTextFieldQRConnectionTimeout;
    private javax.swing.JTextField jTextFieldQRPort;
    private javax.swing.JTextField jTextFieldQRResponseTimeout;
    private javax.swing.JTextField jTextFieldQRTimeout;
    private javax.swing.JTextField jTextFieldRGUIExtIP;
    private javax.swing.JTextField jTextFieldRGUIPort;
    private javax.swing.JTextField jTextFieldTagsMGroup;
    private javax.swing.JTextField jTextFieldTagsMName;
    private javax.swing.JTextField jTextFieldTagsMSubG;
    private javax.swing.JTextField jTextFieldTagsModality;
    private javax.swing.JTextField jTextFieldWebPort;
    private javax.swing.JTextField jTextFieldWebServicesPort;
    private javax.swing.JTree jTreeTagsDIM;
    private javax.swing.JTree jTreeTagsManual;
    // End of variables declaration//GEN-END:variables


    private void setStoragePath(String path) throws RemoteException{
        int ret = directorySet.setStoragePath(path);

        if (ret == 0) {
            jLabelPath.setText(path);
        } else if (ret == 1) {
            JOptionPane.showMessageDialog(ServerOptions.this, "The file is not a directory", "Error : Invalid File", JOptionPane.ERROR_MESSAGE);
        } else if (ret == 2) {
            JOptionPane.showMessageDialog(ServerOptions.this, "Read Access denied", "Error : Read Access", JOptionPane.ERROR_MESSAGE);
        } else if (ret == 3) {
            JOptionPane.showMessageDialog(ServerOptions.this, "Write Access denied", "Error : Write Access", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setDicoogleDir(String path) throws RemoteException{
        int ret = directorySet.setDicoogleDir(path);

        if (ret == 0) {
            jLabelDicoogleDirPath.setText(path);
        } else if (ret == 1) {
            JOptionPane.showMessageDialog(ServerOptions.this, "The file is not a directory", "Error : Invalid File", JOptionPane.ERROR_MESSAGE);
        } else if (ret == 2) {
            JOptionPane.showMessageDialog(ServerOptions.this, "Read Access denied", "Error : Read Access", JOptionPane.ERROR_MESSAGE);
        } else if (ret == 3) {
            JOptionPane.showMessageDialog(ServerOptions.this, "Write Access denied", "Error : Write Access", JOptionPane.ERROR_MESSAGE);
        }
    }
}
