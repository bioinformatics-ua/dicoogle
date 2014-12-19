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
package pt.ua.dicoogle.sdk.index.handlers;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import pt.ua.dicoogle.sdk.index.IDoc;



/**
 * Handles different filetypes based on information from Properties
 * Lmitation : The file must have an extension of any form
 * @author marco
 */
public class ExtensionFileHandler {

    private Properties handlerProps;
    
    /**
     * Creates a new Extension File Handler
     * @param p Properties that allow the correct class to be called for each extension
     */
    public ExtensionFileHandler(Properties p) throws IOException
    {
        handlerProps = p;
    }
    
    /**
     * Handle a file, obtaining a Lucene Document from it, if it
     * knows how to process this file type. Since we are interest in
     * indexing files, the path is also saved as a field
     * @param file File to be handled
     * @return Lucene Friendly Document
     */
    public IDoc getDocument(File file) throws FileHandlerException, FileAlreadyExistsException {
        IDoc doc = null;
        String name = file.getName();
        String Error = "Cannot create instance of : ";
        if (name.contains(".DS_Store"))
        {
            return null;
        }
        try
        {
            Class handlerClass = Class.forName("pt.ua.dicoogle.core.index.LuceneSupport.DICOM.DicomDocumentNG");
            DocumentHandler handler = (DocumentHandler) handlerClass.newInstance();
            doc = handler.getDocument(file);
        }
        catch (DocumentHandlerException ex)
        {
            throw new FileHandlerException(Error + "pt.ua.dicoogle.core.index.LuceneSupport.DICOM.DicomDocumentNG", ex);
        }

        catch (FileAlreadyExistsException ex)
        {
            throw ex;
        }

        catch (InstantiationException ex)
        {
            throw new FileHandlerException(Error + "pt.ua.dicoogle.core.index.LuceneSupport.DICOM.DicomDocumentNG", ex);
        }
        catch (IllegalAccessException ex)
        {
            throw new FileHandlerException(Error + "pt.ua.dicoogle.core.index.LuceneSupport.DICOM.DicomDocumentNG", ex);
        }
        catch (ClassNotFoundException ex)
        {
            throw new FileHandlerException(Error + "pt.ua.dicoogle.core.index.LuceneSupport.DICOM.DicomDocumentNG", ex);
        }

        return doc;
    }
}
