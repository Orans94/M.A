import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LastState {
    public LastCommitInformation getLastCommitInformation() {
        return m_lastCommitInformation;
    }

    LastCommitInformation m_lastCommitInformation = new LastCommitInformation();

    private Path m_RootPath;

    //private Commit m_CurrentCommitLoaded;
    public LastState(Path i_RootPath) {
        m_RootPath = i_RootPath;
    }

    public void addNodeItem(Path i_FilePath ,String i_NodeSha1, Node i_NewNodeToadd) {
        m_lastCommitInformation.getSha1FileToNode().put(i_NodeSha1, i_NewNodeToadd);
        m_lastCommitInformation.getFilePathToSha1().put(i_FilePath,i_NodeSha1);
    }

    public void clearAll() {
        m_lastCommitInformation.clear();
    }

//Todo - understatnd what exactly meant here .
    public void showAllFilesFromActiveBranch() {
        for (Map.Entry<String, Node> entry : m_lastCommitInformation.getSha1FileToNode().entrySet()) {
            Node currentNode = entry.getValue();
            if(currentNode.getClass().getSimpleName()=="Folder"){
                System.out.println(currentNode.toString());
            }
        }
    }

    public void uploadAllNodesFromNewRepositoryLastState(String fileSha1 , Path i_repositoryPath) throws IOException {
        String zipContext = FileUtils.getStringFromFolderZip(fileSha1);
        String[] lines = zipContext.split(System.lineSeparator());
        for (String line : lines) {
            String fileType = getTypeFromLine(line);
            if(fileType.equals("Blob")){
                String blobSha1 = getSha1FromLine(line);
                String blobName = getNameFromLine(line);
                blobName = blobName.substring(0,blobName.length()-4);
                String blobContent = FileUtils.getStringFromFolderZip(blobSha1,blobName);
                addNodeItem(i_repositoryPath.resolve(getNameFromLine(line)), blobSha1, new Blob(blobContent));

            }
            else{
                //create the directory in the current path and deep into the directory
                String folderSha1 = getSha1FromLine(line);
                String fodlerContent = FileUtils.getStringFromFolderZip(folderSha1);
                addNodeItem(i_repositoryPath.resolve(getNameFromLine(line)), folderSha1, new Folder(fodlerContent));
                uploadAllNodesFromNewRepositoryLastState(folderSha1,i_repositoryPath.resolve(getNameFromLine(line)));
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

    public void addRootFolerToNodes(String rootFolderSha1, Path i_repositoryPath) throws IOException {
        String rootFolderContent = FileUtils.getStringFromFolderZip(rootFolderSha1);
        addNodeItem(i_repositoryPath, rootFolderSha1, new Folder(rootFolderContent ));

    }
}
