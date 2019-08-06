import java.io.*;
import java.nio.file.*;
import java.util.List;

public class Engine {
    private static String s_ActiveUser = "admin";
    private Repository m_Repository;

    //-------------------------------------S Properties--------------------------//
    public Repository getRepository() {
        return m_Repository;
    }

    public static String getActiveUser() {
        return s_ActiveUser;
    }

    public void setActiveUserName(String i_NewUserName) {
        s_ActiveUser = i_NewUserName;
    }

    //-------------------------------------E Properties--------------------------//

    //------------------S create new repository and init-----------------------------------
    public void createNewRepository(String i_PathToCreateRepository, String i_RepositoryName) throws IOException {
        Path pathOfNewRepository = Paths.get(i_PathToCreateRepository); //.resolve(i_RepositoryName);
        Path magitPath = pathOfNewRepository.resolve(".magit");

        try {
            Files.createDirectory(pathOfNewRepository);
            Files.createDirectory(magitPath);
            Files.createDirectory(magitPath.resolve("objects"));
            Files.createDirectory(magitPath.resolve("branches"));
        } catch (IOException e) {
            //fail to create directory
            e.printStackTrace();
        }
        //to check if i need the path with the repo name or not ?!
        m_Repository = new Repository(i_RepositoryName, pathOfNewRepository, true);
        FileUtils.CreateAndWriteTxtFile(magitPath.resolve("repositoryName.txt"), i_RepositoryName);

        //Todo:delete down.
        Files.createFile(pathOfNewRepository.resolve("hello.txt"));
        Files.write(pathOfNewRepository.resolve("hello.txt"), "hello world".getBytes());
    }

    //------------------------------S change repository---------------------------//
    public void changeRepositories(String repositoryPath) throws IOException {
        m_Repository = new Repository("", Paths.get(repositoryPath), false);
        m_Repository.uploadOrChangeRepositories(repositoryPath);
    }

    public boolean checkIfRepository(String repositoryPath) {
        repositoryPath = repositoryPath.concat("\\.magit");
        File magitFile = new File(repositoryPath);
        return magitFile.exists();
    }

    //------------------------------E change repository---------------------------//

    public void createNewCommit(String i_Message) throws IOException {
        m_Repository.createCommit(i_Message);
    }


    //------------------S Branches commands-----------------------------------
    public List<Node> getAllFilesPointsFromLastCommit() {
        return m_Repository.getAllFilesFromActiveBranch();
    }

    public FileWalkResult getStatus() throws IOException {
        return m_Repository.getStatus();
    }

    public boolean checkBranchNameIsExist(String i_BranchName) {
        return m_Repository.getMagit().checkIfBranchNameIsExist(i_BranchName);
    }

    public void createNewBranch(String i_BranchName) throws IOException {
        m_Repository.createNewBranch(i_BranchName);
    }

    public void deleteExistingBranch(String branchToDeleteName) throws IOException, ActiveBranchDeleteExeption {
        m_Repository.deleteExistingBranch
                (branchToDeleteName);
    }

    public List<BranchInformation> getAllBranches() {
        return m_Repository.getMagit().getAllBarnchesInSystem();
    }

    public void checkOutBranch(String branchNametoCheckOut) throws IOException {
        m_Repository.checkOut(branchNametoCheckOut);
    }

    public List<CommitInformation> getActiveBranchHistory() {
        return m_Repository.getMagit().showActiveBranchHistory();
    }

    public void initializeHeadToCommit(String i_CommitSha1) throws IOException {
        m_Repository.initalizeHeadToCommit(i_CommitSha1);
    }

    //------------------E Branches commands-----------------------------------





}


