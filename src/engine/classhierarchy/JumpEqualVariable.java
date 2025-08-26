package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public ArrayList<AbstractInstruction> expand(ProgramVars context) {
        this.commands = new ArrayList<>();
        Iterator<Variable> it = context.getZinputs(2).iterator();
        Variable z1 = it.next();
        Variable z2 = it.next();
        Variable z3 = Variable.createDummyVar(VariableType.WORK, 1, 0);
        this.commands.add(new Assignment(this.lab.myClone(), z1, this.var));
        this.commands.add(new Assignment(z2, arg));
        this.commands.add(new JumpZero(new Label("L2"), z3, new Label("L3")));
        this.commands.add(new JumpZero(z2, new Label("L1")));
        this.commands.add(new Decrease(z1));
        this.commands.add(new Decrease(z2));
        this.commands.add(new GotoLabel(z3, new Label("L2")));
        this.commands.add(new JumpZero(new Label("L3"), z2, gotoLabel.myClone()));
        this.commands.add(new Neutral(new Label("L1"), this.var));
        replaceLabels();
        return commands; // Getter for commands
    }
    private void replaceLabels() {
        int nextLabelNum = 4;
        ArrayList<String> usedLabels = new ArrayList<>(List.of("L1","L2","L3"));
        if(usedLabels.contains(this.lab.getLabel())) {
            if(gotoLabel.getLabel().equals("L"+nextLabelNum)) {
                nextLabelNum++;
            }
            String currentLabel = this.lab.getLabel();
            if(currentLabel.equals("L1")) {
                replaceL1(new Label("L"+nextLabelNum));
            }
            else if(currentLabel.equals("L2")) {
                replaceL2(new Label("L"+nextLabelNum));
            }
            else if(currentLabel.equals("L3")) {
                replaceL3(new Label("L"+nextLabelNum));
            }
            usedLabels.remove(currentLabel);
            usedLabels.add("L"+nextLabelNum);
            nextLabelNum++;
        }
        if(usedLabels.contains(gotoLabel.getLabel())) {
            if(lab.getLabel().equals("L"+nextLabelNum)) {
                nextLabelNum++;
            }
            String currentLabel = this.gotoLabel.getLabel();
            if(currentLabel.equals("L1")) {
                replaceL1(new Label("L"+nextLabelNum));
            }
            else if(currentLabel.equals("L2")) {
                replaceL2(new Label("L"+nextLabelNum));
            }
            else if(currentLabel.equals("L3")) {
                replaceL3(new Label("L"+nextLabelNum));
            }

        }

        }

    private void replaceL1(HasLabel label) {
        this.commands.get(8).setLab(label);
        ((HasGotoLabel)this.commands.get(3)).setGotolabel(label);
    }
    private void replaceL2(HasLabel label) {
        this.commands.get(2).setLab(label);
        ((HasGotoLabel)this.commands.get(6)).setGotolabel(label);
    }
    private void replaceL3(HasLabel label) {
        this.commands.get(7).setLab(label);
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
    @Override
    public JumpEqualVariable clone(ProgramVars context) {
        return new JumpEqualVariable(lab.myClone(),var.clone(context),arg,gotoLabel.myClone());
    }
}
