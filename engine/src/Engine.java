import java.io.*;
import java.nio.file.*;

public class Engine {
    private static String s_ActiveUser = "admin";
    private Repository m_Repository;

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
        return true;
    }

}


