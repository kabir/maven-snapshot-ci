package org.wildfly.util.maven.snapshot.ci.config;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ComponentImpl implements Component {
    private String name;
    private String org;
    private String branch;
    private List<Dependency> dependencies = Collections.emptyList();

    public ComponentImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    @Override
    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Override
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = Collections.unmodifiableList(dependencies);
    }
}
