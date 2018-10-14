package com.codestats.hostspots;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codestats.utils.GitUtils.*;
import static java.util.stream.Collectors.toList;

public class DiffCollector {

    private Git git;
    private ObjectId newCommit;
    private ObjectId oldCommit;

    private DiffCollector(Git git) {
        this.git = git;
    }

    private List<String> allPaths = new ArrayList<>();

    /**
     * Gets the most frequently committed files and the count of commits
     * sorted from most frequently changed on the top
     *
     * @return a map of file path to count of commits of this file
     */
    public static Map<String, Long> getHotSpots(Git git) throws GitAPIException {
        Stream<ObjectId> revTrees = getRevTrees(getCommits(git));

        DiffCollector collector = revTrees
                .collect(() -> new DiffCollector(git), DiffCollector::accept, DiffCollector::combine);

        Map<String, Long> countsMap = collector.allPaths.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return countsMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

    }

    private static void combine(DiffCollector c1, DiffCollector c2) {
        throw new RuntimeException("parallel streams not supported");
    }

    private static void accept(DiffCollector collector, ObjectId objectId) {
        collector.newCommit = objectId;

        if (collector.newCommit != null && collector.oldCommit != null) {
            try {
                List<DiffEntry> diffs = diff(collector.git, collector.oldCommit, collector.newCommit);
                List<String> paths = diffs.stream()
                        .filter(e -> e.getChangeType() != DiffEntry.ChangeType.DELETE)
                        .map(DiffEntry::getNewPath)
                        .collect(toList());
                collector.allPaths.addAll(paths);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        collector.oldCommit = collector.newCommit;
    }

}