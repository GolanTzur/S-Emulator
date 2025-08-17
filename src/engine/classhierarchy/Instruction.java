package engine.classhierarchy;
import engine.basictypes.*;

public abstract class Instruction extends AbstractInstruction {

    public Instruction(HasLabel label, InstructionType it, Variable var, int cycles) {
       super(label,it,var,cycles);
    }
    public Instruction(InstructionType it,Variable var ,int cycles) {
        super(it, var,cycles);
    }
    public String toString()
    {
        return String.format("(B)"+"[%3s]",this.lab);
    }

}