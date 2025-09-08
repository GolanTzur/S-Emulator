package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;

import java.util.ArrayList;
import java.util.Iterator;

public class JumpZero extends SyntheticSugar implements HasGotoLabel {
    private HasLabel gotoLabel;// Variable to be decremented

    public JumpZero(HasLabel lab, Variable value,HasLabel gotoLabel) {
        super(lab, SyntheticType.JUMP_ZERO,value);
        this.gotoLabel = gotoLabel;
    }
    public JumpZero(Variable value,HasLabel gotoLabel) {
        super(SyntheticType.JUMP_ZERO,value);
        this.gotoLabel = gotoLabel;
    }
    public ArrayList<AbstractInstruction> expand(ProgramVars context) {
        Iterator<Variable> getz=context.getZinputs(1).iterator();
        ArrayList<AbstractInstruction> tempCommands = new ArrayList<>();
        tempCommands.add(new JumpNotZero(this.lab.myClone(),this.var, new Label("L1")));
        tempCommands.add(new GotoLabel(getz.next(), gotoLabel.myClone()));
        tempCommands.add(new Neutral(new Label("L1"),this.var));
        this.commands = tempCommands;
        replaceL1();
        return commands; // Getter for commands
    }
    private void replaceL1()
    {
        int nextLabelNum = 1;
        if (this.lab.getLabel().equals("L1") || this.gotoLabel.getLabel().equals("L1")) {
            nextLabelNum++;
            if (this.gotoLabel.getLabel().equals("L2") || this.lab.getLabel().equals("L2")) {
                nextLabelNum++;
            }
            HasLabel newLabel = new Label("L"+nextLabelNum);
            ((HasGotoLabel)this.commands.get(0)).setGotolabel(newLabel);
            this.commands.get(2).setLab(newLabel);
        }

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
    public String getChildPart(){
        return String.format("IF %s = 0 GOTO %s", var, gotoLabel);
    }
    @Override
    public JumpZero clone(ProgramVars context) {
        return new JumpZero(lab.myClone(),var.clone(context),gotoLabel.myClone());
    }
}
