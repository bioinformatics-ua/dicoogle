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

import java.awt.FlowLayout;
import java.awt.image.RenderedImage;
import java.net.URI;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import org.slf4j.LoggerFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import pt.ua.dicoogle.core.QueryHistorySupport;
import pt.ua.dicoogle.rGUI.MultihomeRMIClientSocketFactory;
import pt.ua.dicoogle.rGUI.client.UserRefs;
import pt.ua.dicoogle.rGUI.client.signals.SearchSignal;
import pt.ua.dicoogle.rGUI.client.windows.MainWindow;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISearch;
import pt.ua.dicoogle.rGUI.interfaces.signals.ISearchSignal;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;


/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
@Deprecated
public class Result2Tree extends Observable
{
    private static Result2Tree instance;
    private static DefaultMutableTreeNode top = new DefaultMutableTreeNode("Search Results");
    private HashMap<String, SearchResult> pendingP2PThumbnails;
    private Set<String> patients = new HashSet<>();
    private List<SearchResult> resultsList;
    private long time;
    private ISearch searchRef;
    private ISearchSignal searchSignal;
    /* Counter the DICOM (DIM) found */
    private int counterPatiends = 0;
    private int counterImages = 0;
    private javax.swing.JPanel jPanelThumbnail;
    private boolean agressive = false;
    
    private int MAX_DRAW = 20000;
    
    

    public static synchronized Result2Tree getInstance()
    {
        if (instance == null)
        {
            instance = new Result2Tree();
        }
        return instance;
    }

    private Result2Tree()
    {
        try
        {
            this.resultsList = new ArrayList();
            pendingP2PThumbnails = new HashMap<String, SearchResult>();

            searchSignal = new SearchSignal(this);
            ISearchSignal searchSignalStub = (ISearchSignal) UnicastRemoteObject.exportObject(searchSignal, 0, new MultihomeRMIClientSocketFactory(), RMISocketFactory.getDefaultSocketFactory());

            searchRef = UserRefs.getInstance().getSearch();

            //System.out.println("1.4.3.chegou aqui");
            searchRef.RegisterSignalBack(searchSignalStub);
            //System.out.println("1.4.4.chegou aqui");

        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Result2Tree.class).error(ex.getMessage(), ex);
        }
    }

    
    public void pruneQuery(String id) 
    {
        try {
            searchRef.pruneQuery(id);
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(Result2Tree.class).error(ex.getMessage(), ex);
        }
    }
    
    public void search(String query, boolean keywords, HashMap<String, Boolean> range)
    {
        //
        this.deleteObservers();
        DefaultTreeModel model = (DefaultTreeModel) MainWindow.getInstance().getjTreeResults().getModel();
        model.setRoot(new DefaultMutableTreeNode("Search Results"));
        MainWindow.getInstance().getjLabelResults().setText("Searching...");
        MainWindow.getInstance().getjLabelTime().setText("");
        try
        {
            this.time = System.nanoTime();
            this.resetCounters();
            this.resultsList.clear();

            searchRef.Search(query, keywords, range);

            if (!query.equals(""))
            {
                QueryHistorySupport.getInstance().addQuery(query, keywords);
            }


            showImage("Image Thumbnail", null, jPanelThumbnail);
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Result2Tree.class).error(ex.getMessage(), ex);
        }
    }

    public void searchToExport(String query, boolean keywords, HashMap<String, Boolean> range, ArrayList<String> extraFields, Observer obs)
    {
        try
        {
            this.deleteObservers(); // delete all observers from the past
            this.addObserver(obs);

            searchRef.SearchToExport(query, keywords, range, extraFields, obs);

        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Result2Tree.class).error(ex.getMessage(), ex);
        }
    }

    /**
     * Search only for onde File to get the Thumbnail
     *
     * @param FileName
     * @param FileHash
     * @return
     */
    public SearchResult searchThumbnail(URI FileName, String FileHash)
    {
        try
        {
            return searchRef.getThumbnail(FileName, FileHash);
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Result2Tree.class).error(ex.getMessage(), ex);
        }

        return null;
    }

    
    public void finishSearch()
    {
        MainWindow.getInstance().finishQuery();
    }
    
    
    /**
     * Search only for one File to get the Thumbnail in P2P network
     *
     * @param result
     */
    public void searchP2PThumbnail(SearchResult result)
    {
        try
        {
            searchRef.getP2PThumbnail(result.getURI(), (String)result.getExtraData().get("filehash"), result.getURI().toString());

            pendingP2PThumbnails.put((String)result.getExtraData().get("filehash"), result);

        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Result2Tree.class).error(ex.getMessage(), ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="showImage">
    public static void showImage(String title, RenderedImage image, JPanel panel)
    {
        if (panel == null)
        {

            /** It can be used to show image in external window*/
            JFrame f = new JFrame(title);
            if (image != null)
            {
                f.getContentPane().add(new DisplayJAI(image));
            }
            f.pack();
            //f.setVisible(true);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else
        {
            panel.removeAll();
            panel.setLayout(new FlowLayout());

            // Yet another bugfix
            // If the indexed image does not have a thumbnail, it leads to a Null Pointer Exception
            if (image != null)
            {
                panel.add(new DisplayJAI(image));
            }

            panel.validate();
            panel.setVisible(true);
        }
    }// </editor-fold>

    public DefaultMutableTreeNode convertQueryResult2Tree(List queryResultL)
    {
        LoggerFactory.getLogger(Result2Tree.class).trace("Chegou a convertQueryResult2Tree com :" + queryResultL.size());
        top.removeAllChildren();
        //this.resultsList = queryResultL;

        if (queryResultL != null)
        {
            HashMap<String, HashMap> names = nameToTree();
            fillTree(names);
            
        }

        return top;
    }

    /**
     * Convert a String indexed (given in UTF) to ASCII normal mode
     * @param utfString utf string (2chars per bit) with terminal char
     * @return n ascii string (1 char per bit) without terminal char
     */
    private String asciiMode(String utfString)
    {
        /*String n = null;
        try
        {
            n = new String(utfString.getBytes("US-ASCII"));
        } catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            return null;
        }*/
        return utfString.trim();
        //return n.trim();

    }

    /**
     * This function is necessary to grep the resultList
     *
     * @param patientName - returns only the search results that have the same PatientName
     * @return
     */
    private ArrayList<SearchResult> grepByPatientName(String patientName)
    {
        ArrayList<SearchResult> tmpList = new ArrayList<SearchResult>();
        HashMap extra;

        for (SearchResult tmp : resultsList)
        {
            String _n = null;

            if (tmp instanceof SearchResult)
            {
                extra = tmp.getExtraData();
                _n = (String) extra.get("PatientName");
            } else if (tmp instanceof SearchResult)
            {
                // TODO...
            }

            if (_n != null && asciiMode(_n).equals(patientName))
            {
                tmpList.add(tmp);
            }
        }

        return tmpList;
    }

    private ArrayList<SearchResult> grepByStudyDate(String patientName, String studyDate)
    {

        ArrayList<SearchResult> tmpList = new ArrayList<SearchResult>();
        HashMap extra;

        for (SearchResult tmp : resultsList)
        {
            String _patientName = null;
            String _studyDate = null;

            extra = tmp.getExtraData();
            _patientName = (String) extra.get("PatientName");
            _studyDate = (String) extra.get("StudyDate");

            if (_patientName != null && asciiMode(_patientName).equals(patientName) && _studyDate.equals(studyDate))
            {
                tmpList.add(tmp);
            }
        }

        return tmpList;

    }

    private ArrayList<SearchResult> grepByModality(String patientName, String studyDate, String modality)
    {
        ArrayList<SearchResult> tmpList = new ArrayList<SearchResult>();
        HashMap extra;

        for (SearchResult tmp : resultsList)
        {
            String _patientName = null;
            String _studyDate = null;
            String _modality = null;


            extra = tmp.getExtraData();
            _patientName = (String) extra.get("PatientName");
            _studyDate = (String) extra.get("StudyDate");
            _modality = (String) extra.get("Modality");

            if (_patientName != null && asciiMode(_patientName).equals(patientName) && _studyDate.equals(studyDate)
                    && _modality.equals(modality))
            {
                tmpList.add(tmp);
            }
        }
        return tmpList;

    }

    public void completeTree(javax.swing.event.TreeExpansionEvent evt)
    {
        if (agressive)
        {
            LoggerFactory.getLogger(Result2Tree.class).trace("completeTree, Aggressive mode");
            return;
        }

        //long tBegin = System.nanoTime();

        int level = evt.getPath().getPathCount();
        LoggerFactory.getLogger(Result2Tree.class).trace("level calculated {}", level);
        if (level == 2)
        {
            // Study
            //DebugManager.getSettings().debug("Study");
            //System.out.println("PatientName:" + evt.getWatchDirectory().getPathComponent(1));
            LoggerFactory.getLogger(Result2Tree.class).trace("level 2");
            DefaultMutableTreeNode patient_name = (DefaultMutableTreeNode) evt.getPath().getPathComponent(1);

            if (patient_name.getFirstChild().isLeaf())
            {
                LoggerFactory.getLogger(Result2Tree.class).trace("PatientName: {}", patient_name );
                completeByName(patient_name);
            }


            //System.out.println("Chegou ao fim");
        } else if (level == 3)
        {
            // Serie
            //DebugManager.getSettings().debug("Serie");
            //System.out.println("Study Date:" + evt.getWatchDirectory().getPathComponent(2));
            LoggerFactory.getLogger(Result2Tree.class).trace("completeTree - level 3");
            DefaultMutableTreeNode study_date = (DefaultMutableTreeNode) evt.getPath().getPathComponent(2);

            if (study_date.getFirstChild().isLeaf())
            {
                completeByStudyDate(study_date);
            }
        } else if (level == 4)
        {
            // Image
            //DebugManager.getSettings().debug("Image");
            //System.out.println("Modality:" + evt.getWatchDirectory().getPathComponent(3));
            LoggerFactory.getLogger(Result2Tree.class).trace("completeTree - level 4");
            DefaultMutableTreeNode modality = (DefaultMutableTreeNode) evt.getPath().getPathComponent(3);

            if (modality.getFirstChild().isLeaf())
            {
                completeByModality(modality);
            }
        }

        //long tEnd = System.nanoTime();

        //System.out.println("TIME COMPLETE TREE");
        //System.out.println(Integer.toString((int) ( ( tEnd - tBegin ) / 1000000L )));
    }

    private void completeByName(DefaultMutableTreeNode patient_name)
    {
        ArrayList<SearchResult> result = grepByPatientName(patient_name.toString());

        patient_name.removeAllChildren();

        DefaultMutableTreeNode date;
        HashMap extra;
        HashSet<String> studyDates = new HashSet<String>(); // to elimitate repetition

        LoggerFactory.getLogger(Result2Tree.class).trace("result size" + result.size());
        for (SearchResult temp : result)
        {
            extra = temp.getExtraData();
            studyDates.add((String) extra.get("StudyDate"));
        }
        Iterator<String> it = studyDates.iterator();

        while (it.hasNext())
        {
            date = new DefaultMutableTreeNode(it.next());
            date.add(new DefaultMutableTreeNode("Loading.."));

            patient_name.add(date);
        }

        DefaultTreeModel model = (DefaultTreeModel) MainWindow.getInstance().getjTreeResults().getModel();
        model.reload(patient_name);
    }

    private void completeByStudyDate(DefaultMutableTreeNode study_date)
    {
        DefaultMutableTreeNode patient_name = (DefaultMutableTreeNode) study_date.getParent();

        ArrayList<SearchResult> result = grepByStudyDate(patient_name.toString(), study_date.toString());

        study_date.removeAllChildren();

        DefaultMutableTreeNode modality;
        HashMap extra;
        HashSet<String> modalities = new HashSet<String>(); // to elimitate repetition

        for (SearchResult temp : result)
        {
            extra = temp.getExtraData();
            modalities.add((String) extra.get("Modality"));
        }
        Iterator<String> it = modalities.iterator();

        while (it.hasNext())
        {
            modality = new DefaultMutableTreeNode(it.next());
            modality.add(new DefaultMutableTreeNode("Loading.."));

            study_date.add(modality);
        }

        DefaultTreeModel model = (DefaultTreeModel) MainWindow.getInstance().getjTreeResults().getModel();
        model.reload(study_date);
    }

    private void completeByModality(DefaultMutableTreeNode modality)
    {
        DefaultMutableTreeNode study_date = (DefaultMutableTreeNode) modality.getParent();
        DefaultMutableTreeNode patient_name = (DefaultMutableTreeNode) study_date.getParent();

        ArrayList<SearchResult> result = grepByModality(patient_name.toString(), study_date.toString(), modality.toString());

        modality.removeAllChildren();

        DefaultMutableTreeNode image;
        Hashtable extra;
        Hashtable<String, ArrayList<SearchResult>> files = new Hashtable<String, ArrayList<SearchResult>>();

        // to elimitate possible repetition from p2p
        for (SearchResult temp : result)
        {
            ArrayList<SearchResult> list = files.get(temp.getURI() + " : " + temp.getExtraData().get("filehash"));

            if (list == null)
            {
                list = new ArrayList<SearchResult>();
                files.put(temp.getURI() + " : " + temp.getExtraData().get("filehash"), list);
            }

            list.add(temp);
        }

        Iterator<String> it = files.keySet().iterator();

        while (it.hasNext())
        {
            String fileDesc = it.next();

            ArrayList<SearchResult> sr = files.get(fileDesc);

            DefaultMutableTreeNode DescFile = new DefaultMutableTreeNode(fileDesc);

            Iterator<SearchResult> iter = sr.iterator();

            while (iter.hasNext())
            {
                DescFile.add(new DefaultMutableTreeNode(iter.next()));
            }

            modality.add(DescFile);
        }


        DefaultTreeModel model = (DefaultTreeModel) MainWindow.getInstance().getjTreeResults().getModel();
        model.reload(modality);
    }

    
    private HashMap<String, HashMap> nameToTree()
    {
        /* Hashtable to support */
        HashMap<String, HashMap> tree = new HashMap<String, HashMap>();
        //resetCounters();
        
        for (SearchResult tmp : resultsList)
        {
            String _n = (String)tmp.getExtraData().get("PatientName");

            if (_n != null)
            {
                String n = asciiMode(_n);

                HashMap<String, HashMap> studies = tree.get(n);
                
                if( !this.patients.contains(n))
                
                {
                    this.patients.add(n);
                    counterPatiends++;
                }
                if (studies == null)
                {
                    

                    if (agressive)
                    {
                        studies = new HashMap<String, HashMap>();
                        tree.put(n, studies);

                        dateToTree(studies, tmp);
                    } else
                    {
                        studies = new HashMap<String, HashMap>();
                        studies.put("Loading", new HashMap());

                        tree.put(n, studies);
                    }
                } else if (agressive)
                {
                    dateToTree(studies, tmp);
                }

            } else
            {
                System.out.println("Problem \"PatientName == null\"");
                //the file indexed is not DICOM or there is a problem in the result
            }

        }
        return tree;
    }

    private void dateToTree(HashMap<String, HashMap> studies, SearchResult result)
    {

        String date = (String) result.getExtraData().get("StudyDate");
        if (date.equals(""))
        {
            date = "00000000 ";
        }

        HashMap<String, HashMap> modalities = studies.get(date);

        if (modalities == null)
        {
            modalities = new HashMap<String, HashMap>();
            studies.put(date, modalities);
        }

        modalityToTree(modalities, result);
    }

    private void modalityToTree(HashMap<String, HashMap> modalities, SearchResult result)
    {
        String m = (String)result.getExtraData().get("Modality");

        HashMap<String, ArrayList<SearchResult>> images = modalities.get(m);

        if (images == null)
        {
            images = new HashMap<String, ArrayList<SearchResult>>();
            modalities.put(m, images);
        }

        resToTree(images, result);
    }

    private void resToTree(HashMap<String, ArrayList<SearchResult>> images, SearchResult result)
    {
        String fileDesc = result.getURI() + " : " + result.getExtraData().get("filehash");

        ArrayList<SearchResult> file = images.get(fileDesc);

        if (file == null)
        {
            file = new ArrayList<>();

            images.put(fileDesc, file);
        }

        addressToTree(file, result);
    }

    private void addressToTree(ArrayList<SearchResult> fileList, SearchResult result)
    {
        fileList.add(result);
    }

    /**
     * Fill the tree
     *
     * @param tree - HashTable with the tree
     */
    private void fillTree(HashMap<String, HashMap> tree)
    {
        Iterator<String> en_tree = tree.keySet().iterator();

        while (en_tree.hasNext())   //Patient Names
        {
            String name = en_tree.next();

            HashMap<String, HashMap> t_date = tree.get(name);

            DefaultMutableTreeNode patient_name = new DefaultMutableTreeNode(name);

            Iterator<String> en_date = t_date.keySet().iterator();

            while (en_date.hasNext())   //Study Dates
            {
                String dt = en_date.next();

                HashMap<String, HashMap> t_mod = (HashMap) t_date.get(dt);

                DefaultMutableTreeNode date = new DefaultMutableTreeNode(dt);

                Iterator<String> en_mod = t_mod.keySet().iterator();

                while (en_mod.hasNext()) //Modalities
                {
                    String md = en_mod.next();

                    HashMap<String, ArrayList> t_name = t_mod.get(md);

                    DefaultMutableTreeNode modality = new DefaultMutableTreeNode(md);

                    Iterator<String> en_fname = t_name.keySet().iterator();

                    while (en_fname.hasNext()) //FileDescription
                    {

                        String fname = en_fname.next();
                        ArrayList<SearchResult> res = t_name.get(fname);
                        DefaultMutableTreeNode filename = new DefaultMutableTreeNode(fname);

                        for (SearchResult tmp : res) //SearchResult
                        {
                            DefaultMutableTreeNode details = new DefaultMutableTreeNode(tmp);
                            filename.add(details);
                        }

                        modality.add(filename);
                    }
                    date.add(modality);
                }
                patient_name.add(date);
            }
            top.add(patient_name);
        }

        if (this.resultsList.isEmpty())
        {
            MainWindow.getInstance().getjLabelResults().setText("No matches found.");
        } else
        {
            MainWindow.getInstance().getjLabelResults().setText("<HTML>" + counterImages + " matches found <BR>"
                    + "Patients:" + counterPatiends + "<BR>"
                    //  + "Studies:"+  counterStudies +"<BR>"
                    //  + "Series:"+ counterSeries+"<BR>" +
                    + "</HTML>");
        }

        long timeEnd = System.nanoTime();
        MainWindow.getInstance().getjLabelTime().setText(Integer.toString((int) ((timeEnd - time) / 1000000L)));
    }

    private void resetCounters()
    {
        
        patients = new HashSet<String>();
        counterPatiends = 0;
        counterImages = 0;
    }

    public void getSearchTime()
    {
        try
        {
            long serverSearchTime = searchRef.getSearchTime();

            MainWindow.getInstance().getjLabelTime().setText(String.valueOf(serverSearchTime));
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Result2Tree.class).error(ex.getMessage(), ex);
        }
    }

    /**
     * get the Local Search Results from the GUI Server
     */
    public void getLocalSearchResults()
    {
        try
        {
            resultsList = searchRef.getSearchResults();

            DefaultTreeModel model = (DefaultTreeModel) MainWindow.getInstance().getjTreeResults().getModel();
            
            model.setRoot(convertQueryResult2Tree(resultsList));
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Result2Tree.class).error(ex.getMessage(), ex);
        }

    }

    /**
     * get the P2P Search Results from the GUI Server
     */
    public void getP2PSearchResults()
    {

        LoggerFactory.getLogger(Result2Tree.class).trace("getP2PSearchResults");
        
        try
        {
            this.resultsList = new ArrayList();
            List<SearchResult> temp = searchRef.getP2PSearchResults();
            counterImages += temp.size();
            if (temp != null)
            {
                this.resultsList.addAll(temp);
                //counterImages += resultsList.size();
            }
            
            
            DefaultTreeModel model = (DefaultTreeModel) MainWindow.getInstance().getjTreeResults().getModel();
            
            DefaultMutableTreeNode root = convertQueryResult2Tree(temp);
            if (temp.size()<=MAX_DRAW)
            {
                model.setRoot(root);
                MainWindow.getInstance().repaint();
                
            }
            
            LoggerFactory.getLogger(Result2Tree.class.getName()).trace("getP2PSearchResults, size of resultList {}", resultsList.size());
            
            LoggerFactory.getLogger(Result2Tree.class.getName()).trace("getP2PSearchResults, size of tmpSize {}", temp.size());
            

            
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Result2Tree.class).error(ex.getMessage(), ex);
        }

    }

    /**
     * get the Search Results with the selected extraFields to export data to a file
     */
    public void getExportSearchResults()
    {
        try
        {
            List<SearchResult> temp = searchRef.getExportSearchResults();

            if (temp != null)
            {
                setChanged();
                notifyObservers(temp);
                
            }
        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(MainWindow.class).error(ex.getMessage(), ex);
        }
    }

    public void getPendingP2PThumbnails()
    {
        try
        {
            ArrayList<SearchResult> thumbnails = searchRef.getPendingP2PThumnails();

            SearchResult res;
            String thumb;

            for (SearchResult temp : thumbnails)
            {
                res = pendingP2PThumbnails.remove(temp.get("filehash"));
                thumb = (String) temp.get("Thumbnail");

                if (res != null && thumb != null)
                {
                    res.put("Thumbnail", thumb);
                    MainWindow.getInstance().updateP2PThumbnail(res);
                }
            }

        } catch (RemoteException ex)
        {
            LoggerFactory.getLogger(Result2Tree.class).error(ex.getMessage(), ex);
        }
    }

    /**
     * Unexport the remote object SearchSignal
     */
    public void unexportSearchSignal()
    {
        try
        {
            if (searchSignal != null)
            {
                UnicastRemoteObject.unexportObject(searchSignal, true);
            }
        } catch (NoSuchObjectException ex)
        {
            LoggerFactory.getLogger(Result2Tree.class).error(ex.getMessage(), ex);
        }
    }

    public DefaultMutableTreeNode getTop()
    {
        return top;
    }
}
