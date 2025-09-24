package engine.basictypes;

import engine.ProgramVars;
import engine.classhierarchy.Instruction;

import java.io.Serializable;
import java.util.Optional;

public class Variable implements Cloneable, Serializable {
    private VariableType type;
    private int value;
    private int position; //Position in the list of variables

    protected Variable(VariableType type,int pos,int...value) {
        this.position = pos; //Constructor initializes position
        if(value.length==0)
         this.value = 0; //Default value is 0
        else
            this.value = value[0];
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

    public static Variable createOrGetNewVar(VariableType it,int pos,ProgramVars pv,int...val) { // Factory method to create or get an existing variable
        switch (it) {
            case INPUT:
                pv.getInput().putIfAbsent(pos, new Variable(VariableType.INPUT, pos,val));
                return pv.getInput().get(pos);
            case WORK:
                pv.getEnvvars().putIfAbsent(pos, new Variable(VariableType.WORK, pos,val));
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
    /*public Variable cloneWithValue(ProgramVars context, int... pos) {
        int targetPos = (pos.length == 0) ? this.position : pos[0];
        Variable res = createOrGetNewVar(this.type, targetPos, context, this.value);
        res.position = this.position; // Always assign original position
        return res;
    }*/

    public Variable cloneWithValue(ProgramVars context, int... pos) {
        return cloneWithValue(context, null, pos);
    }

    public Variable cloneWithValue(ProgramVars context, VariableType maybeType, int... pos) {
        int targetPos = (pos.length == 0) ? this.position : pos[0];
        VariableType targetType = (maybeType == null) ? this.type : maybeType;
        Variable res = createOrGetNewVar(targetType, targetPos, context, this.value);
        res.position = this.position;
        if (maybeType != null) {
            res.type = this.type;
        }
        return res;
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