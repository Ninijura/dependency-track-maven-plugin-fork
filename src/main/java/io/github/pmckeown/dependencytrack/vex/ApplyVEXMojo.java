package io.github.pmckeown.dependencytrack.vex;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.inject.Inject;

@Mojo(name = "vex-from-other-project")
public class ApplyVEXMojo extends AbstractDependencyTrackMojo {

    @Parameter(required = true, defaultValue = "${project.artifactId}")
    private String vexParentName;

    @Parameter(required = true, defaultValue = "master")
    private String vexParentVersion;

    private final ApplyVEXAction applyVEXAction;


    @Inject
    public ApplyVEXMojo(ApplyVEXAction applyVEXAction, CommonConfig commonConfig, Logger logger) {
        super(commonConfig, logger);
        this.applyVEXAction = applyVEXAction;
    }

    @Override
    protected void performAction() throws MojoExecutionException, MojoFailureException {
        commonConfig.setVexParentVersion(vexParentVersion);
        commonConfig.setVexParentName(vexParentName);

        logger.info("Getting the vulnerability exploitability exchange (VEX) document from " +
                "the project with name: %s and version: %s."
                , vexParentName
                , vexParentVersion);

        applyVEXAction.performAction();
    }
}
