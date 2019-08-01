import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

public class Repository {
    private String m_Name;
    private Path m_Path;
    private LastState m_LastState;
    private Magit m_Magit;
    private static List<String> m_ChildrenInformation;

    public Repository(String i_Name, Path i_RepoPath) throws IOException {
        m_Path = i_RepoPath;
        m_Name = i_Name;
        m_LastState = new LastState(m_Path);
        m_Magit = new Magit(getMatgitPath());
        m_ChildrenInformation = new LinkedList<>();
    }

    public Path getMatgitPath() {
        return m_Path.resolve(".magit");
    }

    //--------------------S properties-----------------------
    public Magit getMagit() {
        return m_Magit;
    }
    //--------------------E properties-----------------------

    //-----------------------------------S make commit--------------------------------//

    public void createCommit(String i_message) throws IOException {
        FileWalkResult walkTreeResult = FileWalkTree();
        FindAllDeletedFiles(walkTreeResult, m_LastState.m_lastCommitInformation);
        //there were no changes
        if (walkTreeResult.getUnchangedFiles().getSha1FileToNode().size() ==
                m_LastState.m_lastCommitInformation.getSha1FileToNode().size()
                && walkTreeResult.getFilesToZip().getSha1FileToNode().size() == 0)
            return;
            //there is at least one file that changed
        else {
            LastCommitInformation mapsToZip = walkTreeResult.getFilesToZip();
            LastCommitInformation unchangedFiles = walkTreeResult.getUnchangedFiles();
            ZipAllNewFiles(mapsToZip);
            LastCommitInformation combinedMaps = mergeMaps(mapsToZip, unchangedFiles);
            m_LastState.m_lastCommitInformation = combinedMaps;
            String rootFolderItemString = m_ChildrenInformation.get(0);
            String rootFolderSha1 = getSha1FromItemString(rootFolderItemString);
            updateCommitsAndBranches(rootFolderSha1, i_message, walkTreeResult.getCommitDelta());
        }
        m_ChildrenInformation.clear();
    }

    private FileWalkResult FileWalkTree() throws IOException {

        FileWalkResult fileWalkResult = new FileWalkResult();

        FileVisitor<Path> fileVisitor = new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attסrs) throws IOException {
                if (dir.getFileName().toString().equals(".magit")) {
                    return FileVisitResult.SKIP_SUBTREE;
                } else {
                    return FileVisitResult.CONTINUE;
                }
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                //1. create new blob and make sha-1
                String blobContent = new String(Files.readAllBytes(file));
                Blob blob = new Blob(blobContent);
                String blobSha1 = blob.SHA1();

                //1.check if the file path exist
                //  1.if it is : check the sha1 - equal= not chagne. not equal-modifies.
                //  2.if it isn't : means that it is a new file .
                if (!m_LastState.m_lastCommitInformation.getFilePathToSha1().containsKey(file)) {
                    PathDoesnotExist(fileWalkResult, file, blobSha1, blob);
                } else {
                    pathExistCheckIfModify(fileWalkResult, file, blobSha1, blob);
                }
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
                if (dir.equals(m_Path))
                    numOfChildren--;

                //2. add the item information of my children to my content
                String folderContent = generateFolderContent(numOfChildren);

                //3.create a folder object
                Folder folder = new Folder(folderContent);

                //4.add content details to item list of folder
                folder.createItemListFromContent();

                //5.update content before making sha1 (remove modifier name and date)
                String contentToSHA1 = folder.CutContentForMakingSha1();
                String folderSHA1 = DigestUtils.sha1Hex(contentToSHA1);
                //addFolderToMap(dir, folder, contentToSHA1); TODO check if need!!

                //5. update children information
                updateChildrenInformation(dir, numOfChildren, folder, folderSHA1);
                if (!m_LastState.m_lastCommitInformation.getFilePathToSha1().containsKey(dir)) {
                    PathDoesnotExist(fileWalkResult, dir, folderSHA1, folder);
                } else {
                    pathExistCheckIfModify(fileWalkResult, dir, folderSHA1, folder);
                }

                return FileVisitResult.CONTINUE;
            }
        };

        Files.walkFileTree(m_Path, fileVisitor);

        return fileWalkResult;
    }

    //------------Helper function for walking tree-----------------------------//
    private int getNumOfChildren(Path i_Path) throws IOException {
        return (int) Files.walk(i_Path, 1).count() - 1;
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

    //Todo - check what is the  purpose of this function
    private String addFolderToMap(Path i_FolderPath, Folder i_Folder, String i_FolderContentToSHA1) {
        String folderSHA1 = DigestUtils.sha1Hex(i_FolderContentToSHA1);
        //m_LastState.addNodeItem(i_FolderPath, folderSHA1, i_Folder);
        return folderSHA1;
    }

    private void updateChildrenInformation(Path i_Dir, int i_NumOfChildren, Folder i_Folder, String i_FolderSHA1) {
        m_ChildrenInformation = m_ChildrenInformation.stream()
                .limit(m_ChildrenInformation.size() - i_NumOfChildren)
                .collect(Collectors.toList());

        //make an item string from my content and add it to m_ChildrenInformation
        String itemString = i_Folder.generateStringInformation(i_FolderSHA1, i_Dir.toFile().getName());
        m_ChildrenInformation.add(itemString);
    }

    private String getSha1FromItemString(String i_ItemString) {
        String[] members = i_ItemString.split(",");
        return members[1];
    }

    private void PathDoesnotExist(FileWalkResult i_fileWalkResult, Path i_FilePath, String i_NodeSha1, Node i_NewNode) {
        i_fileWalkResult.getFilesToZip().getFilePathToSha1().put(i_FilePath, i_NodeSha1);
        i_fileWalkResult.getFilesToZip().getSha1FileToNode().put(i_NodeSha1, i_NewNode);
        i_fileWalkResult.getCommitDelta().m_ListNewFiles.add(i_FilePath);
    }

    private void pathExistCheckIfModify(FileWalkResult i_fileWalkResult, Path i_FilePath, String i_NodeSha1, Node i_NewNode) {
        if (m_LastState.m_lastCommitInformation.getFilePathToSha1().get(i_FilePath).equals(i_NodeSha1)) {
            i_fileWalkResult.getUnchangedFiles().getFilePathToSha1().put(i_FilePath, i_NodeSha1);
            i_fileWalkResult.getUnchangedFiles().getSha1FileToNode().put(i_NodeSha1, i_NewNode);
        } else {
            i_fileWalkResult.getFilesToZip().getFilePathToSha1().put(i_FilePath, i_NodeSha1);
            i_fileWalkResult.getFilesToZip().getSha1FileToNode().put(i_NodeSha1, i_NewNode);
            i_fileWalkResult.getCommitDelta().m_ListModifiedFiles.add(i_FilePath);
        }
    }

    private void updateCommitsAndBranches(String i_RootFolderSha1, String i_message, CommitDelta i_CommitDeltaFromWalkTree) throws IOException {
        //1.create new commit and point the head to the new commit
        m_Magit.handleNewCommit(i_RootFolderSha1, i_message, i_CommitDeltaFromWalkTree);
    }

    private void ZipAllNewFiles(LastCommitInformation mapToZip) throws IOException {
        //walk throw all the hash map of the paths send the path and pass the
        //node from the other map also
        for (Map.Entry<Path, String> entry : mapToZip.getFilePathToSha1().entrySet()) {
            Path filtPath = entry.getKey();
            Node nodeInThePath = mapToZip.getSha1FileToNode().get(entry.getValue());
            nodeInThePath.Zip(entry.getValue(), filtPath);
        }
    }

    private LastCommitInformation mergeMaps(LastCommitInformation mapToZip, LastCommitInformation unchangedFiles) {
        mapToZip.getFilePathToSha1().putAll(unchangedFiles.getFilePathToSha1());
        mapToZip.getSha1FileToNode().putAll(unchangedFiles.getSha1FileToNode());
        return mapToZip;
    }

    private void FindAllDeletedFiles(FileWalkResult walkTreeResult, LastCommitInformation m_lastCommitInformation) {

        //1.concat all the paths in the walkFileTreeResult to one map (and to list of paths)
        Map<Path, String> unChangedFiles = walkTreeResult.getUnchangedFiles().getFilePathToSha1();
        Map<Path, String> FilesToZip = walkTreeResult.getFilesToZip().getFilePathToSha1();

        Map<Path, String> FilesToZipAndUnchanged = new HashMap<>();
        FilesToZipAndUnchanged.putAll(unChangedFiles);
        FilesToZipAndUnchanged.putAll(FilesToZip);

        //get a map of the files path of the last commit
        Map<Path, String> pathTosSha1LastCommit = m_lastCommitInformation.getFilePathToSha1();

        //List of all the paths from the last commit
        List<Path> allPathsFromLastCommit = pathTosSha1LastCommit.keySet().stream().collect(Collectors.toList());

        //List of all the paths from the walking tree
        List<Path> allPathsfromWalkTree = FilesToZipAndUnchanged.keySet().stream().collect(Collectors.toList());

        for (Path path : allPathsfromWalkTree) {
            if (allPathsFromLastCommit.contains(path)) {
                allPathsFromLastCommit.remove(path);
            }
        }

        walkTreeResult.getCommitDelta().m_ListDeletedFiles = allPathsFromLastCommit;
    }

    //-----------------------------------E make commit--------------------------------//

    //-----------------------------------S make New Branch --------------------------------//
    public void createNewBranch(String i_branchName) throws IOException {
        m_Magit.createNewBranch(i_branchName);
    }


    public void deleteExistingBranch(String i_BranchToDeleteName) throws ActiveBranchDeleteExeption, IOException {

        if(i_BranchToDeleteName.equals(m_Magit.getHead().getActiveBranch().getBracnhName()))
            throw new ActiveBranchDeleteExeption();

            m_Magit.deleteExistingBranch(i_BranchToDeleteName);


    }

}


