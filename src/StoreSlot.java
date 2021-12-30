public class StoreSlot {

    boolean busy;
    int address;
    int timeLeft;
    double value;
    String Qi;

    public StoreSlot(){
        busy=false;
        address=0;
        timeLeft=0;
        value=0;
        Qi ="0";

    }
    public boolean execute(){
        if (this.busy==true) {
            if(timeLeft>0){
                timeLeft--;
                return false;
            }else {
                return true;
            }
        }
        return false;
    }
    public void print (){
        System.out.println(busy+"   "+address+ "      "+ Qi + "     "+value);
    }
}
