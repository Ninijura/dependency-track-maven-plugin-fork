package io.github.pmckeown.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static junit.framework.TestCase.assertEquals;

public class GitInfoLoaderTest {

    private File headFile;
    private String userDir;

    @Before
    public void setup() throws IOException {
        userDir = System.getProperty("user.dir");
        headFile = new File(userDir + "/HEAD");
        if (!headFile.createNewFile()){
            throw new RuntimeException("File HEAD already exists. Please make sure that test isn't overwriting the actual .git/HEAD file.");
        }
    }

    @After
    public void teardown() {
        headFile.delete();
    }

    @Test
    public void testBranchFromHEADFile() throws IOException {
        //Set up
        try (FileWriter fwrite = new FileWriter(headFile)){
            fwrite.write("ref: refs/heads/something/branchname");
        }

        //Do stuff
        String branch = GitInfoLoader.getGitBranch(userDir);

        //Assert
        assertEquals("branchname", branch);
    }

    @Test
    public void testBranchFromHeadFileNoSlashes() throws IOException {
        //Set up
        try (FileWriter fwrite = new FileWriter(headFile)){
            fwrite.write("aæsdlfjæ eu foawejfk a0ef");
        }

        //Do stuff
        String branch = GitInfoLoader.getGitBranch(userDir);

        //Assert
        assertEquals("aæsdlfjæ eu foawejfk a0ef", branch);
    }

    @Test
    public void throwsWhenFileNotPresent() throws IOException {
        //Set up
        if (headFile.delete()){

            //Assert
            assertThrows(IOException.class, () -> GitInfoLoader.getGitBranch(userDir));
        }
        //Clean up
        setup();
    }


}
