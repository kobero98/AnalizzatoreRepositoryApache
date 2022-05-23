import java.util.Date;

public class InfoVersion {
    private Date data;
    private String s;
    public InfoVersion(){}
    public InfoVersion(Date data, String nome){
        this.data=data;
        this.s=nome;
    }
    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    @Override
    public boolean equals(Object obj) {

        if(obj.getClass()==this.getClass()) 
            return ((InfoVersion) obj).getS().equals(this.getS());
        return false;
    }
}
