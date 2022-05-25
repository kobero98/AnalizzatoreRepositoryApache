package creator.file.csv;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class InfoJira {
    private  String projName="BOOKKEEPER";//AVRO
    private List<InfoVersion> listVersion;

    private static final String FIX="fixVersions";
    private static final String FIELDS="fields";
    private static final String RELEASEDATE="releaseDate";
    private static final String VERSION="versions";
    private static final String DATAPATH="yyyy-MM-dd";
    public InfoJira(String nome){
        this.projName=nome;
    }
    private InfoVersion searchOpening(Date d){
        for (InfoVersion s:listVersion)
        {
            if(s.getData().after(d)) return s;
        }
        return null;
    }
    private void sort(List <InfoVersion> s){
            int i;
            int j;
            int dim=s.size();
            for (i=0;i<dim;i++){
                for (j=i;j<dim;j++)
                {
                    if(s.get(i).getData().after(s.get(j).getData()))
                    {
                        InfoVersion t=s.get(i);
                        s.set(i,s.get(j));
                        s.set(j,t);
                    }
                }
            }
    }
    private List<InfoVersion> creationFixedList(JSONArray issues,int dim,int i,String arg) throws ParseException {
        String nome;
        Date data;
        ArrayList<InfoVersion> fixedList=new ArrayList<InfoVersion>();
        for (int k=0;k<dim;k++) {
            if(issues.getJSONObject(i % 50).getJSONObject(FIELDS).getJSONArray(arg).getJSONObject(k).getBoolean("released") &&
                    !issues.getJSONObject(i % 50).getJSONObject(FIELDS).getJSONArray(arg).getJSONObject(k).isNull(RELEASEDATE)) {
                nome = issues.getJSONObject(i % 50).getJSONObject(FIELDS).getJSONArray(arg).getJSONObject(k).getString("name");
                data = new SimpleDateFormat(DATAPATH).parse(issues.getJSONObject(i % 50).getJSONObject(FIELDS).getJSONArray(arg).getJSONObject(k).getString(RELEASEDATE));
                fixedList.add(new InfoVersion(data,nome));
            }
        }
        return fixedList;
    }
    private void proportionIncrease(int fv, int ov, int av)
    {
        if (fv - ov != 0) Proportion.getPropotion().increment(((double) (fv - av)) / (fv - ov));
        else Proportion.getPropotion().increment(0);
    }
    private void insertNormalBug(Bug b,List<Bug> bug){
        if (b.getAffected() != null && b.distance() > 0) {
            bug.add(b);
            Proportion.getPropotion().increment(0);
        }
    }
    private int openingVersion(JSONArray issues,int i) throws ParseException {
        int ov=-1;
        if (!issues.getJSONObject(i % 50).getJSONObject(FIELDS).isNull("created")) {
            String opening = issues.getJSONObject(i % 50).getJSONObject(FIELDS).get("created").toString();
            Date openingVersion = new SimpleDateFormat(DATAPATH).parse(opening);
            InfoVersion v = searchOpening(openingVersion);
            ov = this.listVersion.indexOf(v);
        }
        return ov;
    }
    private void addBugList(JSONArray issues,int i,List<Bug> bug,Bug b) throws ParseException {
        if (b.getFixed() != null) {
            List<InfoVersion> affectedList=new ArrayList<InfoVersion>();
            List<InfoVersion> fixedList=new ArrayList<InfoVersion>();
            int fv = this.listVersion.indexOf(b.getFixed());
            int ov= openingVersion(issues,i);
            if (ov > -1 && ov < fv) {
                if (b.getAffected() == null || b.distance() < 0) {
                    double p = Proportion.getPropotion().getValor();
                    long round = Math.round(fv - (fv - ov) * p);
                    if ((int) round < 0) affectedList.add(this.listVersion.get(0));
                    else affectedList.add(this.listVersion.get((int) round));
                    fixedList.add(b.getFixed());
                    b = new Bug(b.getName(), fixedList, affectedList);
                    Proportion.getPropotion().increment(0);
                } else {
                    int av = this.listVersion.indexOf(b.getAffected());
                    proportionIncrease(fv,ov,av);
                }
                if (b.distance() != 0) bug.add(b);
            } else {
                insertNormalBug(b,bug);
            }
        }
    }
    public  List<Bug> listBug() throws IOException, ParseException {
        Integer i=0;
        Integer j=0;
        ArrayList<Bug> bug;
        bug = new ArrayList<Bug>();
        int total=1;
        do{
            String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                    + projName + "%22AND%20fixVersion%20is%20not%20EMPTY%20%20AND%20type%20%3D%20Bug%20AND%20(status%20%3D%20Closed%20OR%20status%20%3DResolved)%20" +
                    "&fields=key,fixVersions,versions,created,resolution&&startAt="
                    + j.toString();
            j=i+50;
            JSONObject json = readJsonFromUrl(url);
            JSONArray issues = json.getJSONArray("issues");
            total = json.getInt("total");
            for (; i < total && i<j; i++) {
                String name = issues.getJSONObject(i%50).getString("key");
                int dim=issues.getJSONObject(i%50).getJSONObject(FIELDS).getJSONArray(FIX).length();
                List<InfoVersion> fixedList=creationFixedList(issues,dim,i,FIX);
                dim=issues.getJSONObject(i%50).getJSONObject(FIELDS).getJSONArray(VERSION).length();
                List<InfoVersion> affectedList=creationFixedList(issues,dim,i,VERSION);

                Bug b=new Bug(name,fixedList,affectedList);
                addBugList(issues,i,bug,b);
            }
        }while(i<total);
        return bug;
    }
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return new JSONArray(jsonText);
        } finally {
            is.close();
        }
    }
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } finally {
            is.close();
        }
    }
    public List<InfoVersion> listVersion() throws IOException, JSONException, ParseException {
        Integer i = 0;
        Integer total ;
        listVersion=new ArrayList<InfoVersion>();
        String url= "https://issues.apache.org/jira/rest/api/2/project/"+this.projName+"/version?";
        JSONObject json = readJsonFromUrl(url);
        JSONArray issues = json.getJSONArray("values");
        total = json.getInt("total");
        for (; i < total ; i++){
            String name=issues.getJSONObject(i).get("name").toString();
            Date day=null;
            if(!issues.getJSONObject(i).isNull(RELEASEDATE)) {
                day = new SimpleDateFormat(DATAPATH).parse(issues.getJSONObject(i).get(RELEASEDATE).toString());
                InfoVersion f = new InfoVersion(day, name);
                listVersion.add(f);
            }
        }
        sort(listVersion);
        return listVersion;
    }
}
