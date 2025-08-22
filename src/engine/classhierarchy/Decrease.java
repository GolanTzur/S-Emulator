package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;

public class Decrease extends Instruction {

    public Decrease(HasLabel label,Variable value) {
        super(label, InstructionType.DECREASE,value);

    }
    public Decrease(Variable value) {
        super(InstructionType.DECREASE,value);

    }
    @Override
    public HasLabel evaluate(ProgramVars context) {
       if(this.var.getValue() > 0) {
            this.var.setValue(this.var.getValue() - 1);
       }
        return FixedLabel.EMPTY;
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("%s <- %s-1", var, var);
        String parentSuffix = String.format("(%d)", this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }
    @Override
    public Decrease clone() {
        return new Decrease(lab.myClone(),this.var);
    }
}
