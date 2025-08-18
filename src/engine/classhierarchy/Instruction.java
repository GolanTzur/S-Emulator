package engine.classhierarchy;
import engine.basictypes.*;

import java.util.Optional;

public abstract class Instruction extends AbstractInstruction {

    public Instruction(HasLabel label, InstructionType it, Variable var) {
       super(label,it,var);
    }
    public Instruction(InstructionType it,Variable var) {
        super(it, var);
    }

    @Override
    public String toString() {
        return Optional.ofNullable(this.source)
                .map(src -> String.format("%s <<< #%d (B) [%-3s]",src,this.pos,this.lab))
                .orElse(String.format(" #%d (B) [%-3s]", this.pos,this.lab));
    }

}