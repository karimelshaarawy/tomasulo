public class loadSlot {

    String tag ;
    boolean busy;
    int address;
    int timeLeft;

    public loadSlot(String tag){
        this.tag=tag;
        busy=false;
        address=0;
        timeLeft=0;

    }
}
