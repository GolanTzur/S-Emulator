package engine.classhierarchy;

import engine.ProgramVars;
import engine.Runner;
import engine.basictypes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class SyntheticSugar extends AbstractInstruction {
    protected ArrayList<AbstractInstruction> commands;

    public SyntheticSugar(HasLabel label,SyntheticType st,Variable var) {
        super(label, st,var);
    }
    public SyntheticSugar(SyntheticType st, Variable var) {
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

    public abstract ArrayList<AbstractInstruction> expand(ProgramVars... context);

    @Override
    public HasLabel evaluate(/*ProgramVars context*/)
    { //Inner runner
        //super.evaluate(); //if there result vars - calculate them

        if(this.commands == null) {
            this.commands = expand(/*context*/);
        }
        replaceResultVars();
        return new Runner(this.commands/*,context*/).run(false);
    }
    private void replaceResultVars()
    {
        Map<ResultVar,Variable> map = new HashMap<>();
        int numArgs=0;
        for(int i=0;i<commands.size();i++)
        {
            AbstractInstruction inst=commands.get(i);
            Variable var=inst.getVar();
            if(var instanceof ResultVar)
            {
                map.put((ResultVar)var,Variable.createDummyVar(VariableType.WORK,++numArgs,var.getValue()));
            }
            if(inst instanceof HasExtraVar)
            {
                Variable extraVar=((HasExtraVar) inst).getArg();
                if(extraVar instanceof ResultVar)
                {
                    map.put((ResultVar)extraVar,Variable.createDummyVar(VariableType.WORK,++numArgs,extraVar.getValue()));
                }
            }

        }
        for(int i=0;i<commands.size();i++)
        {
            AbstractInstruction inst=commands.get(i);
            Variable var=inst.getVar();
            if(var instanceof ResultVar)
            {
                Variable newVar=map.get(var);
                inst.setVar(newVar);
            }
            if(inst instanceof HasExtraVar)
            {
                Variable extraVar=((HasExtraVar) inst).getArg();
                if(extraVar instanceof ResultVar)
                {
                    Variable newVar=map.get(extraVar);
                    ((HasExtraVar) inst).setArg(newVar);
                }
            }

        }
    }

}
