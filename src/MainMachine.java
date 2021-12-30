import java.util.ArrayList;

public class MainMachine {

    int Cycle ;
    int InstructionToIssue;
    ArrayList<Instruction> InstructionsQueue ;
    Register [] RegisterFile ;
    ReservationSlot [] AddtionStation ;
    ReservationSlot [] MultiplicationStation ;
    double[] Memory ;
    LoadSlot[] LoadBuffer;
    StoreSlot[] StoreBuffer;
    boolean busLine ;


    public MainMachine(int regNum,int multStationsNum,int addStationNum,int memSize,int loadBufferSize,int storeBufferSize){
        this.Cycle=0;
        this.InstructionToIssue =0;
        this.InstructionsQueue = new ArrayList<Instruction>();
        this.RegisterFile=new Register[regNum];
        this.AddtionStation =new ReservationSlot[addStationNum];
        this.MultiplicationStation = new ReservationSlot[multStationsNum];
        this.Memory = new double[memSize];
        this.LoadBuffer = new LoadSlot[loadBufferSize];
        this.StoreBuffer = new StoreSlot[storeBufferSize];
        this.busLine=true;
        //initialization
        for(int i=0;i<RegisterFile.length;i++)
            RegisterFile[i]=new Register("F"+i);
        for(int i=0;i<AddtionStation.length;i++)
            AddtionStation[i]=new ReservationSlot("A"+i);
        for(int i=0;i<MultiplicationStation.length;i++)
            MultiplicationStation[i]=new ReservationSlot("M"+i);
        for(int i=0;i<LoadBuffer.length;i++)
            LoadBuffer[i]=new LoadSlot("L"+i);
        for(int i=0;i<StoreBuffer.length;i++)
            StoreBuffer[i]=new StoreSlot();
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
