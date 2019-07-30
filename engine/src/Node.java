import java.io.*;
import java.nio.file.Path;
import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;

abstract public class Node {
    protected String m_Content = "";

    public Node(String i_Content) {
        m_Content = i_Content;
    }

    protected String Commit(Path i_Path) throws IOException {
        String Sha1String;
        //this method is returning the sha-1 of the Node's content
        // it also makes a zip of the file, put it in objects folder and the name of it will be the sha1
        //1. SHA-1 the file.
        Sha1String = SHA1();
        //2. ZIP the file.
        Zip(Sha1String, i_Path);
        //3. save it in objects.
        return Sha1String;
    }

    protected String SHA1() {
        return DigestUtils.sha1Hex(m_Content);
    }

    protected void Zip(String i_SHA1FileName, Path i_PathOfTheFile) throws IOException {
        FileUtils.Zip(i_SHA1FileName, i_PathOfTheFile);
    }

    public String generateStringInformation(String i_Sha1, String i_FileName) {
        return "" + i_FileName + "," +
                i_Sha1 + "," +
                this.getClass().getSimpleName() + "," +
                Engine.getActiveUser() + "," +
                DateUtils.FormatToString(new Date()) + System.lineSeparator();
    }
}