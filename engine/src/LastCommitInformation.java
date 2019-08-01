import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LastCommitInformation {
    private Map<String, Node> m_Sha1FileToNode = new HashMap<>();
    private Map<Path,String> m_FilePathToSha1 = new HashMap<>();

    public Map<String, Node> getSha1FileToNode() {
        return m_Sha1FileToNode;
    }

    public void setSha1FileToNode(Map<String, Node> m_Sha1FileToNode) {
        this.m_Sha1FileToNode = m_Sha1FileToNode;
    }

    public Map<Path, String> getFilePathToSha1() {
        return m_FilePathToSha1;
    }

    public void setFilePathToSha1(Map<Path, String> m_FilePathToSha1) {
        this.m_FilePathToSha1 = m_FilePathToSha1;
    }

}
