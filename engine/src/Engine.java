import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Engine {
    private static String s_ActiveUser = "admin";
    private Repository m_Repository;

    public void createNewRepository(String i_Path, String i_RepositoryName) throws IOException {
        Path path = Paths.get(i_Path).resolve(i_RepositoryName);
        Path repoPath = path;
            try {
                Files.createDirectory(path);
                path = path.resolve(".magit");
                Files.createDirectory(path);
                Files.createDirectory(path.resolve("objects"));
                Files.createDirectory(path.resolve("branches"));
            } catch (IOException e) {
                //fail to create directory
                e.printStackTrace();
            }
            //to check if i need the path with the repo name or not ?!
            m_Repository = new Repository(i_RepositoryName,repoPath.toString());
        Files.createFile(repoPath.resolve("hello.txt"));
        Files.write(repoPath.resolve("hello.txt"), "hello world".getBytes());
        }

    public void createNewCommit(String  i_Message) throws IOException {
        m_Repository.createCommit(i_Message);
    }


    //Todo : implement this function
    public boolean isDirectoryNameValid(String repositoryName) {
        return true;
    }

    public static String getActiveUser() {
        return s_ActiveUser;
    }
}


