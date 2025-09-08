package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;

import java.util.*;

public class JumpEqualConstant extends SyntheticSugar implements HasGotoLabel {
    private final int arg;// Variable to be decremented
    private HasLabel gotoLabel;

    public JumpEqualConstant(HasLabel lab, Variable value, int arg, HasLabel gotoLabel) {
        super(lab, SyntheticType.JUMP_EQUAL_CONSTANT, value);
        this.arg = arg;
        this.gotoLabel = gotoLabel; // Initialize gotoLabel
    }

    public JumpEqualConstant(Variable value, int arg, HasLabel gotoLabel) {
        super(SyntheticType.JUMP_EQUAL_CONSTANT, value);
        this.arg = arg;
        this.gotoLabel = gotoLabel; // Initialize gotoLabel
    }

    public ArrayList<AbstractInstruction> expand(ProgramVars context) {
        this.commands = new ArrayList<>();
        Iterator<Variable> it = context.getZinputs(1).iterator();
        Variable z1 = it.next(); // Get the first variable from the iterator
        Variable z2 = Variable.createDummyVar(VariableType.WORK, 1, 0);
        this.commands.add(new Assignment(this.lab.myClone(), z1, this.var)); // Call parent constructor with label and value

        for (int i = 0; i < arg; i++) {
            this.commands.add(new JumpZero(z1, new Label("L1")));// Add Increase instruction
            this.commands.add(new Decrease(z1)); // Add Increase instruction
        }
        this.commands.add(new JumpNotZero(z1, new Label("L1")));// Add JumpNotZero instruction
        this.commands.add(new GotoLabel(z2, gotoLabel.myClone())); // Add GotoLabel instruction
        this.commands.add(new Neutral(new Label("L1"), this.var));
        replaceLabels();
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
        String childPart = String.format("IF %s = %d GOTO %s", var, arg, gotoLabel);
        String parentSuffix = String.format("(%d)", this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }
    public String getChildPart(){
        return String.format("IF %s = %d GOTO %s", var, arg, gotoLabel);
    }

    private void replaceLabels() {
        int nextLabelNum = 1;
        if (this.lab.getLabel().equals("L1") || this.gotoLabel.getLabel().equals("L1")) {
            nextLabelNum++;
            if (this.gotoLabel.getLabel().equals("L2") || this.lab.getLabel().equals("L2")) {
                nextLabelNum++;
            }
        }
        if (nextLabelNum != 1) {
            HasLabel newLabel = new Label("L" + nextLabelNum);
            for (int i = 1; i < arg*2 + 1; i += 2) {
                ((HasGotoLabel) this.commands.get(i)).setGotolabel(newLabel);
            }
            int lastindex = this.commands.size() - 1;
            this.commands.get(lastindex).setLab(newLabel);
            ((HasGotoLabel) this.commands.get(lastindex - 2)).setGotolabel(newLabel);
        }
    }

    @Override
    public JumpEqualConstant clone(ProgramVars context) {
        return new JumpEqualConstant(lab.myClone(),var.clone(context),arg,gotoLabel.myClone());
    }
}
