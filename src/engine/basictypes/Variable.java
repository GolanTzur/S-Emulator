package engine.basictypes;

import engine.ProgramVars;
import engine.classhierarchy.Instruction;

import java.io.Serializable;
import java.util.Optional;

public class Variable implements Cloneable, Serializable {
    private final VariableType type;
    private int value;
    private final int position; //Position in the list of variables

    protected Variable(VariableType type,int pos) {
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

    public static Variable createOrGetNewVar(VariableType it,int pos,ProgramVars pv) { // Factory method to create or get an existing variable
        switch (it) {
            case INPUT:
                pv.getInput().putIfAbsent(pos, new Variable(VariableType.INPUT, pos));
                return pv.getInput().get(pos);
            case WORK:
                pv.getEnvvars().putIfAbsent(pos, new Variable(VariableType.WORK, pos));
                return pv.getEnvvars().get(pos);
            default:
                return pv.getY()==null ?
                    new Variable(VariableType.RESULT, 0) :
                        pv.getY(); // If y is null, create a new Variable of type RESULT at position 0
        }
    }

    public Variable clone(ProgramVars context) {
         return createOrGetNewVar(this.type,this.position,context);
    }
    public static Variable createDummyVar(VariableType it,int pos,int value) { // Factory method to create a new variable without checking existing ones
        Variable dummy =new Variable(it,pos);
        dummy.setValue(value);
        return dummy;
    }
    public Variable cloneDummy() {
        return createDummyVar(this.type,this.position,this.value);
    }
}