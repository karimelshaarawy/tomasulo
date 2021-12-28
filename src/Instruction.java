public class Instruction {

    String operation;
    String destination;
    String reg1 ;
    String reg2 ;
    int issue;
    int executionStart ;
    int executionEnd ;
    int resultWrite ;

    public Instruction(String operation,String destination,String reg1, String reg2){
        this.operation=operation;
        this.destination=destination;
        this.reg1=reg1;
        this.reg2=reg2;
        this.issue=-1;
        this.executionStart=-1;
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




}
