package engine.classhierarchy;

import engine.Runner;
import engine.basictypes.*;

import java.util.ArrayList;
import java.util.Optional;

public abstract class SyntheticSugar extends AbstractInstruction {
    protected ArrayList<AbstractInstruction> commands;

    public SyntheticSugar(HasLabel label,SyntheticType st,Variable var) {
        super(label, st,var);
    }
    public SyntheticSugar( SyntheticType st, Variable var) {
        super(st, var);
    }
    public ArrayList<AbstractInstruction> getCommands() {
        return commands; // Getter for commands
    }

    @Override
    public String toString() {
        return Optional.ofNullable(this.source)
                .map(src -> String.format("%s <<< #%d (S) [%-3s]",src,this.pos,this.lab))
                .orElse(String.format(" #%d (S) [%-3s]", this.pos,this.lab));
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
