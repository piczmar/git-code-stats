package com.codestats;

import static com.codestats.hostspots.DiffCollector.getHotSpots;
import static com.codestats.utils.GitUtils.openGitRepository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;

import com.codestats.utils.GitUtils;

public class Analyser {


    public static void main(String[] args) throws IOException, GitAPIException {
        new Analyser().run(args);
    }

    private void run(String[] args) throws IOException, GitAPIException {

        System.out.println("Running analysis...");

        try (Repository repository = openGitRepository(Paths.get(args[0]).toFile())) {

            List<DiffEntry> diff = GitUtils
                .diff(repository, repository.resolve("bd31aff3fd8c9fb5cc072e72ac864e3b518cbbc3"),
                    repository.resolve("8e96317dbc4f30d3b2835eb90ade00f00ab82b15"));
            diff.forEach(
                e -> System.out.println(e.getChangeType() + "[" + e.getOldPath() + "," + e.getNewPath() + "]"));

            System.out.println(repository.getRepositoryState());
            try (Git git = new Git(repository)) {
                System.out.println("Branch: " + repository.getBranch() + ", description: " + repository.getRepositoryState().getDescription());

                Map<String, Long> result = getHotSpots(git);

                result.entrySet().forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));
            }
        }
    }
}
