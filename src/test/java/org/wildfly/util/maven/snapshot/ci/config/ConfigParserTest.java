package org.wildfly.util.maven.snapshot.ci.config;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ConfigParserTest {
    @Test
    public void testParseYaml() throws Exception {
        URL url = this.getClass().getResource("test.yml");
        Path path = Paths.get(url.toURI());
        Config config = ConfigParser.create(path).parse();

        Assert.assertNotNull(config);

        List<Component> components = config.getComponents();
        Assert.assertNotNull(components);
        Assert.assertEquals(2, components.size());

        Component wfCommon = components.get(0);
        Assert.assertEquals("wildfly-common", wfCommon.getName());
        Assert.assertEquals("wildfly", wfCommon.getOrg());
        Assert.assertEquals("test", wfCommon.getBranch());
        Assert.assertNotNull(wfCommon.getDependencies());
        Assert.assertEquals(0, wfCommon.getDependencies().size());

        Component wfElytron = components.get(1);
        Assert.assertEquals("wildfly-elytron", wfElytron.getName());
        Assert.assertEquals("wildfly-security", wfElytron.getOrg());
        Assert.assertEquals("feature", wfElytron.getBranch());
        Assert.assertNotNull(wfElytron.getDependencies());
        Assert.assertEquals(1, wfElytron.getDependencies().size());
        Assert.assertEquals("wildfly-common", wfElytron.getDependencies().get(0).getName());
        Assert.assertEquals("version.wildfly.common", wfElytron.getDependencies().get(0).getProperty());
    }
}
