import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Magit {
    //private Map<String, Node> m_Nodes;        //trade off ram vs
    private Map<String, Commit> m_Commits;
    private Map<String, Branch> m_Branches;
    private static Path s_MagitPath;
    private static Path s_ObjectsPath;
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

        s_ObjectsPath = s_MagitPath.resolve("branches");
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

    public void addNewCommit(String i_CommitSha1, Commit i_NewCommit) {
        m_Commits.put(i_CommitSha1, i_NewCommit);
    }


    public Head getHead() {
        return m_Head;
    }

    //------------------------------------------adding new commit to the system----------------------------------
    public void handleNewCommit(String i_rootFolderSha1, String i_message, CommitDelta i_CommitDeltaFromWalkTree) throws IOException {
        //1.create new sha1
        Commit commit = new Commit(i_rootFolderSha1,
                m_Head.getActiveBranch().getSha1LastCommit(),
                i_message, i_CommitDeltaFromWalkTree);

        //2.generate commit sha1 and add to commits
        String commitSha1 = commit.generateSha1();
        m_Commits.put(commitSha1, commit);

        //3.make a zip from commit
        commit.Zip(commitSha1);

        //update active branch Content to new commit sha1
        updateActiveBranchContent(commitSha1);
    }

    //------------------------------------------adding new commit to the system----------------------------------
    private void updateActiveBranchContent(String newCommitSha1Content) throws IOException {
        m_Head.getActiveBranch().setSha1LastCommit(newCommitSha1Content);
        String activeBranch = m_Head.getActiveBranch().getBracnhName();
        FileUtils.changeFileContent(s_MagitPath.resolve("branches").resolve(activeBranch + ".txt"), newCommitSha1Content);
    }

    //-----------------------------------adding a new branch to the system---------------------------
    public boolean checkIfBranchNameIsExist(String i_NewBrachName) {
        if (Files.exists(s_MagitPath.resolve("branches").resolve(i_NewBrachName + ".txt"))) {
            System.out.println("is exist");
            return true;
        } else {
            return false;
        }
    }

    public void createNewBranch(String i_branchName) throws IOException {
        String sha1OfLastCommitFromHeadBranch = m_Head.getActiveBranch().getSha1LastCommit();
        Branch newBranch = new Branch(i_branchName, sha1OfLastCommitFromHeadBranch);
        m_Branches.put(i_branchName, newBranch);
        FileUtils.CreateAndWriteTxtFile(s_ObjectsPath.resolve(i_branchName + ".txt"), newBranch.getSha1LastCommit());
    }

    public void deleteExistingBranch(String i_branchToDeleteName) throws IOException {
        m_Branches.remove(i_branchToDeleteName);
        Path p = s_MagitPath.resolve("branches").resolve(i_branchToDeleteName + "txt");
        FileUtils.deleteFile(s_MagitPath.resolve("branches").resolve(i_branchToDeleteName + ".txt"));
    }
}
