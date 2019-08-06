public class CommitInformation {
    String m_CommitSha1;
    String m_CommitMessage;
    String m_CreateingDate;
    String m_UserNameCreateor;

    public CommitInformation(String i_CommitSha1, String i_CommitMessage, String i_CreateingDate, String i_UserNameCreateor) {
        this.m_CommitSha1 = i_CommitSha1;
        this.m_CommitMessage = i_CommitMessage;
        this.m_CreateingDate = i_CreateingDate;
        this.m_UserNameCreateor = i_UserNameCreateor;
    }

    @Override
    public String toString() {
        return
                "CommitSha1='" + m_CommitSha1 + '\'' +
                        ", CommitMessage='" + m_CommitMessage +
                        ", CreateingDate='" + m_CreateingDate +
                        ", UserNameCreateor='" + m_UserNameCreateor;
    }
}
