import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

public class Commit<pubic> {
    private String m_RootFolderSha1;
    private String m_Parent;
    private String m_Message;
    private String m_CommitAuthor;
    private Date m_CommitDate;
    private CommitDelta m_CommitDelta;

    public Commit(String i_RootFolderSha1, String i_Parent, String i_Message,CommitDelta i_CommitDelta) {
        m_RootFolderSha1 = i_RootFolderSha1;
        m_Parent = i_Parent;
        m_Message = i_Message;
        m_CommitDate = new Date();
        m_CommitAuthor = Engine.getActiveUser();
        m_CommitDelta = i_CommitDelta;
    }

    @Override
    public String toString() {
        return
                "" + m_RootFolderSha1+ ','
                        + m_Parent + ','
                        + m_Message + ','
                        + m_CommitDate.toString() + ','
                        + m_CommitAuthor ;
                        //Todo add all the commit delta information to toString.
                        //  + ', + m_CommitDelta.
    }

    public String generateSha1() {
        String sha1CommitContent = StringUtilities.makeSha1Content(this.toString());
        return DigestUtils.sha1Hex(sha1CommitContent);
    }
    public void Zip(String i_CommitSha1) throws IOException {
        Path objectsPath = Magit.getMagitDir().resolve("objects");
        FileUtils.createFileZipAndDelete(objectsPath,i_CommitSha1,this.toString());
    }
}