public class Head {
    private Branch m_ActiveBranch ;


    public Head(Branch i_ActiveBranch){
        m_ActiveBranch = i_ActiveBranch;
    }

    public Branch getActiveBranch() {
        return m_ActiveBranch;
    }

    public void setActiveBranch(Branch m_ActiveBranch) {
        this.m_ActiveBranch = m_ActiveBranch;
    }

}
