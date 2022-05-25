package deliverable;

import creator.file.csv.Deliverable1;
import org.eclipse.jgit.api.errors.GitAPIException;
import java.io.IOException;
import java.text.ParseException;

public class Main {
    public static void main(String [] args) throws GitAPIException, IOException, ParseException {
        new Deliverable1().csvFile("Avro","https://github.com/kobero98/avro.git",0);
        new Deliverable1().csvFile("Bookkeeper","https://github.com/kobero98/bookkeeper.git",0);


    }
}
