import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.*;
import java.nio.file.Files;
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

    public void start() throws IOException {
        m_Menu.printMenu();
        while (true) {
            int choice = m_Menu.getUserChoice();
            handeUserChoice(choice);
        }
    }

    private void handeUserChoice(int i_UserChoice) throws IOException {
        switch (i_UserChoice) {
            //Todo:Send the variables to the function.. now its just for checking.
            case 1:
                // String Path = getRepositoryPath();
                // String RepositoryName = getRepositoryName();
                m_Engine.createNewRepository("C:\\Users\\ziv3r\\Desktop\\gitPractiseProject", "game");
                break;

            case 2:
                String commitMessage = getCommitMessageFromUser();
                m_Engine.createNewCommit("First Commit");
                break;
        }
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


//making sha-1
/*
        String fileName = "C:\\Users\\ziv3r\\Desktop\\summer semester\\Game\\.magit\\objects\\a.txt";
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        String text = new String();
        String line;

        while((line = br.readLine()) != null){
            text.concat(line);
        }

        String shaone = DigestUtils.sha1Hex(text);

        System.out.println(shaone);

 */


