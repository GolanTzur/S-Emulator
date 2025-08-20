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
    public ArrayList<AbstractInstruction> expand() {
        Collection<Variable> nextz= ProgramVars.getZinputs(2);
        Iterator<Variable> it=nextz.iterator();
        Variable gotolabelarg=it.next();
        Variable helper=it.next();

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
        return commands; // Getter for commands
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("%s <- %s", var, arg);
        String parentSuffix = String.format("(%d)",this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }

}
