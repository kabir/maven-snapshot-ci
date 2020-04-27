package org.wildfly.util.maven.snapshot.ci.config;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ConfigImpl implements Config {
    List<Component> components = Collections.emptyList();

    @Override
    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = Collections.unmodifiableList(components);
    }

}
