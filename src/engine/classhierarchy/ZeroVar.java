package engine.classhierarchy;

import java.util.ArrayList;
import java.util.List;

import engine.basictypes.*;

public class ZeroVar extends SyntheticSugar{

    public ZeroVar(HasLabel lab, Variable value) {
        super(lab, 1, SyntheticType.ZeroVar,value);
    }
    public ZeroVar(Variable value) {
        super(1, SyntheticType.ZeroVar,value);
    }
    public ArrayList<AbstractInstruction> expand() {
        this.commands=new ArrayList<>(List.of(new Decrease(this.lab.clone(),this.var),
                new JumpNotZero(this.var,this.lab.clone())));
        return commands; // Getter for commands
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("%s <- 0", var);
        String parentSuffix = String.format("(%d)", cycles);
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }

}
