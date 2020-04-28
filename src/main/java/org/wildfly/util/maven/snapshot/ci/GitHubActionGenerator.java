package org.wildfly.util.maven.snapshot.ci;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.wildfly.util.maven.snapshot.ci.config.Component;
import org.wildfly.util.maven.snapshot.ci.config.Config;
import org.wildfly.util.maven.snapshot.ci.config.ConfigParser;
import org.wildfly.util.maven.snapshot.ci.config.Dependency;
import org.yaml.snakeyaml.Yaml;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class GitHubActionGenerator {
    static final String PROJECT_VERSIONS_DIRECTORY = ".project_versions";
    private final Map<String, Object> workflow = new LinkedHashMap<>();
    private final Path workflowFile;
    private final Path yamlConfig;
    private final String branchName;
    private final String issueNumber;

    private GitHubActionGenerator(Path workflowFile, Path yamlConfig, String branchName, String issueNumber) {
        this.workflowFile = workflowFile;
        this.yamlConfig = yamlConfig;
        this.branchName = branchName;
        this.issueNumber = issueNumber;
    }

    static GitHubActionGenerator create(Path workflowDir, Path yamlConfig, String branchName, String issueNumber) {
        Path workflowFile = workflowDir.resolve("ci-" + issueNumber + ".yml");
        return new GitHubActionGenerator(workflowFile, yamlConfig, branchName, issueNumber);
    }

    void generate() throws Exception {
        if (workflow.size() > 0) {
            throw new IllegalStateException("generate() called twice?");
        }
        Config config = ConfigParser.create(yamlConfig).parse();
        System.out.println("Wil create workflow file at " + workflowFile.toAbsolutePath());

        setupWorkFlowHeaderSection(config);
        setupJobs(config);

        Yaml yaml = new Yaml();
        String output = yaml.dump(workflow);
        System.out.println("-----------------");
        System.out.println(output);
        System.out.println("-----------------");
        Files.write(workflowFile, output.getBytes(StandardCharsets.UTF_8));
    }

    private void setupWorkFlowHeaderSection(Config config) {
        workflow.put("name", config.getName());
        workflow.put("on", Collections.singletonMap("push", Collections.singletonMap("branches", branchName)));

        if (config.getEnv().size() > 0) {
            Map<String, Object> env = new HashMap<>();
            for (String key : config.getEnv().keySet()) {
                env.put(key, config.getEnv().get(key));
            }
            workflow.put("env", env);
        }
    }

    private void setupJobs(Config config) {
        Map<String, Object> componentJobs = new LinkedHashMap<>();

        for (Component component : config.getComponents()) {
            Map<String, Object> job = setupComponentBuildJob(component);
            String id = getComponentBuildId(component.getName());
            componentJobs.put(id, job);
        }
        workflow.put("jobs", componentJobs);

        // TODO the main jobs

    }

    private Map<String, Object> setupComponentBuildJob(Component component) {
        Map<String, Object> job = new LinkedHashMap<>();
        job.put("name", component.getName());
        job.put("runs-on", "ubuntu-latest");

        if (component.getDependencies().size() > 0) {
            List<String> needs = new ArrayList<>();
            for (Dependency dep : component.getDependencies()) {
                needs.add(getComponentBuildId(dep.getName()));
            }
            job.put("needs", needs);
        }

        List<Object> steps = new ArrayList<>();
        steps.add(
                new CheckoutBuilder()
                        .setRepo(component.getOrg(), component.getName())
                        .setBranch(component.getBranch())
                        .build());
        steps.add(
                new CacheMavenRepoBuilder()
                        .build());
        steps.add(
                new SetupJavaBuilder()
                        .setVersion("11")
                        .build());

        for (Dependency dependency : component.getDependencies()) {
            steps.add(
                    new DownloadArtifactBuilder()
                            .setPath(PROJECT_VERSIONS_DIRECTORY)
                            .setName(getVersionArtifactName(dependency.getName()))
                            .build());
            steps.add(
                    new ReadFileIntoEnvVarBuilder()
                            .setPath(PROJECT_VERSIONS_DIRECTORY + "/" + dependency.getName())
                            .setEnvVarName(getVersionEnvVarName(dependency.getName()))
                            .build());
        }

        steps.add(
                new GrabProjectVersionBuilder()
                        .setFileName(PROJECT_VERSIONS_DIRECTORY, component.getName())
                        .build());
        steps.add(
                new UploadArtifactBuilder()
                        .setName(getVersionArtifactName(component.getName()))
                        .setPath(PROJECT_VERSIONS_DIRECTORY + "/" + component.getName())
                        .build());
        steps.add(
                new MavenBuildBuilder()
                        .setOptions(getMavenOptions(component))
                        .build());
        job.put("steps", steps);
        return job;
    }

    private String getMavenOptions(Component component) {
        StringBuilder sb = new StringBuilder();
        if (component.getMavenOpts() != null) {
            sb.append(component.getMavenOpts());
        }
        for (Dependency dep : component.getDependencies()) {
            sb.append(" ");
            sb.append("-D" + dep.getProperty() + "=\"${" + getVersionEnvVarName(dep.getName()) + "}\"");
        }
        return sb.toString();
    }

    private String getComponentBuildId(String name) {
        return "build-" + name;
    }

    private String getVersionArtifactName(String name) {
        return "version-" + name;
    }

    private String getVersionEnvVarName(String name) {
        return "VERSION_" + name.replace("-", "_");
    }
}
