public class propotion {
    private static propotion me=null;
    private int n;
    private double durata;
    private propotion(){
        this.n=0;
        this.durata=0.0;
    }
    public static propotion getPropotion(){
        if(me==null) me=new propotion();
        return me;
    }
    public void increment(double d){
        durata=durata+d;
        n=n+1;
    }
    public int getValor(){
       if(n==0) return 0;
       return (int) Math.round(durata/n);
    }
}
