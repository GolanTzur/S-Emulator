package engine.classhierarchy;

import engine.basictypes.*;

public class Increase extends Instruction {

    public Increase(HasLabel label,Variable value) {
        super(label, InstructionType.Decrease,value);
    }
    public Increase(Variable value) {
        super(InstructionType.Decrease,value);
    }

    @Override
    public HasLabel evaluate(){
        this.var.setValue(this.var.getValue()+1);
        return FixedLabel.EMPTY;
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("%s <- %s+1", var, var);
        String parentSuffix = String.format("(%d)", this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }
}
