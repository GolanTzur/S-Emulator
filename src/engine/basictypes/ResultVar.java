package engine.basictypes;

import engine.ProgramVars;
import engine.classhierarchy.Assignment;
import engine.classhierarchy.Function;

public class ResultVar extends Variable {
    private Function func;
    public ResultVar(Function func,VariableType type,int pos) {
        super(type,pos);
        this.func = func;
    }
    public Function getFunction() {
        return func;
    }
    public void setFunction(Function func) {
        this.func = func;
    }

    public static Variable createOrGetNewResultVar(VariableType it, int pos, ProgramVars pv,Function func) { // Factory method to create or get an existing variable
        switch (it) {
            case INPUT:
                pv.getInput().putIfAbsent(pos, new ResultVar(func,VariableType.INPUT, pos));
                return pv.getInput().get(pos);
            case WORK:
                pv.getEnvvars().putIfAbsent(pos, new ResultVar(func,VariableType.WORK, pos));
                return pv.getEnvvars().get(pos);
            default:
                return pv.getY()==null ?
                        new ResultVar(func,VariableType.RESULT, 0) :
                        pv.getY(); // If y is null, create a new Variable of type RESULT at position 0
        }
    }
    public static ResultVar createDummyVar(VariableType it,int pos,int value,Function func) { // Factory method to create a new variable without checking existing ones
        ResultVar dummy =new ResultVar(func,it,pos);
        dummy.setValue(value);
        return dummy;
    }
    @Override
    public String toString() {
        return func.toString();
    }
    public void evaluate()
    {
        if(!this.func.isEvaluated()) {
            this.func.evaluate();
            this.setValue(this.func.getVar().getValue());
        }
    }
}
