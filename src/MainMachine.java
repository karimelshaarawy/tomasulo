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
    int addTime;
    int subTime;
    int multiplyTime;
    int divideTime;
    int loadTime ;
    int storeTime;




    public MainMachine(int regNum,int mulStationsNum,int addStationNum,int memSize,int loadBufferSize,int storeBufferSize,int addTime,int subTime,int multiplyTime,int divideTime,int loadTime ,int storeTime){
        this.cycle =1;
        this.instructionToIssue =0;
        this.instructionsQueue = new ArrayList<Instruction>();
        this.registerFile=new Register[regNum];
        this.additionStation =new ReservationSlot[addStationNum];
        this.multiplicationStation = new ReservationSlot[mulStationsNum];
        this.memory = new double[memSize];
        this.loadBuffer = new LoadSlot[loadBufferSize];
        this.storeBuffer = new StoreSlot[storeBufferSize];
        this.busLineFree =true;
        this.addTime=addTime;
        this.subTime=subTime;
        this.multiplyTime=multiplyTime;
        this.divideTime=divideTime;
        this.loadTime=loadTime;
        this.storeTime=storeTime;



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
        String busTag="";
        double busRes=-1;
        for(int i = 0; i< additionStation.length; i++)
        {
            ReservationSlot s = additionStation[i];
            boolean done = s.execute();
            if(s.busy&&s.Qk.equals("0")&&s.Qj.equals("0")&& instructionsQueue.get(additionStation[i].id).executionStart==-1){
                instructionsQueue.get(additionStation[i].id).executionStart=cycle;
                if(additionStation[i].operation.equals("add"))
                    instructionsQueue.get(additionStation[i].id).executionEnd=cycle+addTime-1;

                if(additionStation[i].operation.equals("sub"))
                    instructionsQueue.get(additionStation[i].id).executionEnd=cycle+subTime-1;
            }

            if(done && busLineFree)
            {


                String tag = s.tag;
                double res = s.terminate();
                busTag=tag;
                busRes=res;
                //writeBackOP(tag,res);
                busLineFree=false;
                instructionsQueue.get(additionStation[i].id).resultWrite=cycle;
                additionStation[i]=new ReservationSlot("A"+i);
            }
        }
        for(int i = 0; i< multiplicationStation.length; i++)
        {
            ReservationSlot s = multiplicationStation[i];
            if(s.busy&&s.Qk.equals("0")&&s.Qj.equals("0")&& instructionsQueue.get(multiplicationStation[i].id).executionStart==-1){
                instructionsQueue.get(multiplicationStation[i].id).executionStart=cycle;
                if(multiplicationStation[i].operation.equals("div"))
                    instructionsQueue.get(multiplicationStation[i].id).executionEnd=cycle+divideTime-1;

                if(multiplicationStation[i].operation.equals("mul"))
                    instructionsQueue.get(multiplicationStation[i].id).executionEnd=cycle+multiplyTime-1;
            }

                boolean done = s.execute();
            if(done && busLineFree)
            {
                String tag = s.tag;
                double res = s.terminate();
                busTag=tag;
                busRes=res;
                //writeBackOP(tag,res);
                instructionsQueue.get(multiplicationStation[i].id).resultWrite=cycle;
                busLineFree=false;
                multiplicationStation[i]=new ReservationSlot("M"+i);
            }
        }
        for(int i = 0; i< loadBuffer.length; i++)
        {
            LoadSlot s = loadBuffer[i];
            if(s.busy&&instructionsQueue.get(loadBuffer[i].id).executionStart==-1){
                instructionsQueue.get(loadBuffer[i].id).executionStart=cycle;
                instructionsQueue.get(loadBuffer[i].id).executionEnd=cycle+loadTime-1;

            }


                boolean done = s.execute();
            if(done && busLineFree)
            {
//                writeBack()
                String tag = s.tag;
               /* int regNum = getRegNum(s);

                registerFile[regNum].setValue(res);*/
                double res = memory[s.address];
                busTag=tag;
                busRes=res;
                //writeBackOP(tag,res);
                instructionsQueue.get(loadBuffer[i].id).resultWrite=cycle;
                loadBuffer[i]=new LoadSlot("L"+i);
                busLineFree=false;
            }
        }

        for(int i = 0; i< storeBuffer.length; i++)
        {
            StoreSlot s = storeBuffer[i];
            if(s.busy&&s.Qi.equals("0")&&instructionsQueue.get(storeBuffer[i].id).executionStart==-1){
                instructionsQueue.get(storeBuffer[i].id).executionStart=cycle;
                instructionsQueue.get(storeBuffer[i].id).executionEnd=cycle+storeTime-1;

            }
            boolean done = s.execute();
            if(done)
            {

                //writeBack()

                memory[s.address]=s.value;
                instructionsQueue.get(storeBuffer[i].id).resultWrite=cycle;
                storeBuffer[i]=new StoreSlot();

            }
        }
        if(!busTag.equals("")){
            writeBackOP(busTag,busRes);
        }

    }

    private void writeBackOP(String tag, double res) {
        for (Register r :
                registerFile) {
            if (r.Qi.equals(tag))
            {
                r.value=res;
                r.Qi="0";
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


   public void addInstruction(Instruction instruction){
       instruction.id=instructionsQueue.size();
        instructionsQueue.add(instruction);


   }
   



    public void fetch(){
        while(instructionToIssue<instructionsQueue.size()|| !emptyStations() ) {
            if(cycle==4)
                System.out.println();
            checkUpdateAndExecute();
            if(instructionToIssue<instructionsQueue.size()){
            int emptyIndex = checkIfStall();

            if(emptyIndex!=-1) {
                putInStation(emptyIndex);
                instructionsQueue.get(instructionToIssue).issue=cycle;
                instructionToIssue++;
            }}

            // printing
            System.out.println("Cycle  "+ cycle);
            System.out.println(" operation      issue     execution     write result");
            for(int i=0;i<instructionsQueue.size();i++)
                instructionsQueue.get(i).print();
            System.out.println("Tag  Qi  value");
            for(int i=0;i<registerFile.length; i++)
                registerFile[i].print();
            System.out.println("Tag  Busy  operation  Vj   Vk   Qj   Qk");
            for(int i=0;i<multiplicationStation.length; i++)
                multiplicationStation[i].print();
            System.out.println("Tag  Busy  operation  Vj   Vk   Qj   Qk");
            for(int i=0;i<additionStation.length; i++)
                additionStation[i].print();
            System.out.println("Tag  Busy  Address");
            for(int i=0;i<loadBuffer.length; i++)
                loadBuffer[i].print();
            System.out.println("Busy  Address  Qi    Value");
            for(int i=0;i<storeBuffer.length; i++)
                storeBuffer[i].print();
            System.out.println("\n\n");
            cycle++;
            busLineFree=true;

        }
    }

    public Boolean emptyStations(){
        for(int i=0;i<additionStation.length;i++)
            if(additionStation[i].busy==true)
                return false;
        for(int i=0;i<multiplicationStation.length;i++)
            if(multiplicationStation[i].busy==true)
                return false;
        for(int i=0;i<storeBuffer.length;i++)
            if(storeBuffer[i].busy==true)
                return false;
        for(int i=0;i<loadBuffer.length;i++)
            if(loadBuffer[i].busy==true)
                return false;

            return true;


    }

    public void putInStation(int emptyIndex){
        String op = instructionsQueue.get(instructionToIssue).operation;
        int id =instructionsQueue.get(instructionToIssue).id;
        Instruction instructionToFetch = instructionsQueue.get(instructionToIssue);
        int addressToSet = -1;
        int registerIndex=-1;
        if(op.equals("L.D")){
            addressToSet = Integer.parseInt(instructionToFetch.reg1);
           loadBuffer[emptyIndex].setBusy(true);
           loadBuffer[emptyIndex].setAddress(addressToSet);
           loadBuffer[emptyIndex].timeLeft=loadTime;
            loadBuffer[emptyIndex].id=id;
            registerIndex=findRegisterIndex(instructionToFetch.destination);
            registerFile[registerIndex].Qi=loadBuffer[emptyIndex].tag;
        }
      //S.D F1 F2 100
        if(op.equals("S.D")){
            addressToSet = Integer.parseInt(instructionToFetch.reg1);
            storeBuffer[emptyIndex].busy=true;
            storeBuffer[emptyIndex].address=addressToSet;
            storeBuffer[emptyIndex].timeLeft=storeTime;
            storeBuffer[emptyIndex].id=id;
            registerIndex = findRegisterIndex(instructionToFetch.destination);
            if(registerFile[registerIndex].Qi.equals("0")){
                storeBuffer[emptyIndex].value=registerFile[registerIndex].value;
            }else{
                storeBuffer[emptyIndex].Qi=registerFile[registerIndex].Qi;

            }



        }
        //MUL F1 F2 F3
        if(op.equals("MUL")||op.equals("DIV")){
            multiplicationStation[emptyIndex].busy=true;
            multiplicationStation[emptyIndex].id=id;
            if(op.equals("MUL")){
            multiplicationStation[emptyIndex].timeLeft=multiplyTime;
            multiplicationStation[emptyIndex].operation="mul";
            }
            else if (op.equals("DIV")){
                multiplicationStation[emptyIndex].timeLeft=divideTime;
                multiplicationStation[emptyIndex].operation="div";
            }
            registerIndex=findRegisterIndex(instructionToFetch.reg1);
            if(registerFile[registerIndex].Qi.equals("0")){
                multiplicationStation[emptyIndex].Vj=registerFile[registerIndex].value;
            }else{
                multiplicationStation[emptyIndex].Qj=registerFile[registerIndex].Qi;
            }

            registerIndex=findRegisterIndex(instructionToFetch.reg2);
            if(registerFile[registerIndex].Qi.equals("0")){
                multiplicationStation[emptyIndex].Vk=registerFile[registerIndex].value;
            }else{
                multiplicationStation[emptyIndex].Qk=registerFile[registerIndex].Qi;
            }

            registerIndex=findRegisterIndex(instructionToFetch.destination);
            registerFile[registerIndex].Qi=multiplicationStation[emptyIndex].tag;
        }

        if(op.equals("ADD") || op.equals("SUB")){
            additionStation[emptyIndex].busy=true;
            additionStation[emptyIndex].id=id;
            if(op.equals("ADD")){
                additionStation[emptyIndex].timeLeft=addTime;
                additionStation[emptyIndex].operation="add";
            }
            else {
                additionStation[emptyIndex].timeLeft = subTime;
                additionStation[emptyIndex].operation="sub";
            }
            registerIndex=findRegisterIndex(instructionToFetch.reg1);
            if(registerFile[registerIndex].Qi.equals("0")){
                additionStation[emptyIndex].Vj=registerFile[registerIndex].value;
            }else{
                additionStation[emptyIndex].Qj=registerFile[registerIndex].Qi;
            }

            registerIndex=findRegisterIndex(instructionToFetch.reg2);
            if(registerFile[registerIndex].Qi.equals("0")){
                additionStation[emptyIndex].Vk=registerFile[registerIndex].value;
            }else{
                additionStation[emptyIndex].Qk=registerFile[registerIndex].Qi;
            }

            registerIndex=findRegisterIndex(instructionToFetch.destination);
            registerFile[registerIndex].Qi=additionStation[emptyIndex].tag;
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

        if(op.equals("L.D")){
            for(int i=0;i<loadBuffer.length;i++){
                if(loadBuffer[i].busy!=true){
                    return i;
                }
            }
        }

        if(op.equals("S.D")){
            for(int i=0;i<storeBuffer.length;i++){
                if(storeBuffer[i].busy!=true){
                    return i;
                }
            }
        }

        if(op.equals("MUL")||op.equals("DIV")){
            for(int i=0;i<multiplicationStation.length;i++){
                if(multiplicationStation[i].busy!=true){
                    return i;
                }
            }
        }

        if(op.equals("ADD") || op.equals("SUB")){
            for(int i=0;i<additionStation.length;i++){
                if(additionStation[i].busy!=true){
                    return i;
                }
            }
        }
        return -1;
    }

    public static void main (String[] args){


        MainMachine main = new MainMachine(32,2,3,1000,3,3,2,2,10,40,2,1);
        //f6
        main.memory[32]=10;
        //f2
        main.memory[44]=2;
        //f4
        main.registerFile[4].value=1.0;
        Instruction instruction = new Instruction("L.D","F6","32","");
        Instruction instruction2 = new Instruction("L.D","F2","44","");
        Instruction instruction3 = new Instruction("MUL","F0","F2","F4");
        Instruction instruction4 = new Instruction("SUB","F8","F2","F6");
        Instruction instruction5 = new Instruction("DIV","F10","F0","F6");
        Instruction instruction6 = new Instruction("ADD","F6","F8","F2");
//        Instruction instruction4= new Instruction("SUB","F0","100","F2");
//        ReservationSlot slot = new ReservationSlot("M1");
//        Register register =new Register("F4");
//        LoadSlot load_slot =new LoadSlot("L1");
//        StoreSlot storeSlot = new StoreSlot();
//        System.out.println(" operation      issue     execution     write result");
//        instruction.print();
//        instruction2.print();
//        System.out.println("Tag  Busy  operation  Vj   Vk   Qj   Qk");
//        slot.print();
//        System.out.println("Tag  Qi  value");
//        register.print();
//        System.out.println("Tag  Busy  Address");
//        load_slot.print();
//        System.out.println("Busy  Address  Qi    Value");
//        storeSlot.print();
        main.addInstruction(instruction);
        main.addInstruction(instruction2);
        main.addInstruction(instruction3);
        main.addInstruction(instruction4);
        main.addInstruction(instruction5);
        main.addInstruction(instruction6);
        main.fetch();
    }
















}
