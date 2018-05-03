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

package pt.ua.dicoogle.webui;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static pt.ua.dicoogle.server.web.servlets.webui.WebUIServlet.camelize;

/**
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class CamelizeTest {

    @Test
    public void theTest() {
        assertEquals("dicoogleAnnotationEngine", camelize("dicoogle-annotation-engine"));
        assertEquals("somethingHere", camelize("something-here"));
        assertEquals("iAmIronMan", camelize("i-am-iron-man"));
        assertEquals("cantPlayTricksOnMe", camelize("cant-play--tricks---on----me"));
    }
}
