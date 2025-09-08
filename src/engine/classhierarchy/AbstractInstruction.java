package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;

import java.io.Serializable;

public abstract class AbstractInstruction implements Evaluable,Cloneable, Serializable {
    protected HasLabel lab;
    protected AbstractInstructionType type;
    protected Variable var;
    protected SyntheticSugar source; // For synthetic instructions
    protected int pos;

    public AbstractInstruction(HasLabel label, AbstractInstructionType ait, Variable var) {
        this.lab = label; // Default to an empty label
        this.type = ait;
        this.var = var; // Variable to be used in the instruction
    }

    public AbstractInstruction(AbstractInstructionType ait,Variable var) {
        this.lab = FixedLabel.EMPTY;
        this.type = ait;
        this.var = var; // Variable to be used in the instruction
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
    public void setSyntheticSource(AbstractInstruction source) {
        if(source != null && (source instanceof SyntheticSugar)) {
            this.source = (SyntheticSugar)source;
        }
        else {
            throw new RuntimeException(source.type+" is not a SyntheticSugar");
        }
    }
    public int getPos() {
        return pos;
    }
    public void setPos(int pos) {
        this.pos = pos;
    }
    public SyntheticSugar getSource() {
        return source;
    }


    public abstract AbstractInstruction clone(ProgramVars context);
    public abstract String getChildPart();

}
