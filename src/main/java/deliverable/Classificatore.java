package deliverable;

public class Classificatore {

    private String nomeClassifiatore;
    private String release;
    private String costSensitive;
    private String sampling;
    private String feature;


    private double defectedTesting;
    private double defectedTraing;
    private double percentageDate;

    private double tp;
    private double tn;
    private double fp;
    private double fn;
    private double recall;
    private double precision;
    private double kappa;
    private double auc;


    public void setNomeClassifiatore(String nomeClassifiatore) {
        this.nomeClassifiatore = nomeClassifiatore;
    }


    public void setCostSensitive(String costSensitive) {
        this.costSensitive = costSensitive;
    }

    public String getSampling() {
        return sampling;
    }

    public void setSampling(String sampling) {
        this.sampling = sampling;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }


    public double getTp() {
        return tp;
    }

    public void setTp(double tp) {
        this.tp = tp;
    }


    public void setTn(double tn) {
        this.tn = tn;
    }

    public double getFp() {
        return fp;
    }

    public void setFp(double fp) {
        this.fp = fp;
    }

    public double getFn() {
        return fn;
    }

    public void setFn(double fn) {
        this.fn = fn;
    }


    public void setRecall(double recall) {
        this.recall = recall;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }


    public void setKappa(double kappa) {
        this.kappa = kappa;
    }



    public void setAuc(double auc) {
        this.auc = auc;
    }



    public void setPercentageDate(double percentageDate) {
        this.percentageDate = percentageDate;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }
    public static String [] getListNameROW(){
        return new String[]{"Release", "nomeClassificatore", "CostSensitive", "Sampling", "feature","PercentageDate","DefectedTraining","DefectedTesting","TP","TN","FP","FN","precision","Recall","AUC","kappa"};
    }
    public String [] getROW(){
        String [] s=new String[16];
        s[0]= release;
        s[1]=nomeClassifiatore;
        s[2]=costSensitive;
        s[3]=sampling;
        s[4]=feature;
        s[5]=Double.toString(percentageDate);
        s[6]=Double.toString(defectedTraing);
        s[7]=Double.toString(defectedTesting);
        s[8]=Double.toString(tp);
        s[9]=Double.toString(fp);
        s[10]=Double.toString(tn);
        s[11]=Double.toString(fn);
        s[12]=Double.toString(precision);
        s[13]=Double.toString(recall);
        s[14]=Double.toString(auc);
        s[15]=Double.toString(kappa);
        return s;
    }

    public void setDefectedTesting(double defectedTesting) {
        this.defectedTesting = defectedTesting;
    }

    public void setDefectedTraing(double defectedTraing) {
        this.defectedTraing = defectedTraing;
    }
}
