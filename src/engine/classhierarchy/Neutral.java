package engine.classhierarchy;

import engine.basictypes.*;

public class Neutral extends Instruction {

    public Neutral(HasLabel label,Variable value) {
        super(label, InstructionType.Decrease,value);
    }
    public Neutral(Variable value) {
        super(InstructionType.Decrease,value);
    }

    public HasLabel evaluate(){
        return FixedLabel.EMPTY;
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("%s <- %s", var, var);
        String parentSuffix = String.format("(%d)", this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }
}
