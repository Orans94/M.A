import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils
{
    public static void CreateAndWriteTxtFile(Path i_Path, String i_Content) throws IOException {
        Files.createFile(i_Path);
        Files.write(i_Path, i_Content.getBytes());
    }
    public static void changeFileContent(Path i_Path, String i_NewContent) throws IOException {
        FileOutputStream writer = new FileOutputStream(String.valueOf(i_Path));
        writer.write(("").getBytes());
        writer.write(i_NewContent.getBytes());
        writer.close();
    }

    //create temp file in objects make a zip from this file and delete it.
    public static void createFileZipAndDelete(Path i_PathToPutTheTempText,String i_NameOfTheZipAsSha1,String i_ContentTowWriteInFile ) throws IOException {
        Path createTempTxtPath = i_PathToPutTheTempText.resolve(i_NameOfTheZipAsSha1+".txt");
        CreateAndWriteTxtFile(createTempTxtPath, i_ContentTowWriteInFile);
        Zip(i_NameOfTheZipAsSha1,createTempTxtPath);
        deleteFile(createTempTxtPath);
    }

    public static void Zip(String i_ZipName, Path i_FilePath)
    { //TODO handle exepction
        Path zippingPath = Magit.getMagitDir().resolve("objects").resolve(i_ZipName + ".zip");
        try
        {
            String sourceFile = i_FilePath.toString();
            FileOutputStream fos = new FileOutputStream(zippingPath.toString());
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(sourceFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0)
            {
                zipOut.write(bytes, 0, length);
            }
            zipOut.close();
            fis.close();
            fos.close();
        } catch (IOException e)
        {

        }

    }

    public static void deleteFile(Path i_FileToDelte) throws IOException {
        Files.delete(i_FileToDelte);
    }
}