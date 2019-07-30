import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Repository {
    private String m_Name;
    private String m_Path;
    private Wc m_WorkingCopy;
    private Magit m_Magit;
    private static List<String> m_ChildrenInformation;


    public Repository(String i_Name,String i_Path) throws IOException {
        m_Path = i_Path;
        m_Name = i_Name;
        m_WorkingCopy = new Wc(m_Path);
        String magitPath = i_Path.concat("\\.magit");
        m_Magit = new Magit(magitPath);
        m_ChildrenInformation = new LinkedList<>();
    }

    public Magit getMagit() {
        return m_Magit;
    }


    ////////////////////////S --------------new commit-------------------/////////////////////
    public void createCommit(String i_message) throws IOException {
        FileVisitor<Path> fileVisitor = new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.getFileName().toString().equals(".magit")) {
                    return FileVisitResult.SKIP_SUBTREE;
                } else {
                    return FileVisitResult.CONTINUE;
                }
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                //System.out.println(file.getFileName());   //for testing
                //1. create new blob and make sha-1
                String blobContent = new String(Files.readAllBytes(file));
                Blob blob = new Blob(blobContent);
                String blobSha1 = blob.SHA1();

                //2. Zip the file and put in objects
                blob.Zip(blobSha1, file);

                //3. push Blob to m_Nodes
                m_WorkingCopy.addNodeItem(blobSha1, blob);

                //4. append my info to m_ChildrenInformation
                m_ChildrenInformation.add(blob.generateStringInformation(blobSha1, file.toFile().getName()));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException { //TODO handle exception
                //1. num Children <- Check how many sub items i have.
                int numOfChildren = getNumOfChildren(dir);
                if(dir.equals(Paths.get(m_Path)))
                    numOfChildren--;
                //2. add the item information of my children to my content
                String folderContent = generateFolderContent(numOfChildren);

                //3.create a folder object from the details and put it in m_Nodes
                Folder folder = new Folder(folderContent);

                //4.add content details to item list of folder
                folder.createItemListFromContent();

                //5.update content before making sha1 (remove modifier name and date)
                String contentToSHA1 = folder.CutContentForMakingSha1();
                String folderSHA1 = addFolderToMap(folder, contentToSHA1);

                //5. update children information
                updateChildrenInformation(dir, numOfChildren, folder, folderSHA1);

                //6. zip the folder and save me in objects dir
                folder.Zip(folderSHA1, dir);

                return FileVisitResult.CONTINUE;
            }
        };
        Path repoPath = Paths.get(Magit.getMagitDir().toFile().getParent());
        Files.walkFileTree(repoPath, fileVisitor);
        String rootFolderItemString = m_ChildrenInformation.get(0);
        String rootFolderSha1 = getSha1FromItemString(rootFolderItemString);
        m_ChildrenInformation.clear();

        updateCommitsAndBranches(rootFolderSha1,i_message);

    }

    private void updateCommitsAndBranches(String i_RootFolderSha1,String i_message) throws IOException {

        //1.create new commit and point the head to the new commit
        m_Magit.handleNewCommit(i_RootFolderSha1,i_message);
    }

    private String getSha1FromItemString(String i_ItemString) {
        String[] members = i_ItemString.split(",");
        return members[1];
    }

    private void updateChildrenInformation(Path i_Dir, int i_NumOfChildren, Folder i_Folder, String i_FolderSHA1) {
        m_ChildrenInformation = m_ChildrenInformation.stream()
                .limit(m_ChildrenInformation.size() - i_NumOfChildren)
                .collect(Collectors.toList());

        //make an item string from my content and add it to m_ChildrenInformation
        String itemString = i_Folder.generateStringInformation(i_FolderSHA1, i_Dir.toFile().getName());
        m_ChildrenInformation.add(itemString);
    }

    private String addFolderToMap(Folder i_Folder, String i_FolderContentToSHA1) {
        String folderSHA1 = DigestUtils.sha1Hex(i_FolderContentToSHA1);
        m_WorkingCopy.addNodeItem(folderSHA1, i_Folder);
        return folderSHA1;
    }

    //generate the folder content from the m_Children List that hold the children data
    private String generateFolderContent(int i_NumOfChildren) {
        List<String> folderContentList =
                m_ChildrenInformation.stream()
                        .skip(m_ChildrenInformation.size() - i_NumOfChildren)
                        .collect(Collectors.toList());

        String folderContent = "";
        for (String s : folderContentList) {
            folderContent = folderContent.concat(s);
        }

        //delete last line from the string
        folderContent = folderContent.substring(0, folderContent.length() - 2);
        return folderContent;
    }

    private int getNumOfChildren(Path i_Path) throws IOException {
        return (int) Files.walk(i_Path, 1).count() - 1;
    }
}
