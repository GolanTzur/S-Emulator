package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.*;
import engine.classhierarchy.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ConstAssignment extends SyntheticSugar {
    private int arg;// Variable to be decremented

    public ConstAssignment(HasLabel lab, Variable value,int arg) {
        super(lab,SyntheticType.CONST_ASSIGNMENT,value);
        this.arg = arg;
    }
    public ConstAssignment(Variable value,int arg) {
        super( SyntheticType.CONST_ASSIGNMENT,value);
        this.arg = arg;
    }
    public ArrayList<AbstractInstruction> expand() {
        this.commands=new ArrayList<>();
        this.commands.add(new ZeroVar(this.lab.myClone(),this.var));
        for (int i=0;i<arg;i++)
        {
            this.commands.add(new Increase(this.var)); // Add Increase instruction
        }
        return commands; // Getter for commands
    }
    public String toString() {
        String parentPrefix = super.toString();
        String childPart = String.format("%s <- %d", var, arg);
        String parentSuffix = String.format("(%d)", this.type.getCycles());
        return String.format("%s %s %s", parentPrefix, childPart, parentSuffix);
    }

}
