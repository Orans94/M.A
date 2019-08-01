import java.nio.file.Path;
import java.util.*;

public class LastState {
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
}
