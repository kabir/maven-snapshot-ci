package org.wildfly.util.maven.snapshot.ci.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ConfigImpl implements Config {
    String name;
    Map<String, Object> env = Collections.singletonMap("MAVEN_OPTS", "-Xms756M -Xmx1g");
    List<Component> components = Collections.emptyList();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getEnv() {
        return env;
    }

    public void setEnv(Map<String, Object> env) {
        this.env = env;
    }

    @Override
    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = Collections.unmodifiableList(components);
    }

    public void validate() {
        if (name == null) {
            throw new IllegalStateException("No 'name' defined for the config");
        }
        if (components.size() == 0) {
            throw new IllegalStateException("No 'components' defined");
        }
        Set<String> names = new HashSet<>();
        for (Component component : components) {
            if (names.contains(component.getName())) {
                throw new IllegalStateException("Component '" + component.getName() + "' appears more than once");
            }
            for (Dependency dependency : component.getDependencies()) {
                if (!names.contains(dependency.getName())) {
                    throw new IllegalStateException("Component '" + component.getName() + "' depends on a component '"
                            + dependency.getName() + "' which does not exist yet.");
                }
            }
            component.validate();
            names.add(component.getName());
        }
    }

}
