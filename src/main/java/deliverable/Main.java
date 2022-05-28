package deliverable;

import creator.file.csv.ARFFList;
import creator.file.csv.Deliverable1;

import java.util.List;

public class Main {
    private static final String prName="Avro";
    private static final String link="https://github.com/kobero98/avro.git";
    public static void main(String [] args) throws Exception {
        Deliverable1 d=new Deliverable1();
        List<ARFFList> testingSet =d.obtainARFFList(prName,link,0);
        d.printTestingSet(testingSet);
        for (int i=1;i<testingSet.size()/2;i++)
        {
            List<ARFFList> training=d.obtainARFFList(prName,link,i);
            d.printTraningSet(training,prName+"_"+i);
        }

    }
}
