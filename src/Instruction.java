public class Instruction {

    String operation;
    String destination;
    String reg1 ;
    String reg2 ;
    int issue;
    int executionStart ;
    int executionEnd ;
    int resultWrite ;
    int id=-1;

    public Instruction(String operation,String destination,String reg1, String reg2){
        this.operation=operation;
        this.destination=destination;
        this.reg1=reg1;
        this.reg2=reg2;
        this.issue=-1;
        this.executionStart=-1;
        this.executionEnd=-1;
        this.resultWrite=-1;

    }

    public void setIssue(int issue){
        this.issue=issue;
    }
    public void setExecutionStart(int execution){
        this.executionStart=execution;
    }
    public void setExecutionEnd(int execution){
        this.executionEnd=execution;
    }
    public void setResultWrite(int resultWrite){
        this.resultWrite=resultWrite;
    }

    public void print(){
        if (operation.equals("L.D"))
        System.out.println(operation + " " + destination + " " + reg1 + "       "+ issue +"       "+ executionStart+".."+executionEnd+"          "+ resultWrite);
        else
            System.out.println(operation + " " + destination + " " + reg1 + " " + reg2 + "    " + issue +"       "+ executionStart+".."+executionEnd+"          "+ resultWrite);


    }




}
