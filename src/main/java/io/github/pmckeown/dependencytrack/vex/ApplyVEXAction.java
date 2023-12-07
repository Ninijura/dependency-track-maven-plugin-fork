package io.github.pmckeown.dependencytrack.vex;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.util.Logger;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Base64;

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
    }

    public void performAction(){
        toProjectName = commonConfig.getProjectName();
        toProjectVersion = commonConfig.getProjectVersion();
        fromProjectName = commonConfig.getVexParentName();
        fromProjectVersion = commonConfig.getVexParentVersion();
        if (getVEXFromProject()) postVEXToProject();
    }

    private boolean getVEXFromProject() {

        UUID vexParentUUID = getUUIDForProject(fromProjectName, fromProjectVersion);
        if (!vexParentUUID.isUuidSet()) return false;

        logger.info("Requesting VEX. Name: %s, Version: %s, UUID: %s",
                fromProjectName, fromProjectVersion, vexParentUUID.getUuid());
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

    private void postVEXToProject() {
        logger.info("Posting VEX to project name: %s, version: %s",
                toProjectName, toProjectVersion);

        String address = commonConfig.getDependencyTrackBaseUrl() + V1_VEX;

        JSONObject jsonVex = new JSONObject();
        jsonVex.put("projectName", toProjectName);
        jsonVex.put("projectVersion", toProjectVersion);
        jsonVex.put("vex", Base64.getEncoder().encodeToString(vex.getBytes()));

        Unirest.put(address)
                .header(ACCEPT, "application/json")
                .header("X-Api-Key", commonConfig.getApiKey())
                .header(CONTENT_TYPE, "application/json")
                .body(jsonVex.toString())
                .asString().ifFailure(httpStringResponse -> {
                    logger.info("Uploading the VEX failed: ");
                    logger.info("Code: %d %s: %s"
                            , httpStringResponse.getStatus()
                            , httpStringResponse.getStatusText()
                            , httpStringResponse.getBody());
                    logger.info("%s",httpStringResponse.getRequestSummary().asString());
                });
    }

    private UUID getUUIDForProject(String projectName, String projectVersion) {
        UUID uuid = new UUID();
        Unirest.get(commonConfig.getDependencyTrackBaseUrl() +
                        V1_PROJECT_LOOKUP +
                        String.format("?name=%s&version=%s", projectName, projectVersion))
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
        uuid.setUuid(responseBody.getString("uuid"));
    }

}
