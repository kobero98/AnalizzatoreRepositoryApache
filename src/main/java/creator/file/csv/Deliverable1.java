package creator.file.csv;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Deliverable1 {
    private  String projectName;
    private List<ARFFList> listaF;
    private List<InfoVersion> versionList;//"https://github.com/apache/bookkeeper.git";//"https://github.com/kobero98/avro.git"
    private  String getIDJira(String shortestMessage){
        String s=null;
        Pattern pattern= Pattern.compile(projectName.toUpperCase()+"-\\d+");
        Matcher matcher= pattern.matcher(shortestMessage);
        if(matcher.find()){
             s=matcher.group(0);
        }
        return s;
    }
    private Bug bugContains(List<Bug> list, String id){
        for (Bug b:list){
            if(b.getName().equals(id)) return b;
        }
        return null;
    }
    private void setBuggy(Bug b, List<ARFFList> list, List<InfoVersion> versions, String path){
        if(b==null) return;
        int inizio= versions.indexOf(b.getAffected());
        int fine=versions.indexOf(b.getFixed());
        for (int k=inizio;k<fine && k<list.size()-1;k++){
            for (Row r:list.get(k).getRows())
            {
                if(r.getPath().equals(path)) r.setBuggy(true);
            }
        }
    }
    private void functionControllEntry(DiffEntry diff,ARFFList c,String nameAuthor,EditList listEdit,Bug bugCatch)
    {
        if (diff.getChangeType() == DiffEntry.ChangeType.ADD) {
            Row app = new Row(diff.getNewPath());
            app.increaseNCommit();
            app.modifySizeByEdit(listEdit);
            app.readyWorkerOn(nameAuthor);
            c.add(app);
        }
        else {
            if (diff.getChangeType() == DiffEntry.ChangeType.MODIFY || diff.getChangeType() == DiffEntry.ChangeType.COPY || diff.getChangeType() == DiffEntry.ChangeType.RENAME) {
                int index = c.contains(diff.getOldPath());
                c.getRows().get(index).setPath(diff.getNewPath());
                c.increaseNCommit(index);
                c.getRows().get(index).modifySizeByEdit(listEdit);
                c.increaseWorkOnCommit(index, nameAuthor);
                setBuggy(bugCatch,listaF,versionList,diff.getOldPath());
            } else {
                if (diff.getChangeType() == DiffEntry.ChangeType.DELETE) {
                    c.remove(diff.getOldPath());
                    setBuggy(bugCatch, listaF, versionList, diff.getOldPath());
                }
            }
        }
    }

    public String printTraningSet(List<ARFFList> listaF, String prName) throws IOException {
        File directory=new File("./"+projectName+"/trainingSet_"+projectName);
        directory.mkdir();
        File file=new File("./"+projectName+"/trainingSet_"+projectName+"/"+prName+".arff");
        try(FileWriter writer=new FileWriter(file)){
            writer.write("@relation "+projectName);
            writer.write("\n");
            writer.write("@attribute Size numeric\n");
            writer.write("@attribute N_Commit numeric\n");
            writer.write("@attribute N_Commit_Release numeric\n");
            writer.write("@attribute N_Dev numeric\n");
            writer.write("@attribute LOC_Touched numeric\n");
            writer.write("@attribute LOC_Added numeric\n");
            writer.write("@attribute Max_LOC_Added numeric\n");
            writer.write("@attribute AVG_LOC_Added numeric\n");
            writer.write("@attribute Churn numeric\n");
            writer.write("@attribute Max_Churn numeric\n");
            writer.write("@attribute Avg_Churn numeric\n");
            writer.write("@attribute IsBuggy {false,true}\n");
            writer.write("@data\n");
            for (ARFFList app:listaF.subList(0,listaF.size()-1))
            {
                for (String[] stringArray:app.toArrayString()){
                    int i;
                    for (i=2;i<stringArray.length-1;i++){
                        writer.write(stringArray[i]);
                        writer.write(",");
                    }
                    writer.write(stringArray[i]);
                    writer.write("\n");
                }
            }
        }
        return file.getPath();
    }
    public List<String> printTestingSet(List<ARFFList> arffLists) throws IOException {
        File directory=new File("./"+projectName+"/TestingSet_"+projectName);
        directory.mkdir();
        List<String> list=new ArrayList<>();
        for(int i=0;i<arffLists.size()-1;i++){
            String path="./"+projectName+"/TestingSet_"+projectName+"/"+projectName+"_"+i+".arff";
            list.add(path);
            File file=new File(path);
            try(FileWriter writer=new FileWriter(file)){
                writer.write("@relation "+projectName);
                writer.write("\n");
                writer.write("@attribute Size numeric\n");
                writer.write("@attribute N_Commit numeric\n");
                writer.write("@attribute N_Commit_Release numeric\n");
                writer.write("@attribute N_Dev numeric\n");
                writer.write("@attribute LOC_Touched numeric\n");
                writer.write("@attribute LOC_Added numeric\n");
                writer.write("@attribute Max_LOC_Added numeric\n");
                writer.write("@attribute AVG_LOC_Added numeric\n");
                writer.write("@attribute Churn numeric\n");
                writer.write("@attribute Max_Churn numeric\n");
                writer.write("@attribute Avg_Churn numeric\n");
                writer.write("@attribute IsBuggy {false,true}\n");
                writer.write("@data\n");
                ARFFList app= arffLists.get(i);
                for (String[] stringArray:app.toArrayString()){
                    int j;
                    for (j=2;j<stringArray.length-1;j++){
                        writer.write(stringArray[j]);
                        writer.write(",");
                    }
                    writer.write(stringArray[j]);
                    writer.write("\n");
                }
            }
        }
        return list;
    }

    private void functionDiffEntry(List<DiffEntry> listDiff, ARFFList c, Bug bugCatch, String nameAuthor, DiffFormatter formatter) throws IOException {
        for (DiffEntry diff : listDiff) {
            if (diff.getNewPath().endsWith(".java") || diff.getOldPath().endsWith(".java")) {
                EditList listEdit = formatter.toFileHeader(diff).toEditList();
                functionControllEntry(diff, c, nameAuthor, listEdit, bugCatch);
            }
        }
    }
    private List<RevCommit> listCreationCommit(Git git) throws GitAPIException {
        Iterable<RevCommit> log= git.log().call();
        List<RevCommit> listCommit =new ArrayList<>();
        for (RevCommit s : log) {
            listCommit.add(s);
        }
        return listCommit;
    }
    public List<ARFFList> obtainARFFList(String prName, String link, int version) throws IOException, ParseException, GitAPIException {
        this.projectName=prName;
        File f = new File("./Test"+projectName);
        Git git;
        try {
            git = Git.cloneRepository().setURI(link)
                    .setDirectory(f).setCloneAllBranches(true).call();
        } catch (GitAPIException | JGitInternalException e) {
            git = Git.open(f);
                git.pull().call();
        }
        List<RevCommit> listCommit =listCreationCommit(git);
        InfoJira t = new InfoJira(projectName.toUpperCase());
        this.versionList = t.listVersion();
        if(version<1) version=this.versionList.size();

        List<Bug> listBug=t.listBug();
        versionList.add(new InfoVersion(Date.from(Instant.now()), "VersionAncoraNonConosciuta"));
        this.listaF=new ArrayList<>();
        ARFFList c=new ARFFList(versionList.get(0).getS());
        listaF.add(c);
        int j;
        int k = 0;
        for (j = listCommit.size(); j >= 1 && k<version; j--) {
            RevCommit actual = null;
            if (j != listCommit.size())
                actual = listCommit.get(j);
            RevCommit success = listCommit.get(j - 1);
            String idJira=getIDJira(success.getShortMessage());
            Bug bugCatch=bugContains(listBug,idJira);
            String nameAuthor = success.getAuthorIdent().getName();
            Date dataSuccess = success.getCommitterIdent().getWhen();
            try (ObjectReader reader = git.getRepository().newObjectReader()) {
                AbstractTreeIterator oldTreeIterator = new EmptyTreeIterator();
                if (actual != null)
                    oldTreeIterator = new CanonicalTreeParser(null, reader, actual.getTree().getId());
                AbstractTreeIterator newTreeIterator = new CanonicalTreeParser(null, reader, success.getTree().getId());
                try (DiffFormatter formatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
                    formatter.setRepository(git.getRepository());
                    List<DiffEntry> listDiff = formatter.scan(oldTreeIterator, newTreeIterator);
                    functionDiffEntry(listDiff,c,bugCatch,nameAuthor,formatter);
                }
            }
            if (dataSuccess.after(versionList.get(k).getData())) {
                    k++;
                    ArrayList<Row> finta=new ArrayList<>();
                    for (Row app:c.getRows()){
                        Row s=new Row(app);
                        finta.add(s);
                    }
                    c=new ARFFList(versionList.get(k).getS());
                    c.getRows().addAll(finta);
                    listaF.add(c);
                 }
            }
            return listaF;
        }
    }