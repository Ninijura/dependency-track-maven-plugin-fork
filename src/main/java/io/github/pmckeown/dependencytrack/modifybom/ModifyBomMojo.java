package io.github.pmckeown.dependencytrack.modifybom;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.bom.SbomComponent;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.inject.Inject;

@Mojo(name = "add-bom-component", defaultPhase = LifecyclePhase.VERIFY)
public class ModifyBomMojo extends AbstractDependencyTrackMojo {

    @Parameter(required = true, defaultValue = "${project.build.directory}/bom.json")
    private String bomLocation;
    @Parameter(required = true)
    private SbomComponent[] components;

    @Inject
    protected ModifyBomMojo(CommonConfig commonConfig, Logger logger) {
        super(commonConfig, logger);
    }

    @Override
    protected void performAction() throws MojoExecutionException, MojoFailureException {
        /*
        Should:

        - get bom
        - add all components to the components array
        - write to file
         */
    }
}
