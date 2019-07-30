public class Branch {
    private String m_BracnhName;
    private String m_Sha1LastCommit;

    public Branch(String i_BranchName, String i_LastCommitSha1) {
        m_BracnhName = i_BranchName;
        m_Sha1LastCommit = i_LastCommitSha1;
    }

    public String getBracnhName() {
        return m_BracnhName;
    }

    public String getSha1LastCommit() {
        return m_Sha1LastCommit;
    }

    public void setSha1LastCommit(String i_newCommitSha1){
        m_Sha1LastCommit = i_newCommitSha1;
    }
}
