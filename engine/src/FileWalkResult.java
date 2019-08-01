import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileWalkResult {
    CommitDelta m_commitDelta = new CommitDelta();
    LastCommitInformation m_FilesToZip = new LastCommitInformation();
    LastCommitInformation m_UnchangedFiles = new LastCommitInformation();
}
