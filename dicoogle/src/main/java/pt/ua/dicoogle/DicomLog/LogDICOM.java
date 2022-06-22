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

package pt.ua.dicoogle.DicomLog;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DICOM network event logging.
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public final class LogDICOM {

    private static LogDICOM instance = null;

    // only persist in memory if XML logging is enabled
    private final boolean persist = Boolean.parseBoolean(System.getProperty("dicoogle.dicom.xmlLog", "false"));
    private final ArrayList<LogLine> ll = new ArrayList<>();

    private final Logger log = LoggerFactory.getLogger(LogDICOM.class);

    private LogDICOM() {
        // Nothing to do.
    }

    public static synchronized LogDICOM getInstance() {
        if (instance == null) {
            instance = new LogDICOM();
        }
        return instance;
    }

    /** Check whether the DICOM event logger is persisting log events in memory,
     * which is required for the legacy XML logger to work.
     * 
     * @deprecated XML logging will be removed in Dicoogle,
     * making it as if it would always return `false`
     */
    @Deprecated
    public boolean isPersistent() {
        return persist;
    }

    /** Records a DICOM event, same as `getInstance().addLine(line)` */
    public static void log(LogLine line) {
        getInstance().addLine(line);
    }

    /** Records a DICOM event. */
    public void addLine(LogLine l) {
        log.info("[{}] {} {} {}", l.getAe(), l.getType(), l.getAdd(), l.getParams());
        if (persist) {
            ll.add(l);
        }
    }

    /** Retrieves the full list of recorded DICOM events.
     * 
     * @deprecated the feature that depended on this (XML logging)
     * will be removed in Dicoogle 4
     */
    @Deprecated
    public ArrayList<LogLine> getLl() {
        return ll;
    }
}
