package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;

import java.io.Serializable;
import java.util.Collection;

public abstract class AbstractInstruction implements Evaluable,Cloneable, Serializable {
    protected HasLabel lab;
    protected AbstractInstructionType type;
    protected Variable var;
    protected AbstractInstruction source; // For synthetic instructions
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
    public void setVar(Variable var) {
        this.var = var;
    }

    public AbstractInstructionType getType() {
        return type;
    }
    public void setSyntheticSource(AbstractInstruction source) {
        if(source == null || (source instanceof Instruction)) {
            throw new RuntimeException(source.type+" is not a SyntheticSugar");
        }
        else {
            this.source = source;
        }
    }
    public int getPos() {
        return pos;
    }
    public void setPos(int pos) {
        this.pos = pos;
    }
    public  AbstractInstruction getSource() {
        return source;
    }


    public abstract AbstractInstruction clone(ProgramVars context);
    public abstract String getChildPart();
    public HasLabel evaluate(/*ProgramVars context*/) {

        if (this instanceof HasExtraVar) {
            if (((HasExtraVar) this).getArg() instanceof ResultVar)
                ((ResultVar) ((HasExtraVar) this).getArg()).evaluate();
        } else if (this instanceof Function) {
            Collection<Variable> funcInputs = ((Function) this).getUsedVariables();
            for (Variable v : funcInputs) {
                if (v instanceof ResultVar)
                    ((ResultVar) v).evaluate();
            }
        }
        return FixedLabel.EMPTY;
    }

}
