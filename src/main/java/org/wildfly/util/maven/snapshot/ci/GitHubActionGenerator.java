package org.wildfly.util.maven.snapshot.ci;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        //Config config = ConfigParser.create(yamlConfig).parse();

        System.out.println("Wil create workflow file at " + workflowFile.toAbsolutePath());

        // Copy the temporary workflow to see that we can do another workflow
//        URL url = this.getClass().getResource("temp.yml");
//        Path from = workflowFile.getParent().getParent().getParent().resolve("org/wildfly/util/maven/snapshot/ci");
//        System.out.println("Copying " + from + " to " + workflowFile);
//        System.out.println(from + " " + Files.exists(from));
//        System.out.println(workflowFile + " " + Files.exists(workflowFile));
//        Files.createFile(workflowFile);
//        System.out.println(workflowFile + " " + Files.exists(workflowFile));
//        Files.copy(Paths.get(url.toURI()), workflowFile);

        String tmpYaml =
                "# This will be generated\n" +
                "name: CI\n" +
                "on:\n" +
                "  push:\n" +
                "#    branches: [ master ]\n" +
                "jobs:\n" +
                "  build:\n" +
                "    runs-on: ubuntu-latest\n" +
                "    steps:\n" +
                "      - uses: actions/checkout@v2\n" +
                "      - name: Run a one-line script\n" +
                "        run: echo Hello, world!";
        Files.write(workflowFile, tmpYaml.getBytes(StandardCharsets.UTF_8));
    }
}
