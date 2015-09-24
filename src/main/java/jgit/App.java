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

        // Clone repo 1 to repo 2
        Git repo2git = Git.cloneRepository().setDirectory(repo2path.toFile()).setURI("file://" + repo1path.toString()).call();

        // Look at HEAD of repo 2 after cloning
        ObjectId initialHead = repo2git.getRepository().resolve("HEAD");
        System.out.println("HEAD is initially " + initialHead.getName());

        // Make a second commit into repo 1
        Files.write(Paths.get(repo1path.toString(), "bar"), "bar".getBytes());
        repo1git.commit().setMessage("hi again").call();

        // Fetch the second commit in repo 1 into repo 2
        repo2git.fetch().call();

        // Look at HEAD in repo 2 again
        ObjectId secondHead = repo2git.getRepository().resolve("HEAD");
        System.out.println("HEAD is now " + secondHead.getName());

        // Look at FETCH_HEAD in repo 2
        ObjectId fetchHead = repo2git.getRepository().resolve("FETCH_HEAD");
        System.out.println("FETCH_HEAD is now " + fetchHead.getName());
    }
}
