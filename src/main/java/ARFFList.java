import java.util.ArrayList;
import java.util.List;

public class ARFFList {
    private String release;
    private List<Row> Rows;
    ARFFList(String releaseName){
        this.release=releaseName;
        this.Rows =new ArrayList<>();
    }
    public void add(Row r){
        Rows.add(r);
    }

    public List<Row> getRows(){
        return this.Rows;
    }
    public int contains(String r){
        for(int i = 0; i< Rows.size(); i++)
            if(Rows.get(i).getPath().compareTo(r)==0) return i;
        return -1;
    }
    public String getRelease() {
        return release;
    }
    public void setRelease(String release) {
        this.release = release;
    }
    public void remove(String path){
        for(int i = 0; i< Rows.size(); i++)
            if(Rows.get(i).getPath().compareTo(path)==0) Rows.remove(i);
    }
    public void increaseNCommit(int i){
        if(i<0 ||i>= Rows.size()) return;
        Rows.get(i).increaseNCommit();
    }
    public void increaseWorkOnCommit(int i,String name){
        Rows.get(i).readyWorkerOn(name);
    }
    public List<String []> toArrayString(){

        ArrayList<String[]> list=new ArrayList<String[]>();
        for (int i = 0; i< Rows.size(); i++)
        {
            String[] x={
                    this.release,
                    Rows.get(i).getPath(),
                    Integer.toString(Rows.get(i).getSize()),
                    Integer.toString(Rows.get(i).getN_commit()),
                    Integer.toString(Rows.get(i).getN_commitRelease()),
                    Integer.toString(Rows.get(i).getWorker().size()),
                    Integer.toString(Rows.get(i).getN_LocTouched()),
                    Integer.toString(Rows.get(i).getLoc_Added()),
                    Integer.toString(Rows.get(i).getMax_LocAdded()),
                    Integer.toString(Rows.get(i).getAvg_LocAdded()),
                    Integer.toString(Rows.get(i).getChurn()),
                    Integer.toString(Rows.get(i).getMax_Churn()),
                    Integer.toString(Rows.get(i).getAVG_Churn()),
                    Boolean.toString(Rows.get(i).isBuggy())
            };
            list.add(x);
        }
        return list;
    }
    public void setZero(){
        for (Row s:this.Rows){
            s.setZero();
        }
    }
}
