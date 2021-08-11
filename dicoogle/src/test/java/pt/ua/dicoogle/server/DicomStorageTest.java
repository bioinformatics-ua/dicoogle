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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class DicomStorageTest {
    final Set<String> priorityAETs = new HashSet<>(Arrays.asList("IMPORTANT AE 1", "IMPORTANT AE 2"));

    private void assertPriorityOrderAsc(String aet1, long seq1, String aet2, long seq2) {
        int order = DicomStorage.compareElementsImpl(priorityAETs, aet1, seq1, aet2, seq2);

        assertTrue(String.format("Expected comparison (%s, %d)-(%s, %d) to be <0, but got %d", aet1, seq1, aet2, seq2,
                order), order < 0);
    }

    @Test
    public void testImageElementPriority() {

        assertPriorityOrderAsc("AE01", 3, "AE02", 4);

        assertPriorityOrderAsc("IMPORTANT AE 1", 0, "AE01", 200);

        assertPriorityOrderAsc("IMPORTANT AE 1", 5000, "AE01", 2);

        assertPriorityOrderAsc("IMPORTANT AE 1", 4, "IMPORTANT AE 2", 40);

    }
}
