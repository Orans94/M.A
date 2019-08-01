import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileWalkResult {
    private CommitDelta m_commitDelta = new CommitDelta();
    private LastCommitInformation m_FilesToZip = new LastCommitInformation();
    private LastCommitInformation m_UnchangedFiles = new LastCommitInformation();

    public CommitDelta getCommitDelta() {
        return m_commitDelta;
    }

    public LastCommitInformation getFilesToZip() {
        return m_FilesToZip;
    }

    public LastCommitInformation getUnchangedFiles() {
        return m_UnchangedFiles;
    }
}
