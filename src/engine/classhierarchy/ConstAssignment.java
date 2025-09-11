package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;


import java.util.ArrayList;

public class ConstAssignment extends SyntheticSugar {
    private final int arg;// Variable to be decremented

    public ConstAssignment(HasLabel lab, Variable value,int arg) {
        super(lab,SyntheticType.CONSTANT_ASSIGNMENT,value);
        this.arg = arg;
    }
    public ConstAssignment(Variable value,int arg) {
        super(SyntheticType.CONSTANT_ASSIGNMENT,value);
        this.arg = arg;
    }
    public ArrayList<AbstractInstruction> expand(ProgramVars... context) {
        if (context.length > 1)
            throw new RuntimeException("Wrong number of arguments");

        this.commands=new ArrayList<>();
        this.commands.add(new ZeroVar(this.lab.myClone(),this.var));
        for (int i=0;i<arg;i++)
        {
            this.commands.add(new Increase(this.var)); // Add Increase instruction
        }
        return commands; // Getter for commands
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("%s <- %d", var, arg);
        String parentSuffix = String.format("(%d)", this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }
    public String getChildPart(){
        return String.format("%s <- %d", var, arg);
    }
    @Override
    public ConstAssignment clone(ProgramVars context) {
        return new ConstAssignment(this.var.clone(context),this.arg);
    }

}
