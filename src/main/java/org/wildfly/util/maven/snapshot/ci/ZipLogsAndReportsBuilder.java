package org.wildfly.util.maven.snapshot.ci;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ZipLogsAndReportsBuilder {
    String jobName;
    private IfCondition ifCondition;

    public ZipLogsAndReportsBuilder setName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public ZipLogsAndReportsBuilder setIfCondition(IfCondition ifCondition) {
        this.ifCondition = ifCondition;
        return this;
    }

    public Map<String, Object> build() {
        Map<String, Object> zip = new LinkedHashMap<>();
        zip.put("name", "Zip logs and surefire reports");
        if (ifCondition != null) {
            zip.put("if", ifCondition.getValue());
        }
        zip.put("run", "zip -R " + jobName + ".zip 'server.log' 'host-controller-log' 'surefire-reports/*.txt' 'surefire-reports/*.xml'");

        return zip;
    }
}
