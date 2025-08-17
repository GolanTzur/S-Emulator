package engine.classhierarchy;

import engine.basictypes.*;

public abstract class AbstractInstruction implements Evaluable {
    
    protected HasLabel lab;
    protected final AbstractInstructionType type;
    protected Variable var;
    protected final int cycles;

    public AbstractInstruction(HasLabel label,AbstractInstructionType ait,Variable var, int cycles) {
        this.lab = label; // Default to an empty label
        this.type = ait;
        this.var = var; // Variable to be used in the instruction
        this.cycles = cycles;
    }

    public AbstractInstruction(AbstractInstructionType ait,Variable var, int cycles) {
        this.lab = FixedLabel.EMPTY;
        this.type = ait;
        this.var = var; // Variable to be used in the instruction
        this.cycles = cycles;
    }

    public int getCycles() {
        return cycles;
    }

    public HasLabel getLab() {
        return lab;
    }
    public void setLab(HasLabel lab) {
        this.lab = lab;
    }
    public Variable getVar() {
        return var;
    }

    public AbstractInstructionType getType() {
        return type;
    }

}
