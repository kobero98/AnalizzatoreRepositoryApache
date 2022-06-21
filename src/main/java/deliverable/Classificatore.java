package deliverable;

public class Classificatore {

    private String nomeClassifiatore;
    private String Release;
    private String costSensitive;
    private String sampling;
    private String feature;


    private double defectedTesting;
    private double defectedTraing;
    private double percentageDate;

    private double TP;
    private double TN;
    private double FP;
    private double FN;
    private double recall;
    private double precision;
    private double kappa;
    private double AUC;


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


    public double getTP() {
        return TP;
    }

    public void setTP(double TP) {
        this.TP = TP;
    }


    public void setTN(double TN) {
        this.TN = TN;
    }

    public double getFP() {
        return FP;
    }

    public void setFP(double FP) {
        this.FP = FP;
    }

    public double getFN() {
        return FN;
    }

    public void setFN(double FN) {
        this.FN = FN;
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



    public void setAUC(double AUC) {
        this.AUC = AUC;
    }



    public void setPercentageDate(double percentageDate) {
        this.percentageDate = percentageDate;
    }

    public String getRelease() {
        return Release;
    }

    public void setRelease(String release) {
        Release = release;
    }
    public static String [] getListNameROW(){
        return new String[]{"Release", "nomeClassificatore", "CostSensitive", "Sampling", "feature","PercentageDate","DefectedTraining","DefectedTesting","TP","TN","FP","FN","precision","Recall","AUC","kappa"};
    }
    public String [] getROW(){
        String [] s=new String[16];
        s[0]=Release;
        s[1]=nomeClassifiatore;
        s[2]=costSensitive;
        s[3]=sampling;
        s[4]=feature;
        s[5]=Double.toString(percentageDate);
        s[6]=Double.toString(defectedTraing);
        s[7]=Double.toString(defectedTesting);
        s[8]=Double.toString(TP);
        s[9]=Double.toString(FP);
        s[10]=Double.toString(TN);
        s[11]=Double.toString(FN);
        s[12]=Double.toString(precision);
        s[13]=Double.toString(recall);
        s[14]=Double.toString(AUC);
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
