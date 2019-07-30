import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Blob extends Node
{
    public Blob(String i_Content)
    {
        super(i_Content);
    }

}