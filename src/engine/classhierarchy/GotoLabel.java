package engine.classhierarchy;

import java.util.*;

import engine.basictypes.*;

public class GotoLabel extends SyntheticSugar implements HasGotoLabel {
    private  HasLabel gotolabel;

    public GotoLabel(HasLabel lab, Variable value, HasLabel gotolabel) {
        super(lab, SyntheticType.GOTO_LABEL,value);
        this.gotolabel = gotolabel; // Initialize gotolabel
    }
    public GotoLabel(Variable value, HasLabel gotolabel) {
        super( SyntheticType.GOTO_LABEL,value);
        this.gotolabel = gotolabel; // Initialize gotolabel
    }
    public ArrayList<AbstractInstruction> expand()
    {
        this.commands=new ArrayList<>(List.of(new Increase(this.lab.myClone(),this.var),// Call parent constructor with label and value
                new JumpNotZero(this.var,gotolabel.myClone())));
        return commands; // Getter for commands
    }

    public HasLabel getGotolabel() {
        return gotolabel; // Getter for gotolabel
    }
    public void setGotolabel(HasLabel gotolabel) {
        this.gotolabel = gotolabel; // Setter for gotolabel
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("GOTO %s",gotolabel);
        String parentSuffix = String.format("(%d)", this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }

}
