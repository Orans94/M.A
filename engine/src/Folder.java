import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class Folder extends Node {
    private List<Item> m_Items;

    public Folder(String i_Content) {
        super(i_Content);
        m_Items = new LinkedList<>();
    }

    @Override
    protected String SHA1() {
        String contentToSHA1 = CutContentForMakingSha1();
        return DigestUtils.sha1Hex(contentToSHA1);
    }

    @Override
    protected void Zip(String i_SHA1FileName, Path i_PathOfTheFile) throws IOException {

        Path objectsPath = Magit.getMagitDir().resolve("objects");
        FileUtils.createFileZipAndDelete(objectsPath,i_SHA1FileName,m_Content);
    }

    @Override
    public String toString() {
        String content = "";
        for (Item item : m_Items) {
            content = content.concat(item.toString()).concat(System.lineSeparator());
        }

        return content;
    }

    public void createItemListFromContent() {
        String[] lines = m_Content.split(System.lineSeparator());
        for (String line : lines) {
            String[] members = line.split(",");
            m_Items.add(new Item(members[0], members[1], members[2], members[3], DateUtils.FormatToDate(members[4])));
        }
    }

    public String CutContentForMakingSha1() {
        return StringUtilities.makeSha1Content(m_Content);
    }


}