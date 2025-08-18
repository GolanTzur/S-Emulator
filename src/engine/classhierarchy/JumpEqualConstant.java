package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;

import java.util.ArrayList;
import java.util.Iterator;

public class JumpEqualConstant extends SyntheticSugar implements HasGotoLabel {
    private int arg;// Variable to be decremented
    private HasLabel gotoLabel;

    public JumpEqualConstant(HasLabel lab, Variable value,int arg, HasLabel gotoLabel) {
        super(lab, 2, SyntheticType.JumpEqualConstant,value);
        this.arg = arg;
        this.gotoLabel = gotoLabel; // Initialize gotoLabel
    }
    public JumpEqualConstant(Variable value,int arg,HasLabel gotoLabel) {
        super(2, SyntheticType.JumpEqualConstant,value);
        this.arg = arg;
        this.gotoLabel = gotoLabel; // Initialize gotoLabel
    }
    public ArrayList<AbstractInstruction> expand() {
        this.commands=new ArrayList<>();
        Iterator<Variable> it=ProgramVars.getZinputs(2).iterator();
        Variable z1= it.next(); // Get the first variable from the iterator
        Variable z2= it.next();
        this.commands.add(new Assignment(this.lab.myClone(),z1,this.var)); // Call parent constructor with label and value

        for (int i=0;i<arg;i++)
        {
            this.commands.add(new JumpNotZero(z1,new Label("L1")));// Add Increase instruction
            this.commands.add(new Decrease(z1)); // Add Increase instruction
        }
        this.commands.add(new JumpNotZero(z1,new Label("L1")));// Add JumpNotZero instruction
        this.commands.add(new GotoLabel(z2,gotoLabel.myClone())); // Add GotoLabel instruction
        this.commands.add(new Neutral(new Label("L1"),ProgramVars.y));
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
        String childPart = String.format("IF %s = %d GOTO %s", var,arg, gotoLabel);
        String parentSuffix = String.format("(%d)", cycles);
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }

}
