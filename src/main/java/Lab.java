import com.opencsv.CSVWriter;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Lab {
    static ArrayList<String> a;
    private void createFile(String path){

    }
    private static void StampaFile(String path){
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        if(a==null) a= new ArrayList<String>();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if(listOfFiles[i].getName().endsWith(".java"))
                {
                    a.add(listOfFiles[i].getPath());
                }
            } else if (listOfFiles[i].isDirectory()) {
                StampaFile(listOfFiles[i].getPath());
            }
        }
    }

    public static void main(String[] arg) throws IOException {
        a=null;
        StampaFile("./TestAvro");
        CSVWriter writer = new CSVWriter(new FileWriter("try.csv"));
        for (String s:a)
        {
            writer.writeNext(new String[]{s, "no"});
        }
        writer.flush();
        writer.close();
        return;
    }
}
