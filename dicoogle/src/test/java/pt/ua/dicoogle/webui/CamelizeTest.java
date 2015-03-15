
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
    
    public CamelizeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void theTest() {
        assertEquals("dicoogleAnnotationEngine", camelize("dicoogle-annotation-engine"));
        assertEquals("somethingHere", camelize("something-here"));
        assertEquals("iAmIronMan", camelize("i-am-iron-man"));
        assertEquals("cantPlayTricksOnMe", camelize("cant-play--tricks---on----me"));
    }
}
