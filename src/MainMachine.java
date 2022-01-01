import java.util.ArrayList;

public class MainMachine {

    int cycle;
    int instructionToIssue;
    ArrayList<Instruction> instructionsQueue ;
    Register [] registerFile ;
    ReservationSlot [] additionStation;
    ReservationSlot [] multiplicationStation;
    double[] memory;
    LoadSlot[] loadBuffer;
    StoreSlot[] storeBuffer;
    boolean busLineFree;


    public MainMachine(int regNum,int multStationsNum,int addStationNum,int memSize,int loadBufferSize,int storeBufferSize){
        this.cycle =0;
        this.instructionToIssue =0;
        this.instructionsQueue = new ArrayList<Instruction>();
        this.registerFile=new Register[regNum];
        this.additionStation =new ReservationSlot[addStationNum];
        this.multiplicationStation = new ReservationSlot[multStationsNum];
        this.memory = new double[memSize];
        this.loadBuffer = new LoadSlot[loadBufferSize];
        this.storeBuffer = new StoreSlot[storeBufferSize];
        this.busLineFree =true;
        //initialization
        for(int i=0;i<registerFile.length;i++)
            registerFile[i]=new Register("F"+i);
        for(int i = 0; i< additionStation.length; i++)
            additionStation[i]=new ReservationSlot("A"+i);
        for(int i = 0; i< multiplicationStation.length; i++)
            multiplicationStation[i]=new ReservationSlot("M"+i);
        for(int i = 0; i< loadBuffer.length; i++)
            loadBuffer[i]=new LoadSlot("L"+i);
        for(int i = 0; i< storeBuffer.length; i++)
            storeBuffer[i]=new StoreSlot();
    }

    public void checkUpdateAndExecute()
    {
        for(int i = 0; i< additionStation.length; i++)
        {
            ReservationSlot s = additionStation[i];
            boolean done = s.execute();
            if(done && busLineFree)
            {
                String tag = s.tag;
                double res = s.terminate();
                writeBackOP(tag,res);
                additionStation[i]=new ReservationSlot("A"+i);
            }
        }
        for(int i = 0; i< multiplicationStation.length; i++)
        {
            ReservationSlot s = multiplicationStation[i];
            boolean done = s.execute();
            if(done && busLineFree)
            {
                String tag = s.tag;
                double res = s.terminate();
                writeBackOP(tag,res);
                multiplicationStation[i]=new ReservationSlot("L"+i);
            }
        }
        for(int i = 0; i< loadBuffer.length; i++)
        {
            LoadSlot s = loadBuffer[i];
            boolean done = s.execute();
            if(done && busLineFree)
            {
//                writeBack()
                String tag = s.tag;
                int regNum = getRegNum(tag);
                double res = memory[s.address];
                registerFile[regNum].setValue(res);
                writeBackOP(tag,res);
                loadBuffer[i]=new LoadSlot("M"+i);
                busLineFree=false;
            }
        }

        for(int i = 0; i< storeBuffer.length; i++)
        {
            StoreSlot s = storeBuffer[i];
            boolean done = s.execute();
            if(done)
            {
                //writeBack()
                int regNum = Integer.parseInt(s.Qi);
                memory[s.address]=registerFile[regNum].value;
                storeBuffer[i]=new StoreSlot();

            }
        }

    }

    private void writeBackOP(String tag, double res) {
        for (Register r :
                registerFile) {
            if (r.Qi.equals(tag))
            {
                r.value=res;
            }
        }
        for (ReservationSlot r :
                multiplicationStation) {
            if (r.Qj.equals(tag))
            {
                r.Qj="0";
                r.Vj=res;
            }
            if (r.Qk.equals(tag))
            {
                r.Qk="0";
                r.Vk=res;
            }
        }
        for (ReservationSlot r :
                additionStation) {
            if (r.Qj.equals(tag))
            {
                r.Qj="0";
                r.Vj=res;
            }
            if (r.Qk.equals(tag))
            {
                r.Qk="0";
                r.Vk=res;
            }
        }
        for (StoreSlot r :
                storeBuffer) {
            if (r.Qi.equals(tag))
            {
                r.Qi="0";
                r.value=res;
            }

        }
    }

    private int getRegNum(String register) {

        return Integer.parseInt(register.substring(1,register.length()));
    }



   



    public void fetch(){
        while(instructionToIssue<instructionsQueue.size() ) {
            int emptyIndex = checkIfStall();
            if(emptyIndex==-1){
                //stall
            }else{
                putInStation(emptyIndex);
            }
        }
    }

    public void putInStation(int emptyIndex){
        String op = instructionsQueue.get(instructionToIssue).operation;
        Instruction instructionToFetch = instructionsQueue.get(instructionToIssue);
        int addressToSet = Integer.parseInt(instructionToFetch.reg1);
        int registerIndex=-1;
        if(op=="L.D"){
           loadBuffer[emptyIndex].setBusy(true);
           loadBuffer[emptyIndex].setAddress(addressToSet);

            registerIndex=findRegisterIndex(instructionToFetch.destination);
            registerFile[registerIndex].Qi=instructionToFetch.destination;
        }
      //S.D F1 F2 100
        if(op=="S.D"){
            storeBuffer[emptyIndex].busy=true;
            storeBuffer[emptyIndex].address=addressToSet;
            registerIndex = findRegisterIndex(instructionToFetch.destination);
            if(registerFile[registerIndex].Qi=="0"){
                storeBuffer[emptyIndex].value=registerFile[registerIndex].value;
            }else{
                storeBuffer[emptyIndex].Qi=registerFile[registerIndex].Qi;

            }

            registerIndex=findRegisterIndex(instructionToFetch.destination);
            registerFile[registerIndex].Qi=instructionToFetch.destination;


        }
        //MUL F1 F2 F3
        if(op=="MUL"){
            multiplicationStation[emptyIndex].busy=true;
            registerIndex=findRegisterIndex(instructionToFetch.reg1);
            if(registerFile[registerIndex].Qi=="0"){
                multiplicationStation[emptyIndex].Vj=registerFile[registerIndex].value;
            }else{
                multiplicationStation[emptyIndex].Qj=registerFile[registerIndex].Qi;
            }

            registerIndex=findRegisterIndex(instructionToFetch.reg2);
            if(registerFile[registerIndex].Qi=="0"){
                multiplicationStation[emptyIndex].Vk=registerFile[registerIndex].value;
            }else{
                multiplicationStation[emptyIndex].Qk=registerFile[registerIndex].Qi;
            }

            registerIndex=findRegisterIndex(instructionToFetch.destination);
            registerFile[registerIndex].Qi=instructionToFetch.destination;
        }

        if(op=="ADD" || op=="SUB"){
            additionStation[emptyIndex].busy=true;
            registerIndex=findRegisterIndex(instructionToFetch.reg1);
            if(registerFile[registerIndex].Qi=="0"){
                additionStation[emptyIndex].Vj=registerFile[registerIndex].value;
            }else{
                additionStation[emptyIndex].Qj=registerFile[registerIndex].Qi;
            }

            registerIndex=findRegisterIndex(instructionToFetch.reg2);
            if(registerFile[registerIndex].Qi=="0"){
                additionStation[emptyIndex].Vk=registerFile[registerIndex].value;
            }else{
                additionStation[emptyIndex].Qk=registerFile[registerIndex].Qi;
            }

            registerIndex=findRegisterIndex(instructionToFetch.destination);
            registerFile[registerIndex].Qi=instructionToFetch.destination;
        }

    }

    public int findRegisterIndex(String valueR){
        for (int i = 0; i <registerFile.length ; i++) {
            if(registerFile[i].tag.equals(valueR)){
                return i;
            }
        }
        return -1;
    }

    public int checkIfStall(){
        String op = instructionsQueue.get(instructionToIssue).operation;

        if(op=="L.D"){
            for(int i=0;i<loadBuffer.length;i++){
                if(loadBuffer[i].busy!=true){
                    return i;
                }
            }
        }

        if(op=="S.D"){
            for(int i=0;i<storeBuffer.length;i++){
                if(storeBuffer[i].busy!=true){
                    return i;
                }
            }
        }

        if(op=="MUL"){
            for(int i=0;i<multiplicationStation.length;i++){
                if(multiplicationStation[i].busy!=true){
                    return i;
                }
            }
        }

        if(op=="ADD" || op=="SUB"){
            for(int i=0;i<additionStation.length;i++){
                if(additionStation[i].busy!=true){
                    return i;
                }
            }
        }
        return -1;
    }

    public static void main (String[] args){


        MainMachine main = new MainMachine(32,3,2,1000,3,3);
        Instruction instruction = new Instruction("L.D","F4","100","F2");
        Instruction instruction2 = new Instruction("ADD","F4","100","F2");
        ReservationSlot slot = new ReservationSlot("M1");
        Register register =new Register("F4");
        LoadSlot load_slot =new LoadSlot("L1");
        StoreSlot storeSlot = new StoreSlot();
        System.out.println(" operation      issue     execution     write result");
        instruction.print();
        instruction2.print();
        System.out.println("Tag  Busy  operation  Vj   Vk   Qj   Qk");
        slot.print();
        System.out.println("Tag  Qi  value");
        register.print();
        System.out.println("Tag  Busy  Address");
        load_slot.print();
        System.out.println("Busy  Address  Qi    Value");
        storeSlot.print();
    }

}
