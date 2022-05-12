import com.opencsv.CSVWriter;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class gitTest2 {
    private static String projectName="./TestAvro";
    private static String linkRepo="https://github.com/kobero98/avro.git";
    /*
    private static String projectName="./bookkeeper";
    private static String linkRepo="https://github.com/apache/bookkeeper.git";
  */
    public static void main(String[] arg) throws IOException, ParseException {
        File f = new File(projectName);
        Git git = null;
        try {
            git = Git.cloneRepository().setURI(linkRepo)
                    .setDirectory(f).setCloneAllBranches(true).call();
        } catch (GitAPIException | JGitInternalException e) {
            try {
                git = Git.open(f);
                git.pull().call();
            } catch (IOException es) {
                e.printStackTrace();
            } catch (CanceledException es) {
                e.printStackTrace();
            } catch (NoHeadException es) {
                e.printStackTrace();
            } catch (RefNotAdvertisedException es) {
                e.printStackTrace();
            } catch (RefNotFoundException es) {
                e.printStackTrace();
            } catch (WrongRepositoryStateException es) {
                e.printStackTrace();
            } catch (InvalidRemoteException es) {
                e.printStackTrace();
            } catch (TransportException es) {
                e.printStackTrace();
            } catch (InvalidConfigurationException es) {
                e.printStackTrace();
            } catch (GitAPIException es) {
                e.printStackTrace();
            }
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
        CSVWriter writer = new CSVWriter(new FileWriter("/Users/kobero/Desktop/try.csv"));
        writer.writeNext(new String[]{"Release","Path","Size","Numero_Commit","Numero_Lavoratori","LOC_TOUCHED","LOC_Added","Max_LOC_Added","AVG_LOCADDED","Churn","Max_Churn","Avg_Churn","IsBuggy"});
        List<infoVersion> stringList = t.ListVersion();
        stringList.add(new infoVersion(Date.from(Instant.now()), "VersionAncoraNonConosciuta"));
        int i = 0;
        ArrayList<RevCommit> ListCommit = new ArrayList<RevCommit>();
        for (RevCommit s : log) {
            System.out.println(s.getShortMessage());
            ListCommit.add(s);
        }
        ARFFList c=new ARFFList(stringList.get(0).getS());
        int j;
        int k = 0;
        for (j = ListCommit.size(); j >= 1; j--) {
            RevCommit actual = null;
            Date dataActual = null;
            if (j != ListCommit.size()) {
                actual = ListCommit.get(j);
                dataActual = actual.getCommitterIdent().getWhen();
            }
            RevCommit success = ListCommit.get(j - 1);
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
                                app.setN_commit(1);
                                app.modifySizeByEdit(ListEdit);
                                app.readyWorkerOn(nameAuthor);
                                c.add(app);
                                }
                            else {
                                if (diff.getChangeType() == DiffEntry.ChangeType.MODIFY || diff.getChangeType() == DiffEntry.ChangeType.COPY || diff.getChangeType() == DiffEntry.ChangeType.RENAME) {
                                    int index = c.contains(diff.getOldPath().toString());
                                    c.rows.get(index).setPath(diff.getNewPath());
                                    c.increaseNCommit(index);
                                    c.rows.get(index).modifySizeByEdit(ListEdit);
                                    c.increaseWorkOnCommit(index, nameAuthor);

                                } else {
                                    if (diff.getChangeType() == DiffEntry.ChangeType.DELETE) c.remove(diff.getOldPath());
                                }
                            }
                        }
                    }
                }
                }
                if (dataSuccess.after(stringList.get(k).getData())) {
                    for (String[] s : c.toArrayString()) {
                        writer.writeNext(s);
                    }
                    writer.flush();
                    k++;
                    c.setRelease(stringList.get(k).getS());
                 }
                }

            RetrieveTicketsID ticket = new RetrieveTicketsID();
            writer.close();
        }
    }