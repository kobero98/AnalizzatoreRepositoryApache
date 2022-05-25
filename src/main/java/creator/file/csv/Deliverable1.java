package creator.file.csv;

import com.opencsv.CSVWriter;
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
    private List<InfoVersion> versionList;
    private  String linkRepo;//"https://github.com/apache/bookkeeper.git";//"https://github.com/kobero98/avro.git"
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
    private void printOnFile(String path) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(path));
        writer.writeNext(new String[]{"Release","Path","Size","Numero_Commit","Numero_commit_Release","Numero_Lavoratori","LOC_TOUCHED","LOC_Added","Max_LOC_Added","AVG_LOCADDED","Churn","Max_Churn","Avg_Churn","IsBuggy"});
        int i;
        for (i=0;i<listaF.size()/2;i++) {
            ARFFList appoggio=listaF.get(i);
            for (String[] s : appoggio.toArrayString()) {
                writer.writeNext(s);
            }
            writer.flush();
        }
        writer.close();
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
        List<RevCommit> listCommit =new ArrayList<RevCommit>();
        for (RevCommit s : log) {
            listCommit.add(s);
        }
        return listCommit;
    }
    public void csvFile(String prName,String link,int version) throws IOException, ParseException, GitAPIException {
        this.projectName=prName;
        this.linkRepo=link;
        File f = new File("./Test"+projectName);
        Git git;
        try {
            git = Git.cloneRepository().setURI(linkRepo)
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
        this.listaF=new ArrayList<ARFFList>();
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
                if (j != listCommit.size())
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
            printOnFile("/Users/kobero/Desktop/"+projectName+"_"+version+".csv");
        }
    }