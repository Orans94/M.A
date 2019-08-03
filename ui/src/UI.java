import javafx.scene.Parent;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
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
            //Todo:Send the variables to the function.. now its just for checking.
            case 1:
                // String Path = getRepositoryPath();
                // String RepositoryName = getRepositoryName();
                m_Engine.createNewRepository("C:\\Users\\noamlevy\\Desktop\\דברים", "test");
                //m_Engine.createNewRepository(getRepositoryPath(), getRepositoryName());
                break;

            case 2:
                String commitMessage = getCommitMessageFromUser();
                m_Engine.createNewCommit(commitMessage);
                break;
            case 3:
                String newBranchName = getBranchNameFromUser(); // we have 2 methods with different condition
                m_Engine.createNewBranch(newBranchName);
                break;
            //Todo talk with noam about handles exeptions
            case 4:
                boolean succedToDelete = false;
                while (!succedToDelete) {
                    try {
                        String branchToDeleteName = getBranchName();
                        m_Engine.deleteExistingBranch(branchToDeleteName);
                        succedToDelete = true;
                    } catch (ActiveBranchDeleteExeption ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                break;
            case 5:
                List<BranchInformation> allBranches = m_Engine.showAllBranches();
                printAllBranchesData(allBranches);
                break;
            case 6:
                String branchNametoCheckOut = getBranchName();
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
            case 7:
                String newUserName = getUserName();
                m_Engine.changeActiveUserName(newUserName);
                break;
            case 8:
                m_Engine.showAllFilesPointsFromLastCommit();
                break;
            case 9:
                m_Engine.showActiveBranchHistory();
                break;
            case 10:
                //show the opennig chagnes between current working copy and last commit.
                FileWalkResult fileStatus = m_Engine.showStatus();
                String repositoryName = m_Engine.getRepository().getM_Name();
                String repoPath = m_Engine.getRepository().getM_Path().toString();
                printSystemStatus(fileStatus, repositoryName, repoPath);
                break;
        }
    }

    //Todo implement printing ....
    private void printSystemStatus(FileWalkResult i_FileStatus, String i_RepositoryName, String i_RepoPath) {
        System.out.println("Repository: " + i_RepositoryName);

        System.out.println("Deleted Files:" + System.lineSeparator());
        printCollection(i_FileStatus.getCommitDelta().getDeletedFiles());

        System.out.println(System.lineSeparator() + "Modified Files:" + System.lineSeparator());
        printCollection(i_FileStatus.getCommitDelta().getModifiedFiles());

        System.out.println(System.lineSeparator() + "New Files:" + System.lineSeparator());
        printCollection(i_FileStatus.getCommitDelta().getNewFiles());
    }

    private void printCollection(Iterable i_Collection) {
        i_Collection.forEach(data -> System.out.println(data.toString()));
    }

    private String getUserName() {
        String userName;
        Scanner scanner = new Scanner(System.in);
        System.out.println("please enter user name");
        userName = scanner.nextLine();
        return userName;
    }

    private void printAllBranchesData(List<BranchInformation> allBranches) {
        for (BranchInformation brancInformation : allBranches) {
            System.out.println(brancInformation.toString());
        }
    }

    private String getBranchName() {
        String branchName = "";
        Scanner scanner = new Scanner(System.in);


        while (!m_Engine.checkBranchNameIsExist(branchName)) {
            System.out.println("please enter a branch name:");
            branchName = scanner.nextLine();
        }

        return branchName;
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

    private String getBranchNameFromUser() {
        String branchName;
        Scanner scanner = new Scanner(System.in);
        System.out.println("please enter a branch name");
        branchName = scanner.nextLine();

        while(m_Engine.checkBranchNameIsExist(branchName)){
            System.out.println("please enter a branch name that doesn't exist");
            branchName = scanner.nextLine();
        }
        return branchName;
    }

    //--------------------------------------S methods create new repository-----------------------------

    private String getRepositoryName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter repository name");
        String repositoryName = scanner.nextLine();

        //TODO describe the prohibited symbols in folder name
        while (!m_Engine.isDirectoryNameValid(repositoryName)) {
            System.out.println("Invalid repository name, Please enter a legal repository name");
            repositoryName = scanner.nextLine();
        }

        return repositoryName;
    }

    private String getRepositoryPath() {
        Scanner scanner = new Scanner(System.in);  // Create a Scanner object
        String repPath = "";

        while (repPath.isEmpty()) {
            System.out.println("Please enter a legal path to create new repository");
            repPath = scanner.nextLine();  // Read path input
            try {
                Paths.get(repPath);
            } catch (InvalidPathException e) {
                repPath = "";
            }
        }

        return repPath;
    }

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
//6.does the head always point to the branch that on system ?
//7.it needs to be an option to change repository name(look mail quesion .. i didnt get into it ..)

// big two issues
//6.load new repository to the system using existings methods.

//XML ........

