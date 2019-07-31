import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

public class Magit {
    //private Map<String, Node> m_Nodes;        //trade off ram vs
    private Map<String, Commit> m_Commits;
    private Map<String, Branch> m_Branches;
    private static Path s_MagitPath;
    private Head m_Head;

    public Magit(Path i_magitPath) throws IOException {
        //m_Nodes = new HashMap<>();          //what should it hold?
        m_Commits = new HashMap<>();
        m_Branches = new HashMap<>();
        s_MagitPath = i_magitPath;       //to check static initalizer.
        Branch br = new Branch("master", null);
        m_Branches.put("master", br);
        createHeadAndMasterBranchFiles();
        m_Head = new Head(br);
    }

    public static Path getMagitDir() {
        return s_MagitPath;
    }

    //Todo - fix the bug (Oran)
    private void createHeadAndMasterBranchFiles() throws IOException {

        Path branchesPath = s_MagitPath.resolve("branches");
        Files.createFile(branchesPath.resolve("HEAD.txt"));
        Files.createFile(branchesPath.resolve("master.txt"));
        Files.write(branchesPath.resolve("HEAD.txt"), "master".getBytes());
    }

    public void addNewCommit(String i_CommitSha1,Commit i_NewCommit){
        m_Commits.put(i_CommitSha1,i_NewCommit);
    }


    public Head getHead() {
        return m_Head;
    }


    public void handleNewCommit(String i_rootFolderSha1, String i_message) throws IOException {
        //1.create new sha1
        Commit commit = new Commit(i_rootFolderSha1,
                m_Head.getActiveBranch().getSha1LastCommit(),
                i_message);

        //2.generate commit sha1 and add to commits
        String commitSha1 = commit.generateSha1();
        m_Commits.put(commitSha1,commit);

        //3.make a zip from commit
        commit.Zip(commitSha1);
        m_Head.getActiveBranch().setSha1LastCommit(commitSha1);

        //update activebranch Content to new commit sha1
        updateActiveBranchContent(commitSha1);

    }
    private void updateActiveBranchContent(String newCommitSha1Content) throws IOException {
        String activeBranch = m_Head.getActiveBranch().getBracnhName();
        FileUtils.changeFileContent(s_MagitPath.resolve("branches").resolve(m_Head.getActiveBranch().getBracnhName()+".txt"),newCommitSha1Content);
    }
}
