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

        System.out.println("l");

    }

}
