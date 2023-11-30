package io.github.pmckeown.dependencytrack.bom;

import com.google.gson.annotations.SerializedName;

public class SbomComponent {

    private String group;
    private String name;
    private String version;
    private String scope;
    private String purl;
    private String type;
    @SerializedName("bom-ref")
    private String bomRef;

    public SbomComponent(String group, String name, String version, String scope, String purl, String type, String bomRef) {
        this.group = group;
        this.name = name;
        this.version = version;
        this.scope = scope;
        this.purl = purl;
        this.type = type;
        this.bomRef = bomRef;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBomRef() {
        return bomRef;
    }

    public void setBomRef(String bomRef) {
        this.bomRef = bomRef;
    }
}
