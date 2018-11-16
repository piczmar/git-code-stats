package com.codestats;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

class GitClone {

    public static void main(String[] args) throws GitAPIException, IOException {
//        File dir = new File(args[0]);
        File dir = new File("/Users/marcin/Downloads/tmp");
        Files.createDirectories(dir.toPath());
        Git git = Git.cloneRepository()
//            .setURI( "https://github.com/eclipse/jgit.git" )
            .setURI( "https://github.com/piczmar/git-code-stats/commit/ca4150b13603bf35da7c363d86546f699b33f152" )
            .setDirectory(dir)
            .call();

    }
}
