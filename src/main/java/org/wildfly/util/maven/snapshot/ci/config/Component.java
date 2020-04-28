package org.wildfly.util.maven.snapshot.ci.config;

import java.util.List;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public interface Component extends ConfigElement {
    String getName();

    String getOrg();

    String getBranch();

    String getMavenOpts();

    List<Dependency> getDependencies();
}
