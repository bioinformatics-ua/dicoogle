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
package pt.ua.dicoogle;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import javax.swing.JOptionPane;

//import pt.ieeta.anonymouspatientdata.core.Anonymous;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
class ExceptionHandler implements Thread.UncaughtExceptionHandler {

  public static String getStackTrace(Throwable throwable)
  {
    Writer writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);
    throwable.printStackTrace(printWriter);
    return writer.toString();
  }


  public void uncaughtException(Thread t, Throwable e) {
    handle(e);
  }

  public void handle(Throwable throwable) {
    try {
        /**
         * Here you can insert code to send exception to email etc.
         */
        if (/*DebugManager.getSettings().isDebug()*/true) {
            throwable.printStackTrace();
        } else {
            String msg = getStackTrace(throwable);
            //Anonymous.getSettings().stop();
            if (msg.contains("heap"))
            {
                JOptionPane.showMessageDialog(null, "Generic Error, see log.txt.\n\nError: lack of memory. You should increase memory \n",
                    "Exception Error", JOptionPane.INFORMATION_MESSAGE);
            
            }
            else
            {    
                 JOptionPane.showMessageDialog(null, "Generic Error, see log.txt.\n\nError: \n" + msg,
                    "Exception Error", JOptionPane.INFORMATION_MESSAGE);
            }
            //DebugManager.getSettings().log(getStackTrace(throwable)+"\n");
            System.err.println(getStackTrace(throwable)+"\n");
        }
      } catch (Throwable t) {
      // don't let the exception get thrown out, will cause infinite looping!
    }
  }

  public static void registerExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
  }
}
