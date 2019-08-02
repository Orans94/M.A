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
            currentNode.toString();
        }
    }
}
