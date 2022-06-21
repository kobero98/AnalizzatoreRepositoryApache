package deliverable;
import com.opencsv.CSVWriter;
import creator.file.csv.ARFFList;
import creator.file.csv.Deliverable1;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String PR_NAME ="Avro";
    private static final String LINK ="https://github.com/kobero98/avro.git";
    private static List<Classificatore> computeEvaluation(List<String> listTrainingPath, List<String> listTestingPath) throws Exception {
        String [] sampling={"oversampling","undersampling","smote","nienteSampling"};
        String [] feature={"NienteFeatureSelection","bestFirst"};
        String [] cost={"sensitiveThreshold","sensitiveLearning","nienteCostSensitive"};

        List <Classificatore> listClassificatore=new ArrayList<>();
        for (int j =1; j < listTrainingPath.size(); j++) {
            ConverterUtils.DataSource source1 = new ConverterUtils.DataSource(listTrainingPath.get(j));
            Instances training = source1.getDataSet();
            ConverterUtils.DataSource source2 = new ConverterUtils.DataSource(listTestingPath.get(j + 1));
            Instances testing = source2.getDataSet();
            int numAttr = training.numAttributes();
            training.setClassIndex(numAttr - 1);
            testing.setClassIndex(numAttr - 1);
            for (int i =0;i<sampling.length;i++)
            {
                for(int k=0;k<feature.length;k++)
                {
                    for(int f=0;f<cost.length;f++)
                    {
                        StatisticCreation s1= new StatisticCreation();
                        s1.setCostSensitive(cost[f]);
                        s1.setFeature(feature[k]);
                        s1.setSampling(sampling[i]);
                        NaiveBayes c1=new NaiveBayes();
                        RandomForest c2=new RandomForest();
                        IBk c3=new IBk();

                        Classificatore d=s1.createStatistic(c1,training,testing);
                        d.setNomeClassifiatore("NaiveBayes");
                        d.setRelease(listTestingPath.get(j));
                        listClassificatore.add(d);
                        d=s1.createStatistic(c2,training,testing);
                        d.setNomeClassifiatore("Random Forest");
                        d.setRelease(listTestingPath.get(j));
                        listClassificatore.add(d);
                        d=s1.createStatistic(c3,training,testing);
                        d.setNomeClassifiatore("IBk");
                        d.setRelease(listTrainingPath.get(j));
                        listClassificatore.add(d);
                    }
                }
            }
        }
        return listClassificatore;
    }
    private static void creazioneResultCSV(List<Classificatore>list,File f) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(f))) {
            writer.writeNext(Classificatore.getListNameROW());
            for (Classificatore app:list)
            {
                writer.writeNext(app.getROW());
            }
            writer.flush();
        }
    }
    public static void main(String [] args) throws Exception {
        File file=new File("./"+ PR_NAME);
        file.mkdir();
        Deliverable1 d=new Deliverable1();
        List<ARFFList> testingSet =d.obtainARFFList(PR_NAME, LINK,0);
        List<String> listTestingPath=d.printTestingSet(testingSet);
        List<String> listTrainingPath=new ArrayList<>();
        for (int i=1;i<testingSet.size()/2;i++)
        {
            List<ARFFList> training=d.obtainARFFList(PR_NAME, LINK,i);
            listTrainingPath.add(d.printTraningSet(training, PR_NAME +"_"+i));
        }
        List<Classificatore> list=computeEvaluation(listTrainingPath,listTestingPath);
        File resultSet=new File("./resultFinalStatistic.csv");
        creazioneResultCSV(list,resultSet);
    }
}
