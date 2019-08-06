import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LastState {

    private Path m_RootPath;


    private LastCommitInformation m_lastCommitInformation = new LastCommitInformation();

    //---------------------------------------properties---------------------------------------//
    public LastCommitInformation getLastCommitInformation() {
        return m_lastCommitInformation;
    }

    public void setLastCommitInformation(LastCommitInformation m_lastCommitInformation) {
        this.m_lastCommitInformation = m_lastCommitInformation;
    }

    //private Commit m_CurrentCommitLoaded;
    public LastState(Path i_RootPath) {
        m_RootPath = i_RootPath;
    }
    //---------------------------------------properties---------------------------------------//


    public void addRootFolerToNodes(String rootFolderSha1, Path i_repositoryPath) throws IOException {
        clearAll();
        String rootFolderContent = FileUtils.getStringFromFolderZip(rootFolderSha1, "");
        addNodeItem(i_repositoryPath, rootFolderSha1, new Folder(rootFolderContent));
    }

    //important method that put in system all the nodes of the last commit
    public void uploadAllNodesFromNewRepositoryLastState(String rootFolderSha1, Path i_repositoryPath) throws IOException {
        String zipContext = FileUtils.getStringFromFolderZip(rootFolderSha1, "");
        String[] lines = zipContext.split(System.lineSeparator());
        for (String line : lines) {
            String fileType = getTypeFromLine(line);
            if (fileType.equals("Blob")) {
                String blobSha1 = getSha1FromLine(line);
                String blobName = getNameFromLine(line);
                blobName = blobName.substring(0, blobName.length() - 4);
                String blobContent = FileUtils.getStringFromFolderZip(blobSha1, blobName);
                addNodeItem(i_repositoryPath.resolve(getNameFromLine(line)), blobSha1, new Blob(blobContent));
            } else {
                //create the directory in the current path and deep into the directory
                String folderSha1 = getSha1FromLine(line);
                String fodlerContent = FileUtils.getStringFromFolderZip(folderSha1, "");
                addNodeItem(i_repositoryPath.resolve(getNameFromLine(line)), folderSha1, new Folder(fodlerContent));
                uploadAllNodesFromNewRepositoryLastState(folderSha1, i_repositoryPath.resolve(getNameFromLine(line)));
            }
        }
    }

    public void addNodeItem(Path i_FilePath, String i_NodeSha1, Node i_NewNodeToadd) {
        m_lastCommitInformation.getSha1FileToNode().put(i_NodeSha1, i_NewNodeToadd);
        m_lastCommitInformation.getFilePathToSha1().put(i_FilePath, i_NodeSha1);
    }

    public void clearAll() {
        m_lastCommitInformation.clear();
    }

    //Todo how to print rootfolder Data?
    public List<Node> getAllFilesFromActiveBranch() {
        List<Node> nodesFromActiveBranch = new LinkedList<>();
        for (Map.Entry<String, Node> entry : m_lastCommitInformation.getSha1FileToNode().entrySet()) {
            Node currentNode = entry.getValue();
            if (currentNode.getClass().getSimpleName() == "Folder") {
                nodesFromActiveBranch.add(currentNode);
            }
        }
        return nodesFromActiveBranch;
    }

    //find better place for it ....
    private String getNameFromLine(String i_OneLine) {
        String[] members = i_OneLine.split(",");
        return members[0];
    }

    private String getSha1FromLine(String i_OneLine) {
        String[] members = i_OneLine.split(",");
        return members[1];
    }

    private String getTypeFromLine(String i_oneLine) {
        String[] members = i_oneLine.split(",");
        return members[2];
    }
    //find better place for it ....


}
