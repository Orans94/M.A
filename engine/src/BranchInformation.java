public class BranchInformation {
    boolean m_isACtiveBranch = false;
    String m_BracnhName;
    String m_Sha1LastCommit;
    String m_CommitMessage;

    @Override
    public String toString() {
        return
                m_isACtiveBranch == true ? "Active Branch" : "" +
                        ", BracnhName='" + m_BracnhName +
                        ", m_Sha1LastCommit='" + m_Sha1LastCommit +
                        ", m_CommitMessage='" + m_CommitMessage + System.lineSeparator();
    }
}
