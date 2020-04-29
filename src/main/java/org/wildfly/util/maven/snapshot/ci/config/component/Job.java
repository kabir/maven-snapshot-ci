package org.wildfly.util.maven.snapshot.ci.config.component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class Job {
    private final String name;
    private final Map<String, String> jobEnv;
    private final List<String> needs;
    private final List<JobRunElement> runElements;

    public Job(String name, Map<String, String> jobEnv, List<String> needs, List<JobRunElement> runElements) {
        this.name = name;
        this.jobEnv = jobEnv;
        this.needs = needs;
        this.runElements = runElements;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getJobEnv() {
        return jobEnv;
    }

    public List<String> getNeeds() {
        return needs;
    }

    public List<JobRunElement> getRunElements() {
        return runElements;
    }
}
