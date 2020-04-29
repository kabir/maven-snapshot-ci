package org.wildfly.util.maven.snapshot.ci.config.issue;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public interface Dependency extends ConfigElement {
    String getName();

    String getProperty();
}
