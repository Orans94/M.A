import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

public class Folder extends Node{
    private Set<String> m_Children = new HashSet<String>();

    public Folder(String i_Name, String i_ShaOne, String i_LastModifier, String i_Content, SimpleDateFormat i_ModificationDate, Set<String> i_Children) {
        super(i_Name, i_ShaOne, i_LastModifier, i_ModificationDate);
        this.m_Children = i_Children;
    }
}


