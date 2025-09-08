package engine.classhierarchy;

import java.util.ArrayList;
import java.util.List;

import engine.ProgramVars;
import engine.basictypes.*;

public class ZeroVar extends SyntheticSugar{

    public ZeroVar(HasLabel lab, Variable value) {
         super(lab, SyntheticType.ZERO_VARIABLE,value);
    }
    public ZeroVar(Variable value) {
        super(SyntheticType.ZERO_VARIABLE,value);
    }
    public ArrayList<AbstractInstruction> expand(ProgramVars context) {
        HasLabel firstLabel = this.lab.myClone();
        if(lab==FixedLabel.EMPTY) { //the first label cant be empty
         firstLabel=FixedLabel.DEFAULT;
        }
        this.commands=new ArrayList<>(List.of(new Decrease(firstLabel.myClone(),this.var),
                new JumpNotZero(this.var,firstLabel.myClone())));
        return commands; // Getter for commands
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("%s <- 0", var);
        String parentSuffix = String.format("(%d)", this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }
    public String getChildPart(){
        return String.format("%s <- 0", var);
    }
    public ZeroVar clone(ProgramVars context) {
        return new ZeroVar(lab.myClone(),var.clone(context));
    }

}
