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
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Frederico Valente <fmvalente@ua.pt>
 */
class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static String getStackTrace(Throwable throwable){
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }


    @Override
    public void uncaughtException(Thread t, Throwable e) {handle(e);}

    public void handle(Throwable throwable) {
        Logger.getLogger("dicoogle").log(Level.SEVERE, "exception caught by the general exception handler: ", throwable);
        System.exit(1);        
    }

    public static void registerExceptionHandler() {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());}
}
