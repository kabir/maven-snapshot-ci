package org.wildfly.util.maven.snapshot.ci.config.issue;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ComponentImpl implements Component {
    private String name;
    private String org;
    private String branch;
    private String mavenOpts;
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

    public String getMavenOpts() {
        return mavenOpts;
    }

    public void setMavenOpts(String mavenOpts) {
        this.mavenOpts = mavenOpts;
    }

    @Override
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = Collections.unmodifiableList(dependencies);
    }

    @Override
    public void validate() {
        if (name == null || org == null || branch == null) {
            throw new IllegalStateException("Null 'name', 'org' or 'branch' in a component");
        }
        for (Dependency dependency : dependencies) {
            dependency.validate();
        }

    }
}
