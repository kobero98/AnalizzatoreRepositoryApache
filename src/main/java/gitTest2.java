import com.opencsv.CSVWriter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
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


public class gitTest2 {
    private static String projectName="Bookkeeper";
    private static String linkRepo="https://github.com/kobero98/avro.git";

    /*
    private static String projectName="./bookkeeper";*/
    //private static String linkRepo="https://github.com/apache/bookkeeper.git";

    private static String getIDJira(String shortestMessage){
        String s=null;
        Pattern pattern= Pattern.compile(projectName.toUpperCase()+"-[0-9]+");
        Matcher matcher= pattern.matcher(shortestMessage);
        if(matcher.find()){
             s=matcher.group(0);
        }
        return s;
    }
    private static Bug bugContains(List<Bug> list, String id){
        for (Bug b:list){
            if(b.name.equals(id)) return b;
        }
        return null;
    }
    private static void setBuggy(Bug b,List<ARFFList> list,List<infoVersion> versions,String path){
        if(b==null) return;
        int inizio= versions.indexOf(b.affected);
        int fine=versions.indexOf(b.fixed)-1;
        for (int k=inizio;k<fine && k<list.size()-1;k++){
            for (row r:list.get(k).getRows())
            {
                if(r.getPath().equals(path)) r.setBuggy(true);
            }
        }
    }
    public static void main(String[] arg) throws IOException, ParseException, GitAPIException {
        File f = new File("./Test"+projectName);
        Git git = null;
        try {
            git = Git.cloneRepository().setURI(linkRepo)
                    .setDirectory(f).setCloneAllBranches(true).call();
        } catch (GitAPIException | JGitInternalException e) {
            git = Git.open(f);
                git.pull().call();

        }
        try {
            List<Ref> t = git.tagList().call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
        Iterable<RevCommit> log = null;
        try {
            log = git.log().call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        infoJira t = new infoJira();
        CSVWriter writer = new CSVWriter(new FileWriter("/Users/kobero/Desktop/"+projectName+".csv"));
        writer.writeNext(new String[]{"Release","Path","Size","Numero_Commit","Numero_commit_Release","Numero_Lavoratori","LOC_TOUCHED","LOC_Added","Max_LOC_Added","AVG_LOCADDED","Churn","Max_Churn","Avg_Churn","IsBuggy"});
        List<infoVersion> stringList = t.ListVersion();
        ArrayList<Bug> listBug=t.ListBug();
        stringList.add(new infoVersion(Date.from(Instant.now()), "VersionAncoraNonConosciuta"));
        int i = 0;
        List<RevCommit> ListCommit = new ArrayList<RevCommit>();
        for (RevCommit s : log) {
            ListCommit.add(s);
        }
        List<ARFFList> listaF=new ArrayList<ARFFList>();
        ARFFList c=new ARFFList(stringList.get(0).getS());
        listaF.add(c);
        int j;
        int k = 0;
        for (j = ListCommit.size(); j >= 1; j--) {
            RevCommit actual = null;
            boolean flag=false;
            if (j != ListCommit.size())
                actual = ListCommit.get(j);
            RevCommit success = ListCommit.get(j - 1);
            String idJira=getIDJira(success.getShortMessage());
            Bug bugCatch=bugContains(listBug,idJira);
            String nameAuthor = success.getAuthorIdent().getName();
            Date dataSuccess = success.getCommitterIdent().getWhen();
            try (ObjectReader reader = git.getRepository().newObjectReader()) {
                AbstractTreeIterator oldTreeIterator = new EmptyTreeIterator();
                if (j != ListCommit.size())
                    oldTreeIterator = new CanonicalTreeParser(null, reader, actual.getTree().getId());
                AbstractTreeIterator newTreeIterator = new CanonicalTreeParser(null, reader, success.getTree().getId());
                try (DiffFormatter formatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
                    formatter.setRepository(git.getRepository());
                    List<DiffEntry> ListDiff = formatter.scan(oldTreeIterator, newTreeIterator);
                    for (DiffEntry diff : ListDiff) {
                        if (diff.getNewPath().endsWith(".java") || diff.getOldPath().endsWith(".java")) {
                            EditList ListEdit=formatter.toFileHeader(diff).toEditList();
                            if (diff.getChangeType() == DiffEntry.ChangeType.ADD) {
                                row app = new row(diff.getNewPath());
                                app.increaseNCommit();
                                app.modifySizeByEdit(ListEdit);
                                app.readyWorkerOn(nameAuthor);
                                c.add(app);
                                }
                            else {
                                if (diff.getChangeType() == DiffEntry.ChangeType.MODIFY || diff.getChangeType() == DiffEntry.ChangeType.COPY || diff.getChangeType() == DiffEntry.ChangeType.RENAME) {
                                    int index = c.contains(diff.getOldPath());
                                    c.getRows().get(index).setPath(diff.getNewPath());
                                    c.increaseNCommit(index);
                                    c.getRows().get(index).modifySizeByEdit(ListEdit);
                                    c.increaseWorkOnCommit(index, nameAuthor);
                                    setBuggy(bugCatch,listaF,stringList,diff.getOldPath());
                                } else {
                                    if (diff.getChangeType() == DiffEntry.ChangeType.DELETE) c.remove(diff.getOldPath());
                                    setBuggy(bugCatch,listaF,stringList,diff.getOldPath());
                                }
                            }
                        }
                    }
                }
                }
            if (dataSuccess.after(stringList.get(k).getData())) {
                    k++;
                    ArrayList<row> finta=new ArrayList<>();
                    for (row app:c.getRows()){
                        row s=new row(app);
                        finta.add(s);
                    }
                    c=new ARFFList(stringList.get(k).getS());
                    c.getRows().addAll(finta);
                    listaF.add(c);
                 }
            }
            for (ARFFList appoggio:listaF) {
                for (String[] s : appoggio.toArrayString()) {
                    writer.writeNext(s);
                }
                writer.flush();
            }
            writer.close();
        }
    }