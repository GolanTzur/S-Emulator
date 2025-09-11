package engine.classhierarchy;
import engine.ProgramVars;
import engine.basictypes.*;

public class JumpNotZero extends Instruction implements HasGotoLabel {
    private HasLabel gotoLabel;
    public JumpNotZero(HasLabel label,Variable value,HasLabel gotoLabel) {
        super(label, InstructionType.JUMP_NOT_ZERO,value);
        this.gotoLabel = gotoLabel; // Initialize gotoLabel
    }
    public JumpNotZero(Variable value, HasLabel gotoLabel) {
        super(InstructionType.JUMP_NOT_ZERO,value);
        this.gotoLabel = gotoLabel; // Initialize gotoLabel
    }

    public HasLabel getGotolabel() {
        return gotoLabel; // Getter for gotoLabel
    }
    public void setGotolabel(HasLabel gotoLabel) {
        this.gotoLabel = gotoLabel; // Setter for gotoLabel
    }
    public HasLabel evaluate(/*ProgramVars context*/) {
        return this.var.getValue()!=0 ? gotoLabel : FixedLabel.EMPTY;
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("IF %s != 0 GOTO %s", var, gotoLabel);
        String parentSuffix = String.format("(%d)", this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }
    public String getChildPart(){
        return String.format("IF %s != 0 GOTO %s", var, gotoLabel);
    }
    @Override
    public JumpNotZero clone(ProgramVars context) {
        return new JumpNotZero(lab.myClone(),var.clone(context),gotoLabel.myClone());
    }

}