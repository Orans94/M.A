import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils
{
    public static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

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
        {}
    }

    //zipFilePath = the path of the file end with .zip
    //unzip location is the name of the directory to put the unzipped file
    public static void unzip(final String zipFilePath, final String unzipLocation) throws IOException, IOException {

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            while (entry != null) {
                Path filePath = Paths.get(unzipLocation, entry.getName());
                if (!entry.isDirectory()) {
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath.toAbsolutePath().toString()))) {
                        byte[] bytesIn = new byte[1024];
                        int read = 0;
                        while ((read = zipInputStream.read(bytesIn)) != -1) {
                            bos.write(bytesIn, 0, read);
                        }
                    }
                } else {
                    Files.createDirectories(filePath);
                }

                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
        }
    }

    public static void deleteFile(Path i_FileToDelte) throws IOException {
        Files.delete(i_FileToDelte);
    }

    public static String readFileAndReturnString(Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath));
    }

    public static String getStringFromFolderZip(String i_zipFileName) throws IOException {
        String currentFileContent;

        ZipFile zipFile = new ZipFile(Magit.getObjectsPath().resolve(i_zipFileName+ ".zip").toString());
        ZipEntry zipEntry = zipFile.getEntry(i_zipFileName + ".txt");

        InputStream inputStream =   zipFile.getInputStream(zipEntry);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        currentFileContent = result.toString("UTF-8");
        return currentFileContent;
    }
}



