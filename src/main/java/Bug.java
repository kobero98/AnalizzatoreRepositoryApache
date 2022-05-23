import java.util.ArrayList;

public class Bug {
    public String name;
    public InfoVersion fixed;
    public InfoVersion affected;
    public Bug(String name, ArrayList<InfoVersion>fixedList, ArrayList<InfoVersion>AffectedList){
        this.name=name;
        this.fixed=null;
        this.affected=null;
        if(fixedList.isEmpty()) return ;
        InfoVersion max=fixedList.get(0);
        for (InfoVersion app:fixedList)
        {
            if(max.getData().before(app.getData())) max=app;
        }
        fixed=new InfoVersion(max.getData(),max.getS());
        if(AffectedList.isEmpty()) return;
        max=AffectedList.get(0);
        for (InfoVersion app:AffectedList)
        {
            if(max.getData().after(app.getData())) max=app;
        }
        affected=new InfoVersion(max.getData(),max.getS());
    }
    public int distance(){
        return this.fixed.getData().compareTo(this.affected.getData());
    }
}
