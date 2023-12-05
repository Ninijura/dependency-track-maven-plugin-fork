package io.github.pmckeown.util;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GitInfoLoader {

    public static String getGitBranch(String dotGitDirectory) throws IOException {
        String head = Files.readString(Paths.get(dotGitDirectory + "/HEAD"));
        String[] pathForBranch = head.split("/");
        return pathForBranch[pathForBranch.length - 1];
    }

}
