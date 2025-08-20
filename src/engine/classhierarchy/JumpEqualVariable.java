package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;

import java.util.ArrayList;
import java.util.Iterator;

public class JumpEqualVariable extends SyntheticSugar implements HasGotoLabel {
    private Variable arg;// Variable to be decremented
    private HasLabel gotoLabel;

    public JumpEqualVariable(HasLabel lab, Variable value,Variable arg, HasLabel gotoLabel) {
        super(lab, SyntheticType.JUMP_EQUAL_VARIABLE,value);
        this.arg = arg;
        this.gotoLabel = gotoLabel; // Initialize gotoLabel
    }
    public JumpEqualVariable(Variable value,Variable arg,HasLabel gotoLabel) {
        super(SyntheticType.JUMP_EQUAL_VARIABLE,value);
        this.arg = arg;
        this.gotoLabel = gotoLabel; // Initialize gotoLabel
    }
    public ArrayList<AbstractInstruction> expand() {
        this.commands = new ArrayList<>();
        Iterator<Variable> it = ProgramVars.getZinputs(3).iterator();
        Variable z1 = it.next();
        Variable z2 = it.next();
        Variable z3 = it.next();
        this.commands.add(new Assignment(this.lab.myClone(), z1, this.var));
        this.commands.add(new Assignment(z2, arg));
        this.commands.add(new JumpZero(new Label("L2"), z3, new Label("L3")));
        this.commands.add(new JumpZero(z2, new Label("L1")));
        this.commands.add(new Decrease(z1));
        this.commands.add(new Decrease(z2));
        this.commands.add(new GotoLabel(z3, new Label("L2")));
        this.commands.add(new JumpZero(new Label("L3"), z2, gotoLabel.myClone()));
        this.commands.add(new Neutral(new Label("L1"), ProgramVars.y));
        return commands; // Getter for commands
    }
    public HasLabel getGotolabel() {
        return gotoLabel; // Getter for gotoLabel
    }
    public void setGotolabel(HasLabel gotoLabel) {
        this.gotoLabel = gotoLabel; // Setter for gotoLabel
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("IF %s = %s GOTO %s", var,arg, gotoLabel);
        String parentSuffix = String.format("(%d)", this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }
}
