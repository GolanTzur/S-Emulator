package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;

import java.util.*;

public class Assignment extends SyntheticSugar{
    private Variable arg;// Variable to be decremented

    public Assignment(HasLabel lab, Variable value, Variable arg) {
        super(lab,SyntheticType.ASSIGNMENT,value);
        this.arg = arg;
    }
    public Assignment(Variable value,Variable arg) {
        super(SyntheticType.ASSIGNMENT,value);
        this.arg = arg;
    }
    public ArrayList<AbstractInstruction> expand(ProgramVars context) {
        //Variable gotolabelarg=Variable.createDummyVar(VariableType.WORK,1,0);
        //Variable helper=Variable.createDummyVar(VariableType.WORK,2,0);
        Iterator<Variable> it = context.getZinputs(2).iterator();
        Variable gotolabelarg = it.next();
        Variable helper = it.next();

        ArrayList<AbstractInstruction> tempCommands = new ArrayList<>();
        tempCommands.add(new ZeroVar(this.lab.myClone(), this.var));
        tempCommands.add(new JumpNotZero(arg, new Label("L1")));
        tempCommands.add(new GotoLabel(gotolabelarg, new Label("L3")));
        tempCommands.add(new Decrease(new Label("L1"), arg));
        tempCommands.add(new Increase(helper));
        tempCommands.add(new JumpNotZero(arg, new Label("L1")));
        tempCommands.add(new Decrease(new Label("L2"), helper));
        tempCommands.add(new Increase(this.var));
        tempCommands.add(new Increase(this.arg));
        tempCommands.add(new JumpNotZero(helper, new Label("L2")));
        tempCommands.add(new Neutral(new Label("L3"), this.var));
        this.commands = tempCommands;
        replaceLabels();
        return commands; // Getter for commands
    }
    private void replaceLabels()
    {
        String currentLabel=this.lab.getLabel();
        if(currentLabel.equals("L1"))
        {
            replaceL1(new Label("L4"));
        }
        else if(currentLabel.equals("L2"))
        {
            replaceL2(new Label("L4"));
        }
        else if(currentLabel.equals("L3"))
        {
            replaceL3(new Label("L4"));
        }
    }

    private void replaceL1(HasLabel newLabel)
    {
        ((HasGotoLabel)this.commands.get(1)).setGotolabel(newLabel);
        this.commands.get(3).setLab(newLabel);
        ((HasGotoLabel) this.commands.get(5)).setGotolabel(newLabel);
    }
    private void replaceL2(HasLabel newLabel)
    {
        this.commands.get(6).setLab(newLabel);
        ((HasGotoLabel) this.commands.get(9)).setGotolabel(newLabel);
    }
    private void replaceL3(HasLabel newLabel)
    {
        ((HasGotoLabel)this.commands.get(2)).setGotolabel(newLabel);
        this.commands.get(10).setLab(newLabel);
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("%s <- %s", var, arg);
        String parentSuffix = String.format("(%d)",this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }
    public String getChildPart(){
        return String.format("%s <- %s", var, arg);
    }
    public Assignment clone(ProgramVars context) {
         return new Assignment(this.var.clone(context),this.arg.clone(context));
    }

}
