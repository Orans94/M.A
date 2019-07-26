import java.text.SimpleDateFormat;

abstract public class Node {
    private final String m_Name;
    private final String m_ShaOne;
    private final String m_lastModifier;
    private final SimpleDateFormat m_ModificationDate;

    public Node(String i_Name, String i_ShaOne, String i_LastModifier, SimpleDateFormat i_ModificationDate) {
        m_Name = i_Name;
        m_ShaOne = i_ShaOne;
        m_lastModifier = i_LastModifier;
        m_ModificationDate = i_ModificationDate;
    }
}
