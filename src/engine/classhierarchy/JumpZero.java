package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;

import java.util.ArrayList;
import java.util.Iterator;

public class JumpZero extends SyntheticSugar implements HasGotoLabel {
    private HasLabel gotoLabel;// Variable to be decremented

    public JumpZero(HasLabel lab, Variable value,HasLabel gotoLabel) {
        super(lab, SyntheticType.JumpZero,value);
        this.gotoLabel = gotoLabel;
    }
    public JumpZero(Variable value,HasLabel gotoLabel) {
        super(SyntheticType.JumpZero,value);
        this.gotoLabel = gotoLabel;
    }
    public ArrayList<AbstractInstruction> expand() {
        Iterator<Variable> getz=ProgramVars.getZinputs(1).iterator();
        ArrayList<AbstractInstruction> tempCommands = new ArrayList<>();
        tempCommands.add(new JumpNotZero(this.lab.myClone(),this.var, new Label("L1")));
        tempCommands.add(new GotoLabel(getz.next(), gotoLabel.myClone()));
        tempCommands.add(new Neutral(new Label("L1"),ProgramVars.y));
        this.commands = tempCommands;
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
        String childPart = String.format("IF %s = 0 GOTO %s", var, gotoLabel);
        String parentSuffix = String.format("(%d)", this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }
}
