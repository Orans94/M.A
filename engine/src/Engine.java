import java.io.*;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;

public class Engine {
    private static String s_ActiveUser = "admin";
    private Repository m_Repository;


    public Repository getRepository() {
        return m_Repository;
    }

    public static String getActiveUser() {
        return s_ActiveUser;
    }

    public boolean checkBranchNameIsExist(String i_BranchName) {
        return m_Repository.getMagit().checkIfBranchNameIsExist(i_BranchName);
    }

    //------------------S create new repository -----------------------------------
    public void createNewRepository(String i_PathToCreateRepository, String i_RepositoryName) throws IOException {
        Path pathOfNewRepository = Paths.get(i_PathToCreateRepository).resolve(i_RepositoryName);
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

        m_Repository = new Repository(i_RepositoryName, repoPath);

        //Todo:delete down.
        Files.createFile(repoPath.resolve("hello.txt"));
        Files.write(repoPath.resolve("hello.txt"), "hello world".getBytes());
    }
    //------------------E create new repository -----------------------------------

    public void createNewCommit(String i_Message) throws IOException {
        m_Repository.createCommit(i_Message);
    }

    public void createNewBranch(String i_BranchName) throws IOException {
        m_Repository.createNewBranch(i_BranchName);
    }

    public void deleteExistingBranch(String branchToDeleteName) throws IOException, ActiveBranchDeleteExeption {
        m_Repository.deleteExistingBranch(branchToDeleteName);
    }


    public boolean isDirectoryNameValid(String repositoryName) {
        for(char toCheck : FileUtils.ILLEGAL_CHARACTERS)
        {
            if (repositoryName.contains(String.valueOf(toCheck)))
                return false;
        }

        return repositoryName != "";
    }

    public List<BranchInformation> showAllBranches() {
        return m_Repository.getMagit().getAllBarnchesInSystem();
    }

    public void checkOutBranch(String branchNametoCheckOut) throws IOException {
        //1.delete all the current wc
        m_Repository.checkOut(branchNametoCheckOut);
        //2.find the branch in system and take the commit that it points to.

        //3.unzip in temp directory the "node" and pass all the content to system

        //4.for each row if blob do an zip to path
        //               else do unzip and deep to next path with the directory path

        //5.
    }

    public void changeActiveUserName(String i_NewUserName) {
        s_ActiveUser = i_NewUserName;
    }

    public void showAllFilesPointsFromLastCommit() {
        m_Repository.getLastState().showAllFilesFromActiveBranch();
    }

    public void showActiveBranchHistory() {
        m_Repository.getMagit().showActiveBranchHistory();
    }

    public FileWalkResult showStatus() throws IOException {
        return m_Repository.showStatus();
    }
}


