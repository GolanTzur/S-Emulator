package engine.classhierarchy;

import engine.Runner;
import engine.basictypes.*;

import java.util.ArrayList;
import java.util.Optional;

public abstract class SyntheticSugar extends AbstractInstruction {
    protected ArrayList<AbstractInstruction> commands;

    public SyntheticSugar(HasLabel label,int numcycles,SyntheticType st,Variable var) {
        super(label, st,var ,numcycles);
    }
    public SyntheticSugar(int numcycles, SyntheticType st, Variable var) {
        super(st, var,numcycles);
    }
    public ArrayList<AbstractInstruction> getCommands() {
        return commands; // Getter for commands
    }

    @Override
    public String toString() {
        return Optional.ofNullable(this.source)
                .map(src -> String.format("%s <<< (S) [%-3s]", src, this.lab))
                .orElse(String.format("(S) [%-3s]", this.lab));
    }

    public abstract ArrayList<AbstractInstruction> expand();

    @Override
    public HasLabel evaluate()
    { //Inner runner
        if(this.commands == null) {
            this.commands = expand();
        }
        return new Runner(this.commands).run();
    }


}
