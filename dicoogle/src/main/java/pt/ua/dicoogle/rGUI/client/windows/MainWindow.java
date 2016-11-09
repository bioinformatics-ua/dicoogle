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
 * MainWindow.java
 *
 * Created on 9 de Novembro de 2007, 11:25
 */
package pt.ua.dicoogle.rGUI.client.windows;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.Main;
import pt.ua.dicoogle.core.settings.ClientSettings;
import pt.ua.dicoogle.core.QueryHistorySupport;
import pt.ua.dicoogle.plugins.NetworkMember;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.rGUI.RFileBrowser.FileAction;
import pt.ua.dicoogle.rGUI.RFileBrowser.RemoteFile;
import pt.ua.dicoogle.rGUI.RFileBrowser.RemoteFileChooser;
import pt.ua.dicoogle.rGUI.client.AdminRefs;
import pt.ua.dicoogle.rGUI.client.ClientCore;
import pt.ua.dicoogle.rGUI.client.UIHelper.DisplayJAI;
import pt.ua.dicoogle.rGUI.client.UIHelper.OSXAdapter;
import pt.ua.dicoogle.rGUI.client.UIHelper.Result2Tree;
import pt.ua.dicoogle.rGUI.client.UIHelper.TrayIconCreator;
import pt.ua.dicoogle.rGUI.client.UserRefs;
import pt.ua.dicoogle.rGUI.fileTransfer.FileReceiver;
import pt.ua.dicoogle.rGUI.fileTransfer.TransferStatus;
import pt.ua.dicoogle.rGUI.server.controllers.PluginController4user;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;
import pt.ua.dicoogle.utils.Dicom2JPEG;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

/**
 * Dicoogle GUI Main form
 *
 * @author Filipe Freitas
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Marco Pereira
 * @author João Pereira
 * @author Samuel Campos <samuelcampos@ua.pt>
 * @author Carlos Ferreira <c.ferreira@ua.pt>
 * @author Frederico Valente
 */
@Deprecated
public class MainWindow extends javax.swing.JFrame {

    private Result2Tree searchTree;
    private static MainWindow instance = null;
    private static Semaphore sem = new Semaphore(1, true);
    private ClientCore clientCore;
    //private DefaultMutableTreeNode top = null;

    /*
     * Information about last query executed
     * It is usefull to the Export Module
     */
    private String lastQueryExecuted;
    private boolean lastQueryKeywords;
    private boolean lastQueryAdvanced;
    private ArrayList<javax.swing.JCheckBox> ranges;

    //a popup menu for plugin extension on retrieved results
    private JPopupMenu popupMenu = new JPopupMenu();

    public static synchronized MainWindow getInstance() {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new MainWindow();
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
    private final TaskList taskList;

    /**
     * Creates new form MainWindow
     */
    private MainWindow() {
        ranges = new ArrayList<>();
        List<String> names = null;
        try {
            names = PluginController4user.getInstance().getPluginNames();
            for (String name : names) {
                JCheckBox newJCB = new JCheckBox(name);
                if (PluginController4user.getInstance().isLocalPlugin(name)) {
                    newJCB.setSelected(true);
                }
                this.ranges.add(newJCB);
            }
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(MainWindow.class).error(ex.getMessage(), ex);
        }

        initComponents();

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        SequentialGroup groupBoxesH = jPanel6Layout.createSequentialGroup().addComponent(jLabel2);
        ParallelGroup groupBoxesV = jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel2);

        for (JCheckBox cbox : this.ranges) {
            groupBoxesH.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(cbox);
            groupBoxesV.addComponent(cbox);
        }

        Group groupH = jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).
                addGroup(groupBoxesH.addContainerGap(10, Short.MAX_VALUE));

        Group groupV = jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addGroup(groupBoxesV).addContainerGap(5, Short.MAX_VALUE));

        jPanel6Layout.setHorizontalGroup(groupH);
        jPanel6Layout.setVerticalGroup(groupV);

        Image image = getImage("trayicon.gif");
        this.setIconImage(image);

        clientCore = ClientCore.getInstance();

        if (!clientCore.isAdmin()) {
            jMenu10.setVisible(false);
            jMenuItemShutdown.setVisible(false);
            jMenuDirScan2.setVisible(false);

            //jPanel8.setVisible(false);
            jButtonServices.setVisible(false);
            jButtonPreferences.setVisible(false);
            jButtonLogs.setVisible(false);
        }

        if (!clientCore.isUser()) {
            jPanel5.setVisible(false);
            jButtonClientPreferences.setVisible(false);
        } else {
            searchTree = Result2Tree.getInstance();
        }

        if (Main.isFixedClient()) {
            jMenuItemShutdown.setVisible(false);

        } else {
            jMenuItem7.setVisible(false);
        }

        jLabelResults.setText("Enter your terms and hit the button.");
        SelectDefaultSearch.setSelected(true);
        SelectAdvancedSearch.setSelected(false);
        jPanel2.setVisible(false);
        ModalSelectNone.setSelected(false);
        ModalSelectAll.setSelected(true);
        ModalCR.setSelected(true);
        ModalCT.setSelected(true);
        ModalDX.setSelected(true);
        ModalES.setSelected(true);
        ModalMG.setSelected(true);
        ModalMR.setSelected(true);
        ModalNM.setSelected(true);
        ModalOT.setSelected(true);
        ModalPT.setSelected(true);
        ModalRF.setSelected(true);
        ModalSC.setSelected(true);
        ModalUS.setSelected(true);
        ModalXA.setSelected(true);

        StudyDateRangeInitialBoundary.setEnabled(false);
        StudyDateRangeTerminalBoundary.setEnabled(false);
        DateRange.setSelected(false);
        ExactDate.setSelected(true);

        StudyDateRangeInitialBoundaryActivation.setEnabled(false);
        StudyDateRangeTerminalBoundaryActivation.setEnabled(false);
        StudyDateRangeInitialBoundary.setEnabled(false);
        StudyDateRangeTerminalBoundary.setEnabled(false);

        //tree view init
        jTreeResults.setModel(new DefaultTreeModel(Result2Tree.getInstance().getTop()));
        jTreeResults.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        jButtonDownload.setEnabled(false);
        jButtonView.setEnabled(false);
        jButtonDump.setEnabled(false);
        this.resizeWindow();
        registerForMacOSXEvents();
        centerWindow();

//////////////////////////////////////////////////////////////
        /**
         * This search is needed to autocomplete
         */
        /*
         QueryResults q = new QueryResults("*:*");
        
        
         List<String> items = q.getFields();
        
        
        
         boolean strictMatching = false;
         AutoCompleteDecorator.decorate(jTextFieldQuery, items, strictMatching);
         *
         */
        //plugins v2
        try {
            List<JPanel> panels = PluginController4user.getInstance().getTabPanels();
            if (panels != null) {
                for (JPanel panel : panels) {
                    tabPanel.add(panel);
                }
            }
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(MainWindow.class).error(ex.getMessage(), ex);
        }

        try {
            List<JMenuItem> menus = PluginController4user.getInstance().getPluginMenus();
            if (menus != null) {
                for (JMenuItem menu : menus) {
                    pluginMenu.add(menu);
                }
            }
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(MainWindow.class).error(ex.getMessage(), ex);
        }

        try {
            System.err.println("Checking for plugins requiring gui expansion.,.");
            List<JMenuItem> items = PluginController4user.getInstance().getRightButtonItems();
            if (items != null) {
                for (JMenuItem item : items) {
                    popupMenu.add(item);
                }
            }
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(MainWindow.class).error(ex.getMessage(), ex);
        }

        taskList = new TaskList();
        taskList.setName("Task Progress");
        tabPanel.add(taskList);

    }

    /**
     * Center the main Window taking into account the Screen Size
     */
    private void centerWindow() {
        // Positions the window in the center of screen
        int width = this.getWidth();
        int height = this.getHeight();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, width, height);
    }

    private void resizeWindow() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screen.width - 30, screen.height - 50);
    }

    /**
     * Register the Events to Quit and About if Dicoogle is running on Mac_OSX
     */
    private void registerForMacOSXEvents() {
        if (Main.MAC_OS_X) {
            try {
                //System.out.println("Registering MAC_OSX Events");

                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                if (Main.isFixedClient()) {
                    OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("exit", (Class[]) null));
                } else {
                    OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("exitClient", (Class[]) null));
                }

                OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[]) null));
                //OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[])null));
                //OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadImageFile", new Class[] { String.class }));

            } catch (Exception e) {
                System.err.println("Error while loading the OSXAdapter:");
                e.printStackTrace();
            }
        }
    }

    public void exit() {
        jMenuItem1ActionPerformed(null);
    }

    public void exitClient() {
        jMenuItem11ActionPerformed(null);
    }

    public void about() {
        jMenuItem3ActionPerformed(null);
    }

    /**
     * ************************************************
     * Private Methods
     *************************************************
     */
    /**
     * Stops the window from minimizing to tray while in options screen
     */
    private void showOptions() {
    }

    private void cleanThumbnails() {
//        Result2Tree.showImage("Image Thumbnail", null, jPanelThumbnail);
        jPanelThumbnail.setSize(64, 64);
        repaint();
        return;
    }

    /**
     *  * @author Joaoffr  <joaoffr@ua.pt>
     *  * @author DavidP   <davidp@ua.pt>
     * @param t
     * @return
     */
    private String convMillisToTimeString(long t) {
        long milis = t % 1000;
        t /= 1000;
        long segs = t % 60;
        t /= 60;
        long mins = t % 60;
        t /= 60;
        long hours = t % 60;
        t /= 24;
        long days = t;

        return String.format("%d:%02d:%02d:%02d", days, hours, mins, segs);
    }

    private void dcm2JPEG(int thumbnailSize) {

        /**
         * Why couldn't? It works!
         *
         * if (System.getProperty("os.name").toUpperCase().indexOf("MAC OS") !=
         * -1) { JOptionPane.showMessageDialog(this, "Operation Not Available to
         * MAC OS.", "Missing JAI Tool", JOptionPane.WARNING_MESSAGE); return; }
         */
        String pathDir = ".";

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(pathDir));
        chooser.setDialogTitle("Dicoogle Dcm2JPG - Select DICOM File");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //chooser.setFileFilter(arg0)
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File filePath = new File(chooser.getSelectedFile().toString());
            if (filePath.exists() && filePath.isFile() && filePath.canRead()) {
                File jpgFile = new File(filePath.getAbsolutePath() + ".jpg");
                Dicom2JPEG.convertDicom2Jpeg(filePath, jpgFile, thumbnailSize);
            }
        }
    }

    /**
     * @return ArrayList<String> with selected items in the tree
     */
    private ArrayList<String> getSelectedLocalFiles() {
        ArrayList<String> files = new ArrayList<String>();

        TreePath path = jTreeResults.getSelectionPath();

        // Tree Root is not permited
        if (path == null || path.getPathCount() < 2) {
            return null;
        }

        jTreeResults.expandPath(path);

        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

            // recieves all childs
            ArrayList<DefaultMutableTreeNode> childs = getLocalLeafs(node, path);

            Iterator<DefaultMutableTreeNode> it = childs.iterator();

            // converts TreeNodes to filePaths
            while (it.hasNext()) {
                Object obj = it.next().getUserObject();

                if (SearchResult.class.isInstance(obj)) {
                    files.add(obj.toString());
                }
            }
        }

        return files;
    }

    private ArrayList<DefaultMutableTreeNode> getLocalLeafs(DefaultMutableTreeNode node, TreePath path) {
        ArrayList<DefaultMutableTreeNode> list = new ArrayList<DefaultMutableTreeNode>();
        TreePath temp;

        if (node.isLeaf()) {
            list.add(node);
        } else {
            Enumeration<DefaultMutableTreeNode> en = node.children();

            while (en.hasMoreElements()) {
                DefaultMutableTreeNode elem = en.nextElement();

                temp = path.pathByAddingChild(elem);
                jTreeResults.expandPath(temp);

                list.addAll(getLocalLeafs(elem, temp));
            }
        }

        return list;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabPanel = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        SelectAdvancedSearch = new javax.swing.JRadioButton();
        SelectDefaultSearch = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        OperatorName = new javax.swing.JTextField();
        Physician = new javax.swing.JTextField();
        PatientGender = new javax.swing.JComboBox();
        PatientName = new javax.swing.JTextField();
        InstitutionName = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        ModalCR = new javax.swing.JCheckBox();
        ModalMG = new javax.swing.JCheckBox();
        ModalPT = new javax.swing.JCheckBox();
        ModalCT = new javax.swing.JCheckBox();
        ModalMR = new javax.swing.JCheckBox();
        ModalRF = new javax.swing.JCheckBox();
        ModalDX = new javax.swing.JCheckBox();
        ModalNM = new javax.swing.JCheckBox();
        ModalSC = new javax.swing.JCheckBox();
        ModalES = new javax.swing.JCheckBox();
        ModalOT = new javax.swing.JCheckBox();
        ModalUS = new javax.swing.JCheckBox();
        ModalXA = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        ModalSelectAll = new javax.swing.JRadioButton();
        ModalSelectNone = new javax.swing.JRadioButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        PatientID = new javax.swing.JTextField();
        AdvancedSearchButton = new javax.swing.JButton();
        ResetFields = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        StudyDateRangeInitialBoundaryActivation = new javax.swing.JCheckBox();
        StudyDateRangeInitialBoundary = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        StudyDateRangeTerminalBoundaryActivation = new javax.swing.JCheckBox();
        StudyDateRangeTerminalBoundary = new javax.swing.JTextField();
        StudyDate = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ExactDate = new javax.swing.JRadioButton();
        DateRange = new javax.swing.JRadioButton();
        jLabel17 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldQuery = new javax.swing.JTextField();
        jButtonSearch = new javax.swing.JButton();
        SearchTips = new javax.swing.JButton();
        jCheckBoxKeywords = new javax.swing.JCheckBox();
        jButtonQueryHistory = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jButtonSend = new javax.swing.JButton();
        jLabelResults = new javax.swing.JLabel();
        jButtonDump = new javax.swing.JButton();
        jButtonDownload = new javax.swing.JButton();
        jLabelTime = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jButtonView = new javax.swing.JButton();
        jPanelThumbnail = new javax.swing.JPanel();
        jButtonExport = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTreeResults = new javax.swing.JTree();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel8 = new javax.swing.JPanel();
        jButtonServices = new javax.swing.JButton();
        jButtonPreferences = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jButtonLogs = new javax.swing.JButton();
        jButtonPeers = new javax.swing.JButton();
        jButtonClientPreferences = new javax.swing.JButton();
        jMenuBar3 = new javax.swing.JMenuBar();
        jMenu9 = new javax.swing.JMenu();
        jMenuItemChangePassword = new javax.swing.JMenuItem();
        jMenuDirScan2 = new javax.swing.JMenuItem();
        jMenuDirScanResume = new javax.swing.JMenuItem();
        jMenuItemShutdown = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu10 = new javax.swing.JMenu();
        jMenuItemPreferences = new javax.swing.JMenuItem();
        jMenuItemServices = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItemUsers = new javax.swing.JMenuItem();
        jMenuItemActiveUsers = new javax.swing.JMenuItem();
        jMenuTools2 = new javax.swing.JMenu();
        jMenuItemDcm2jpeg2 = new javax.swing.JMenuItem();
        jMenu11 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        pluginMenu = new javax.swing.JMenu();
        jMenu12 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Dicoogle PACS Archive");
        setMinimumSize(new java.awt.Dimension(700, 526));
        setName("MainWindow"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
            public void windowDeiconified(java.awt.event.WindowEvent evt) {
                formWindowDeiconified(evt);
            }
        });

        jScrollPane2.setPreferredSize(new java.awt.Dimension(602, 602));

        jPanel5.setMaximumSize(new java.awt.Dimension(1197, 100));
        jPanel5.setPreferredSize(new java.awt.Dimension(1197, 100));

        SelectAdvancedSearch.setText("Advanced search");
        SelectAdvancedSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectAdvancedSearchActionPerformed(evt);
            }
        });

        SelectDefaultSearch.setText("Default search");
        SelectDefaultSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectDefaultSearchActionPerformed(evt);
            }
        });

        jLabel5.setText("Search type:");

        jLabel7.setText("Patient Name:");

        jLabel8.setText("Patient Gender:");

        jLabel9.setText("Institution Name:");

        jLabel10.setText("Physician:");

        jLabel11.setText("Operator Name:");

        OperatorName.setText("(All operators)");

        Physician.setText("(All physicians)");

        PatientGender.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Male", "Female" }));

        PatientName.setText("(All patients)");

        InstitutionName.setText("(All institutions)");

        ModalCR.setText("CR");
        ModalCR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalCRActionPerformed(evt);
            }
        });

        ModalMG.setText("MG");
        ModalMG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalMGActionPerformed(evt);
            }
        });

        ModalPT.setText("PT");
        ModalPT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalPTActionPerformed(evt);
            }
        });

        ModalCT.setText("CT");
        ModalCT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalCTActionPerformed(evt);
            }
        });

        ModalMR.setText("MR");
        ModalMR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalMRActionPerformed(evt);
            }
        });

        ModalRF.setText("RF");
        ModalRF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalRFActionPerformed(evt);
            }
        });

        ModalDX.setText("DX");
        ModalDX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalDXActionPerformed(evt);
            }
        });

        ModalNM.setText("NM");
        ModalNM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalNMActionPerformed(evt);
            }
        });

        ModalSC.setText("SC");
        ModalSC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalSCActionPerformed(evt);
            }
        });

        ModalES.setText("ES");
        ModalES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalESActionPerformed(evt);
            }
        });

        ModalOT.setText("OT");
        ModalOT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalOTActionPerformed(evt);
            }
        });

        ModalUS.setText("US");
        ModalUS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalUSActionPerformed(evt);
            }
        });

        ModalXA.setText("XA");
        ModalXA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalXAActionPerformed(evt);
            }
        });

        jLabel14.setText("Modality:");

        ModalSelectAll.setText("Select all");
        ModalSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalSelectAllActionPerformed(evt);
            }
        });

        ModalSelectNone.setText("Select none");
        ModalSelectNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModalSelectNoneActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ModalSelectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ModalSelectNone))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(ModalDX)
                                .addComponent(ModalCT))
                            .addComponent(ModalCR))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ModalMG)
                            .addComponent(ModalNM)
                            .addComponent(ModalMR))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ModalPT)
                            .addComponent(ModalSC)
                            .addComponent(ModalRF))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ModalUS)
                            .addComponent(ModalXA)
                            .addComponent(ModalOT))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ModalES)))
                .addGap(202, 202, 202))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(ModalSelectAll)
                    .addComponent(ModalSelectNone))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ModalPT)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ModalMG)
                        .addComponent(ModalCR, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ModalXA)
                    .addComponent(ModalES))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ModalUS, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ModalRF)
                    .addComponent(ModalMR)
                    .addComponent(ModalCT))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ModalOT)
                    .addComponent(ModalSC)
                    .addComponent(ModalNM)
                    .addComponent(ModalDX))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel19.setText("Note: Only what you change will modify the default query.");

        jLabel20.setText("Patient ID:");

        PatientID.setText("(All IDs)");

        AdvancedSearchButton.setText("Search");
        AdvancedSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdvancedSearchButtonActionPerformed(evt);
            }
        });

        ResetFields.setText("Reset fields");
        ResetFields.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResetFieldsActionPerformed(evt);
            }
        });

        StudyDateRangeInitialBoundaryActivation.setText("From:");
        StudyDateRangeInitialBoundaryActivation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StudyDateRangeInitialBoundaryActivationActionPerformed(evt);
            }
        });

        StudyDateRangeInitialBoundary.setText("(Beginning)");

        jLabel16.setText("--");

        StudyDateRangeTerminalBoundaryActivation.setText("To:");
        StudyDateRangeTerminalBoundaryActivation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StudyDateRangeTerminalBoundaryActivationActionPerformed(evt);
            }
        });

        StudyDateRangeTerminalBoundary.setText("(Today)");

        StudyDate.setText("(All dates)");

        jLabel13.setText("(yyyymmdd form)");

        jLabel15.setText("Date Range:");

        jLabel12.setText("Date:");

        jLabel4.setText("Study Date search type:");

        ExactDate.setText("Exact Date");
        ExactDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExactDateActionPerformed(evt);
            }
        });

        DateRange.setText("Date Range");
        DateRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DateRangeActionPerformed(evt);
            }
        });

        jLabel17.setText("(yyyymmdd form)");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ExactDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DateRange))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(2, 2, 2)
                        .addComponent(StudyDate, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)))
                .addGap(71, 71, 71))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(StudyDateRangeInitialBoundary, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16))
                    .addComponent(StudyDateRangeInitialBoundaryActivation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(StudyDateRangeTerminalBoundary, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel17))
                    .addComponent(StudyDateRangeTerminalBoundaryActivation))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ExactDate)
                    .addComponent(DateRange)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StudyDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(StudyDateRangeInitialBoundaryActivation)
                            .addComponent(StudyDateRangeTerminalBoundaryActivation)
                            .addComponent(jLabel15)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(StudyDateRangeInitialBoundary, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16)
                            .addComponent(StudyDateRangeTerminalBoundary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(PatientID)
                            .addComponent(PatientGender, 0, 118, Short.MAX_VALUE)
                            .addComponent(Physician)
                            .addComponent(InstitutionName)
                            .addComponent(PatientName)
                            .addComponent(OperatorName)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(AdvancedSearchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ResetFields))
                    .addComponent(jLabel19)
                    .addComponent(jLabel7)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(381, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(AdvancedSearchButton)
                            .addComponent(ResetFields))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(PatientName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(PatientID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(PatientGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(InstitutionName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(Physician, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(OperatorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jLabel1.setText("Search Pattern :");

        jLabel3.setText("Regular expressions are supported (eg: A*).");

        jTextFieldQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldQueryActionPerformed(evt);
            }
        });
        jTextFieldQuery.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldQueryKeyPressed(evt);
            }
        });

        jButtonSearch.setText("Search");
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });

        SearchTips.setText("Search Tips");
        SearchTips.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchTipsActionPerformed(evt);
            }
        });

        jCheckBoxKeywords.setSelected(true);
        jCheckBoxKeywords.setText("keywords");

        jButtonQueryHistory.setText("Query History");
        jButtonQueryHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonQueryHistoryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldQuery, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBoxKeywords))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButtonSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SearchTips)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonQueryHistory))
                    .addComponent(jLabel3))
                .addContainerGap(683, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButtonSearch, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SearchTips, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonQueryHistory, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldQuery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxKeywords))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jButtonSend.setText("Send");
        jButtonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendActionPerformed(evt);
            }
        });

        jLabelResults.setText("jLabel2");

        jButtonDump.setText("Dump");
        jButtonDump.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDumpActionPerformed(evt);
            }
        });

        jButtonDownload.setText("Download");
        jButtonDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownloadActionPerformed(evt);
            }
        });

        jLabelTime.setText("<<results time>>");

        jLabel22.setText("Time Results(ms):");

        jButtonView.setText("View");
        jButtonView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelThumbnailLayout = new javax.swing.GroupLayout(jPanelThumbnail);
        jPanelThumbnail.setLayout(jPanelThumbnailLayout);
        jPanelThumbnailLayout.setHorizontalGroup(
            jPanelThumbnailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 67, Short.MAX_VALUE)
        );
        jPanelThumbnailLayout.setVerticalGroup(
            jPanelThumbnailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 62, Short.MAX_VALUE)
        );

        jButtonExport.setText("Export");
        jButtonExport.setMaximumSize(new java.awt.Dimension(82, 29));
        jButtonExport.setMinimumSize(new java.awt.Dimension(82, 29));
        jButtonExport.setPreferredSize(new java.awt.Dimension(82, 29));
        jButtonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(120, 120, 120)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonExport, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                    .addComponent(jButtonDownload, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                    .addComponent(jButtonSend, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                    .addComponent(jButtonView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                    .addComponent(jButtonDump, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel9Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabelResults)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel22)
                                .addGroup(jPanel9Layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(jPanelThumbnail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, Short.MAX_VALUE)
                            .addComponent(jLabelTime)))
                    .addContainerGap()))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(92, Short.MAX_VALUE)
                .addComponent(jButtonExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDownload)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSend)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonView)
                .addGap(5, 5, 5)
                .addComponent(jButtonDump)
                .addContainerGap())
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel9Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabelResults)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel22)
                        .addComponent(jLabelTime))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                    .addComponent(jPanelThumbnail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(53, 53, 53)))
        );

        jTreeResults.setModel(null);
        jTreeResults.setRowHeight(15);
        jTreeResults.setToggleClickCount(3);
        jTreeResults.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeResultsMouseClicked(evt);
            }
        });
        jTreeResults.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
            }
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                jTreeResultsTreeExpanded(evt);
            }
        });
        jTreeResults.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeResultsValueChanged(evt);
            }
        });
        jTreeResults.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTreeResultsKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTreeResults);

        jLabel2.setText("Search Range:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addContainerGap(474, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2)
        );

        jSeparator2.setMaximumSize(new java.awt.Dimension(50, 10));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 1279, Short.MAX_VALUE)
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 1279, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(SelectDefaultSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SelectAdvancedSearch)
                        .addGap(28, 28, 28)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(SelectDefaultSearch)
                        .addComponent(SelectAdvancedSearch))
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );

        jScrollPane2.setViewportView(jPanel5);

        tabPanel.addTab("Search", jScrollPane2);

        getContentPane().add(tabPanel, java.awt.BorderLayout.CENTER);

        jSplitPane1.setDividerSize(2);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setMaximumSize(new java.awt.Dimension(2147483647, 100));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(606, 100));

        jPanel8.setPreferredSize(new java.awt.Dimension(604, 80));

        jButtonServices.setIcon(new ImageIcon(getImage("services.gif")));
        jButtonServices.setText("Services");
        jButtonServices.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonServices.setMaximumSize(new java.awt.Dimension(80, 80));
        jButtonServices.setMinimumSize(new java.awt.Dimension(80, 80));
        jButtonServices.setPreferredSize(new java.awt.Dimension(80, 80));
        jButtonServices.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonServices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonServicesActionPerformed(evt);
            }
        });

        jButtonPreferences.setIcon(new ImageIcon(getImage("config.gif")));
        jButtonPreferences.setText("Preferences");
        jButtonPreferences.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPreferences.setMaximumSize(new java.awt.Dimension(80, 80));
        jButtonPreferences.setMinimumSize(new java.awt.Dimension(80, 80));
        jButtonPreferences.setPreferredSize(new java.awt.Dimension(80, 80));
        jButtonPreferences.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreferencesActionPerformed(evt);
            }
        });

        jButtonLogs.setIcon(new ImageIcon(getImage("log.gif")));
        jButtonLogs.setText("Logs");
        jButtonLogs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonLogs.setMaximumSize(new java.awt.Dimension(80, 80));
        jButtonLogs.setMinimumSize(new java.awt.Dimension(80, 80));
        jButtonLogs.setPreferredSize(new java.awt.Dimension(80, 80));
        jButtonLogs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonLogs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLogsActionPerformed(evt);
            }
        });

        jButtonPeers.setIcon(new ImageIcon(getImage("peers.png")));
        jButtonPeers.setText("P2P Peers");
        jButtonPeers.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPeers.setMaximumSize(new java.awt.Dimension(80, 80));
        jButtonPeers.setMinimumSize(new java.awt.Dimension(80, 80));
        jButtonPeers.setPreferredSize(new java.awt.Dimension(80, 80));
        jButtonPeers.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPeers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPeersActionPerformed(evt);
            }
        });

        jButtonClientPreferences.setIcon(new ImageIcon(getImage("settings.png")));
        jButtonClientPreferences.setText("Client Prefs");
        jButtonClientPreferences.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonClientPreferences.setMaximumSize(new java.awt.Dimension(80, 80));
        jButtonClientPreferences.setMinimumSize(new java.awt.Dimension(80, 80));
        jButtonClientPreferences.setPreferredSize(new java.awt.Dimension(80, 80));
        jButtonClientPreferences.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonClientPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClientPreferencesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonServices, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPreferences, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonLogs, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPeers, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonClientPreferences, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(102, 102, 102)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(752, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButtonClientPreferences, 0, 0, Short.MAX_VALUE)
                            .addComponent(jButtonPeers, 0, 0, Short.MAX_VALUE)
                            .addComponent(jButtonLogs, 0, 0, Short.MAX_VALUE)
                            .addComponent(jButtonPreferences, 0, 0, Short.MAX_VALUE)
                            .addComponent(jButtonServices, javax.swing.GroupLayout.PREFERRED_SIZE, 64, Short.MAX_VALUE))))
                .addGap(124, 124, 124))
        );

        jSplitPane1.setTopComponent(jPanel8);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.NORTH);

        jMenu9.setText("File");

        jMenuItemChangePassword.setText("Change Password");
        jMenuItemChangePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChangePasswordActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItemChangePassword);

        jMenuDirScan2.setText("Scan Disk");
        jMenuDirScan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuDirScanActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuDirScan2);

        jMenuDirScanResume.setText("Scan Disk (resume)");
        jMenuDirScanResume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuDirScanResumeActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuDirScanResume);

        jMenuItemShutdown.setText("Shutdown Client&Server");
        jMenuItemShutdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemShutdownActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItemShutdown);

        jMenuItem11.setText("Exit Client");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem11);

        jMenuItem7.setText("Exit Client&Server");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem7);

        jMenuBar3.add(jMenu9);

        jMenu10.setText("Edit");

        jMenuItemPreferences.setText("Preferences");
        jMenuItemPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItemPreferences);

        jMenuItemServices.setText("Services");
        jMenuItemServices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemServicesActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItemServices);

        jMenuItem10.setText("Logs");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem10);

        jMenuItemUsers.setText("User Accounts");
        jMenuItemUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUsersActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItemUsers);

        jMenuItemActiveUsers.setText("ActiveUsers");
        jMenuItemActiveUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemActiveUsersActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItemActiveUsers);

        jMenuBar3.add(jMenu10);

        jMenuTools2.setText("Tools");

        jMenuItemDcm2jpeg2.setText("dcm2jpeg");
        jMenuItemDcm2jpeg2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDcm2jpeg2ActionPerformed(evt);
            }
        });
        jMenuTools2.add(jMenuItemDcm2jpeg2);

        jMenuBar3.add(jMenuTools2);

        jMenu11.setText("Skin");

        jMenuItem1.setText("Business");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed1(evt);
            }
        });
        jMenu11.add(jMenuItem1);

        jMenuItem2.setText("Business Blue Steel");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed1(evt);
            }
        });
        jMenu11.add(jMenuItem2);

        jMenuItem3.setText("Business Black Steel");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed1(evt);
            }
        });
        jMenu11.add(jMenuItem3);

        jMenuItem4.setText("Creme");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem4);

        jMenuItem5.setText("Magma");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem5);

        jMenuItem6.setText("Raven");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem6);

        jMenuItem8.setText("Raven Graphite Glass");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem8);

        jMenuBar3.add(jMenu11);

        pluginMenu.setText("Plugins");
        jMenuBar3.add(pluginMenu);

        jMenu12.setText("Help");

        jMenuItem9.setText("About");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu12.add(jMenuItem9);

        jMenuBar3.add(jMenu12);

        setJMenuBar(jMenuBar3);

        getAccessibleContext().setAccessibleDescription("Dicoogle PACS Archive");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (Main.isFixedClient()) {
            jMenuItem7.doClick();
        } else {
            jMenuItem11.doClick();
        }
    }//GEN-LAST:event_formWindowClosing

    private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
        this.setExtendedState(MainWindow.ICONIFIED);
        this.setVisible(false);
    }//GEN-LAST:event_formWindowIconified

    private void formWindowDeiconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeiconified
        this.setExtendedState(MainWindow.NORMAL);
        this.setVisible(true);
    }//GEN-LAST:event_formWindowDeiconified

    private void jButtonPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreferencesActionPerformed
        showOptions();
    }//GEN-LAST:event_jButtonPreferencesActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        showOptions();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        File toDelete = new File("pluginClasses");
        String[] deleteArray = toDelete.list();
        if (deleteArray != null) {
            for (String fileName : deleteArray) {
                File f = new File("pluginClasses/" + fileName);
                f.delete();
            }
        }
        if (clientCore.isAdmin() && AdminRefs.getInstance().unsavedSettings()) {
            Object[] opt
                    = {
                        "Save", "Discard", "Cancel"
                    };

            String message = "There are unsaved Server Settings.\nDo you want to save them?";
            int op = JOptionPane.showOptionDialog(this, message, "Unsaved Server Settings", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opt, opt[2]);

            if (op == 0) {
                AdminRefs.getInstance().saveSettings();
            }

            if (op == 2) {
                return;
            }
        }

        if (ClientOptions.getInstance().unsavedSettings()) {
            Object[] opt
                    = {
                        "Save", "Discard", "Cancel"
                    };

            String message = "There are unsaved Client Settings.\nDo you want to save them?";
            int op = JOptionPane.showOptionDialog(this, message, "Unsaved Client Settings", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opt, opt[2]);

            if (op == 0) {
                ClientOptions.getInstance().saveSettings();
            }

            if (op == 2) {
                return;
            }
        }

        QueryHistorySupport.getInstance().saveQueryHistory();

        if (clientCore.isAdmin()) {
            AdminRefs.getInstance().shutdownServer();
        }

        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
    }//GEN-LAST:event_jMenuItem3ActionPerformed

private void jMenuDirScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuDirScanActionPerformed
    scanDisk(false);
}//GEN-LAST:event_jMenuDirScanActionPerformed

    /**
     * Do an advanced search
     *
     * @param evt
     */
    private void jButtonLogsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogsActionPerformed

        Logs logs = Logs.getInstance();

        if (logs != null) {
            logs.setVisible(true);
            logs.toFront();
            //this.setAutostart(false);
        }

    }//GEN-LAST:event_jButtonLogsActionPerformed

    private void jButtonServicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonServicesActionPerformed
        Services serv = Services.getInstance();
        serv.setVisible(true);
        serv.toFront();
        //this.setAutostart(false);
    }//GEN-LAST:event_jButtonServicesActionPerformed

    private void jMenuItemServicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemServicesActionPerformed

        // invokes the event of the button jButtonServices
        jButtonServices.doClick();
    }//GEN-LAST:event_jMenuItemServicesActionPerformed

    private void jMenuItem1ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed1
        repaint();
    }//GEN-LAST:event_jMenuItem1ActionPerformed1

    private void jMenuItem2ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed1
        repaint();
    }//GEN-LAST:event_jMenuItem2ActionPerformed1

    private void jMenuItem3ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed1
        repaint();
    }//GEN-LAST:event_jMenuItem3ActionPerformed1

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        repaint();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        repaint();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        repaint();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        repaint();
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed

        // invokes the event of the button jButtonLogs
        jButtonLogs.doClick();
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jButtonPeersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPeersActionPerformed
        try {
            List<NetworkMember> peerList = UserRefs.getInstance().getSearch().getPeerList();

            String peerNames = "These are the P2P peers that are connected:";
            if (peerList.size() == 0) {
                peerNames = "No peers connected!";
            }

            for (NetworkMember s : peerList) {
                peerNames += "\n" + s.getPeerName() + " : " + s.getPluginName();
            }

            JOptionPane.showMessageDialog(this, peerNames, "P2P Peers", JOptionPane.INFORMATION_MESSAGE);

        } catch (RemoteException ex) {
            LoggerFactory.getLogger(MainWindow.class).error(ex.getMessage(), ex);
        }
    }//GEN-LAST:event_jButtonPeersActionPerformed

    private void jMenuItemUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUsersActionPerformed
        UsersManager usersManager = UsersManager.getInstance();

        if (usersManager != null) {
            usersManager.setVisible(true);
            usersManager.toFront();
            //this.setAutostart(false);
        }
    }//GEN-LAST:event_jMenuItemUsersActionPerformed

    public void updateP2PThumbnail(SearchResult result) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getjTreeResults().getLastSelectedPathComponent();
        if (node == null) {
            return;
        }

        Object nodeInfo = null;
        DefaultMutableTreeNode nodeLeaf = null;

        if (node.getLevel() == 4 || (node.isLeaf() && node.getLevel() > 1)) {
            Object nodeInfoLeaf = null;

            if (node.getLevel() == 4) {
                nodeLeaf = node.getFirstLeaf();
                nodeInfoLeaf = nodeLeaf.getUserObject();
            } else {
                // Leaf
                nodeInfo = node.getUserObject();
                nodeLeaf = node;
                nodeInfoLeaf = nodeInfo;
            }

            //SearchResult r = (SearchResult) nodeInfoLeaf;
            if (nodeInfoLeaf == result) {
                showThumbnail((String) result.getExtraData().get("Thumbnail"));
            }
        }

    }

    private void jMenuItemChangePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemChangePasswordActionPerformed
        ChangePassword changePassword = ChangePassword.getInstance();

        if (changePassword != null) {
            changePassword.setVisible(true);
            changePassword.toFront();
        }
    }//GEN-LAST:event_jMenuItemChangePasswordActionPerformed

    private void jMenuItemActiveUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemActiveUsersActionPerformed
        ActiveSessions activeSessions = ActiveSessions.getInstance();

        if (activeSessions != null) {
            activeSessions.setVisible(true);
            activeSessions.toFront();
            //this.setAutostart(false);
        }
    }//GEN-LAST:event_jMenuItemActiveUsersActionPerformed

    private void jButtonClientPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClientPreferencesActionPerformed
        ClientOptions cliOptions = ClientOptions.getInstance();

        if (cliOptions != null) {
            cliOptions.setVisible(true);
            cliOptions.toFront();
        }
    }//GEN-LAST:event_jButtonClientPreferencesActionPerformed

    @SuppressWarnings("empty-statement")
    private void jMenuItemShutdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemShutdownActionPerformed
        Object[] opt
                = {
                    "Yes", "No"
                };

        String message = "Are you shure you want to shutdown the server?";
        int op = JOptionPane.showOptionDialog(this, message, "Shut down Server", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opt, opt[1]);

        if (op == 0) {
            //Logout from GUI Server
            if (clientCore.isAdmin()) {

                if (AdminRefs.getInstance().unsavedSettings()) {
                    Object[] opt1
                            = {
                                "Save", "Discard"
                            };

                    message = "There are unsaved Server Settings.\nDo you want to save them?";
                    op = JOptionPane.showOptionDialog(this, message, "Unsaved Server Settings", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opt1, opt1[0]);

                    if (op == 0) {
                        AdminRefs.getInstance().saveSettings();
                    }

                }
            }

            if (clientCore.isUser()) {
                if (ClientOptions.getInstance().unsavedSettings()) {
                    Object[] opt2
                            = {
                                "Save",
                                "Discard"
                            };

                    message = "There are unsaved Client Settings.\nDo you want to save them?";
                    op = JOptionPane.showOptionDialog(this, message, "Unsaved Client Settings", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opt2, opt2[0]);

                    if (op == 0) {
                        ClientOptions.getInstance().saveSettings();
                    }
                }
            }

            QueryHistorySupport.getInstance().saveQueryHistory();

            if (clientCore.isAdmin()) {
                AdminRefs.getInstance().shutdownServer();
            }

            System.exit(0);
        }
    }//GEN-LAST:event_jMenuItemShutdownActionPerformed

    private void jMenuItemDcm2jpeg2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDcm2jpeg2ActionPerformed
        dcm2JPEG(0);
    }//GEN-LAST:event_jMenuItemDcm2jpeg2ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        //Logout from GUI Server
        try {
            if (clientCore.isAdmin() && AdminRefs.getInstance().unsavedSettings()) {
                Object[] opt
                        = {
                            "Save", "Discard", "Cancel"
                        };

                String message = "There are unsaved Server Settings.\nDo you want to save them?";
                int op = JOptionPane.showOptionDialog(this, message, "Unsaved Server Settings", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opt, opt[2]);

                if (op == 0) {
                    AdminRefs.getInstance().saveSettings();
                }

                if (op == 2) {
                    return;
                }
            }

            if (clientCore.isUser() && ClientOptions.getInstance().unsavedSettings()) {
                Object[] opt
                        = {
                            "Save", "Discard", "Cancel"
                        };

                String message = "There are unsaved Client Settings.\nDo you want to save them?";
                int op = JOptionPane.showOptionDialog(this, message, "Unsaved Client Settings", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opt, opt[2]);

                if (op == 0) {
                    ClientOptions.getInstance().saveSettings();
                }

                if (op == 2) {
                    return;
                }
            }

            if (clientCore.isAdmin()) {
                AdminRefs.getInstance().logout();
            }

            searchTree.unexportSearchSignal();
            clientCore.getUser().logout();

        } catch (RemoteException ex) {
            LoggerFactory.getLogger(MainWindow.class).error(ex.getMessage(), ex);
        }
        File toDelete = new File("pluginClasses");
        String[] deleteArray = toDelete.list();
        if (deleteArray != null) {
            for (String fileName : deleteArray) {
                File f = new File("pluginClasses/" + fileName);
                f.delete();
            }
        }
        QueryHistorySupport.getInstance().saveQueryHistory();

        if (Main.isFixedClient()) {
            clientCore.stopKeepAlives();
            this.dispose();
        } else {
            System.exit(0);
        }
    }//GEN-LAST:event_jMenuItem11ActionPerformed

private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
    File toDelete = new File("pluginClasses");
    String[] deleteArray = toDelete.list();
    if (deleteArray != null) {
        for (String fileName : deleteArray) {
            File f = new File("pluginClasses/" + fileName);
            f.delete();
        }
    }
    if (clientCore.isAdmin() && AdminRefs.getInstance().unsavedSettings()) {
        Object[] opt
                = {
                    "Save", "Discard", "Cancel"
                };

        String message = "There are unsaved Server Settings.\nDo you want to save them?";
        int op = JOptionPane.showOptionDialog(this, message, "Unsaved Server Settings", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opt, opt[2]);

        if (op == 0) {
            AdminRefs.getInstance().saveSettings();
        }

        if (op == 2) {
            return;
        }
    }

    if (ClientOptions.getInstance().unsavedSettings()) {
        Object[] opt
                = {
                    "Save", "Discard", "Cancel"
                };

        String message = "There are unsaved Client Settings.\nDo you want to save them?";
        int op = JOptionPane.showOptionDialog(this, message, "Unsaved Client Settings", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opt, opt[2]);

        if (op == 0) {
            ClientOptions.getInstance().saveSettings();
        }

        if (op == 2) {
            return;
        }
    }

    QueryHistorySupport.getInstance().saveQueryHistory();

    if (clientCore.isAdmin()) {
        AdminRefs.getInstance().shutdownServer();
    }

    System.exit(0);
}//GEN-LAST:event_jMenuItem7ActionPerformed

private void jTreeResultsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTreeResultsKeyReleased
    if (KeyEvent.VK_DELETE == evt.getKeyCode() && clientCore.isAdmin()) {
        ArrayList<String> files = getSelectedLocalFiles();

        if (files != null && !files.isEmpty()) {
            try {
                Object[] opt
                        = {
                            "Remove and Delete", "Remove", "Cancel"
                        };
                String message = "Are you sure you want to remove these files from Index Engine?";

                int op = JOptionPane.showOptionDialog(this, message, "Remove Files", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, opt, opt[2]);

                if (op == 0) {
                    AdminRefs.getInstance().getIndexOptions().removeFilesFromIndexer(files, true);
                }
                if (op == 1) {
                    AdminRefs.getInstance().getIndexOptions().removeFilesFromIndexer(files, false);
                }
            } catch (RemoteException ex) {
                LoggerFactory.getLogger(MainWindow.class).error(ex.getMessage(), ex);
            }
        }
    }
}//GEN-LAST:event_jTreeResultsKeyReleased

    /*
     * This need some serious rewritting
     * alot of cruft
     */
private void jTreeResultsValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeResultsValueChanged

    /*
     DefaultMutableTreeNode node = (DefaultMutableTreeNode) getjTreeResults().getLastSelectedPathComponent();
     if (node == null){
     return;
     }
     this.jButtonDownload.setAutostart(false);
     Object nodeInfo = null;
     DefaultMutableTreeNode nodeLeaf = null;


     if (node.getLevel() == 4 || (node.isLeaf() && node.getLevel() > 1)){
     Object nodeInfoLeaf = null;

     if (node.getLevel() == 4){
     nodeLeaf = node.getFirstLeaf();
     nodeInfoLeaf = nodeLeaf.getUserObject();
     }
     else{
     // Leaf
     nodeInfo = node.getUserObject();
     nodeLeaf = node;
     nodeInfoLeaf = nodeInfo;
     }

     SearchResult r = (SearchResult) nodeInfoLeaf;

     //HashMap extras = r.getExtraData();
     //String thumb = (String) extras.get("Thumbnail");

     //System.out.println("Filename: " + r.getFileName());
     //System.out.println("FileHash: " + r.getFileHash());
     */
    /*if (thumb != null){
     showThumbnail(thumb);
     }
     else if (!SearchResult.class.isInstance(nodeInfoLeaf) && SearchResult.class.isInstance(nodeInfoLeaf)){
     SearchResult res = searchTree.searchThumbnail(r.getURI(), "filehash");//was r.getFileHash, should be placed on extradata

     if (res != null){
     HashMap extras2 = res.getExtraData();

     if (extras2 != null){
     thumb = (String) extras2.get("Thumbnail");

     if (thumb != null){
     extras.put("Thumbnail", thumb); // put the thumbnail in the original SearchResult

     showThumbnail(thumb);
     }
     else{
     cleanThumbnails();
     }
     }
     }
     //TODO
     //this must be removed! we must not care where the search comes from
     }
     else if (SearchResult.class.isInstance(nodeInfoLeaf)){
     searchTree.searchP2PThumbnail(r);
     cleanThumbnails();
     }
     else{
     cleanThumbnails();
     }*/
    /*  }
     else{
     cleanThumbnails();
     }

     //Controll the enable buttons
     if (node.isLeaf())
     {
     jButtonDump.setAutostart(true);
     IPluginControllerUser plugins = null;
     try
     {
     plugins = this.clientCore.getUser().getPluginController();
            
     //dafuq is this?
     if ((SearchResult) nodeInfo==null)
     {
                
            
     }
     */
            //TODO: fix this!
            /*if (!plugins.isLocalPlugin(((SearchResult) nodeInfo).getPluginName()))
     {
     jButtonDownload.setAutostart(true);
     jButtonSend.setAutostart(false);
     } else
     {*/
    /*          jButtonSend.setAutostart(true);
     jButtonView.setAutostart(true);
     //}
     }
     catch (RemoteException ex){
     Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     else{
     jButtonDump.setAutostart(false);
     jButtonDownload.setAutostart(false);
     jButtonView.setAutostart(false);

     jButtonSend.setAutostart(true);
     }*/
}//GEN-LAST:event_jTreeResultsValueChanged

private void jTreeResultsTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_jTreeResultsTreeExpanded
    //searchTree.completeTree(evt);
}//GEN-LAST:event_jTreeResultsTreeExpanded

private void jTreeResultsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeResultsMouseClicked
    //Double Click -> Show MetaData
    if (evt.getClickCount() == 2) {
        showMetaData();
    }

    if (evt.getButton() == MouseEvent.BUTTON3) {
        popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        popupMenu.list();
    }
}//GEN-LAST:event_jTreeResultsMouseClicked

private void jButtonExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportActionPerformed
    if (jTreeResults.getModel().getChildCount(jTreeResults.getModel().getRoot()) == 0) {
        JOptionPane.showMessageDialog(this, "You can't export information without search results.", "Lack of Search Results", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    ExportData ed;

    HashMap<String, Boolean> plugins = new HashMap<String, Boolean>();
    for (JCheckBox box : this.ranges) {
        plugins.put(box.getText(), box.isSelected());
    }
    if (!lastQueryAdvanced) {

        ed = new ExportData(lastQueryExecuted, lastQueryKeywords, plugins);
    } else {
        ed = new ExportData(lastQueryExecuted, true, plugins);
    }

    ed.setVisible(true);
    ed.toFront();
}//GEN-LAST:event_jButtonExportActionPerformed

//what a nice and descriptive name... I don't know what it does,
//so there is a big chance of having broke something
private void jButtonViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewActionPerformed
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeResults.getLastSelectedPathComponent();
    if (node == null) {
        return;
    }

    Object nodeInfo = node.getUserObject();
    // int selected = this.jList1.getSelectedIndex();
    if (node.isLeaf()) {
        if (SearchResult.class.isInstance(nodeInfo)) {
            SearchResult tmp = (SearchResult) nodeInfo;

            //Why are we caring about this?
            if (clientCore.isLocalServer()) {

                File f = new File(tmp.getURI());
                if (!f.exists()) {
                    JOptionPane.showMessageDialog(this, "Dicoogle can't open this file, because this file does not exists in your file system. Try Dump button instead View!", "Error opening the file", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (ClientSettings.getInstance().getExtV() == null
                        || ClientSettings.getInstance().getExtV().equals("")) {

                    try {
                        Desktop.getDesktop().open(new File(tmp.getURI()));

                    } catch (IOException ex) {
                        //oh dear
                        String folder = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf('/'));

                        try {
                            Desktop.getDesktop().open(new File(folder));

                        } catch (IOException ex1) {
                            JOptionPane.showMessageDialog(this, "Dicoogle can't open this file!", "Error opening the file", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    try {
                        //I changed something, no ideia do i have on what goes around here
                        ProcessBuilder pb = new ProcessBuilder(ClientSettings.getInstance().getExtV(), tmp.getURI().toString());
                        pb.start();

                        //Runtime.getRuntime().exec(ClientSettings.getSettings().getExtV() + " "+path);
                    } catch (IOException ex) {
                        //ex.printStackTrace();

                        String folder = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf('/'));

                        try {
                            Desktop.getDesktop().open(new File(folder));

                        } catch (IOException ex1) {
                            JOptionPane.showMessageDialog(this, "Dicoogle can't open this file!", "Error opening the file", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                try {
                    SimpleEntry<RemoteFile, Integer> entry = UserRefs.getInstance().getSearch().downloadFile(tmp);

                    TransferStatus ts = new TransferStatus(entry.getKey());

                    FileReceiver receiver = new FileReceiver(entry.getKey(), clientCore.getServerAddress(), entry.getValue(), ts);

                    Thread tReceiver = receiver;
                    tReceiver.start();

                    ts.setVisible(true);
                    ts.toFront();

                } catch (RemoteException ex) {
                    LoggerFactory.getLogger(MainWindow.class).error(ex.getMessage(), ex);
                } catch (IOException ex) {
                    LoggerFactory.getLogger(MainWindow.class).error(ex.getMessage(), ex);
                }
            }
        }
    }
}//GEN-LAST:event_jButtonViewActionPerformed

private void jButtonDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownloadActionPerformed

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeResults.getLastSelectedPathComponent();
    if (node == null) {
        return;
    }

    Object nodeInfo = node.getUserObject();

    if (node.isLeaf() && SearchResult.class.isInstance(nodeInfo)) {
        try {
            SearchResult temp = (SearchResult) nodeInfo;
            UserRefs.getInstance().getSearch().RequestP2PFile(temp);

        } catch (RemoteException ex) {
            LoggerFactory.getLogger(MainWindow.class).error(ex.getMessage(), ex);
        }
    }
}//GEN-LAST:event_jButtonDownloadActionPerformed

private void jButtonDumpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDumpActionPerformed
    showMetaData();
}//GEN-LAST:event_jButtonDumpActionPerformed

private void jButtonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendActionPerformed
    ArrayList<String> files = getSelectedLocalFiles();

    if (files != null && !files.isEmpty()) {
        DicomSend d = new DicomSend(files);

        d.setVisible(true);
        d.toFront();
    } else {
        JOptionPane.showMessageDialog(this, "Please Select Local Files to send.", "Select files", JOptionPane.INFORMATION_MESSAGE);
    }
}//GEN-LAST:event_jButtonSendActionPerformed

private void jButtonQueryHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQueryHistoryActionPerformed
    QueryHistory QH = QueryHistory.getInstance();
    QH.setVisible(true);
    QH.toFront();
    QH.setJTextFieldQuery(jTextFieldQuery, jCheckBoxKeywords);
}//GEN-LAST:event_jButtonQueryHistoryActionPerformed

private void SearchTipsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchTipsActionPerformed
    SearchTips tip = new SearchTips(this.jTextFieldQuery);
    tip.setVisible(true);
    tip.toFront();
}//GEN-LAST:event_SearchTipsActionPerformed

    enum QUERY_STATE {

        READY_TO_SEARCH, WAITING_FOR_RESULTS
    }
    private QUERY_STATE state = QUERY_STATE.READY_TO_SEARCH;
    private boolean basicSearch = true;

    public void search() {

        LoggerFactory.getLogger(MainWindow.class).debug("State: {}", state);
        if (state == QUERY_STATE.WAITING_FOR_RESULTS) {

            LoggerFactory.getLogger(MainWindow.class).debug("Pruning query");

            pruneQuery();
            return;
        }

        this.jButtonDownload.setEnabled(false);
        lastQueryExecuted = jTextFieldQuery.getText();
        lastQueryKeywords = jCheckBoxKeywords.isSelected();
        lastQueryAdvanced = false;
        HashMap<String, Boolean> plugins = new HashMap<String, Boolean>();
        boolean isSelectedPlugins = false;
        for (JCheckBox box : this.ranges) {
            plugins.put(box.getText(), box.isSelected());
            isSelectedPlugins = isSelectedPlugins || box.isSelected();
        }
        if (!isSelectedPlugins) {
            JOptionPane.showMessageDialog(this, "Please select a source to search", "Missing data source", JOptionPane.INFORMATION_MESSAGE);
        } else {
            basicSearch = true;
            searchTree.search(lastQueryExecuted, lastQueryKeywords, plugins);
            state = QUERY_STATE.WAITING_FOR_RESULTS;
            jButtonSearch.setText("Cancel");
            cleanThumbnails();
            QueryHistorySupport.getInstance().saveQueryHistory();
        }

    }

    public void pruneQuery() {
        System.out.println("Prune here");
        searchTree.pruneQuery(null);
        state = QUERY_STATE.READY_TO_SEARCH;
        jButtonSearch.setText("Search");
    }

    public void finishQuery() {
        //System.out.println("The query is done");
        jButtonSearch.setText("Search");

        state = QUERY_STATE.READY_TO_SEARCH;
    }

    /*
     * Updates the tree view using the query results.
     * 
     * todo: the tree is not a parameter to the method, as such it has state. make the tree to be updated an argument
     */
    private void updateSearchView(Iterable<SearchResult> searchResultIterator) {

        HashMap<String, HashMap<String, HashMap<String, HashMap<String, SearchResult>>>> tree;//omfgIHateYou;
        tree = SearchResult.toTree(searchResultIterator);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Search Results");

        for (String patientName : tree.keySet()) {
            DefaultMutableTreeNode patientMap = new DefaultMutableTreeNode(patientName);
            root.add(patientMap);

            for (String studyUID : tree.get(patientName).keySet()) {
                DefaultMutableTreeNode studyMap = new DefaultMutableTreeNode(studyUID);
                patientMap.add(studyMap);

                for (String seriesUID : tree.get(patientName).get(studyUID).keySet()) {
                    DefaultMutableTreeNode seriesMap = new DefaultMutableTreeNode(seriesUID);
                    studyMap.add(seriesMap);

                    for (SearchResult r : tree.get(patientName).get(studyUID).get(seriesUID).values()) {
                        DefaultMutableTreeNode images = new DefaultMutableTreeNode(r);
                        seriesMap.add(images);
                    }
                }
            }
        }
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        jTreeResults.setModel(treeModel);
    }

private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed
    String query = jTextFieldQuery.getText();

    //prepares the info we are interested in
    HashMap<String, Object> options = new HashMap<>();
    options.put("PatientName", null);
    options.put("PatientID", null);
    options.put("StudyInstanceUID", null);
    options.put("SeriesInstanceUID", null);
    options.put("SOPInstanceUID", null);

    //runs the query task asynchronously
    List<String> providers = new ArrayList<>();
    for (JCheckBox chkBox : this.ranges) {
        if (chkBox.isSelected()) {
            providers.add(chkBox.getText());
            System.out.println("Selected: " + chkBox.getText());
        }
    }

    JointQueryTask task = new JointQueryTask() {

        @Override
        public void onReceive(Task<Iterable<SearchResult>> e) {
            // TODO Auto-generated method stub
            try {
                updateSearchView(e.get());
            } catch (InterruptedException | ExecutionException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        @Override
        public void onCompletion() {
			// TODO Auto-generated method stub

        }
    };

    task = PluginController.getInstance().query(task, providers, query, options);


}//GEN-LAST:event_jButtonSearchActionPerformed


private void DateRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DateRangeActionPerformed
    if (ExactDate.isSelected()) {
        ExactDate.setSelected(false);
    }
    StudyDateRangeInitialBoundaryActivation.setEnabled(true);
    StudyDateRangeTerminalBoundaryActivation.setEnabled(true);
    StudyDateRangeInitialBoundary.setEnabled(false);
    StudyDateRangeTerminalBoundary.setEnabled(false);
    StudyDate.setEnabled(false);
}//GEN-LAST:event_DateRangeActionPerformed

private void ExactDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExactDateActionPerformed
    if (DateRange.isSelected()) {
        DateRange.setSelected(false);
    }
    StudyDateRangeInitialBoundaryActivation.setEnabled(false);
    StudyDateRangeTerminalBoundaryActivation.setEnabled(false);
    StudyDateRangeInitialBoundary.setEnabled(false);
    StudyDateRangeTerminalBoundary.setEnabled(false);
    StudyDate.setEnabled(true);
}//GEN-LAST:event_ExactDateActionPerformed

private void StudyDateRangeTerminalBoundaryActivationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StudyDateRangeTerminalBoundaryActivationActionPerformed
    if (StudyDateRangeTerminalBoundaryActivation.isSelected()) {
        StudyDateRangeTerminalBoundary.setEnabled(true);
    } else {
        StudyDateRangeTerminalBoundary.setEnabled(false);
    }
}//GEN-LAST:event_StudyDateRangeTerminalBoundaryActivationActionPerformed

private void StudyDateRangeInitialBoundaryActivationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StudyDateRangeInitialBoundaryActivationActionPerformed
    if (StudyDateRangeInitialBoundaryActivation.isSelected()) {
        StudyDateRangeInitialBoundary.setEnabled(true);
    } else {
        StudyDateRangeInitialBoundary.setEnabled(false);
    }
}//GEN-LAST:event_StudyDateRangeInitialBoundaryActivationActionPerformed

private void ResetFieldsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetFieldsActionPerformed

    ModalSelectNone.setSelected(false);
    ModalSelectAll.setSelected(true);
    ModalCR.setSelected(true);
    ModalCT.setSelected(true);
    ModalDX.setSelected(true);
    ModalES.setSelected(true);
    ModalMG.setSelected(true);
    ModalMR.setSelected(true);
    ModalNM.setSelected(true);
    ModalOT.setSelected(true);
    ModalPT.setSelected(true);
    ModalRF.setSelected(true);
    ModalSC.setSelected(true);
    ModalUS.setSelected(true);
    ModalXA.setSelected(true);

    StudyDateRangeInitialBoundary.setEnabled(false);
    StudyDateRangeTerminalBoundary.setEnabled(false);

    DateRange.setSelected(false);
    ExactDate.setSelected(true);

    StudyDateRangeInitialBoundaryActivation.setEnabled(false);
    StudyDateRangeTerminalBoundaryActivation.setEnabled(false);
    StudyDateRangeInitialBoundary.setEnabled(false);
    StudyDateRangeTerminalBoundary.setEnabled(false);
    StudyDate.setEnabled(true);
    PatientName.setText("(All patients)");
    PatientID.setText("(All IDs)");
    PatientGender.setSelectedIndex(0);
    InstitutionName.setText("(All institutions)");
    Physician.setText("(All physicians)");
    OperatorName.setText("(All operators)");
    StudyDate.setText("(All dates)");
    StudyDateRangeInitialBoundary.setText("(Beginning)");
    StudyDateRangeTerminalBoundary.setText("(Today)");
}//GEN-LAST:event_ResetFieldsActionPerformed

private void AdvancedSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdvancedSearchButtonActionPerformed
    lastQueryExecuted = getAdvancedQuery();
    lastQueryAdvanced = true;
    HashMap<String, Boolean> plugins = new HashMap<String, Boolean>();
    for (JCheckBox box : this.ranges) {
        plugins.put(box.getText(), box.isSelected());
    }
    searchTree.search(lastQueryExecuted, true, plugins);

    cleanThumbnails();
}//GEN-LAST:event_AdvancedSearchButtonActionPerformed

private void ModalSelectNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalSelectNoneActionPerformed
    ModalSelectNone.setSelected(true);
    ModalSelectAll.setSelected(false);
    ModalCR.setSelected(false);
    ModalCT.setSelected(false);
    ModalDX.setSelected(false);
    ModalES.setSelected(false);
    ModalMG.setSelected(false);
    ModalMR.setSelected(false);
    ModalNM.setSelected(false);
    ModalOT.setSelected(false);
    ModalPT.setSelected(false);
    ModalRF.setSelected(false);
    ModalSC.setSelected(false);
    ModalUS.setSelected(false);
    ModalXA.setSelected(false);
}//GEN-LAST:event_ModalSelectNoneActionPerformed

private void ModalSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalSelectAllActionPerformed
    ModalSelectNone.setSelected(false);
    ModalSelectAll.setSelected(true);
    ModalCR.setSelected(true);
    ModalCT.setSelected(true);
    ModalDX.setSelected(true);
    ModalES.setSelected(true);
    ModalMG.setSelected(true);
    ModalMR.setSelected(true);
    ModalNM.setSelected(true);
    ModalOT.setSelected(true);
    ModalPT.setSelected(true);
    ModalRF.setSelected(true);
    ModalSC.setSelected(true);
    ModalUS.setSelected(true);
    ModalXA.setSelected(true);
}//GEN-LAST:event_ModalSelectAllActionPerformed

private void ModalXAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalXAActionPerformed
    ModalSelectNone.setSelected(true);
    ModalSelectAll.setSelected(false);
}//GEN-LAST:event_ModalXAActionPerformed

private void ModalUSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalUSActionPerformed
    ModalSelectNone.setSelected(true);
    ModalSelectAll.setSelected(false);
}//GEN-LAST:event_ModalUSActionPerformed

private void ModalOTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalOTActionPerformed
    ModalSelectAll.setSelected(false);
    ModalSelectNone.setSelected(true);
}//GEN-LAST:event_ModalOTActionPerformed

private void ModalESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalESActionPerformed
    ModalSelectNone.setSelected(true);
    ModalSelectAll.setSelected(false);
}//GEN-LAST:event_ModalESActionPerformed

private void ModalSCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalSCActionPerformed
    ModalSelectNone.setSelected(true);
    ModalSelectAll.setSelected(false);
}//GEN-LAST:event_ModalSCActionPerformed

private void ModalNMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalNMActionPerformed
    ModalSelectNone.setSelected(true);
    ModalSelectAll.setSelected(false);
}//GEN-LAST:event_ModalNMActionPerformed

private void ModalDXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalDXActionPerformed
    ModalSelectNone.setSelected(true);
    ModalSelectAll.setSelected(false);
}//GEN-LAST:event_ModalDXActionPerformed

private void ModalRFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalRFActionPerformed
    ModalSelectNone.setSelected(true);
    ModalSelectAll.setSelected(false);
}//GEN-LAST:event_ModalRFActionPerformed

private void ModalMRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalMRActionPerformed
    ModalSelectNone.setSelected(true);
    ModalSelectAll.setSelected(false);
}//GEN-LAST:event_ModalMRActionPerformed

private void ModalCTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalCTActionPerformed
    ModalSelectNone.setSelected(true);
    ModalSelectAll.setSelected(false);
}//GEN-LAST:event_ModalCTActionPerformed

private void ModalPTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalPTActionPerformed
    ModalSelectNone.setSelected(true);
    ModalSelectAll.setSelected(false);
}//GEN-LAST:event_ModalPTActionPerformed

private void ModalMGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalMGActionPerformed
    ModalSelectNone.setSelected(true);
    ModalSelectAll.setSelected(false);
}//GEN-LAST:event_ModalMGActionPerformed

private void ModalCRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModalCRActionPerformed
    ModalSelectNone.setSelected(true);
    ModalSelectAll.setSelected(false);
}//GEN-LAST:event_ModalCRActionPerformed

private void SelectDefaultSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectDefaultSearchActionPerformed
    SelectAdvancedSearch.setSelected(false);
    SelectDefaultSearch.setSelected(true);
    jPanel1.setVisible(true);
    jPanel2.setVisible(false);
}//GEN-LAST:event_SelectDefaultSearchActionPerformed

private void SelectAdvancedSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectAdvancedSearchActionPerformed
    SelectDefaultSearch.setSelected(false);
    SelectAdvancedSearch.setSelected(true);
    jPanel1.setVisible(false);
    jPanel2.setVisible(true);
}//GEN-LAST:event_SelectAdvancedSearchActionPerformed

    private void scanDisk(boolean resume) {

        if (!clientCore.isLocalServer()) {
            class Action1 extends FileAction {

                private boolean resume = false;

                public void setResume(boolean resume) {

                    this.resume = resume;
                }

                @Override
                public void setFileChoosed(String filePath) {
                    AdminRefs.getInstance().index(filePath, resume);

                    /*TaskList tasks = TaskList.getSettings();
                     tasks.setVisible(true);
                     tasks.toFront();*/
                }
            }

            Action1 action = new Action1();
            action.setResume(resume);

            RemoteFileChooser chooser = new RemoteFileChooser(AdminRefs.getInstance().getRFS(), AdminRefs.getInstance().getDefaultFilePath(), action);

            chooser.setTitle("Dicoogle Scan Directory");
            chooser.setFileSelectionMode(RemoteFileChooser.DIRECTORIES_ONLY);

            chooser.setVisible(true);
            // TODO:  put showTaskList = false; -- somewhere...

        } else {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(AdminRefs.getInstance().getDefaultFilePath()));
            chooser.setDialogTitle("Dicoogle Scan Directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                AdminRefs.getInstance().index(chooser.getSelectedFile().toString(), resume);

                /*TaskList tasks = TaskList.getSettings();
                 tasks.setVisible(true);
                 tasks.toFront();*/
            }

        }

    }

private void jMenuDirScanResumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuDirScanResumeActionPerformed

    scanDisk(true);

}//GEN-LAST:event_jMenuDirScanResumeActionPerformed

private void jTextFieldQueryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldQueryKeyPressed

    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        search();
    }


}//GEN-LAST:event_jTextFieldQueryKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

        if (Main.isFixedClient()) {

            PluginController PController = PluginController.getInstance();
            //TODO: DELETED
            //PController.initGUI();
        }

    }//GEN-LAST:event_formWindowOpened

    private void jTextFieldQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldQueryActionPerformed
        // TODO addMoveDestination your handling code here:
    }//GEN-LAST:event_jTextFieldQueryActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AdvancedSearchButton;
    private javax.swing.JRadioButton DateRange;
    private javax.swing.JRadioButton ExactDate;
    private javax.swing.JTextField InstitutionName;
    private javax.swing.JCheckBox ModalCR;
    private javax.swing.JCheckBox ModalCT;
    private javax.swing.JCheckBox ModalDX;
    private javax.swing.JCheckBox ModalES;
    private javax.swing.JCheckBox ModalMG;
    private javax.swing.JCheckBox ModalMR;
    private javax.swing.JCheckBox ModalNM;
    private javax.swing.JCheckBox ModalOT;
    private javax.swing.JCheckBox ModalPT;
    private javax.swing.JCheckBox ModalRF;
    private javax.swing.JCheckBox ModalSC;
    private javax.swing.JRadioButton ModalSelectAll;
    private javax.swing.JRadioButton ModalSelectNone;
    private javax.swing.JCheckBox ModalUS;
    private javax.swing.JCheckBox ModalXA;
    private javax.swing.JTextField OperatorName;
    private javax.swing.JComboBox PatientGender;
    private javax.swing.JTextField PatientID;
    private javax.swing.JTextField PatientName;
    private javax.swing.JTextField Physician;
    private javax.swing.JButton ResetFields;
    private javax.swing.JButton SearchTips;
    private javax.swing.JRadioButton SelectAdvancedSearch;
    private javax.swing.JRadioButton SelectDefaultSearch;
    private javax.swing.JTextField StudyDate;
    private javax.swing.JTextField StudyDateRangeInitialBoundary;
    private javax.swing.JCheckBox StudyDateRangeInitialBoundaryActivation;
    private javax.swing.JTextField StudyDateRangeTerminalBoundary;
    private javax.swing.JCheckBox StudyDateRangeTerminalBoundaryActivation;
    private javax.swing.JButton jButtonClientPreferences;
    private javax.swing.JButton jButtonDownload;
    private javax.swing.JButton jButtonDump;
    private javax.swing.JButton jButtonExport;
    private javax.swing.JButton jButtonLogs;
    private javax.swing.JButton jButtonPeers;
    private javax.swing.JButton jButtonPreferences;
    private javax.swing.JButton jButtonQueryHistory;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JButton jButtonSend;
    private javax.swing.JButton jButtonServices;
    private javax.swing.JButton jButtonView;
    private javax.swing.JCheckBox jCheckBoxKeywords;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelResults;
    private javax.swing.JLabel jLabelTime;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu12;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar3;
    private javax.swing.JMenuItem jMenuDirScan2;
    private javax.swing.JMenuItem jMenuDirScanResume;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JMenuItem jMenuItemActiveUsers;
    private javax.swing.JMenuItem jMenuItemChangePassword;
    private javax.swing.JMenuItem jMenuItemDcm2jpeg2;
    private javax.swing.JMenuItem jMenuItemPreferences;
    private javax.swing.JMenuItem jMenuItemServices;
    private javax.swing.JMenuItem jMenuItemShutdown;
    private javax.swing.JMenuItem jMenuItemUsers;
    private javax.swing.JMenu jMenuTools2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelThumbnail;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField jTextFieldQuery;
    private javax.swing.JTree jTreeResults;
    private javax.swing.JMenu pluginMenu;
    private javax.swing.JTabbedPane tabPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * ************************************************
     * Public Methods
     *************************************************
     */
    /**
     * Checks if the options form is displayed
     *
     * @return true if not displaying, true otherwise
     */
    private void showMetaData() {
        /**
         * Just show metadata for now
         *
         */
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeResults.getLastSelectedPathComponent();

        if (node != null && node.isLeaf() && node.getLevel() > 3) {

            Object nodeInfo = node.getUserObject();
            SearchResult fileInfo = (SearchResult) nodeInfo;

            if (nodeInfo instanceof SearchResult) {
                IndexedMetaData metadataWindow = new IndexedMetaData(fileInfo, this);
                metadataWindow.setVisible(true);
                metadataWindow.toFront();
            }
        }
    }

    public void showImage(String title, RenderedImage image) {
        if (jPanelThumbnail == null) {

            // It can be used to show image in external window
            JFrame f = new JFrame(title);
            if (image != null) {
                f.getContentPane().add(new DisplayJAI(image));
            }
            f.pack();
            //f.setVisible(true);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else {
            jPanelThumbnail.removeAll();
            jPanelThumbnail.setLayout(new FlowLayout());

            // Yet another bugfix
            // If the indexed image does not have a thumbnail, it leads to a Null Pointer Exception
            if (image != null) {
                jPanelThumbnail.add(new DisplayJAI(image));
            }

            jPanelThumbnail.validate();
            jPanelThumbnail.setVisible(true);
        }
    }

    /**
     * @return the jLabelResults
     */
    public javax.swing.JLabel getjLabelResults() {
        return jLabelResults;
    }

    /**
     * @param jLabelResults the jLabelResults to set
     */
    public void setjLabelResults(javax.swing.JLabel jLabelResults) {
        this.jLabelResults = jLabelResults;
    }

    /**
     * @return the jLabelTime
     */
    public javax.swing.JLabel getjLabelTime() {
        return jLabelTime;
    }

    /**
     * @param jLabelTime the jLabelTime to set
     */
    public void setjLabelTime(javax.swing.JLabel jLabelTime) {
        this.jLabelTime = jLabelTime;
    }

    /**
     * @return the jTreeResults
     */
    public javax.swing.JTree getjTreeResults() {
        return jTreeResults;
    }

    /**
     * @param jTreeResults the jTreeResults to set
     */
    public void setjTreeResults(javax.swing.JTree jTreeResults) {
        this.jTreeResults = jTreeResults;
    }

    private void showThumbnail(String thumb) {
        if (thumb == null) {
            return;
        }

        byte[] tb = Base64.decodeBase64(thumb.getBytes());
        ByteArrayInputStream in = new ByteArrayInputStream(tb);
        RenderedImage out;
        try {
            out = ImageIO.read(in);
            jPanelThumbnail.setSize(64, 64);
            Result2Tree.showImage("Image Thumbnail", out, jPanelThumbnail);
            repaint();
        } catch (IOException ex) {
            cleanThumbnails();
        }
    }

    private String getAdvancedQuery() {
        boolean modified = false;
        String advancedquery = "";

        if (!((PatientName.getText()).equals("(All patients)")) && !((PatientName.getText()).isEmpty())) {
            if (!modified) {
                advancedquery = (advancedquery + "PatientName:(" + PatientName.getText() + ")");
                modified = true;
            } else {
                advancedquery = (advancedquery + " AND PatientName:(" + PatientName.getText() + ")");
            }
        }

        if (!((PatientID.getText()).equals("(All IDs)")) && !((PatientID.getText()).isEmpty())) {
            if (!modified) {
                advancedquery = (advancedquery + "PatientID:(" + PatientID.getText() + ")");
                modified = true;
            } else {
                advancedquery = (advancedquery + " AND PatientID:(" + PatientID.getText() + ")");
            }
        }

        // 0 - All 1 - Male  1 - Female
        if (PatientGender.getSelectedItem().equals("All")) {
        } else if (PatientGender.getSelectedItem().equals("Male")) {
            if (!modified) {
                advancedquery = (advancedquery + "PatientSex:M");
                modified = true;
            } else {
                advancedquery = (advancedquery + " AND PatientSex:M");
            }
        } else if (PatientGender.getSelectedItem().equals("Female")) {
            if (!modified) {
                advancedquery = (advancedquery + "PatientSex:F");
                modified = true;
            } else {
                advancedquery = (advancedquery + " AND PatientSex:F");
            }
        }

        if (!((InstitutionName.getText()).equals("(All institutions)")) && !((InstitutionName.getText()).isEmpty())) {
            if (!modified) {
                advancedquery = (advancedquery + "InstitutionName:(" + InstitutionName.getText() + ")");
                modified = true;
            } else {
                advancedquery = (advancedquery + " AND InstitutionName:(" + InstitutionName.getText() + ")");
            }
        }

        if (!((Physician.getText()).equals("(All physicians)")) && !((Physician.getText()).isEmpty())) {
            if (!modified) {
                advancedquery = (advancedquery + "(PerformingPhysicianName:(" + Physician.getText() + ") OR ReferringPhysicianName:(" + Physician.getText() + "))");
                modified = true;
            } else {
                advancedquery = (advancedquery + " AND (PerformingPhysicianName:(" + Physician.getText() + ") OR ReferringPhysicianName:(" + Physician.getText() + "))");
            }
        }

        if (!((OperatorName.getText()).equals("(All operators)")) && !((OperatorName.getText()).isEmpty())) {
            if (!modified) {
                advancedquery = (advancedquery + "OperatorName:(" + OperatorName.getText() + ")");
                modified = true;
            } else {
                advancedquery = (advancedquery + " AND OperatorName:(" + OperatorName.getText() + ")");
            }
        }

        if (ExactDate.isSelected()) {
            if (!((StudyDate.getText()).equals("(All dates)")) && !((StudyDate.getText()).isEmpty())) {
                if (!modified) {
                    advancedquery = (advancedquery + "StudyDate:(" + StudyDate.getText() + ")");
                    modified = true;
                } else {
                    advancedquery = (advancedquery + " AND StudyDate:(" + StudyDate.getText() + ")");
                }
            }
        } else {
            //http://www.rgagnon.com/javadetails/java-0106.html
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            //sdf.format(cal.getTime());

            if (modified) {
                advancedquery = (advancedquery + " AND StudyDate:[");
            } else {
                advancedquery = advancedquery + "StudyDate:[";
            }
            modified = true;

            if (StudyDateRangeInitialBoundaryActivation.isSelected() && StudyDateRangeTerminalBoundaryActivation.isSelected()) {
                if (((StudyDateRangeInitialBoundary.getText()).equals("(Beginning)")) || ((StudyDateRangeInitialBoundary.getText()).isEmpty())) {
                    advancedquery = (advancedquery + "0000101 TO ");
                } else {
                    advancedquery = advancedquery + StudyDateRangeInitialBoundary.getText() + " TO ";
                }

                if (((StudyDateRangeTerminalBoundary.getText()).equals("(Today)")) || ((StudyDateRangeTerminalBoundary.getText()).isEmpty())) {
                    advancedquery = advancedquery + sdf.format(cal.getTime()) + "]";
                } else {
                    advancedquery = advancedquery + StudyDateRangeTerminalBoundary.getText() + "]";
                }
            } else if (StudyDateRangeInitialBoundaryActivation.isSelected() && !StudyDateRangeTerminalBoundaryActivation.isSelected()) {
                if (((StudyDateRangeInitialBoundary.getText()).equals("(Beginning)")) || ((StudyDateRangeInitialBoundary.getText()).isEmpty())) {
                    advancedquery = (advancedquery + "0000101 TO ");
                } else {
                    advancedquery = advancedquery + StudyDateRangeInitialBoundary.getText() + " TO ";
                }

                advancedquery = advancedquery + sdf.format(cal.getTime()) + "]";
            } else if (!StudyDateRangeInitialBoundaryActivation.isSelected() && StudyDateRangeTerminalBoundaryActivation.isSelected()) {
                advancedquery = advancedquery + "0000101 TO ";
                if (((StudyDateRangeTerminalBoundary.getText()).equals("(Today)")) || ((StudyDateRangeTerminalBoundary.getText()).isEmpty())) {
                    advancedquery = advancedquery + sdf.format(cal.getTime()) + "]";
                } else {
                    advancedquery = advancedquery + StudyDateRangeTerminalBoundary.getText() + "]";
                }

            } else {
                advancedquery = (advancedquery + "0000101 TO ");
                advancedquery = advancedquery + sdf.format(cal.getTime()) + "]";
            }
        }

        if (ModalSelectAll.isSelected()) {
            if (modified) {
                advancedquery = advancedquery + " AND ";
            }

            advancedquery = advancedquery + "*:*";
        } else {
            String modalities = "";
            if (modified) {
                modalities = modalities + " AND (";
            } else {
                modalities = modalities + "(";
            }
            boolean ModSelected = false;

            if (ModalCR.isSelected()) {
                modified = true;
                ModSelected = true;
                modalities = modalities + "Modality:CR";
            }

            if (ModalCT.isSelected()) {
                modified = true;
                if (ModSelected) {
                    modalities = modalities + " OR ";
                }
                ModSelected = true;
                modalities = modalities + "Modality:CT";
            }

            if (ModalDX.isSelected()) {
                modified = true;
                if (ModSelected) {
                    modalities = modalities + " OR ";
                }
                ModSelected = true;
                modalities = modalities + "Modality:DX";
            }

            if (ModalES.isSelected()) {
                modified = true;
                if (ModSelected) {
                    modalities = modalities + " OR ";
                }
                ModSelected = true;
                modalities = modalities + "Modality:ES";
            }

            if (ModalMG.isSelected()) {
                modified = true;
                if (ModSelected) {
                    modalities = modalities + " OR ";
                }
                ModSelected = true;
                modalities = modalities + "Modality:MG";
            }

            if (ModalMR.isSelected()) {
                modified = true;
                if (ModSelected) {
                    modalities = modalities + " OR ";
                }
                ModSelected = true;
                modalities = modalities + "Modality:MR";
            }

            if (ModalNM.isSelected()) {
                modified = true;
                if (ModSelected) {
                    modalities = modalities + " OR ";
                }
                ModSelected = true;
                modalities = modalities + "Modality:NM";
            }

            if (ModalOT.isSelected()) {
                modified = true;
                if (ModSelected) {
                    modalities = modalities + " OR ";
                }
                ModSelected = true;
                modalities = modalities + "Modality:OT";
            }

            if (ModalPT.isSelected()) {
                modified = true;
                if (ModSelected) {
                    modalities = modalities + " OR ";
                }
                ModSelected = true;
                modalities = modalities + "Modality:PT";
            }

            if (ModalRF.isSelected()) {
                modified = true;
                if (ModSelected) {
                    modalities = modalities + " OR ";
                }
                ModSelected = true;
                modalities = modalities + "Modality:RF";
            }

            if (ModalSC.isSelected()) {
                modified = true;
                if (ModSelected) {
                    modalities = modalities + " OR ";
                }
                ModSelected = true;
                modalities = modalities + "Modality:SC";
            }

            if (ModalUS.isSelected()) {
                modified = true;
                if (ModSelected) {
                    modalities = modalities + " OR ";
                }
                ModSelected = true;
                modalities = modalities + "Modality:US";
            }

            if (ModalXA.isSelected()) {
                modified = true;
                if (ModSelected) {
                    modalities = modalities + " OR ";
                }
                ModSelected = true;
                modalities = modalities + "Modality:XA";
            }

            modalities = modalities + ")";

            if (!modalities.equals(" AND ()")) {
                advancedquery = advancedquery + modalities;
            }
        }

        // System.out.println(modalities);
        if (!modified) {
            advancedquery = "*:*";
        }

        return advancedquery;
    }

    public javax.swing.JMenu getMenu() {
        return jMenuTools2;
    }

}
