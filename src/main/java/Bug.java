import java.util.ArrayList;

public class Bug {
    public String name;
    public infoVersion fixed;
    public infoVersion affected;
    public Bug(String name, ArrayList<infoVersion>fixedList,ArrayList<infoVersion>AffectedList){
        this.name=name;
        this.fixed=null;
        this.affected=null;
        if(fixedList.isEmpty()) return ;
        infoVersion max=fixedList.get(0);
        for (infoVersion app:fixedList)
        {
            if(max.getData().before(app.getData())) max=app;
        }
        fixed=new infoVersion(max.getData(),max.getS());
        if(AffectedList.isEmpty()) return;
        max=AffectedList.get(0);
        for (infoVersion app:AffectedList)
        {
            if(max.getData().after(app.getData())) max=app;
        }
        affected=new infoVersion(max.getData(),max.getS());
    }
    public int distance(){
        return this.fixed.getData().compareTo(this.affected.getData());
    }
}
