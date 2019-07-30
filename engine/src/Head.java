public class Head {
    private Branch m_ActiveBranch ;

    public Head(Branch i_ActiveBranch){
        m_ActiveBranch = i_ActiveBranch;
    }


    public Branch getActiveBranch() {
        return m_ActiveBranch;
    }

}
