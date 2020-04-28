package org.wildfly.util.maven.snapshot.ci.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ConfigParser {
    private final Path yamlFile;

    private ConfigParser(Path yamlFile) {
        this.yamlFile = yamlFile;
    }

    public static ConfigParser create(Path yamlFile) {
        return new ConfigParser(yamlFile);
    }

    public Config parse() throws Exception {
        ConfigImpl config = null;
        try {
            Constructor constructor = new Constructor(ConfigImpl.class);

            TypeDescription componentsDescription = new TypeDescription(ConfigImpl.class);
            componentsDescription.addPropertyParameters("components", ComponentImpl.class);
            constructor.addTypeDescription(componentsDescription);

            TypeDescription dependenciesDescription = new TypeDescription(ComponentImpl.class);
            dependenciesDescription.addPropertyParameters("dependencies", DependencyImpl.class);
            constructor.addTypeDescription(dependenciesDescription);

            Yaml yaml = new Yaml(constructor);
            config = yaml.load(new BufferedInputStream(new FileInputStream(yamlFile.toFile())));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        config.validate();
        return config;
    }
}
