package deliverable;
import creator.file.csv.ARFFList;
import creator.file.csv.Deliverable1;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String prName="Avro";
    private static final String link="https://github.com/kobero98/avro.git";
    private static void creazioneCSV(List<String> listTrainingPath, List<String> listTestingPath, File f) throws Exception {

        try(FileWriter writer=new FileWriter(f)){
            writer.write("Project,Version,classifier,precision,recall,AUC,kappa\n");
            for (int j=0;j<listTrainingPath.size();j++)
            {
                ConverterUtils.DataSource source1 = new ConverterUtils.DataSource(listTrainingPath.get(j));
                Instances training = source1.getDataSet();
                ConverterUtils.DataSource source2 = new ConverterUtils.DataSource(listTestingPath.get(j+1));
                Instances testing = source2.getDataSet();
                int numAttr = training.numAttributes();
                training.setClassIndex(numAttr - 1);
                testing.setClassIndex(numAttr - 1);
                //NaiveBayes
                NaiveBayes classifier1 = new NaiveBayes();
                classifier1.buildClassifier(training);
                Evaluation eval = new Evaluation(testing);
                eval.evaluateModel(classifier1, testing);
                writer.write(prName+","+(j+1)+",NaiveBayes,"+ Math.round(eval.precision(1) * 10000) / 10000d +","
                        + Math.round(eval.recall(1) * 10000) / 10000d +","
                        + Math.round(eval.areaUnderROC(1) * 10000) / 10000d +","
                        + Math.round(eval.kappa() * 10000) / 10000d +"\n");
                //RandomForest
                RandomForest classifier2=new RandomForest();
                classifier2.buildClassifier(training);
                Evaluation eval2 = new Evaluation(testing);
                eval2.evaluateModel(classifier2, testing);
                writer.write(prName+","+(j+1)+",RandomForest,"+ Math.round(eval2.precision(1) * 10000) / 10000d+","
                        + Math.round(eval2.recall(1) * 10000) / 10000d +","
                        + Math.round(eval2.areaUnderROC(1) * 10000) / 10000d +","
                        + Math.round(eval2.kappa() * 10000) / 10000d +"\n");
                //IBk
                IBk classifier3=new IBk();
                classifier3.buildClassifier(training);
                Evaluation eval3 = new Evaluation(testing);
                eval3.evaluateModel(classifier3, testing);
                eval3.evaluateModel(classifier3, testing);
                writer.write(prName+","+(j+1)+",IBk,"+ Math.round(eval3.precision(1) * 10000) / 10000d +","
                        + Math.round(eval3.recall(1) * 10000) / 10000d+","
                        + Math.round(eval3.areaUnderROC(1) * 10000) / 10000d +","
                        + Math.round(eval3.kappa() * 10000) / 10000d +"\n");
            }
        }
    }
    public static void main(String [] args) throws Exception {
        File file=new File("./"+prName);
        file.mkdir();
        Deliverable1 d=new Deliverable1();
        List<ARFFList> testingSet =d.obtainARFFList(prName,link,0);
        List<String> listTestingPath=d.printTestingSet(testingSet);
        List<String> listTrainingPath=new ArrayList<>();
        for (int i=1;i<testingSet.size()/2;i++)
        {
            List<ARFFList> training=d.obtainARFFList(prName,link,i);
            listTrainingPath.add(d.printTraningSet(training,prName+"_"+i));
        }
        File f=new File("./"+prName+"result_"+prName+".csv");
        creazioneCSV(listTrainingPath,listTestingPath,f);

    }
}
