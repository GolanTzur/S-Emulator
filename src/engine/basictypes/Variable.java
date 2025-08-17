package engine.basictypes;

public class Variable {
    private final VariableType type;
    private int value;
    private final int position; //Position in the list of variables

    public Variable(int value,int pos,VariableType type) {
        this.position = pos; //Constructor initializes position
        this.value = value;
        this.type = type; //Constructor initializes value and typ
    }
    public Variable(VariableType type,int pos) {
        this.position = pos; //Constructor initializes position
        this.value = 0; //Default value is 0
        this.type = type; //Constructor initializes type
    }
    public int getValue() {
        return value; //Getter for value
    }

    public void setValue(int value) {
        this.value = value; //Setter for value
    }
    public int getPosition() {
        return position; //Getter for position
    }
    public VariableType getType() {
        return type; //Getter for type
    }
    public String toString()
    {
        return this.type.getRepresentation(position);
    }


}