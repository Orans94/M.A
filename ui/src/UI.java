import javafx.scene.Parent;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.*;

public class UI {
    private Engine m_Engine = new Engine();
    private Menu m_Menu = new Menu();

    public UI() {
        //Engine m_Engine = new Engine();
        //Menu m_Menu = new Menu();
    }

    public void start() throws IOException, ActiveBranchDeleteExeption {
        m_Menu.printMenu();
        while (true) {
            int choice = m_Menu.getUserChoice();
            handleUserChoice(choice);
        }
    }

    private void handleUserChoice(int i_UserChoice) throws IOException, ActiveBranchDeleteExeption {
        switch (i_UserChoice) {
            case 1:
                String newUserName = getUserName();
                m_Engine.changeActiveUserName(newUserName);
                break;

            case 2:
                //handle xml and all this shit......
                break;

            case 3:
                //Todo:Send the variables to the function.. now its just for checking.
                // String Path = getRepositoryPath();
                // String RepositoryName = getRepositoryName();
                //m_Engine.createNewRepository(getRepositoryPath(), getRepositoryName());
                //m_Engine.createNewRepository("C:\\Users\\ziv3r\\Desktop\\newReposforSwitch","newRepo");
                m_Engine.createNewRepository("C:\\Users\\ziv3r\\Desktop\\newRepo", "newRepo");
                break;

            case 4:
                m_Engine.changeRepositories(getValidExistingRepositoryPath(), false);
                break;

            case 5:
                //Todo - add information about root folder ?!
                m_Engine.showAllFilesPointsFromLastCommit();
                break;

            case 6:
                //show the open changes between current working copy and last commit.
                FileWalkResult fileStatus = m_Engine.showStatus();
                String repositoryName = m_Engine.getRepository().getName();
                String repoPath = m_Engine.getRepository().getPath().toString();
                printSystemStatus(fileStatus, repositoryName, repoPath);
                break;

            case 7:
                String commitMessage = getCommitMessageFromUser();
                m_Engine.createNewCommit(commitMessage);
                break;

            case 8:
                List<BranchInformation> allBranches = m_Engine.showAllBranches();
                printAllBranchesData(allBranches);
                break;

            case 9:
                String newBranchName = getBranchNameFromUser(); // we have 2 methods with different condition
                m_Engine.createNewBranch(newBranchName);
                break;

            case 10:
                boolean succedToDelete = false;
                try {
                    String branchToDeleteName = getExistBranchName();
                    m_Engine.deleteExistingBranch(branchToDeleteName);
                    succedToDelete = true;
                } catch (ActiveBranchDeleteExeption ex) {
                    System.out.println(ex.getMessage());
                }
                break;

            case 11:
                String branchNametoCheckOut = getExistBranchName();
                if (!m_Engine.getRepository().isWcClean()) {
                    System.out.println("There are unsaved changes, would you like to commit first?");
                    Scanner scanner = new Scanner(System.in);
                    if (scanner.nextLine().toLowerCase().equals("yes")) {
                        m_Engine.createNewCommit(getCommitMessageFromUser());
                    }
                }
                m_Engine.checkOutBranch(branchNametoCheckOut);
                printCollection(m_Engine.getRepository().getLastState().getLastCommitInformation().getFilePathToSha1().values());
                break;

            case 12:
                m_Engine.showActiveBranchHistory();
                break;
            case 13:
                exitSystem();
                break;
        }
    }

    private void exitSystem() {
        System.exit(0);
    }

    //----------------------------------S chagne user Name - task1----------------------------------
    private String getUserName() {
        String userName;
        Scanner scanner = new Scanner(System.in);
        System.out.println("please enter user name");
        userName = scanner.nextLine();
        return userName;
    }
    //----------------------------------E chagne user Name - task1----------------------------------

    //----------------------------------S first Bounous and chagne repositories user Name - task1----------------------------------
    private String getRepositoryName() {
         /*
        //TODO describe the prohibited symbols in folder name , check if needed and how.
        while (!m_Engine.isDirectoryNameValid(repositoryName)) {
            System.out.println("Invalid repository name, Please enter a legal repository name");
            repositoryName = scanner.nextLine();
        }*/
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter repository name");
        String repositoryName = scanner.nextLine();
        return repositoryName;
    }

    private String getRepositoryPath() {
        Scanner scanner = new Scanner(System.in);  // Create a Scanner object
        String repoPath = "";

        while (repoPath.isEmpty()) {
            System.out.println("Please enter a legal repository path");
            repoPath = scanner.nextLine();  // Read path input
            try {
                Paths.get(repoPath);
            } catch (InvalidPathException e) {
                repoPath = "";
            }
        }
        return repoPath;
    }

    private String getValidExistingRepositoryPath() {
        String repositoryPath = "";
        repositoryPath = getRepositoryPath();

        while (!m_Engine.checkIfRepository(repositoryPath)) {
            System.out.println("please enter an existing path of repository");
            repositoryPath = getRepositoryPath();
        }
        return repositoryPath;
    }
    //----------------------------------E first Bounous and chagne repositories user Name - task1----------------------------------

    private void printSystemStatus(FileWalkResult i_FileStatus, String i_RepositoryName, String i_RepoPath) {
        boolean isChanges = false;
        System.out.println("Repository Path:" + i_RepoPath);
        System.out.println("Repository: " + i_RepositoryName);

        if (i_FileStatus.getCommitDelta().getDeletedFiles().size() > 0) {
            System.out.println("Deleted Files:");
            printCollection(i_FileStatus.getCommitDelta().getDeletedFiles());
            isChanges = true;
        }
        if (i_FileStatus.getCommitDelta().getModifiedFiles().size() > 0) {
            System.out.println("Modified Files:" + System.lineSeparator());
            printCollection(i_FileStatus.getCommitDelta().getModifiedFiles());
            isChanges = true;
        }
        if (i_FileStatus.getCommitDelta().getNewFiles().size() > 0) {
            System.out.println("New Files:" + System.lineSeparator());
            printCollection(i_FileStatus.getCommitDelta().getNewFiles());
            isChanges = true;
        }

        if (!isChanges) {
            System.out.println("There were no changes at all");
        }
    }

    private void printCollection(Iterable i_Collection) {
        i_Collection.forEach(data -> System.out.println(data.toString()));
    }

    private void printAllBranchesData(List<BranchInformation> allBranches) {
        for (BranchInformation brancInformation : allBranches) {
            System.out.println(brancInformation.toString());
        }
    }
//    private String getBranchNameForCheckOut(){
//        String branchName;
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("please enter a branch name");
//        branchName = scanner.nextLine();
//
//        while(!m_Engine.checkBranchNameIsExist(branchName)){
//            System.out.println("please enter a branch name that exist");
//            branchName = scanner.nextLine();
//        }
//        return branchName;
//    }

    //for delete and for checkout (need to be exist).
    private String getExistBranchName() {
        String branchName = "";
        Scanner scanner = new Scanner(System.in);

        while (!m_Engine.checkBranchNameIsExist(branchName)) {
            System.out.println("please enter an exist branch name:");
            branchName = scanner.nextLine();
        }
        return branchName;
    }

    //for add new branch
    private String getBranchNameFromUser() {
        String branchName;
        Scanner scanner = new Scanner(System.in);
        System.out.println("please enter a branch name");
        branchName = scanner.nextLine();

        while (m_Engine.checkBranchNameIsExist(branchName)) {
            System.out.println("please enter a branch name that doesn't exist");
            branchName = scanner.nextLine();
        }
        return branchName;
    }

    //--------------------------------------S methods create new repository-----------------------------


    //--------------------------------------E methods create new repository-----------------------------

    //--------------------------------------S methods create new Commit-----------------------------
    private String getCommitMessageFromUser() {
        String commitMessage;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter commit message");
        commitMessage = scanner.nextLine();
        return commitMessage;
    }
    //--------------------------------------E methods create new Commit-----------------------------
}

//Todo:
//2.take a look of the desgin of repository and magit.
//3.make m_MagitPath , m_Activeuser non static.
//4.change item typeHolder to be file and not Blob !! (folder content).
//5.think where and to handle all the exceptions.

//XML ........

//importans questions :
//2. commits that down need and how to go there ?
//3. XML explanations !?
//4.remmber this commit delta - think about it with noam ...
