package org.wildfly.util.maven.snapshot.ci;

import java.nio.file.Path;

import org.wildfly.util.maven.snapshot.ci.config.Config;
import org.wildfly.util.maven.snapshot.ci.config.ConfigParser;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class GitHubActionGenerator {
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
        Config config = ConfigParser.create(yamlConfig).parse();


    }
}
