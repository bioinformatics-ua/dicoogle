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
package pt.ua.dicoogle.server;

/**
 * Keeps track of modifications on a file
 * @author Filipe Freitas
 */
import java.util.*;
import java.io.*;

public abstract class FileWatcher extends TimerTask {
  private long timeStamp;
  private File file;

  public FileWatcher( File file )
  {
    this.file = file;
    this.timeStamp = file.lastModified();    
  }

    @Override
  public final void run()
  { 
    /* STUPID WINDOWS HACK
     * 
     * windows does NOT update the modified time
     * of a file until it is closed (DOS Legacy code)
     * 
     */
    
    if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1)
    {
        onChange(file);
    }
    else
    {
        long localtimeStamp = file.lastModified();    
        if( this.timeStamp != localtimeStamp ) 
        {
            this.timeStamp = localtimeStamp;
            onChange(file);
        }
    }
  }

  protected abstract void onChange( File file );
}
