package engine.classhierarchy;

import java.util.ArrayList;
import java.util.List;

import engine.basictypes.*;

public class ZeroVar extends SyntheticSugar{

    public ZeroVar(HasLabel lab, Variable value) {
         super(lab, SyntheticType.ZERO_VAR,value);
    }
    public ZeroVar(Variable value) {
        super(SyntheticType.ZERO_VAR,value);
    }
    public ArrayList<AbstractInstruction> expand() {
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

}
