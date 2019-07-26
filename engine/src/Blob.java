import java.text.SimpleDateFormat;

public class Blob extends Node {
    private final String m_Content;

    public Blob(String i_Name, String i_ShaOne, String i_LastModifier, SimpleDateFormat i_ModificationDate, String i_Content) {
        super(i_Name, i_ShaOne, i_LastModifier, i_ModificationDate);
        this.m_Content = i_Content;
    }
}
