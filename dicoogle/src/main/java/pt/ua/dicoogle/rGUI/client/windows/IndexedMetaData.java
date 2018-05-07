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
 * IndexedMetaData.java
 *
 * Created on Feb 19, 2010, 6:39:59 PM
 */

package pt.ua.dicoogle.rGUI.client.windows;

import java.rmi.RemoteException;
import org.slf4j.LoggerFactory;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import pt.ua.dicoogle.Main;
import pt.ua.dicoogle.rGUI.client.UserRefs;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;



/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
@Deprecated
public class IndexedMetaData extends javax.swing.JFrame
{

    /*********************
     * Private attributes
     *********************/
     private SearchResult searchResult ;
     private MainWindow main ;

     private MetaDataModel model;


    /** Creates new form IndexedMetaData */
    public IndexedMetaData(SearchResult searchResult, MainWindow main)
    {
        this.main = main;
        this.searchResult = searchResult;
        initComponents();

        Image image = Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("trayicon.gif"));
        this.setIconImage(image);
        
        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;

        // Move the window
        this.setLocation(x, y);

        String PatientName = (String) searchResult.getExtraData().get("PatientName");

        this.setTitle(PatientName + " - " + searchResult.getURI());
        fill();
        
    }




    /***************************
     * Private Methods
     ***************************/




    private void fill()
    {

        TransferHandler th = jTableMetaData.getTransferHandler();
        if (th != null) {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            th.exportToClipboard(jTableMetaData, cb, TransferHandler.COPY);
        }

        fillTitle();
    }

    private void fillTitle()
    {
        jLabelTitle.setText(searchResult.getURI().toString());
    }
    
    /***************************
     * Private Classes
     ***************************/
    
    class MetaData implements Comparable
    {
        private String name  = "" ;
        private String value = "" ;

        public MetaData(String name, String value )
        {
            this.name = name ;
            this.value = value ;
        }

        /**
         * @return the value
         */
        public String getValue()
        {
            return value;
        }

        /**
         * @param value the value to set
         */
        public void setValue(String value)
        {
            this.value = value;
        }


        /**
         * @return the name
         */
        public String getName()
        {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name)
        {
            this.name = name;
        }

        

        @Override
        public int compareTo(Object o)
        {

            MetaData meta = (MetaData) o;
            return this.name.compareTo(meta.getName());

        }

        

    }


    class MetaDataModel extends AbstractTableModel
    {

        static final int NAME = 0 ;
        static final int VALUE = 1 ;

        private String[] headers = { "Name", "Value", };
        private ArrayList<MetaData> metaData = new ArrayList<MetaData>();

        public MetaDataModel()
        {            
            try {
                HashMap resultFields = searchResult.getExtraData();

                List resultList = UserRefs.getInstance().getSearch().SearchIndexedMetaData(searchResult);
                

                if (resultList.size() > 0) {
                    //DebugManager.getSettings().debug("Found results (In Meta)");
                    
                    SearchResult r = (SearchResult) resultList.get(0);
                    resultFields = r.getExtraData();
                }

                Iterator it = resultFields.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    metaData.add(new MetaData(key, (String) resultFields.get(key)));
                }
                
                Collections.sort(metaData);
            } catch (RemoteException ex) {
                LoggerFactory.getLogger(IndexedMetaData.class).error(ex.getMessage(), ex);
            }
        }

        public String getColumnName(int c)
        {
            return headers[c];
        }


        @Override
        public int getRowCount()
        {
            return metaData.size();
        }

        @Override
        public int getColumnCount()
        {
            return headers.length ;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            String result = "";
            MetaData m = this.metaData.get(rowIndex);
            if (columnIndex==NAME)
            {
                result = m.getName();
            }
            else if (columnIndex==VALUE)
            {
                result = m.getValue();
            }
            else
            {
                boolean cond = (columnIndex!=NAME||columnIndex!=VALUE);
                assert cond ;
            }
            
            return result ;

        }


        
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        model = new MetaDataModel();
        jTableMetaData = new javax.swing.JTable(model);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Meta-data fields"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel1.setFont(new java.awt.Font("Lucida Grande", 1, 18));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jLabelTitle.setFont(new java.awt.Font("Lucida Grande", 1, 18));
        jLabelTitle.setText("jLabelTitle");
        jPanel1.add(jLabelTitle);

        jSplitPane1.setTopComponent(jPanel1);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jTableMetaData.setModel(model);
        jScrollPane1.setViewportView(jTableMetaData);

        jPanel2.add(jScrollPane1);

        jSplitPane1.setRightComponent(jPanel2);

        getContentPane().add(jSplitPane1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.setVisible(false);

        main.setEnabled(true);
        main.toFront();

    }//GEN-LAST:event_formWindowClosing

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTableMetaData;
    // End of variables declaration//GEN-END:variables

}
