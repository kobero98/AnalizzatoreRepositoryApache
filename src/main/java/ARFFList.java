import java.util.ArrayList;

public class ARFFList {
    private String release;
    public ArrayList<row> rows;
    ARFFList(String releaseName){
        this.release=releaseName;
        this.rows=new ArrayList<row>();
    }
    public void add(row r){
        rows.add(r);
    }
    public int contains(String r){
        for(int i=0;i< rows.size();i++)
            if(rows.get(i).getPath().compareTo(r)==0) return i;
        return -1;
    }
    public String getRelease() {
        return release;
    }
    public void setRelease(String release) {
        this.release = release;
    }
    public void remove(String path){
        for(int i=0;i< rows.size();i++)
            if(rows.get(i).getPath().compareTo(path)==0) rows.remove(i);
    }
    public void increaseNCommit(int i){
        if(i<0 ||i>= rows.size()) return;
        rows.get(i).increaseNCommit();
    }
    public void increaseWorkOnCommit(int i,String name){
        rows.get(i).readyWorkerOn(name);
    }
    public ArrayList<String []> toArrayString(){

        ArrayList<String[]> list=new ArrayList<String[]>();
        for (int i=0;i< rows.size();i++)
        {
            String[] x={
                    this.release,
                    rows.get(i).getPath(),
                    Integer.toString(rows.get(i).getSize()),
                    Integer.toString(rows.get(i).getN_commit()),
                    Integer.toString(rows.get(i).getWorker().size()),
                    Integer.toString(rows.get(i).getN_LocTouched()),
                    Integer.toString(rows.get(i).getLoc_Added()),
                    Integer.toString(rows.get(i).getMax_LocAdded()),
                    Integer.toString(rows.get(i).getAvg_LocAdded()),
                    Integer.toString(rows.get(i).getChurn()),
                    Integer.toString(rows.get(i).getMax_Churn()),
                    Integer.toString(rows.get(i).getAVG_Churn()),
                    Boolean.toString(rows.get(i).isBuggy())
            };
            list.add(x);
        }
        return list;
    }
}
