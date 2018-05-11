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
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import pt.ua.dicoogle.Main;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.rGUI.client.UserRefs;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IDicomSend;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class DicomSend extends javax.swing.JFrame {

    private ArrayList<String> filePaths;
    private ArrayList<MoveDestination> dest;
    private Hashtable<String, MoveDestination> destHash = new Hashtable<String, MoveDestination>();

    private IDicomSend dmSend;

    /** Creates new form DicomSend */
    public DicomSend(ArrayList<String> filePaths) {
        initComponents();

        Image image = Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("trayicon.gif"));
        this.setIconImage(image);

        this.filePaths = filePaths;

        dmSend = UserRefs.getInstance().getDicomSend();
        
        jLabel1.setText(filePaths.size() + " DICOM Files to be send:");

        fillStorageServerTree();

        if (filePaths != null)
            fillList();
    }

    /* Fill the Storage Server Tree */
    private void fillStorageServerTree() {
        try {
            /* Uses the Storage Server Destinations of Query/Retrive System*/
            dest = dmSend.getDestinations();

            DefaultMutableTreeNode treeNodeRoot = new DefaultMutableTreeNode("Storage Servers");

            if (dest.size() != 0) {
                Iterator<MoveDestination> itDest = dest.iterator();

                MoveDestination m;
                DefaultMutableTreeNode treeNode;

                while (itDest.hasNext()) {
                    m = itDest.next();
                    destHash.put(m.getAETitle(), m);

                    treeNode = new DefaultMutableTreeNode(m.getAETitle());

                    treeNode.add(new DefaultMutableTreeNode("AETitle: " + m.getAETitle()));
                    treeNode.add(new DefaultMutableTreeNode("IP: " + m.getIpAddrs()));
                    treeNode.add(new DefaultMutableTreeNode("Port: " + m.getPort()));
                    
                    treeNodeRoot.add(treeNode);
                }
            }

            jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNodeRoot));
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(DicomSend.class).error(ex.getMessage(), ex);
        }

    }

    /** Fill the Objects to Send Tree with patients, studies, series and images */
    private void fillList() {
        DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("root");

        DefaultListModel model = (DefaultListModel) jListFiles.getModel();

        Iterator<String> it = filePaths.iterator();

        while(it.hasNext())
            model.addElement(it.next());

        jListFiles.setModel(model);
    }

    /**
     * @return the selcted Server to move data
     */
    private MoveDestination getSelectedDestination() {
        TreePath path = jTree1.getSelectionPath();

        if (path != null && path.getPathCount() > 1) {
            return destHash.get(path.getPathComponent(1).toString());
        }

        return null;
    }

    /*
    private ArrayList<String> getAllImages() {
        if (dimGen == null) {
            return null;
        }

        ArrayList<String> images = new ArrayList<String>();

        Iterator<Patient> it = dimGen.getPatients().iterator();

        while (it.hasNext()) {
            Patient patient = it.next();

            Iterator<Study> itStudy = patient.getStudies().iterator();

            while (itStudy.hasNext()) {
                Study study = itStudy.next();

                Iterator<Series> itSerie = study.getSeries().iterator();

                while (itSerie.hasNext()) {
                    Series serie = itSerie.next();

                    images.addAll(serie.getImageList());
                }
            }
        }

        return images;
    }
     *
     */

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelVBox = new javax.swing.JPanel();
        jPanelSenderBox = new javax.swing.JPanel();
        jPanelfields = new javax.swing.JPanel();
        jLabelAETitle = new javax.swing.JLabel();
        jTextFieldAETitle = new javax.swing.JTextField();
        jLabelIP = new javax.swing.JLabel();
        jTextFieldIP = new javax.swing.JTextField();
        jLabelPort = new javax.swing.JLabel();
        jTextFieldPort = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();
        jPanelDICOMObjects = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        DefaultListModel model = new DefaultListModel();
        jListFiles = new javax.swing.JList(model);
        jButtonSend = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Send");
        setResizable(false);

        jPanelVBox.setLayout(new javax.swing.BoxLayout(jPanelVBox, javax.swing.BoxLayout.PAGE_AXIS));

        jPanelfields.setLayout(new java.awt.GridLayout(3, 2));

        jLabelAETitle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelAETitle.setText("AETitle");
        jPanelfields.add(jLabelAETitle);

        jTextFieldAETitle.setEditable(false);
        jPanelfields.add(jTextFieldAETitle);

        jLabelIP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelIP.setText("IP:");
        jPanelfields.add(jLabelIP);

        jTextFieldIP.setEditable(false);
        jPanelfields.add(jTextFieldIP);

        jLabelPort.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelPort.setText("Port:");
        jPanelfields.add(jLabelPort);

        jTextFieldPort.setEditable(false);
        jPanelfields.add(jTextFieldPort);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jTree1);

        jLabel1.setText("DICOM Files to be send:");

        javax.swing.GroupLayout jPanelSenderBoxLayout = new javax.swing.GroupLayout(jPanelSenderBox);
        jPanelSenderBox.setLayout(jPanelSenderBoxLayout);
        jPanelSenderBoxLayout.setHorizontalGroup(
            jPanelSenderBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSenderBoxLayout.createSequentialGroup()
                .addGroup(jPanelSenderBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSenderBoxLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanelfields, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelSenderBoxLayout.setVerticalGroup(
            jPanelSenderBoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSenderBoxLayout.createSequentialGroup()
                .addComponent(jPanelfields, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addComponent(jLabel1))
            .addGroup(jPanelSenderBoxLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelVBox.add(jPanelSenderBox);

        jPanelDICOMObjects.setLayout(new javax.swing.BoxLayout(jPanelDICOMObjects, javax.swing.BoxLayout.Y_AXIS));
        jPanelVBox.add(jPanelDICOMObjects);

        jPanelButtons.setLayout(new javax.swing.BoxLayout(jPanelButtons, javax.swing.BoxLayout.LINE_AXIS));
        jPanelVBox.add(jPanelButtons);

        jScrollPane1.setViewportView(jListFiles);

        jPanelVBox.add(jScrollPane1);

        jButtonSend.setText("Send");
        jButtonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendActionPerformed(evt);
            }
        });
        jPanelVBox.add(jButtonSend);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelVBox, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jPanelVBox, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendActionPerformed
        try {
            MoveDestination destination = getSelectedDestination();

            if (destination == null || filePaths.isEmpty()) {
                JOptionPane.showMessageDialog(this, "You need to choose a destination.", "Destination", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (dmSend.sendFiles(destination, filePaths))
                JOptionPane.showMessageDialog(this, "Sending files to choosed destination.", "Sending files", JOptionPane.INFORMATION_MESSAGE);
            else
                JOptionPane.showMessageDialog(this, "Error sending files to choosed destination.", "Error sending files", JOptionPane.ERROR_MESSAGE);
            
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(DicomSend.class).error(ex.getMessage(), ex);
        }
    }//GEN-LAST:event_jButtonSendActionPerformed

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        MoveDestination tmp = getSelectedDestination();

        if (tmp != null) {
            jTextFieldAETitle.setText(tmp.getAETitle());
            jTextFieldIP.setText(tmp.getIpAddrs());
            jTextFieldPort.setText(String.valueOf(tmp.getPort()));
        } else {
            jTextFieldAETitle.setText("");
            jTextFieldIP.setText("");
            jTextFieldPort.setText("");
        }

    }//GEN-LAST:event_jTree1ValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonSend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelAETitle;
    private javax.swing.JLabel jLabelIP;
    private javax.swing.JLabel jLabelPort;
    private javax.swing.JList jListFiles;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelDICOMObjects;
    private javax.swing.JPanel jPanelSenderBox;
    private javax.swing.JPanel jPanelVBox;
    private javax.swing.JPanel jPanelfields;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextFieldAETitle;
    private javax.swing.JTextField jTextFieldIP;
    private javax.swing.JTextField jTextFieldPort;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}
