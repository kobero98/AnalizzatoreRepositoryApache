import java.util.ArrayList;
import java.util.List;

public class ARFFList {
    private String release;
    private List<Row> rows;
    ARFFList(String releaseName){
        this.release=releaseName;
        this.rows =new ArrayList<>();
    }
    public void add(Row r){
        rows.add(r);
    }

    public List<Row> getRows(){
        return this.rows;
    }
    public int contains(String r){
        for(int i = 0; i< rows.size(); i++)
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
        for(int i = 0; i< rows.size(); i++) {
            if (rows.get(i).getPath().compareTo(path) == 0) {
                rows.remove(i);
                i--;
            }
        }

    }
    public void increaseNCommit(int i){
        if(i<0 ||i>= rows.size()) return;
        rows.get(i).increaseNCommit();
    }
    public void increaseWorkOnCommit(int i,String name){
        rows.get(i).readyWorkerOn(name);
    }
    public List<String []> toArrayString(){

        ArrayList<String[]> list=new ArrayList<String[]>();
        for (int i = 0; i< rows.size(); i++)
        {
            String[] x={
                    this.release,
                    rows.get(i).getPath(),
                    Integer.toString(rows.get(i).getSize()),
                    Integer.toString(rows.get(i).getNcommit()),
                    Integer.toString(rows.get(i).getNcommitRelease()),
                    Integer.toString(rows.get(i).getWorker().size()),
                    Integer.toString(rows.get(i).getNLocTouched()),
                    Integer.toString(rows.get(i).getLocAdded()),
                    Integer.toString(rows.get(i).getMaxLocAdded()),
                    Integer.toString(rows.get(i).getAvgLocAdded()),
                    Integer.toString(rows.get(i).getChurn()),
                    Integer.toString(rows.get(i).getMaxChurn()),
                    Integer.toString(rows.get(i).getAVGChurn()),
                    Boolean.toString(rows.get(i).isBuggy())
            };
            list.add(x);
        }
        return list;
    }
}
