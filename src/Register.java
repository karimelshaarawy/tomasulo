public class Register {
    String Qi = "0";
    double value ;
    String tag;

    public Register(String tag) {
        this.tag = tag;
        Qi="0";
        value=0.0;
    }

    public boolean isValid() {
        if(Qi.equals("0"))
            return true;
        return false;
    }

    public void setValue(double value) {
        this.value = value;
        this.Qi="0";
    }

    public void setQi(String qi) {
        Qi = qi;
    }
}
