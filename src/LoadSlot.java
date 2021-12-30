public class LoadSlot {

    String tag ;
    boolean busy;
    int address;
    int timeLeft;

    public LoadSlot(String tag){
        this.tag=tag;
        busy=false;
        address=0;
        timeLeft=0;

    }
    public void print() {
        System.out.println(tag+"   "+busy + "   "+ address);
    }

}
