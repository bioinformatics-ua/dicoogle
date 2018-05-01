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

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.ImageIcon;

import javax.swing.JTree;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import pt.ua.dicoogle.rGUI.client.signals.LogsSignal;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ILogs;
import pt.ua.dicoogle.Main;

import pt.ua.dicoogle.DicomLog.LogLine;
import pt.ua.dicoogle.rGUI.MultihomeRMIClientSocketFactory;
import pt.ua.dicoogle.rGUI.client.AdminRefs;
import pt.ua.dicoogle.rGUI.interfaces.signals.ILogsSignal;

/**
 * There are two diferent logs in this class
 * the Server Log with the activities in server
 * and the DICOM Services Log
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class Logs extends JFrame {

    private static Logs instance = null;
    private static ILogs logs;
    private static ILogsSignal logsSignal;
    private DefaultMutableTreeNode topLog = null;
    
     public static Image getImage(final String pathAndFileName) {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(pathAndFileName);
        return Toolkit.getDefaultToolkit().getImage(url);
    }


    public static synchronized Logs getInstance() {
        if (instance == null) {
            instance = new Logs();
        }

        return instance;
    }

    /** Creates new form Logs */
    private Logs() {
        initComponents();

        Image image = Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("trayicon.gif"));
        this.setIconImage(image);


        Logs.logs = AdminRefs.getInstance().getLogs();

        topLog = new DefaultMutableTreeNode("Logging..");
        jTreeLog.setModel(new DefaultTreeModel(topLog));

        try {
            logsSignal = new LogsSignal(this);
            
            ILogsSignal logsSignalStub = (ILogsSignal) UnicastRemoteObject.exportObject(logsSignal, 0, new MultihomeRMIClientSocketFactory(), RMISocketFactory.getDefaultSocketFactory());;
            
            //if (logs == null)
            //    System.out.println("LOGS IS NULL");

            logs.RegisterSignalBack(logsSignalStub);

        } catch (RemoteException ex) {
            LoggerFactory.getLogger(Logs.class).error(ex.getMessage(), ex);
        }
    }

    public void getDICOMLog() {
        try {
            ArrayList<LogLine> logLines = logs.getPendingDICOMLog();

            for (LogLine line : logLines) {
                addDICOMLog(line);
            }

            jTreeLog.setModel(new DefaultTreeModel(topLog));

        } catch (RemoteException ex) {
            LoggerFactory.getLogger(Logs.class).error(ex.getMessage(), ex);
        }
    }

    public void getServerLog() {
        try {
            String logText = logs.getServerLog();

            jTextLogWindow.setText(logText);
            jTextLogWindow.setCaretPosition(jTextLogWindow.getDocument().getLength());

        } catch (RemoteException ex) {
            LoggerFactory.getLogger(Logs.class).error(ex.getMessage(), ex);
        }
    }

    public void getSessionsLog(){
        try {
            String addLog = logs.getPendingSessionsLog();
            
            jTextSessionsLogWindow.setText(jTextSessionsLogWindow.getText() + addLog);

        } catch (RemoteException ex) {
            LoggerFactory.getLogger(Logs.class).error(ex.getMessage(), ex);
        }
    }

    private void addDICOMLog(LogLine l) {
        DefaultMutableTreeNode group = null;
        DefaultMutableTreeNode subGroup = null;

        group = new DefaultMutableTreeNode(l.getType() + " -- " + l.getDate());
        topLog.add(group);
        subGroup = new DefaultMutableTreeNode(l.getAe() + ":: " + l.getAdd());
        group.add(subGroup);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextLogWindow = new javax.swing.JTextArea();
        jScrollPane8 = new javax.swing.JScrollPane();
        //Create the nodes.
        topLog =new DefaultMutableTreeNode("Logging..");
        jTreeLog = new JTree(topLog);
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextSessionsLogWindow = new javax.swing.JTextArea();
        jButtonClear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Logs");
        setMinimumSize(new java.awt.Dimension(400, 300));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTextLogWindow.setColumns(20);
        jTextLogWindow.setEditable(false);
        jTextLogWindow.setRows(5);
        jScrollPane1.setViewportView(jTextLogWindow);

        jTabbedPane1.addTab("Server Log", jScrollPane1);

        jScrollPane8.setViewportView(jTreeLog);

        jTabbedPane1.addTab("DICOM Log Services", jScrollPane8);

        jTextSessionsLogWindow.setColumns(20);
        jTextSessionsLogWindow.setEditable(false);
        jTextSessionsLogWindow.setRows(5);
        jScrollPane2.setViewportView(jTextSessionsLogWindow);

        jTabbedPane1.addTab("User Sessions Log", jScrollPane2);

        jButtonClear.setIcon(new ImageIcon(getImage("log.gif")));
        jButtonClear.setText("Clear Log");
        jButtonClear.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonClear.setMaximumSize(new java.awt.Dimension(97, 21));
        jButtonClear.setMinimumSize(new java.awt.Dimension(97, 21));
        jButtonClear.setPreferredSize(new java.awt.Dimension(97, 21));
        jButtonClear.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jButtonClear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jButtonClear, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Server Log");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        MainWindow main = MainWindow.getInstance();

        main.toFront();
        main.setEnabled(true);

        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        try {
            if (jTabbedPane1.getSelectedIndex() == 0) {
                logs.clearServerLog();
            } else if(jTabbedPane1.getSelectedIndex() == 1) {

                //LogDICOM.getSettings().clearLog();
                logs.clearDICOMLog();

                topLog = new DefaultMutableTreeNode("Logging..");

                jTreeLog.setModel(new DefaultTreeModel(topLog));
            }
            else {
                jTextSessionsLogWindow.setText("");
                logs.clearSessionsLog();
            }
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(Logs.class).error(ex.getMessage(), ex);
        }
}//GEN-LAST:event_jButtonClearActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClear;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextLogWindow;
    private javax.swing.JTextArea jTextSessionsLogWindow;
    private javax.swing.JTree jTreeLog;
    // End of variables declaration//GEN-END:variables
}
