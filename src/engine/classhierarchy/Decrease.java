package engine.classhierarchy;

import engine.basictypes.*;

public class Decrease extends Instruction {

    public Decrease(HasLabel label,Variable value) {
        super(label, InstructionType.Decrease,value,1);

    }
    public Decrease(Variable value) {
        super(InstructionType.Decrease,value,1);

    }
    @Override
    public HasLabel evaluate(){
       if(this.var.getValue() > 0) {
            this.var.setValue(this.var.getValue() - 1);
       }
        return FixedLabel.EMPTY;
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("%s <- %s-1", var, var);
        String parentSuffix = String.format("(%d)", cycles);
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }
}
