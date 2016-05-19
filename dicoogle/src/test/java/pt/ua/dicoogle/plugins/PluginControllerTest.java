package pt.ua.dicoogle.plugins;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import pt.ua.dicoogle.plugins.mock.MockPluginSet;
import pt.ua.dicoogle.sdk.PluginSet;

import java.util.Arrays;
import java.util.List;

/** Plugin Controller tests
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class PluginControllerTest {

    private PluginController controller;

    @Before
    public void init() {
        controller = new PluginController();
    }

    @Test
    public void initState() {
        assertTrue(controller.getQueryPlugins(false).isEmpty());
        assertTrue(controller.getIndexingPlugins(false).isEmpty());
        assertTrue(controller.getStoragePlugins(false).isEmpty());
        assertTrue(controller.getServletPlugins(false).isEmpty());
        assertTrue(controller.getRestletPlugins(false).isEmpty());
    }

    @Test
    public void loadPlugins() throws Exception {
        List<PluginSet> pluginSets = Arrays.asList( (PluginSet)
                MockPluginSet.withQueryAndIndexer("lucene"),
                MockPluginSet.withJettyPlugin("qido-rs"),
                MockPluginSet.withRestletPlugin("wado-rs"),
                MockPluginSet.withQueryAndIndexer("cbir"),
                MockPluginSet.withStorage("filesystem"));

        for (PluginSet set: pluginSets) {
            controller.loadPlugin(set);
        }
        assertSame( pluginSets.get(0), controller.getQueryProviderByName("lucene", false));
        assertSame( pluginSets.get(0), controller.getIndexerByName("lucene", false));
        assertSame( pluginSets.get(3), controller.getQueryProviderByName("cbir", false));
        assertEquals( 1, controller.getServletPlugins(false));
        assertEquals( "qido-rs", controller.getServletPlugins(false).iterator().next().getName());
        assertEquals( 1, controller.getRestletPlugins(false));
        assertEquals( "wado-rs", controller.getRestletPlugins(false).iterator().next().getName());
        assertEquals( 1, controller.getStoragePlugins(false));
        assertEquals( "filesystem", controller.getStoragePlugins(false).iterator().next().getName());
        assertEquals( "filesystem", controller.getStorageForSchema("dummy").getName());
    }

}
