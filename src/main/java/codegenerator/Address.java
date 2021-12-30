package codegenerator;

/**
 * Created by mohammad hosein on 6/28/2015.
 */
public class Address {
    public int num;
    public TypeAddress Type;
    public VarType varType;

    public Address(int num, VarType varType, TypeAddress Type) {
        this.num = num;
        this.Type = Type;
        this.varType = varType;
    }

    public Address(int num, VarType varType) {
        this.num = num;
        this.Type = TypeAddress.DIRECT;
        this.varType = varType;
    }

    public String toString() {
        switch (Type) {
            case DIRECT:
                return num + "";
            case INDIRECT:
                return "@" + num;
            case IMMEDIATE:
                return "#" + num;
        }
        return num + "";
    }
}
