import java.nio.file.Path;
import java.util.*;

public class LastState {
    private Map<String, Node> m_LastCommitNodes = new HashMap<>();    //hold all the nodes of the last commit.
    private Path m_RootPath;
    //private Commit m_CurrentCommitLoaded;

    public LastState(Path i_RootPath) {
        m_RootPath = i_RootPath;
    }

    public void addNodeItem(String i_NodeSha1, Node i_NewNodeToadd) {
        m_LastCommitNodes.put
                (i_NodeSha1, i_NewNodeToadd);
    }
}
