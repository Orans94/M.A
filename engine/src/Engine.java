import java.io.*;

public class Engine {
    public void createNewRepository(String i_Path, String i_RepositoryName) {

        try {
            String folder = i_Path + "\\" + i_RepositoryName;
            File newFolder = new File(folder);
            newFolder.mkdir();

            folder = folder.concat("\\.magit");
            newFolder = new File(folder + "\\objects");
            newFolder.mkdirs();

            newFolder = new File(folder + "\\branches");
            newFolder.mkdir();
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }

    }
}

