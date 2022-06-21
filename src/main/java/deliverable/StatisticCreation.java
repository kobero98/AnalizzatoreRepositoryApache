package deliverable;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;

public class StatisticCreation {
    private String sampling;
    private String costSensitive;
    private String feature;
    private Instances training;
    private Instances testing;

    void setSampling(String sampling){
        this.sampling=sampling;
    }
    public void setCostSensitive(String costSensitive) {
        this.costSensitive = costSensitive;
    }
    public void setFeature(String feature) {
        this.feature = feature;
    }
    private Instances smote(Instances training) throws Exception {
        SMOTE smote=new SMOTE();
        String[] opts = new String[]{ "-C", "0", "-K", "5", "-P", "100.0", "-S", "1" };
        smote.setOptions(opts);
        smote.setInputFormat(training);
        return Filter.useFilter(training, smote);
    }
    private static CostMatrix createCostMatrix(double weightFalsePositive, double weightFalseNegative) {
        CostMatrix costMatrix = new CostMatrix(2);
        costMatrix.setCell(0, 0, 0.0);
        costMatrix.setCell(1, 0, weightFalsePositive);
        costMatrix.setCell(0, 1, weightFalseNegative);
        costMatrix.setCell(1, 1, 0.0);
        return costMatrix;
    }
    private Classifier costSensitive(Classifier c){
        CostSensitiveClassifier csf= new CostSensitiveClassifier();
        csf.setClassifier(c);
        csf.setCostMatrix(createCostMatrix(10,1));
        if(this.costSensitive.compareTo("sensitiveLearning")==0)
        {
            csf.setMinimizeExpectedCost(false);
            return csf;
        }
        if (this.costSensitive.compareTo("sensitiveThreshold")==0){
            csf.setMinimizeExpectedCost(true);
            return csf;
        }
        return c;
    }
    private static Classifier underSampling(Classifier c) throws Exception {
        FilteredClassifier fc = new FilteredClassifier();
        fc.setClassifier(c);
        SpreadSubsample spreadSubsample = new SpreadSubsample();
        String[] opts = new String[]{ "-M", "1.0"};
        spreadSubsample.setOptions(opts);
        fc.setFilter(spreadSubsample);
        return fc;
    }
    private Classifier overSampling(Classifier c) throws Exception {
        FilteredClassifier fc = new FilteredClassifier();
        fc.setClassifier(c);
        Resample resample = new Resample();
        resample.setInputFormat(this.training);
        resample.setOptions(Utils.splitOptions("-B 1.0 -Z 130.3"));
        fc.setFilter(resample);
        return fc;
    }
    private Classifier applySampling(Classifier c) throws Exception {
        if(this.sampling.compareTo("undersampling")==0){
            return underSampling(c);
        }
        if(this.sampling.compareTo("oversampling")==0){
            return overSampling(c);
        }
        return c;
    }
    private Classifier applyFeatureSelection(Classifier c) throws Exception {
        if(this.feature.compareTo("bestFirst")==0){
            BestFirst bestFirst = new BestFirst();
            AttributeSelection filter = new AttributeSelection();
            CfsSubsetEval eval12 = new CfsSubsetEval();
            filter.setEvaluator(eval12);
            filter.setSearch(bestFirst);
            filter.setInputFormat(this.training);
            this.training = Filter.useFilter(this.training, filter);
            int numAttrFiltered = this.training.numAttributes();
            this.training.setClassIndex(numAttrFiltered - 1);
            this.testing = Filter.useFilter(this.testing, filter);
            this.testing.setClassIndex(numAttrFiltered - 1);
        }
        return c;
    }

    public Classificatore createStatistic(Classifier c, Instances training,Instances testing) throws Exception {
        this.training=training;
        this.testing=testing;
        if(this.sampling.compareTo("smote")==0){
            this.training=smote(this.training);
        }
        c=costSensitive(c);
        c=applySampling(c);
        c=applyFeatureSelection(c);
        c.buildClassifier(this.training);
        Evaluation eval = new Evaluation(this.testing);
        eval.evaluateModel(c, this.testing);

        Classificatore date=new Classificatore();
        date.setSampling(this.sampling);
        date.setCostSensitive(this.costSensitive);
        date.setFeature(this.feature);
        date.setKappa(eval.kappa());
        date.setPrecision(0.0);
        if(eval.precision(1)!=Double.NaN)
            date.setPrecision(eval.precision(1));
        date.setRecall(eval.recall(1));
        date.setAuc(eval.areaUnderROC(1));
        date.setFn(eval.numFalseNegatives(1));
        date.setFp(eval.numFalsePositives(1));
        date.setTp(eval.numTruePositives(1));
        date.setTn(eval.numTrueNegatives(1));
        date.setPercentageDate(((double) this.training.size())/(this.training.size()+this.testing.size()));
        int count=0;
        for(Instance i:this.training){
            if(i.toString(i.numAttributes()-1).compareTo("true")==0) count++;
        }
        date.setDefectedTraing(((double) count)/this.training.size());
        count=0;
        for(Instance i:this.testing){
            if(i.toString(i.numAttributes()-1).compareTo("true")==0) count++;
        }
        date.setDefectedTesting(((double) count)/this.testing.size());
        return date;
    }


}
