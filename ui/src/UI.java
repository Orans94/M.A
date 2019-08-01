import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class UI {
    Engine m_Engine;
    Menu m_Menu;

    public UI() {
        m_Engine = new Engine();
        m_Menu = new Menu();
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
                m_Engine.createNewRepository("C:\\Users\\ziv3r\\Desktop\\gitPractiseProject", "game");
                break;

            case 2:
                String commitMessage = getCommitMessageFromUser();
                m_Engine.createNewCommit(commitMessage);
                break;

            case 3 :
                String newBranchName = getBranchNameFromUser();
                m_Engine.createNewBranch(newBranchName);
                break;
                //Todo talk with noam about handles exeptions
            case 4:
                boolean succedToDelete = false ;
                while(!succedToDelete){
                    try {
                        String branchToDeleteName = getBranchNameToDelete();
                        m_Engine.deleteExistingBranch(branchToDeleteName);
                        succedToDelete=true;
                    }catch(ActiveBranchDeleteExeption ex ){
                        System.out.println(ex.getMessage());
                    }
                }
                break;
            case 5:
                List<BranchInformation> allBranches = m_Engine.showAllBranches();
                printAllBranchesData(allBranches);
            case 6 :
                String branchNametoCheckOut = getBranchNameForCheckOut();
                m_Engine.checkOutBranch(branchNametoCheckOut);
        }
    }

    private void printAllBranchesData(List<BranchInformation> allBranches) {
        for(BranchInformation brancInformation : allBranches){
            System.out.println(brancInformation.toString());
        }
    }

    private String getBranchNameToDelete() {
        String branchNameToDelete;
        Scanner scanner = new Scanner(System.in);
        System.out.println("please enter a branch name to delete");

        branchNameToDelete = scanner.nextLine();

        while(!m_Engine.checkBranchNameIsExist(branchNameToDelete)){
            System.out.println("branch name doesn't exist, enter another name");
            branchNameToDelete = scanner.nextLine();
        }
        return branchNameToDelete;
    }

    private String getBranchNameForCheckOut(){
        String branchName;
        Scanner scanner = new Scanner(System.in);
        System.out.println("please enter a branch name");
        branchName = scanner.nextLine();

        while(!m_Engine.checkBranchNameIsExist(branchName)){
            System.out.println("please enter a branch name that exist");
            branchName = scanner.nextLine();
        }
        return branchName;
    }

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
        System.out.println("Please enter a legal path to create new repository");
        String repPath = scanner.nextLine();  // Read path input
        Path path = Paths.get(repPath);

        return path.toString();
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




