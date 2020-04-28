package org.wildfly.util.maven.snapshot.ci;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class UploadArtifactBuilder {
    private String name;
    private String path;

    public UploadArtifactBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UploadArtifactBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    Map<String, Object> build() {
        Map<String, Object> upload = new LinkedHashMap<>();
        upload.put("uses", "actions/upload-artifact@v1");

        Map<String, Object> with = new LinkedHashMap<>();
        with.put("name", name);
        with.put("path", path);
        upload.put("with", with);

        return upload;
    }
}