import sun.invoke.empty.Empty;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Magit {
    private Map<String, Commit> m_Commits;
    private Map<String, Branch> m_Branches;
    private static Path s_MagitPath;
    private static Path s_ObjectsPath;
    private Head m_Head = new Head();


    public Magit(Path i_magitPath, boolean newRepo) throws IOException {
        m_Commits = new HashMap<>();
        m_Branches = new HashMap<>();
        s_MagitPath = i_magitPath;       //to check static initalizer.
        s_ObjectsPath = s_MagitPath.resolve("objects");

        if (newRepo == true) {
            Branch br = new Branch("master", null);
            m_Branches.put("master", br);
            createHeadAndMasterBranchFiles();
            m_Head = new Head(br);
        }
    }

    public Map<String, Commit> getCommits() {
        return m_Commits;
    }

    public Map<String, Branch> getBranches() {
        return m_Branches;
    }


    public static Path getMagitDir() {
        return s_MagitPath;
    }

    public static Path getObjectsPath() {
        return s_ObjectsPath;
    }

    public Head getHead() {
        return m_Head;
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


    //------------------------------------------adding new commit to the system----------------------------------
    public void handleNewCommit(String i_rootFolderSha1, String i_message) throws IOException {
        //1.create new sha1
        Commit commit = new Commit(i_rootFolderSha1,
                m_Head.getActiveBranch().getSha1LastCommit() == "" ? null :
                        (m_Head.getActiveBranch().getSha1LastCommit()),
                i_message);

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
        FileUtils.CreateAndWriteTxtFile(s_MagitPath.resolve("branches").resolve(i_branchName + ".txt"), newBranch.getSha1LastCommit());
    }

    public void deleteExistingBranch(String i_branchToDeleteName) throws IOException {
        m_Branches.remove(i_branchToDeleteName);
        Path p = s_MagitPath.resolve("branches").resolve(i_branchToDeleteName + "txt");
        FileUtils.deleteFile(s_MagitPath.resolve("branches").resolve(i_branchToDeleteName + ".txt"));
    }

    public List<BranchInformation> getAllBarnchesInSystem() {
        List<BranchInformation> allBranchesInformation = new LinkedList<>();
        for (Map.Entry<String, Branch> entry : m_Branches.entrySet()) {
            BranchInformation branchInformaton = new BranchInformation();
            branchInformaton.m_BracnhName = entry.getKey();
            if (branchInformaton.m_BracnhName.equals(m_Head.getActiveBranch().getBracnhName()))
                branchInformaton.m_isACtiveBranch = true;
            branchInformaton.m_Sha1LastCommit = entry.getValue().getSha1LastCommit();
            branchInformaton.m_CommitMessage = m_Commits.get(branchInformaton.m_Sha1LastCommit).getMessage();
            allBranchesInformation.add(branchInformaton);
        }
        return allBranchesInformation;
    }

    public void showActiveBranchHistory() {
        String lastCommitSha1 = m_Head.getActiveBranch().getSha1LastCommit();
        Commit currentCommit = m_Commits.get(lastCommitSha1);
        printActiveBranchHistory(currentCommit);
    }

    //Todo delete root folder sha1 from here.
    private void printActiveBranchHistory(Commit currentCommit) {
        if (currentCommit != null) {
            System.out.println(currentCommit.toString());
            printActiveBranchHistory(m_Commits.get(currentCommit.getParent()));
        }
    }

    public void initalize() {
        m_Commits.clear();
        m_Branches.clear();
    }

    public void updateCommitsAndBranchesFromNewRepository(String i_repositoryPath) throws IOException {
        Path repoPath = Paths.get(i_repositoryPath);
        Path magitPath = repoPath.resolve(".magit");
        Path branchesPath = magitPath.resolve("branches");
        Path objectsPath = magitPath.resolve("objects");

        addAllBarnches(branchesPath);
        addAllCommits(objectsPath);
    }


    private void addAllBarnches(Path branchesPath) throws IOException {
        File branchesDir = new File(branchesPath.toString());
        for (File file : branchesDir.listFiles()) {
            if (!file.getName().equals("HEAD.txt")) {
                String branchName = file.getName();
                branchName = branchName.substring(0, branchName.length() - 4);
                String commitSha1OfCurrentBranch = FileUtils.readFileAndReturnString(file.toPath());
                Branch newBranch = new Branch(branchName, commitSha1OfCurrentBranch);
                m_Branches.put(branchName, newBranch);
            }
        }
        Path headPath = branchesPath.resolve("HEAD.txt");
        String activeBranchName = FileUtils.readFileAndReturnString(headPath);
        Branch BranchActiveBranch = m_Branches.get(activeBranchName);
        m_Head.setActiveBranch(BranchActiveBranch);
    }

    private void addAllCommits(Path objectsPath) throws IOException {
        for (Map.Entry<String, Branch> entry : m_Branches.entrySet()) {
            String branchName = entry.getKey();
            Branch branch = m_Branches.get(branchName);
            addCurrentBranchCommits(branch.getSha1LastCommit(), objectsPath);
        }
    }

    private void addCurrentBranchCommits(String sha1LastCommit, Path objectsPath) throws IOException {
        //1.read the commit sha1 content from the zip file and generate commit
        if (!sha1LastCommit.equals("")) {
            String commitContent = FileUtils.getStringFromFolderZip(sha1LastCommit);
            String[] commitData = commitContent.split(",");
            if (commitData[1].equals("null")) {
                Commit newCommit = gennerateCommitFromFile((commitContent));
                m_Commits.put(sha1LastCommit, newCommit);
            } else {
                addCurrentBranchCommits(commitData[1], objectsPath);
                Commit newCommit = gennerateCommitFromFile(commitContent);
                m_Commits.put(sha1LastCommit, newCommit);
            }
        }
    }

    private Commit gennerateCommitFromFile(String commitContent) throws IOException {
        String[] commitData = commitContent.split(",");
        String commitParent = commitData[1].equals("null") ? null : commitData[1];
        return new Commit(commitData[0], commitParent, commitData[2], commitData[3], commitData[4]);
    }

}