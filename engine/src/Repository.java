import org.apache.commons.codec.digest.DigestUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Repository {
    private String m_Name;
    private Path m_Path;
    private LastState m_LastState;
    private Magit m_Magit;
    private static List<String> m_ChildrenInformation;

    public Repository(String i_Name, Path i_RepoPath,boolean isNewRepo) throws IOException {
        m_Path = i_RepoPath;
        m_Name = i_Name;
        m_LastState = new LastState(m_Path);
        m_Magit = new Magit(getMatgitPath(),isNewRepo);
        m_ChildrenInformation = new LinkedList<>();
    }


    //---------------------------------------S Properties-----------------------//
    public LastState getLastState() {
        return m_LastState;
    }

    public String getName() {
        return m_Name;
    }

    public Path getPath() {
        return m_Path;
    }

    public Magit getMagit() {
        return m_Magit;
    }
    //--------------------E properties-----------------------


    public Path getMatgitPath() {
        return m_Path.resolve(".magit");
    }

    //-----------------------------------S make commit--------------------------------//

    public void createCommit(String i_message) throws IOException {
        //this two lines find
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
        i_fileWalkResult.getCommitDelta().getNewFiles().add(i_FilePath);
    }

    private void pathExistCheckIfModify(FileWalkResult i_fileWalkResult, Path i_FilePath, String i_NodeSha1, Node i_NewNode) {
        if (m_LastState.m_lastCommitInformation.getFilePathToSha1().get(i_FilePath).equals(i_NodeSha1)) {
            i_fileWalkResult.getUnchangedFiles().getFilePathToSha1().put(i_FilePath, i_NodeSha1);
            i_fileWalkResult.getUnchangedFiles().getSha1FileToNode().put(i_NodeSha1, i_NewNode);
        } else {
            i_fileWalkResult.getFilesToZip().getFilePathToSha1().put(i_FilePath, i_NodeSha1);
            i_fileWalkResult.getFilesToZip().getSha1FileToNode().put(i_NodeSha1, i_NewNode);
            i_fileWalkResult.getCommitDelta().getModifiedFiles().add(i_FilePath);
        }
    }

    private void updateCommitsAndBranches(String i_RootFolderSha1, String i_message, CommitDelta i_CommitDeltaFromWalkTree) throws IOException {
        //1.create new commit and point the head to the new commit
        m_Magit.handleNewCommit(i_RootFolderSha1, i_message, i_CommitDeltaFromWalkTree);
    }

    //Think if to move this to file.utils??
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

        walkTreeResult.getCommitDelta().setDeletedFiles(allPathsFromLastCommit);
    }

    //-----------------------------------E make commit--------------------------------//

    //-----------------------------------S make New Branch --------------------------------//
    public void createNewBranch(String i_branchName) throws IOException {
        m_Magit.createNewBranch(i_branchName);
    }

    public void deleteExistingBranch(String i_BranchToDeleteName) throws ActiveBranchDeleteExeption, IOException {

        if (i_BranchToDeleteName.equals(m_Magit.getHead().getActiveBranch().getBracnhName()))
            throw new ActiveBranchDeleteExeption();

        m_Magit.deleteExistingBranch(i_BranchToDeleteName);
    }

    //---------------------------------S Check Out--------------------------------

    public boolean isWcClean() throws IOException{
        FileWalkResult wc = getStatus();
        CommitDelta delta = wc.getCommitDelta();
        return delta.getModifiedFiles().size() + delta.getNewFiles().size() +
                delta.getDeletedFiles().size() == 0;
    }

    public void checkOut(String branchNametoCheckOut) throws IOException {
        //1.delete all the current wc
        deleteCurrentWc();

        //2.find the branch in system and take the commit that it points to.
        Branch branchToCheckOut = m_Magit.getBranches().get(branchNametoCheckOut);
        String commitSha1 = branchToCheckOut.getSha1LastCommit();

        String rootFolderSha1 = m_Magit.getCommits().get(commitSha1).getRootFolderSha1();

        //3.update all the data structurs that holds last commit data
        updateSystemData(branchNametoCheckOut, rootFolderSha1);

        //4.update the wc - get in from root folder in
        updateWcFromCommit(m_Path, rootFolderSha1);

        FileUtils.changeFileContent(Magit.getMagitDir().resolve("branches").resolve("HEAD.txt"), branchNametoCheckOut);
    }

    private void updateSystemData(String branchNametoCheckOut, String i_RootSha1) throws IOException {
        m_LastState.clearAll();
        Branch activeBranch = m_Magit.getBranches().get(branchNametoCheckOut);
        m_LastState.addNodeItem(m_Path, i_RootSha1, new Folder(FileUtils.getStringFromFolderZip(i_RootSha1)));
        m_Magit.getHead().setActiveBranch(activeBranch);
    }

    private void updateWcFromCommit(Path path, String currentSha1) throws IOException {
        String zipContext = FileUtils.getStringFromFolderZip(currentSha1);
        String[] lines = zipContext.split(System.lineSeparator());
        for (String line : lines) {
            String fileType = getTypeFromLine(line);
            if(fileType.equals("Blob")){
                //do unzip of the blob to workingCopy
                //void unzip(final String zipFilePath, final String unzipLocation) throws IOException, IOException {
                String blobSha1 = getSha1FromItemString(line);
                FileUtils.unzip(Magit.getObjectsPath().resolve(blobSha1+".zip").toString(),path.toString());
                String blobContent = new String(Files.readAllBytes(path.resolve(getNameFromLine(line))));
                m_LastState.addNodeItem(path.resolve(getNameFromLine(line)), blobSha1, new Blob(blobContent));

            }
            else{
                //create the directory in the current path and deep into the directory
                String dirName = getNameFromLine(line);
                Files.createDirectory(path.resolve(dirName));
                updateWcFromCommit(path.resolve(dirName),getSha1FromLine(line));
                String folderSha1 = getSha1FromItemString(line);
                String fodlerContent = FileUtils.getStringFromFolderZip(folderSha1);
                m_LastState.addNodeItem(path.resolve(getNameFromLine(line)), folderSha1, new Folder(fodlerContent));
            }
        }
    }

    private String getNameFromLine(String i_OneLine) {
        String[]members = i_OneLine.split(",");
        return members[0];
    }

    private String getSha1FromLine(String i_OneLine) {
        String[]members = i_OneLine.split(",");
        return members[1];
    }

    private String getTypeFromLine(String i_oneLine) {
        String[]members = i_oneLine.split(",");
        return members[2];
    }


    public void deleteCurrentWc() throws IOException {
        FileVisitor<Path> fv = new FileVisitor<Path>() {
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
                Files.delete(file);

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (dir.getFileName().toString().equals(m_Name))
                    return FileVisitResult.CONTINUE;
                else {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            }

        };
        Path thePath = Paths.get("C:\\Users\\ziv3r\\Desktop\\Check");
        Files.walkFileTree(m_Path, fv);
    }

    public FileWalkResult getStatus() throws IOException {

        FileWalkResult walkTreeResult = FileWalkTree();
        FindAllDeletedFiles(walkTreeResult, m_LastState.m_lastCommitInformation);
        return walkTreeResult;
    }

    public void uploadOrswitchRepositories(String i_RepositoryPath) throws IOException {
        //0. update repository details
        //take the name of the repository from repository naem
        Path repoName = m_Path.resolve(".magit").resolve("repositoryName.txt");
        this.m_Name = FileUtils.readFileAndReturnString(repoName);

        //1.delete all the data from system
        m_Magit.initalize();
        m_LastState.clearAll();

        //2.upload commits from new repository
        //3.upload all branches from new repository
        m_Magit.updateCommitsAndBranchesFromNewRepository(i_RepositoryPath);

        //4.upload all nodes of last commit(pull from head -> branch -> last commit sha1)
        m_LastState.uploadAllNodesFromNewRepositoryLastState(i_RepositoryPath);
    }

    //---------------------------------S Check Out--------------------------------

}


