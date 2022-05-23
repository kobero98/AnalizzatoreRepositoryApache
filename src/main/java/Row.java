import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;

import java.util.ArrayList;
import java.util.List;


//a
public class Row {
    private String path;
    private Integer size;
    private int n_commit;
    private int n_commitRelease;
    private int loc_Added;
    private int loc_Delete;
    private int loc_replace;
    private int max_LocAdded;
    private int churn;
    private int max_Churn;
    private ArrayList<String>worker;
    private boolean buggy;
    Row(String name) {
        this.path=name;
        this.size=0;
        this.n_commit =0;
        this.loc_Added =0;
        this.loc_Delete =0;
        this.loc_replace =0;
        this.max_LocAdded =0;
        this.churn =0;
        this.max_Churn =0;
        this.n_commitRelease =0;
        this.worker=new ArrayList<>();
        this.buggy=false;

    }
    Row(Row riga){
        this.path=riga.getPath();
        this.size=riga.getSize();
        this.n_commit = riga.getNcommit();
        this.n_commitRelease =0;
        this.loc_Added =0;
        this.loc_Delete =0;
        this.loc_replace =0;
        this.max_LocAdded =0;
        this.churn =0;
        this.max_Churn =0;
        this.worker=new ArrayList<String>();
        for(String s:riga.getWorker()){
            String d=s;
            this.worker.add(d);
        }
        this.buggy= riga.isBuggy();
    }
    public void modifySizeByEdit(EditList listaEdit){
        int a=0;
        int r=0;
        for (Edit app:listaEdit)
        {
            this.size+= app.getLengthB()-app.getLengthA();
            if(app.getType()== Edit.Type.INSERT){
                this.loc_Added +=app.getLengthB();
                a+=app.getLengthB();
                if(this.max_LocAdded <app.getLengthB()) this.max_LocAdded = app.getLengthB();
            }
            if (app.getType()== Edit.Type.DELETE){
                r+=app.getLengthA();
                this.loc_Delete += app.getLengthA();
            }
            if(app.getType()== Edit.Type.REPLACE)
            {
                if(app.getLengthA()> app.getLengthB()) {
                    this.loc_replace +=app.getLengthB();
                    this.loc_Delete +=app.getLengthA()-app.getLengthB();
                    r+=app.getLengthA()-app.getLengthB();
                }
                else{
                    this.loc_replace +=app.getLengthA();
                    this.loc_Added +=app.getLengthB()-app.getLengthA();
                    a+=app.getLengthB()-app.getLengthA();
                    if(this.max_LocAdded <app.getLengthB()) this.max_LocAdded = app.getLengthB();
                }
            }
        }
        this.churn +=a-r;
        if(this.max_Churn <a-r || this.n_commitRelease ==1 ) max_Churn =a-r;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public int getMaxLocAdded(){
        return this.max_LocAdded;
    }
    public int getLocAdded(){
        return this.loc_Added;
    }
    public int getAvgLocAdded(){
        if(this.n_commitRelease !=0)
            return this.loc_Added /this.n_commitRelease;
        return 0;
    }
    public void increaseNCommit(){
        n_commit = n_commit +1;
        this.n_commitRelease =this.n_commitRelease +1;
    }
    public int getNcommit() {
        return n_commit;
    }
    public boolean isBuggy(){
        return this.buggy;
    }
    public void setN_commit(int n_commit) {
        this.n_commit = n_commit;
        n_commitRelease =n_commit;
    }

    //controlla e aggiunge
    public boolean readyWorkerOn(String n){
        for(String s:this.worker)
        {
            if(s.equals(n)) return false;
        }
        this.worker.add(n);
        return true;
    }
    public List<String> getWorker() {
        return worker;
    }

    /*public void setWorker(ArrayList<String> worker) {
        this.worker = worker;
    }
     */
    public int getNLocTouched() {
        return loc_Added + loc_Delete + loc_replace;
    }

    public int getChurn() {
        return churn;
    }

    public int getAVGChurn() {
        if(this.n_commitRelease !=0)return this.churn /this.n_commitRelease;
        return 0;
    }

    public int getMaxChurn() {
        return max_Churn;
    }
    public void setZero(){
        this.churn =0;
        this.max_Churn =0;
        this.max_LocAdded =0;
        this.loc_Added =0;
        this.loc_replace =0;
        this.loc_Delete =0;
        this.n_commit =0;
    }

    public int getNcommitRelease() {
        return n_commitRelease;
    }

    public void setBuggy(boolean buggy){
        this.buggy=buggy;
    }
    public void setN_commitRelease(int n_commitRelease) {
        this.n_commitRelease = n_commitRelease;
    }
}
