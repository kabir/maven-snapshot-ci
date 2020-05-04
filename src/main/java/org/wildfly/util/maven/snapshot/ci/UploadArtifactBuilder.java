package org.wildfly.util.maven.snapshot.ci;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class UploadArtifactBuilder extends AbstractArtifactBuilder<UploadArtifactBuilder> {

    private String timestamp;

    UploadArtifactBuilder() {
        super("actions/upload-artifact@v2");
    }
    @Override
    UploadArtifactBuilder getThis() {
        return this;
    }

    public UploadArtifactBuilder setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    protected String formatName(String name) {
        return name + " " + timestamp;
    }
}