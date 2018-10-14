package com.codestats;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import static com.codestats.hostspots.DiffCollector.getHotSpots;
import static com.codestats.utils.GitUtils.openGitRepository;

public class Analyser {


    public static void main(String[] args) throws IOException, GitAPIException {
        new Analyser().run(args);
    }

    private void run(String[] args) throws IOException, GitAPIException {

        System.out.println("Running analysis...");

        try (Repository repository = openGitRepository(Paths.get(args[0]).toFile())) {
            System.out.println(repository.getRepositoryState());
            try (Git git = new Git(repository)) {
                System.out.println("Branch: " + repository.getBranch() + ", description: " + repository.getRepositoryState().getDescription());

                Map<String, Long> result = getHotSpots(git);

                result.entrySet().forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));
            }
        }
    }
}
