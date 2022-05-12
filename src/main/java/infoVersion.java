import java.util.Date;

public class infoVersion {
    private Date data;
    private String s;
    public infoVersion(Date data,String nome){
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
}
