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
    //-------------------------------------E Properties--------------------------//


    //--------------------------E Active User Name-------------------------------//
    public void changeActiveUserName(String i_NewUserName) {
        s_ActiveUser = i_NewUserName;
    }
    //--------------------------E Active User Name-------------------------------//

    //------------------S create new repository and switch  -----------------------------------
    public void createNewRepository(String i_PathToCreateRepository, String i_RepositoryName) throws IOException {
        Path pathOfNewRepository = Paths.get(i_PathToCreateRepository); //.resolve(i_RepositoryName);
        Path repoPath = pathOfNewRepository;

        try {
            Files.createDirectory(pathOfNewRepository);
            pathOfNewRepository = pathOfNewRepository.resolve(".magit");
            Files.createDirectory(pathOfNewRepository);
            Files.createDirectory(pathOfNewRepository.resolve("objects"));
            Files.createDirectory(pathOfNewRepository.resolve("branches"));
        } catch (IOException e) {
            //fail to create directory
            e.printStackTrace();
        }
        //to check if i need the path with the repo name or not ?!
        m_Repository = new Repository(i_RepositoryName, repoPath,true);
        FileUtils.CreateAndWriteTxtFile(repoPath.resolve(".magit").resolve("repositoryName.txt"),i_RepositoryName);

        //Todo:delete down.
        Files.createFile(repoPath.resolve("hello.txt"));
        Files.write(repoPath.resolve("hello.txt"), "hello world".getBytes());
    }

    public boolean checkIfRepository(String repositoryPath) {
        repositoryPath = repositoryPath.concat("\\.magit");
        File magitFile = new File(repositoryPath);
        return magitFile.exists();
    }

    public void changeRepositories(String repositoryPath,boolean isNewRepo) throws IOException {
        m_Repository = new Repository("temp" ,Paths.get(repositoryPath),isNewRepo);
        m_Repository.uploadOrswitchRepositories(repositoryPath);
    }
    //------------------E create new repository and switch-----------------------------------


    public boolean checkBranchNameIsExist(String i_BranchName) {
        return m_Repository.getMagit().checkIfBranchNameIsExist(i_BranchName);
    }

    public void createNewCommit(String i_Message) throws IOException {
        m_Repository.createCommit(i_Message);
    }

    //------------------S Branches commands-----------------------------------
    public void createNewBranch(String i_BranchName) throws IOException {
        m_Repository.createNewBranch(i_BranchName);
    }

    public void deleteExistingBranch(String branchToDeleteName) throws IOException, ActiveBranchDeleteExeption {
        m_Repository.deleteExistingBranch
                (branchToDeleteName);
    }

    public List<BranchInformation> showAllBranches() {
        return m_Repository.getMagit().getAllBarnchesInSystem();
    }

    public void checkOutBranch(String branchNametoCheckOut) throws IOException {
        m_Repository.checkOut(branchNametoCheckOut);
    }
    //------------------S Branches commands-----------------------------------

    public void showAllFilesPointsFromLastCommit() {
        m_Repository.getLastState().showAllFilesFromActiveBranch();
    }

    public void showActiveBranchHistory() {
        m_Repository.getMagit().showActiveBranchHistory();
    }

    public FileWalkResult showStatus() throws IOException {
        return m_Repository.getStatus();
    }

    public boolean isDirectoryNameValid(String repositoryName) {
        for (char toCheck : FileUtils.ILLEGAL_CHARACTERS) {
            if (repositoryName.contains(String.valueOf(toCheck)))
                return false;
        }

        return repositoryName != "";
    }

}


