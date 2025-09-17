package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;

public class Increase extends Instruction {

    public Increase(HasLabel label,Variable value) {
        super(label, InstructionType.INCREASE,value);
    }
    public Increase(Variable value) {
        super(InstructionType.INCREASE,value);
    }

    @Override
    public HasLabel evaluate(/*ProgramVars context*/){
        super.evaluate();
        this.var.setValue(this.var.getValue()+1);
        return FixedLabel.EMPTY;
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("%s <- %s+1", var, var);
        String parentSuffix = String.format("(%d)", this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }
    public String getChildPart(){
        return String.format("%s <- %s+1", var, var);
    }
    @Override
    public Increase clone(ProgramVars context) {
        return new Increase(lab.myClone(),this.var.clone(context));
    }
}
