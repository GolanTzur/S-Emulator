package engine.classhierarchy;
import engine.basictypes.*;

import java.util.Optional;

public abstract class Instruction extends AbstractInstruction {

    public Instruction(HasLabel label, InstructionType it, Variable var, int cycles) {
       super(label,it,var,cycles);
    }
    public Instruction(InstructionType it,Variable var ,int cycles) {
        super(it, var,cycles);
    }

    @Override
    public String toString() {
        return Optional.ofNullable(this.source)
                .map(src -> String.format("%s <<< (B) [%-3s]", src, this.lab))
                .orElse(String.format("(B) [%-3s]", this.lab));
    }

}