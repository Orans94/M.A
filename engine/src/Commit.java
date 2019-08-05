import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

public class Commit<pubic> {
    private String m_RootFolderSha1;
    private String m_Parent;
    private String m_Message;
    private String m_CommitAuthor;
    private String  m_CommitDate;

    //Todo - remmber this commit delta - think about it with noam ...
    //private CommitDelta m_CommitDelta;

    public Commit(String i_RootFolderSha1, String i_ParentSha1, String i_Message, String i_Date, String i_UserName) {
        m_RootFolderSha1 = i_RootFolderSha1;
        m_Parent = i_ParentSha1;
        m_Message = i_Message;
        m_CommitDate = i_Date;
        m_CommitAuthor = i_UserName;
    }
//CommitDelta i_CommitDelta)
    public Commit(String i_RootFolderSha1, String i_ParentSha1, String i_Message){
        m_RootFolderSha1 = i_RootFolderSha1;
        m_Parent = i_ParentSha1;
        m_Message = i_Message;
        m_CommitAuthor = Engine.getActiveUser();
        m_CommitDate = DateUtils.dateToString(new Date());
        //m_CommitDelta = i_CommitDelta;
    }

    public String getRootFolderSha1() {
        return m_RootFolderSha1;
    }

    @Override
    public String toString() {
        return
                "" + m_RootFolderSha1 + ','
                        + m_Message + ','
                        + m_CommitDate.toString() + ','
                        + m_CommitAuthor;
    }

    public String generateSha1() {
        String sha1CommitContent = StringUtilities.makeSha1Content(this.toString());
        return DigestUtils.sha1Hex(sha1CommitContent);
    }

    public void Zip(String i_CommitSha1) throws IOException {
        Path objectsPath = Magit.getMagitDir().resolve("objects");
        FileUtils.createFileZipAndDelete(objectsPath, i_CommitSha1, this.toString());
    }

    public String getMessage() {
        return m_Message;
    }

    public String getParent() {
        return m_Parent;
    }
}