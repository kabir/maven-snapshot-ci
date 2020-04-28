package org.wildfly.util.maven.snapshot.ci.config;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public interface Config extends ConfigElement {
    String getName();
    Map<String, Object> getEnv();
    List<Component> getComponents();
}
