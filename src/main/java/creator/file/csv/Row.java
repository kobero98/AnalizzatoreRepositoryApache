package creator.file.csv;

import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;

import java.util.ArrayList;
import java.util.List;


//a
public class Row {
    private String path;
    private Integer size;
    private int nCommit;
    private int nCommitRelease;
    private int locAdded;
    private int locDelete;
    private int locReplace;
    private int maxLocAdded;
    private int churn;
    private int maxChurn;
    private ArrayList<String>worker;
    private boolean buggy;
    Row(String name) {
        this.path=name;
        this.size=0;
        this.nCommit =0;
        this.locAdded =0;
        this.locDelete =0;
        this.locReplace =0;
        this.maxLocAdded =0;
        this.churn =0;
        this.maxChurn =0;
        this.nCommitRelease =0;
        this.worker=new ArrayList<>();
        this.buggy=false;

    }
    Row(Row riga){
        this.path=riga.getPath();
        this.size=riga.getSize();
        this.nCommit = riga.getNcommit();
        this.nCommitRelease =0;
        this.locAdded =0;
        this.locDelete =0;
        this.locReplace =0;
        this.maxLocAdded =0;
        this.churn =0;
        this.maxChurn =0;
        this.worker=new ArrayList<String>();
        for(String s:riga.getWorker()){
            String d=s;
            this.worker.add(d);
        }
        this.buggy= false;
    }
    private void incrementSize(Edit app,int []v){
        this.size+= app.getLengthB()-app.getLengthA();
        if(app.getType()== Edit.Type.INSERT){
            this.locAdded +=app.getLengthB();
            v[0]+=app.getLengthB();
            if(this.maxLocAdded <app.getLengthB()) this.maxLocAdded = app.getLengthB();
        }
        if (app.getType()== Edit.Type.DELETE){
            v[1]+=app.getLengthA();
            this.locDelete += app.getLengthA();
        }
        if(app.getType()== Edit.Type.REPLACE)
        {
            if(app.getLengthA()> app.getLengthB()) {
                this.locReplace +=app.getLengthB();
                this.locDelete +=app.getLengthA()-app.getLengthB();
                v[1]+=app.getLengthA()-app.getLengthB();
            }
            else{
                this.locReplace +=app.getLengthA();
                this.locAdded +=app.getLengthB()-app.getLengthA();
                v[0]+=app.getLengthB()-app.getLengthA();
                if(this.maxLocAdded <app.getLengthB()) this.maxLocAdded = app.getLengthB();
            }
        }
    }
    public void modifySizeByEdit(EditList listaEdit){
        int a=0;
        int r=0;
        for (Edit app:listaEdit)
        {
            int []v={0,0};
            incrementSize(app,v);
            a+=v[0];
            r+=v[1];
        }
        this.churn +=a-r;
        if(this.maxChurn <a-r || this.nCommitRelease ==1 ) maxChurn =a-r;
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
        return this.maxLocAdded;
    }
    public int getLocAdded(){
        return this.locAdded;
    }
    public int getAvgLocAdded(){
        if(this.nCommitRelease !=0)
            return this.locAdded /this.nCommitRelease;
        return 0;
    }
    public void increaseNCommit(){
        nCommit = nCommit +1;
        this.nCommitRelease =this.nCommitRelease +1;
    }
    public int getNcommit() {
        return nCommit;
    }
    public boolean isBuggy(){
        return this.buggy;
    }
    public void setnCommit(int nCommit) {
        this.nCommit = nCommit;
        nCommitRelease = nCommit;
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
        return locAdded + locDelete + locReplace;
    }

    public int getChurn() {
        return churn;
    }

    public int getAVGChurn() {
        if(this.nCommitRelease !=0)return this.churn /this.nCommitRelease;
        return 0;
    }

    public int getMaxChurn() {
        return maxChurn;
    }

    public int getNcommitRelease() {
        return nCommitRelease;
    }

    public void setBuggy(boolean buggy) {
        this.buggy = buggy;
    }
}
