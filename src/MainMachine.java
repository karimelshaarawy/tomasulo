import java.util.ArrayList;

public class MainMachine {

    int cycle;
    int instructionToIssue;
    ArrayList<Instruction> instructionsQueue ;
    Register [] registerFile ;
    ReservationSlot [] addtionStation;
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
        this.addtionStation =new ReservationSlot[addStationNum];
        this.multiplicationStation = new ReservationSlot[multStationsNum];
        this.memory = new double[memSize];
        this.loadBuffer = new LoadSlot[loadBufferSize];
        this.storeBuffer = new StoreSlot[storeBufferSize];
        this.busLineFree =true;
        //initialization
        for(int i=0;i<registerFile.length;i++)
            registerFile[i]=new Register("F"+i);
        for(int i = 0; i< addtionStation.length; i++)
            addtionStation[i]=new ReservationSlot("A"+i);
        for(int i = 0; i< multiplicationStation.length; i++)
            multiplicationStation[i]=new ReservationSlot("M"+i);
        for(int i = 0; i< loadBuffer.length; i++)
            loadBuffer[i]=new LoadSlot("L"+i);
        for(int i = 0; i< storeBuffer.length; i++)
            storeBuffer[i]=new StoreSlot();
    }

    public void checkUpdateAndExecute()
    {
        for(int i = 0; i< addtionStation.length; i++)
        {
            ReservationSlot s = addtionStation[i];
            boolean done = s.execute();
            if(done && busLineFree)
            {
                String tag = s.tag;
                double res = s.terminate();
                writeBackOP(tag,res);
                addtionStation[i]=new ReservationSlot("A"+i);
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
                addtionStation) {
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
