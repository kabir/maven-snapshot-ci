package org.wildfly.util.maven.snapshot.ci.config.component;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ComponentJobsParser {
    private final Path yamlFile;
    private final String fileName;
    private final Set<String> jobKeys = new HashSet<>();

    private ComponentJobsParser(Path yamlFile, String fileName) {
        this.yamlFile = yamlFile;
        this.fileName = fileName;
    }

    public static ComponentJobsParser create(Path yamlFile) {
        String fileName = yamlFile.getFileName().toString();
        fileName = fileName.substring(0, fileName.indexOf("."));
        return new ComponentJobsParser(yamlFile, fileName);
    }

    public ComponentJobsConfig parse() throws Exception {
        System.out.println("Parsing component job config: " + yamlFile);
        Map<String, Object> input = null;
        try {
            Yaml yaml = new Yaml();
            input = yaml.load(new BufferedInputStream(new FileInputStream(yamlFile.toFile())));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Object envInput = input.remove("env");
        Object jobsInput = input.remove("jobs");
        if (input.size() > 0) {
            throw new IllegalStateException("Unknown entries: " + input.keySet());
        }
        if (jobsInput == null) {
            throw new IllegalStateException("No 'jobs' entry");
        }

        Map<String, String> mainEnv = Collections.emptyMap();
        if (envInput != null) {
            mainEnv = parseEnv(envInput);
        }
        List<Job> jobs = parseJobs(mainEnv, jobsInput);
        if (jobs.size() == 0) {
            throw new IllegalStateException("'jobs' entry is empty");
        }

        return new ComponentJobsConfig(jobs);
    }

    private List<Job> parseJobs(Map<String, String> mainEnv, Object input) {
        if (input instanceof Map == false) {
            throw new IllegalStateException("Not an instance of Map");
        }
        List<Job> jobs = new ArrayList<>();
        Map<String, Object> map = (Map)input;
        for (String key : map.keySet()) {
            jobs.add(parseJob(mainEnv, key, map.get(key)));
        }
        return jobs;
    }

    private Job parseJob(Map<String, String> mainEnv, String jobKey, Object input) {
        if (input instanceof Map == false) {
            throw new IllegalStateException("Not an instance of Map");
        }
        Map<String, Object> map = (Map)input;

        String name = createJobName(jobKey);
        Map<String, String> jobEnv = new HashMap<>();
        List<String> needs = new ArrayList<>();
        List<JobRunElement> runElements = null;
        for (String key : map.keySet()) {
            switch (key) {
                case "env": {
                    jobEnv = parseEnv(map.get(key));
                    break;
                }
                case "needs":
                    needs = parseNeeds(map.get(key));
                    break;
                case "run": {
                    runElements = parseJobRunElements(map.get(key));
                    break;
                }
                default:
                    throw new IllegalStateException("Unknown entry: " + key);
            }
        }
        jobKeys.add(jobKey);


        //Merge the  main env entries and the job env entries, making sure the job ones come last
        jobEnv = mergeEnv(mainEnv, jobEnv);
        if (runElements == null) {
            throw new IllegalStateException("Null 'run'");
        }

        return new Job(name, jobEnv, needs, runElements);
    }

    private Map<String, String> mergeEnv(Map<String, String> mainEnv, Map<String, String> jobEnv) {
        Map<String, String> merge = new LinkedHashMap<>();
        merge.putAll(mainEnv);
        merge.putAll(jobEnv);
        return merge;
    }

    private List<String> parseNeeds(Object input) {
        if (input instanceof List == false) {
            throw new IllegalStateException("Not an instance of List");
        }
        List<Object> list = (List) input;
        List<String> needs = new ArrayList<>();
        for (Object o : list) {
            if (o instanceof String == false) {
                throw new IllegalStateException(o + " is not a string");
            }
            String jobKey = (String) o;
            if (!jobKeys.contains(jobKey)) {
                throw new IllegalStateException("Unknown job '" + jobKey + "' in 'needs'. If it is in the file, " +
                        "make sure to add them in the correct order for resolution to happen.");
            }
            needs.add(createJobName(jobKey));
        }
        return needs;
    }

    private List<JobRunElement> parseJobRunElements(Object input) {
        if (input instanceof List == false) {
            throw new IllegalStateException("Not an instance of List");
        }
        List<Object> list = (List)input;
        List<JobRunElement> jobRunElements = new ArrayList<>();
        for (Object o : list) {
            JobRunElement jobRunElement = parseJobRunElement(o);
            jobRunElements.add(jobRunElement);
        }
        return jobRunElements;
    }

    private JobRunElement parseJobRunElement(Object input) {
        if (input instanceof Map == false) {
            throw new IllegalStateException("Not an instance of Map");
        }
        Map<String, String> map = (Map) input;
        if (map.size() != 1) {
            throw new IllegalStateException("Only one entry allowed per 'run' element");
        }
        String key = map.keySet().iterator().next();
        JobRunElement.Type type = null;
        switch (key) {
            case "mvn": {
                type = JobRunElement.Type.MVN;
                break;
            }
            case "shell":
                type = JobRunElement.Type.SHELL;
                break;
            default:
                throw new IllegalStateException("Unknown type for 'run' element");
        }

        Object value = map.get(key);
        if (value instanceof String == false) {
            throw new IllegalStateException(value + " is not a String");
        }
        String command = (String) value;
        if (command.trim().startsWith("mvn ")) {
            if (type == JobRunElement.Type.SHELL) {
                throw new IllegalStateException("If you want to run mvn commands, that should happen in a 'mvn' run entry");
            } else if (type == JobRunElement.Type.MVN) {
                throw new IllegalStateException("In a 'mvn' run entry, don't include the mvn command. e.g.\n" +
                        "\t\t - mvn: install\n" +
                        "gets translated to:\n" +
                        "\t\t $mvn install");
            }
        }
        return new JobRunElement(type, command);
    }

    private Map<String, String> parseEnv(Object input) {
        if (input instanceof Map == false) {
            throw new IllegalStateException("Not an instance of Map");
        }
        Map<String, Object> map = (Map)input;
        Map<String, String> result = new LinkedHashMap<>();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof String == false && value instanceof Number == false) {
                throw new IllegalStateException("Value under key '" + key + "' is not a String or a Number");
            }
            result.put(key, value.toString());
        }
        return result;
    }

    private String createJobName(String jobKey) {
        return fileName + "-" + jobKey;
    }
}
