package engine.basictypes;

import engine.ProgramVars;

import java.util.Optional;

public class Variable {
    private final VariableType type;
    private int value;
    private final int position; //Position in the list of variables

//    public Variable(int value,int pos,VariableType type) {
//        this.position = pos; //Constructor initializes position
//        this.value = value;
//        this.type = type; //Constructor initializes value and typ
//    } //No need since every variable has a default value of 0

    private Variable(VariableType type,int pos) {
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

    public static Variable createOrGetNewVar(VariableType it,int pos) { // Factory method to create or get an existing variable
        switch (it) {
            case INPUT:
                ProgramVars.input.putIfAbsent(pos, new Variable(VariableType.INPUT, pos));
                return ProgramVars.input.get(pos);
            case WORK:
                ProgramVars.envvars.putIfAbsent(pos, new Variable(VariableType.WORK, pos));
                return ProgramVars.envvars.get(pos);
            default:
                return ProgramVars.y==null ?
                    new Variable(VariableType.RESULT, 0) :
                    ProgramVars.y; // If y is null, create a new Variable of type RESULT at position 0
        }
    }

}