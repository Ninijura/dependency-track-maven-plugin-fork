package io.github.pmckeown.dependencytrack.bom;

import org.json.JSONObject;

public class SbomComponent {

    private String group;
    private String name;
    private String version;
    private String cpe;
    private String type;
    private String bomRef;

    public SbomComponent(){}

    public SbomComponent(String group, String name, String version, String scope, String cpe, String type, String bomRef) {
        this.group = group;
        this.name = name;
        this.version = version;
        this.cpe = cpe;
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

    public String getCpe() {
        return cpe;
    }

    public void setCpe(String cpe) {
        this.cpe = cpe;
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

    public JSONObject serialize(){
        JSONObject thisJSON = new JSONObject();
        thisJSON.put("name", name);
        thisJSON.put("group", group);
        thisJSON.put("version", version);
        thisJSON.put("cpe", cpe);
        thisJSON.put("type", type);
        thisJSON.put("bom-ref", bomRef);

        return thisJSON;
    }
}
