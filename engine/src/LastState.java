import java.nio.file.Path;
import java.util.*;

public class Wc {
    private Map<String, Node> m_LastCommitNodes = new HashMap<>();    //hold all the nodes of the last commit.
    private String m_RootPath;
    //private Commit m_CurrentCommitLoaded;

    public Wc(String i_RootPath) {
        m_RootPath = i_RootPath;
    }

    public void addNodeItem(String i_NodeSha1, Node i_NewNodeToadd) {
        m_LastCommitNodes.put
                (i_NodeSha1, i_NewNodeToadd);
    }
}
