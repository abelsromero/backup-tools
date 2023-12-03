package org.backup.tools.report;

public final class ReportResource {

    private String name;
    private String location;
    private String hash;
    private long size;

    public ReportResource() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    static ReportResource reportResource(String name, String location, String hash, long size) {
        final ReportResource reportResource = new ReportResource();
        reportResource.setName(name);
        reportResource.setLocation(location);
        reportResource.setHash(hash);
        reportResource.setSize(size);
        return reportResource;
    }
}
