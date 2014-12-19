/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk-ext.
 *
 * Dicoogle/dicoogle-sdk-ext is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk-ext is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.sdk.p2p.Messages.Handlers;

import java.io.File;
import java.awt.Desktop;
import java.util.HashMap;
import java.util.List;
import pt.ua.dicoogle.sdk.NetworkPluginAdapter;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.Utils.TaskRequestsConstants;
import pt.ua.dicoogle.sdk.observables.FileObservable;
import pt.ua.dicoogle.sdk.p2p.Messages.FileMessage;
import pt.ua.dicoogle.sdk.p2p.Messages.MessageI;

/**
 *
 * @author Carlos Ferreira
 */
public class FileResponseHandler implements MessageHandler
{

    private String DirectoryPath;
    private NetworkPluginAdapter NPA;

    public FileResponseHandler(String DirectoryPath, NetworkPluginAdapter NPA)
    {
        this.NPA = NPA;
        this.DirectoryPath = DirectoryPath;
        File directory = new File(this.DirectoryPath);

        //System.out.println(directory.getAbsoluteFile());
        if (!directory.exists())
        {
            directory.mkdir();
        } else
        {
            if (!directory.isDirectory())
            {
                //   throw new BadMessage();
            }
        }
    }

    public void handleMessage(MessageI message, String address)
    {
        if (!FileMessage.class.isInstance(message))
        {
            return;
        }
        FileMessage fmessage = (FileMessage) message;

        /**
         * ATTENTION: the FileMessage sent by the plugin is now only constituted by the filePath...
         * The plugin is responsible for the storage of the file into the local file system
         */
        String filePath = new String(fmessage.getMessage());
        List<FileObservable> filesRequested = this.NPA.getRequestedFiles();
        for (FileObservable fo : filesRequested)
        {
            if ((fo.getFileOrigin().compareTo(address) == 0) && (fo.getFileName().compareTo(fmessage.getFilename()) == 0))
            {
                /**
                 * Request the local indexing of the new file
                 */
                HashMap<Integer, Object> parameters = new HashMap<Integer, Object>();
                parameters.put(TaskRequestsConstants.P_FILE_PATH, filePath);
                TaskRequest task = new TaskRequest(TaskRequestsConstants.T_INDEX_FILE, this.NPA.getName(), parameters);
                this.NPA.getTaskRequestsList().addTask(task);

                /**
                 * TODO: This must be removed when the GUI is able to deal with the new files and show them
                 */
                try
                {
                    Desktop.getDesktop().open(new File(filePath.replace('\\', '/')));
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                fo.setFilePath(filePath);
                break;
            }
        }


        /*ObjMessage msg = (ObjMessage) message;
        
        //File received = (File) msg.getMessage();
        
        File newFile = new File(this.DirectoryPath + File.separator + ((FileMessage)message).getFilename() );
        
        FileOutputStream fos = null;
        byte[] bytes = null;
        bytes = (byte[]) msg.getMessage();
        try
        {
        fos = new FileOutputStream(newFile);
        } catch (FileNotFoundException ex) {
        ex.printStackTrace(System.out);
        }
        try {
        fos.write(bytes);
        } catch (IOException ex) {
        ex.printStackTrace(System.out);
        } finally {
        try {
        fos.close();
        } catch (IOException ex) {
        ex.printStackTrace(System.out);
        }
        }
        IndexEngine.getInstance().index(newFile.getAbsolutePath());
         */
    }
}
