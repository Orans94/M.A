import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class CommitDelta {
    private List<Path> m_ListNewFiles = new LinkedList<>();
    private List<Path> m_ListModifiedFiles = new LinkedList<>();
    private List<Path> m_ListDeletedFiles = new LinkedList<>();

    public List<Path> getNewFiles(){ return m_ListNewFiles; }
    public List<Path> getModifiedFiles(){ return m_ListModifiedFiles; }
    public List<Path> getDeletedFiles(){ return m_ListDeletedFiles; }

    public void setDeletedFiles(List<Path> p) { m_ListDeletedFiles = p; }
}
