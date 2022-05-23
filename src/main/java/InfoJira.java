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
    private  String projName ="AVRO";
    private List<InfoVersion> listVersion;


    private InfoVersion searchOpening(Date d){
        for (InfoVersion s:listVersion)
        {
            if(d.after(s.getData())) return s;
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
    public  List<Bug> listBug() throws IOException, ParseException {
        Integer i=0;
        Integer j=0;
        ArrayList<Bug> bug;
        bug = new ArrayList<Bug>();
        int total=1;
        do{
            String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                    + projName + "%22AND%20affectedVersion%20%20is%20not%20EMPTY%20AND%20fixVersion%20is%20not%20EMPTY%20%20AND%20type%20%3D%20Bug%20AND%20(status%20%3D%20Closed%20OR%20status%20%3DResolved)%20" +
                    "&fields=key,fixVersions,versions,created,resolution&&startAt="
                    + j.toString();
            j=i+50;
            JSONObject json = readJsonFromUrl(url);
            JSONArray issues = json.getJSONArray("issues");
            total = json.getInt("total");
            for (; i < total && i<j; i++) {
                String name = issues.getJSONObject(i%50).getString("key");
                int dim=issues.getJSONObject(i%50).getJSONObject("fields").getJSONArray("fixVersions").length();
                String nome;
                Date data;
                ArrayList<InfoVersion> fixedList=new ArrayList<InfoVersion>();
                for (int k=0;k<dim;k++) {
                    if(issues.getJSONObject(i % 50).getJSONObject("fields").getJSONArray("fixVersions").getJSONObject(k).getBoolean("released") &&
                            !issues.getJSONObject(i % 50).getJSONObject("fields").getJSONArray("fixVersions").getJSONObject(k).isNull("releaseDate")) {
                        nome = issues.getJSONObject(i % 50).getJSONObject("fields").getJSONArray("fixVersions").getJSONObject(k).getString("name");
                        data = new SimpleDateFormat("yyyy-MM-dd").parse(issues.getJSONObject(i % 50).getJSONObject("fields").getJSONArray("fixVersions").getJSONObject(k).getString("releaseDate"));
                        fixedList.add(new InfoVersion(data,nome));
                    }
                }
                ArrayList<InfoVersion> affectedList=new ArrayList<InfoVersion>();
                dim=issues.getJSONObject(i%50).getJSONObject("fields").getJSONArray("versions").length();
                for (int k=0;k<dim;k++) {
                    if(issues.getJSONObject(i % 50).getJSONObject("fields").getJSONArray("versions").getJSONObject(k).getBoolean("released") &&
                            !issues.getJSONObject(i % 50).getJSONObject("fields").getJSONArray("versions").getJSONObject(k).isNull("releaseDate")) {
                        nome= issues.getJSONObject(i % 50).getJSONObject("fields").getJSONArray("versions").getJSONObject(k).getString("name");
                        data = new SimpleDateFormat("yyyy-MM-dd").parse(issues.getJSONObject(i % 50).getJSONObject("fields").getJSONArray("versions").getJSONObject(k).getString("releaseDate"));
                        affectedList.add(new InfoVersion(data,nome));
                    }
                }
                if(!fixedList.isEmpty()){
                    int ov=-1;
                    Bug b=new Bug(name,fixedList,affectedList);
                    int fv= this.listVersion.indexOf(b.getFixed());
                    String opening;
                    InfoVersion v=null;
                    if(!issues.getJSONObject(i%50).getJSONObject("fields").isNull("created")) {
                        opening = issues.getJSONObject(i % 50).getJSONObject("fields").get("created").toString();
                        Date openingVersion = new SimpleDateFormat("yyyy-MM-dd").parse(opening);
                        v = searchOpening(openingVersion);
                        ov = this.listVersion.indexOf(v);
                    }
                    if(ov>-1 && ov<fv) {
                        if (b.getAffected() == null || b.distance() < 0) {
                            int p = Proportion.getPropotion().getValor();
                            int index = this.listVersion.indexOf(v);
                            affectedList.add(this.listVersion.get(fv - (fv - index) * p));
                            b = new Bug(name, fixedList, affectedList);
                        } else {
                            int av = this.listVersion.indexOf(b.getFixed());
                            Proportion.getPropotion().increment(((double) (fv - av)) / (fv - ov));
                        }
                        if (b.distance() != 0) bug.add(b);
                    }
                    else{
                        if(b.getAffected()!=null && b.distance()>0)
                        {
                            bug.add(b);
                        }
                    }
                }
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
            JSONArray json = new JSONArray(jsonText);
            return json;
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
            if(!issues.getJSONObject(i).isNull("releaseDate")) {
                day = new SimpleDateFormat("yyyy-MM-dd").parse(issues.getJSONObject(i).get("releaseDate").toString());
                InfoVersion f = new InfoVersion(day, name);
                listVersion.add(f);
            }
        }
        sort(listVersion);
        return listVersion;
    }
}
