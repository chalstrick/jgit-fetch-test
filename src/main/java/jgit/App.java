package jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App
{
    public static void main( String[] args ) throws IOException, GitAPIException {
        Path repo1path = Files.createTempDirectory("repo1");
        Path repo2path = Files.createTempDirectory("repo2");

        // Initialize repo 1
        Git repo1git = Git.init().setDirectory(repo1path.toFile()).call();

        // Write a test file into repo 1 and commit.
        Files.write(Paths.get(repo1path.toString(), "foo"), "foo".getBytes());
        repo1git.commit().setMessage("first commit").call();

        final boolean isBare = true;
        System.out.println();
        System.out.println("Bare? " + isBare);

        // Clone repo 1 to repo 2
        Git repo2git = Git.cloneRepository().setBare(isBare).setDirectory(repo2path.toFile()).setURI("file://" + repo1path.toString()).call();

        // Look at HEAD, FETCH_HEAD, and 'master' of repo 2 after cloning
        ObjectId initialHead = repo2git.getRepository().resolve("HEAD");
        ObjectId initialFetchHead = repo2git.getRepository().resolve("FETCH_HEAD");
        ObjectId initialMaster = repo2git.getRepository().resolve("master");
        System.out.println();
        System.out.println("HEAD right after clone is " + initialHead.getName());
        System.out.println("FETCH_HEAD right after clone is " + initialFetchHead.getName());
        System.out.println("master right after clone is " + initialMaster.getName());

        // Make a second commit into repo 1
        Files.write(Paths.get(repo1path.toString(), "bar"), "bar".getBytes());
        repo1git.commit().setMessage("hi again").call();

        // Fetch the second commit in repo 1 into repo 2
        repo2git.fetch().call();

        // Look at HEAD, FETCH_HEAD, and 'master' in repo 2 again
        ObjectId secondHead = repo2git.getRepository().resolve("HEAD");
        ObjectId secondFetchHead = repo2git.getRepository().resolve("FETCH_HEAD");
        ObjectId secondMaster = repo2git.getRepository().resolve("master");
        System.out.println();
        System.out.println("HEAD after fetch is " + secondHead.getName());
        System.out.println("FETCH_HEAD after fetch is " + secondFetchHead.getName());
        System.out.println("master after fetch is " + secondMaster.getName());
    }
}
