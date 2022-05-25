package creatoreFileCSV;

import java.util.List;

public class Bug {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public InfoVersion getFixed() {
        return fixed;
    }

    public void setFixed(InfoVersion fixed) {
        this.fixed = fixed;
    }

    public InfoVersion getAffected() {
        return affected;
    }

    public void setAffected(InfoVersion affected) {
        this.affected = affected;
    }

    private InfoVersion fixed;
    private InfoVersion affected;
    public Bug(String name, List<InfoVersion> fixedList, List<InfoVersion>affectedList){
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
        if(affectedList.isEmpty()) return;
        InfoVersion min=affectedList.get(0);
        for (InfoVersion app:affectedList)
        {
            if(min.getData().after(app.getData())) min=app;
        }
        affected=new InfoVersion(min.getData(),min.getS());
    }
    public int distance(){
        return this.fixed.getData().compareTo(this.affected.getData());
    }
}
