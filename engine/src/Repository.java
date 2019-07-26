public class Repository {
    private final String m_Name;
    private final String m_Path;
    private Wc m_WorkingCopy ;
    private Magit m_Magit ;

    public Repository(String m_Name, String m_Path) {
        this.m_Name = m_Name;
        this.m_Path = m_Path;
    }
}
