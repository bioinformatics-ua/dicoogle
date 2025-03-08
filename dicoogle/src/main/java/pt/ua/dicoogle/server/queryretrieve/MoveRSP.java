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

/**
 *
 */
package pt.ua.dicoogle.server.queryretrieve;

import java.io.IOException;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.Status;

import pt.ua.dicoogle.server.SearchDicomResult;

/** Custom C-MOVE response for {@linkplain CMoveServiceSCP}
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class MoveRSP implements DimseRSP {
    DicomObject rsp = null;
    DicomObject keys = null;

    DicomObject current = null;
    SearchDicomResult search = null;


    public MoveRSP(DicomObject keys, DicomObject rsp) {
        /* Save args */
        this.rsp = rsp;
        this.keys = keys;
        this.current = rsp;

        if (!this.rsp.contains(Tag.Status)) {
            this.rsp.putInt(Tag.Status, VR.US, Status.Success);
        }
    }

    @Override
    public boolean next() throws IOException, InterruptedException {
        if (this.current == null) {
            return false;
        }
        /** Clean pointers */
        this.current = null;
        return true;

    }

    @Override
    public DicomObject getCommand() {
        return this.rsp;
    }

    @Override
    public DicomObject getDataset() {
        return this.current != null ? this.current.subSet(this.keys) : null;
    }

    @Override
    public void cancel(Association arg0) throws IOException {
        // TODO
    }

}
