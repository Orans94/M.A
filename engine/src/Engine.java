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

    public static String getActiveUser() {
        return s_ActiveUser;
    }

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

    public void createNewCommit(String i_Message) throws IOException {
        m_Repository.createCommit(i_Message);
    }


    public boolean isDirectoryNameValid(String repositoryName) {
        return true;
    }
}


