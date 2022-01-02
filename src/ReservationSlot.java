import javax.swing.plaf.basic.BasicSliderUI;

public class ReservationSlot {

    String tag ;
    Boolean busy;
    String operation;
    double Vj ;
    double Vk ;
    String Qj ;
    String Qk ;
    int timeLeft;
    int id;
//Test
    public ReservationSlot(String tag){
        this.tag=tag;
        this.operation="";
        busy=false;
        Vj=0;
        Vk=0;
        Qj="0";
        Qk="0";
        timeLeft=0;
        id=-1;

    }
    public boolean execute() {
        if (this.busy==true) {
            if(Qj.equals("0")&& Qk.equals("0")){
                if(timeLeft>0){
                timeLeft--;
                return false;
            }else {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public double terminate(){
        this.busy=true;
        if (operation.equals("add"))
            return Vj+ Vk;
        else if (operation.equals("sub"))
            return Vj- Vk;
        else if (operation.equals("mul"))
            return Vj*Vk;
        else
            return Vj/Vk;
    }

    public void print() {
        System.out.println(tag +"   "+busy + "  " + operation + "     " + Vj +"   " +Vk +"   "+Qj +"   "+Qk);
    }


}
