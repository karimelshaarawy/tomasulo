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

//    public void terminate() {
//
//    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }
}





