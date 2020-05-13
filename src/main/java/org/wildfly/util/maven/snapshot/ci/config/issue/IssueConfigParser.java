package org.wildfly.util.maven.snapshot.ci.config.issue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.wildfly.util.maven.snapshot.ci.config.BaseParser;
import org.yaml.snakeyaml.Yaml;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class IssueConfigParser extends BaseParser {
    private final Path yamlFile;

    private IssueConfigParser(Path yamlFile) {
        this.yamlFile = yamlFile;
    }

    public static IssueConfigParser create(Path yamlFile) {
        return new IssueConfigParser(yamlFile);
    }

    public IssueConfig parse()  {
        Map<String, Object> map = null;
        try {
            Yaml yaml = new Yaml();
            map = yaml.load(new BufferedInputStream(new FileInputStream(yamlFile.toFile())));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Object name = map.remove("name");
        Object envInput = map.remove("env");
        Object componentsInput = map.remove("components");

        if (map.keySet().size() > 0) {
            throw new IllegalStateException("Unknown in components entries: " + map.keySet());
        }

        validateNotNullType(String.class, name, "name", " workflow trigger");
        Map<String, String> env = parseEnv(envInput);

        List<Component> components = parseComponents(componentsInput);

        return new IssueConfigImpl((String) name, env, components);
    }

    private List<Component> parseComponents(Object input) {
        if (input instanceof List == false) {
            throw new IllegalStateException("'components' in workflow trigger yaml is not a list");
        }
        List<Object> list = (List) input;
        List<Component> components = new ArrayList<>();
        for (Object o : list) {
            components.add(parseComponent(o));
        }
        return components;
    }

    private Component parseComponent(Object input) {
        if (input instanceof Map == false) {
            throw new IllegalStateException("Not an instance of Map");
        }
        Map<String, Object> map = (Map)input;
        Object name = map.remove("name");
        Object org = map.remove("org");
        Object branch = map.remove("branch");
        Object mavenOpts = map.remove("mavenOpts");
        validateNotNullType(String.class, name, "name", "a components entry");
        validateNotNullType(String.class, org, "org", "a components entry");
        validateNotNullType(String.class, branch, "branch", "a components entry");
        if (mavenOpts != null) {
            validateType(String.class, mavenOpts, "mavenOpts", "a components entry");
        }

        Object depsInput = map.remove("dependencies");
        if (map.keySet().size() > 0) {
            throw new IllegalStateException("Unknown in components entries: " + map.keySet());
        }

        List<Dependency> dependencies = parseDependencies(depsInput);
        return new ComponentImpl(
                (String) name,
                (String) org,
                (String) branch,
                (String) mavenOpts,
                Collections.unmodifiableList(dependencies));
    }

    private List<Dependency> parseDependencies(Object input) {
        if (input == null) {
            return Collections.emptyList();
        }
        if (input instanceof List == false) {
            throw new IllegalStateException("'dependencies' is not an instance of List");
        }
        List<Object> list = (List) input;
        List<Dependency> dependencies = new ArrayList<>();
        for (Object o : list) {
            dependencies.add(parseDependency(o));
        }
        return Collections.unmodifiableList(dependencies);
    }

    private Dependency parseDependency(Object input) {
        if (input instanceof Map == false) {
            throw new IllegalStateException("Not an instance of Map");
        }
        Map<String, Object> map = (Map)input;

        Object name = map.remove("name");
        Object property = map.remove("property");
        validateNotNullType(String.class, name, "name", "a dependencies entry");
        validateNotNullType(String.class, property, "property", "a dependencies entry");

        if (map.keySet().size() > 0) {
            throw new IllegalStateException("Unknown in dependencies entries: " + map.keySet());
        }

        return new DependencyImpl((String) name, (String) property);
    }

    private void validateNotNullType(Class<?> clazz, Object value, String name, String description) {
        validateNotNull(value, name, description);
        validateType(clazz, value, name, description);
    }

    private void validateNotNull(Object value, String name, String description) {
        if (value == null) {
            String msg = String.format("Null '%s' for %s", name, description);
            throw new IllegalStateException(msg);
        }
    }

    private void validateType(Class<?> clazz, Object value, String name, String description) {
        if (!clazz.isAssignableFrom(value.getClass())) {
            String msg = String.format("'%s' for %s was not a %s", name, description, clazz.getSimpleName());
            throw new IllegalStateException(msg);
        }
    }
}
