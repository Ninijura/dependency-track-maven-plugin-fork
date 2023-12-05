package io.github.pmckeown.dependencytrack.vex;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.util.Logger;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.github.pmckeown.dependencytrack.ResourceConstants.*;
import static kong.unirest.HeaderNames.ACCEPT;
import static kong.unirest.HeaderNames.CONTENT_TYPE;

@Singleton
public class ApplyVEXAction {

    private CommonConfig commonConfig;
    private Logger logger;
    private String fromProjectVersion;
    private String fromProjectName;
    private String toProjectName;
    private String toProjectVersion;
    private String vex;

    @Inject
    public ApplyVEXAction(CommonConfig commonConfig, Logger logger) {
        this.commonConfig = commonConfig;
        this.logger = logger;
        toProjectName = commonConfig.getProjectName();
        toProjectVersion = commonConfig.getProjectVersion();
        fromProjectName = commonConfig.getVexParentName();
        fromProjectVersion = commonConfig.getVexParentVersion();
    }

    public boolean performAction(){
        return (getVEXFromProject() && postVEXToProject());
    }

    private boolean getVEXFromProject() {

        UUID vexParentUUID = getUUIDForProject(fromProjectName, fromProjectVersion);
        if (!vexParentUUID.isUuidSet()) return false;

        requestVex(vexParentUUID);
        return !vex.isBlank();
    }

    private void requestVex(UUID uuid) {

        Unirest.get(commonConfig.getDependencyTrackBaseUrl() + V1_VEX_GET)
                .routeParam("uuid", uuid.getUuid())
                .header(ACCEPT, "application/vnd.cyclonedx+json")
                .header("X-Api-Key", commonConfig.getApiKey())
                .asString()
                .ifFailure(stringHttpResponse -> logger.error(String.format(
                        "Oh no! I was trying to get the VEX (Vulnerability Exploitability Exchange) document " +
                                "for the project with UUID: %s. " +
                                "But something went wrong. The server answered with Status code: %s"
                        , uuid.getUuid()
                        , stringHttpResponse.getStatus()
                )))
                .ifSuccess(response -> vex = response.getBody());
    }

    private boolean postVEXToProject() {

        Unirest.post(commonConfig.getDependencyTrackBaseUrl() + V1_VEX)
                .header(ACCEPT, "application/json")
                .header("X-Api-Key", commonConfig.getApiKey())
                .header(CONTENT_TYPE, "multipart/form-data")
                .field("name", toProjectName)
                .field("version", toProjectVersion)
                .field("vex",vex);

        return false;
    }

    private UUID getUUIDForProject(String projectName, String projectVersion) {
        UUID uuid = new UUID();

        Unirest.get(String.format(commonConfig.getDependencyTrackBaseUrl(),
                        V1_PROJECT_LOOKUP,
                        "?name=%s&version=%s", projectName, projectVersion))
                .header(ACCEPT, "application/json")
                .header("X-Api-Key", commonConfig.getApiKey())
                .asString()
                .ifFailure(stringHttpResponse -> logger.error(String.format(
                        "Oh no! I was trying to get the UUID for the project with name: %s and version: %s, " +
                                "but something went wrong. The server answered with Status code: %s"
                        , projectName
                        , projectVersion
                        , stringHttpResponse.getStatus()
                )))
                .ifSuccess(stringHttpResponse -> uuidExtractor(stringHttpResponse, uuid));

        return uuid;
    }

    private void uuidExtractor(HttpResponse<String> stringHttpResponse, UUID uuid) {

        String response = stringHttpResponse.getBody();

        JSONObject responseBody = new JSONObject(response);
        JSONArray versionsArray = responseBody.getJSONArray("versions");

        for (Object obj : versionsArray) {
            if (obj instanceof JSONObject
                    &&  ((JSONObject) obj).getString("version").equals(toProjectVersion)) {
                uuid.setUuid(((JSONObject) obj).getString("uuid"));
            }
        }
    }

}
