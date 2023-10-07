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

package pt.ua.dicoogle.server.users;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HashServiceTest {

    /** Hash service reliably accepts the same password,
     * while rejecting different passwords.
     */
    @Test
    public void canValidatePasswords() {

        String[] passwords = new String[] {"dicoogledicoogle", "IAmTest123456789", "áÀéìóÙãõçüºª",
                "very long password here very long password here very long password here"};

        for (String password : passwords) {
            String hash = HashService.hashPassword(password);
            assertTrue(HashService.verifyPassword(hash, password));

            assertFalse(HashService.verifyPassword(hash, "wrongpasswordgoeshere123"));
        }
    }
}
