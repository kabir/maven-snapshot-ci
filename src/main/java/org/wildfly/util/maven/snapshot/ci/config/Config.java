package org.wildfly.util.maven.snapshot.ci.config;

import java.util.List;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public interface Config {
    List<Component> getComponents();
}
