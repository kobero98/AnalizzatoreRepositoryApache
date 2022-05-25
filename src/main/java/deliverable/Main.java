package deliverable;

import creator.file.csv.Deliverable1;
import org.eclipse.jgit.api.errors.GitAPIException;
import java.io.IOException;
import java.text.ParseException;

public class Main {
    public static void main(String [] args)
    {

            try {
                new Deliverable1().csvFile("AVRO","https://github.com/kobero98/avro.git");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (GitAPIException e) {
                throw new RuntimeException(e);
            }



    }
}
