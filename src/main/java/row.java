import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;

import java.util.ArrayList;
import java.util.Random;
import java.util.random.RandomGenerator;

//a
public class row {
    private String path;
    private Integer size;
    private int N_commit;
    private int N_Fix;
    private int Loc_Added;
    private int Loc_Delete;
    private int Loc_replace;
    private int Max_LocAdded;
    private int Churn;
    private int Max_Churn;
    private int AVG_Churn;
    private ArrayList<String>worker;
    private boolean buggy;
    row(String name) {
        this.path=name;
        this.size=0;
        this.N_commit=0;
        this.N_Fix=0;
        this.Loc_Added=0;
        this.Loc_Delete=0;
        this.Loc_replace=0;
        this.Max_LocAdded=0;
        this.Churn=0;
        this.Max_Churn=0;
        this.worker=new ArrayList<>();
        if(Math.random()<0.5) this.buggy=false;
        else this.buggy=true;
    }
    public void modifySizeByEdit(EditList listaEdit){
        int A=0;
        int R=0;
        for (Edit app:listaEdit)
        {
            this.size+= app.getLengthB()-app.getLengthA();
            if(app.getType()== Edit.Type.INSERT){
                this.Loc_Added+=app.getLengthB();
                A+=app.getLengthB();
                if(this.Max_LocAdded<app.getLengthB()) this.Max_LocAdded= app.getLengthB();
            }
            if (app.getType()== Edit.Type.DELETE){
                R+=app.getLengthA();
                this.Loc_Delete+= app.getLengthA();
            }
            if(app.getType()== Edit.Type.REPLACE)
            {
                if(app.getLengthA()> app.getLengthB()) {
                    this.Loc_replace+=app.getLengthB();
                    this.Loc_Delete+=app.getLengthA()-app.getLengthB();
                    R+=app.getLengthA()-app.getLengthB();
                }
                else{
                    this.Loc_replace+=app.getLengthA();
                    this.Loc_Added+=app.getLengthB()-app.getLengthA();
                    A+=app.getLengthB()-app.getLengthA();
                    if(this.Max_LocAdded<app.getLengthB()) this.Max_LocAdded= app.getLengthB();
                }
            }
        }
        this.Churn+=Math.abs(A-R);
        if(this.Max_Churn<Math.abs(A-R)) Max_Churn=Math.abs(A-R);
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
    public int getMax_LocAdded(){
        return this.Max_LocAdded;
    }
    public int getLoc_Added(){
        return this.Loc_Added;
    }
    public int getAvg_LocAdded(){
        return this.Loc_Added/N_commit;
    }
    public void increaseNCommit(){
        N_commit=N_commit+1;
    }
    public int getN_commit() {
        return N_commit;
    }
    public boolean isBuggy(){
        return this.buggy;
    }
    public void setN_commit(int n_commit) {
        N_commit = n_commit;
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
    public ArrayList<String> getWorker() {
        return worker;
    }
    public void setWorker(ArrayList<String> worker) {
        this.worker = worker;
    }
    public int getN_LocTouched() {
        return Loc_Added+Loc_Delete+Loc_replace;
    }

    public int getChurn() {
        return Churn;
    }

    public int getAVG_Churn() {
        return this.Churn/this.N_commit;
    }

    public int getMax_Churn() {
        return Max_Churn;
    }
}
