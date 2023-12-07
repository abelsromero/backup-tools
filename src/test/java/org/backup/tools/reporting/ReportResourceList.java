package org.backup.tools.reporting;

import java.util.List;
import java.util.Map;

public class ReportResourceList {

    private Map<String,String> metadata;
    private List<ReportResource> resources;

    ReportResourceList() {
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public List<ReportResource> getResources() {
        return resources;
    }

    public void setResources(List<ReportResource> resources) {
        this.resources = resources;
    }

}
