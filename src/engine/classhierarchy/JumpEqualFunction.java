package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;

import java.util.ArrayList;
import java.util.Iterator;

public class JumpEqualFunction extends SyntheticSugar implements HasGotoLabel {
    private final Function func;
    private HasLabel gotolabel;
    public JumpEqualFunction(HasLabel label, Variable var, Function func, HasLabel gotolabel) {
        super(label, SyntheticType.JUMP_EQUAL_FUNCTION, var);
        this.func = func;
        this.gotolabel = gotolabel; // Initialize gotoLabel
    }
    public JumpEqualFunction(Variable var, Function func, HasLabel gotolabel) {
        super(SyntheticType.JUMP_EQUAL_FUNCTION, var);
        this.func = func;
        this.gotolabel = gotolabel; // Initialize gotoLabel
    }
    public HasLabel getGotolabel() {
        return gotolabel; // Getter for gotoLabel
    }
    public void setGotolabel(HasLabel gotolabel) {
        this.gotolabel = gotolabel; // Setter for gotoLabel
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("IF %s = %s GOTO %s", var, func.getChildPart(), gotolabel);
        String parentSuffix = String.format("(%d)", this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }
    public String getChildPart(){
        return String.format("IF %s = %s GOTO %s", var, func.getChildPart(), gotolabel);
    }
    public Function getFunc() {
        return func;
    }
    @Override
    public ArrayList<AbstractInstruction> expand(ProgramVars... context)
    {
        Variable z1;
        if(context.length==0)
        {
            z1=Variable.createDummyVar(VariableType.WORK,1,0);
        }
        else if(context.length==1) {
            Iterator<Variable> it = context[0].getZinputs(1).iterator();
            z1 = it.next();
        }
        else
        {
            throw new RuntimeException("Wrong number of arguments");
        }
        this.commands = new ArrayList<>();
        func.setVar(z1);
        this.commands.add(func); // Call
        this.commands.add(new JumpEqualVariable(var, z1, gotolabel.myClone()));// Add JumpEqualVariable instruction
        return commands;
    }
    public JumpEqualFunction clone(ProgramVars context) {
        return new JumpEqualFunction(lab.myClone(),var.clone(context),func.clone(context),gotolabel.myClone());
    }

}
