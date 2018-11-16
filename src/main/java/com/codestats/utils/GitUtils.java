package com.codestats.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class GitUtils {

    public static Repository openGitRepository(File gitDir) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder
            .findGitDir(gitDir)
            .readEnvironment()
            .build();
    }

    public static List<String> listFiles(Repository repository, RevCommit commit) {
        try {
            TreeWalk treeWalk = new TreeWalk(repository);
//            treeWalk.addTree(headCommit.getTree());
            treeWalk.reset(commit.getTree());
            treeWalk.setRecursive(false);
            List<String> filePaths = new ArrayList();
            while (treeWalk.next()) {
                if (treeWalk.isSubtree()) {
                    System.out.println("dir: " + treeWalk.getPathString() + ", " + treeWalk.getOperationType());
                    treeWalk.enterSubtree();
                } else {
                    String filePath = treeWalk.getPathString();
                    System.out.println("file: " + filePath);
                    filePaths.add(filePath);
                }
            }
            return filePaths;
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    public static List<DiffEntry> diff(Git git, ObjectId newCommit, ObjectId oldCommit) throws IOException {
        DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());
        df.setRepository(git.getRepository());
        return df.scan(newCommit, oldCommit);
    }

    public static List<DiffEntry> diff(Repository repository, ObjectId newCommit, ObjectId oldCommit)
        throws IOException {
        DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());
        df.setRepository(repository);
        return df.scan(newCommit, oldCommit);
    }

    public static void printDiff(Repository repository, ObjectId newCommit, ObjectId oldCommit) throws IOException {
        diff(repository, newCommit, oldCommit)
            .forEach(System.out::println);
    }

    public static void printLastDiff(Repository repository) throws GitAPIException, IOException {
        ObjectId treeId = repository.resolve("HEAD^{tree}"); // equals newCommit.getTree()
        ObjectId tree2Id = repository.resolve("HEAD~1^{tree}"); // equals newCommit.getTree()
        printDiff(repository, tree2Id, treeId);
    }

    public static void getCommitsInfo(Iterable<RevCommit> logs) {
        int count = 0;
        for (RevCommit rev : logs) {
            System.out.println("Commit: " + rev + " at: " + rev.getCommitterIdent()
                .getWhen()/* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
            count++;
        }
        System.out.println("Had " + count + " commits overall on current branch");
    }

    public static void getCommitInfo(Repository repository, String commitHash) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(commitHash));
            System.out.println(commit.getFullMessage());
            System.out.println("parent: " + commit.getParent(0));
        }
    }

    public static RevCommit getPreviousCommit(Repository repository, String commitHash) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(commitHash));
            return commit.getParent(0);
        }
    }

    public static Stream<RevCommit> getCommits(Git git) throws GitAPIException {
        return StreamSupport.stream(git.log().call().spliterator(), false);
    }

    public static Stream<ObjectId> getRevTrees(Stream<RevCommit> commitsStream) {
        return commitsStream
            .peek(rev -> System.out.println("Processing commit: " + rev.getId()))
            .map(rev -> rev.getTree().getId());
    }

    public static List<String> getAllCommiters(Git git) throws GitAPIException {
        return StreamSupport.stream(git.log().call().spliterator(), false)
            .map(l -> l.getCommitterIdent().getEmailAddress())
            .distinct()
            .collect(toList());
    }

}
