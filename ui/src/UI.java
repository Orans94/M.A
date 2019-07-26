import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;



public class UI {
    public static void main(String []args) throws IOException {
        //Engine eng = new Engine();
        //eng.createNewRepository("C:\\Users\\ziv3r\\Desktop\\summer semester","Game");


        String fileName = "C:\\Users\\ziv3r\\Desktop\\summer semester\\Game\\.magit\\objects\\a.txt";
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        String text = new String();
        String line;

        while((line = br.readLine()) != null){
            text.concat(line);
        }

        String shaone = DigestUtils.sha1Hex(text);

        System.out.println(shaone);


    }
}


