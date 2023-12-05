package io.github.pmckeown.dependencytrack.vex;

public class UUID {

    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isUuidSet() {
        return !uuid.isBlank();
    }
}
