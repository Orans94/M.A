import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class CommitDelta {
    List<Path> m_ListNewFiles = new LinkedList<>();
    List<Path> m_ListModifiedFiles = new LinkedList<>();
    List<Path> m_ListDeletedFiles = new LinkedList<>();

}
