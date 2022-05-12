import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class infoJira {
    private  String projName ="AVRO";

    private boolean contain(List<infoVersion> s,infoVersion l)
    {
        for(infoVersion app:s){
            if(app.getS().compareTo(l.getS())==0)
            {
                return true;
            }
        }
        return false;
    }
    private void sort(List <infoVersion> s){
            int i,j;
            int dim=s.size();
            for (i=0;i<dim;i++){
                for (j=i;j<dim;j++)
                {
                    if(s.get(i).getData().after(s.get(j).getData()))
                    {
                        infoVersion t=s.get(i);
                        s.set(i,s.get(j));
                        s.set(j,t);
                    }
                }
            }
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
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
    public List<infoVersion> ListVersion() throws IOException, JSONException, ParseException {
        Integer j = 0, i = 0, total = 1;
        List<infoVersion> s=new ArrayList<infoVersion>();
        //Get JSON API for closed bugs w/ AV in the project
        //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000

            /*String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                    + projName + "%22&fields=key,resolutiondate,versions,created&startAt="
                    + i.toString() + "&maxResults=" + j.toString();

             */
            String url= "https://issues.apache.org/jira/rest/api/2/project/"+this.projName+"/version?";
            JSONObject json = readJsonFromUrl(url);
            JSONArray issues = json.getJSONArray("values");
            total = json.getInt("total");
            for (; i < total ; i++){
                String name=issues.getJSONObject(i).get("name").toString();
                Date day=null;
                if(!issues.getJSONObject(i).isNull("releaseDate")) {
                    day = new SimpleDateFormat("yyyy-MM-dd").parse(issues.getJSONObject(i).get("releaseDate").toString());
                    infoVersion f = new infoVersion(day, name);
                    s.add(f);
                }
            }
           /* for (; i < total && i < j; i++) {
                //Iterate through each bug
                boolean x= issues.getJSONObject(i).getJSONObject("fields").getJSONArray("versions").isEmpty();
                if(!x) {
                    for (int k=0;k<issues.getJSONObject(i%1000).getJSONObject("fields").getJSONArray("versions").length();k++) {
                        String key = issues.getJSONObject(i % 1000).getJSONObject("fields").getJSONArray("versions").getJSONObject(k).get("name").toString();
                        Date day = null;
                        if (!issues.getJSONObject(i % 1000).getJSONObject("fields").getJSONArray("versions").getJSONObject(k).isNull("releaseDate")) {
                            day = new SimpleDateFormat("yyyy-MM-dd").parse(issues.getJSONObject(i % 1000).getJSONObject("fields").getJSONArray("versions").getJSONObject(k).get("releaseDate").toString());
                        }
                        infoVersion f = new infoVersion(day, key);
                        if (!contain(s,f) &&(day != null)) {
                            s.add(f);
                        }
                    }
                }
            }
            */

        sort(s);
        return s;
    }
}
