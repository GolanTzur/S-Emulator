package engine;

import engine.basictypes.InstructionType;
import engine.basictypes.Label;
import engine.basictypes.Variable;
import engine.basictypes.VariableType;
import engine.classhierarchy.Instruction;

import java.util.*;

public class ProgramVars implements Cloneable {
    private Map<Integer, Variable> input;
    private Map<Integer, Variable> envvars;
    private Variable y; //Default pos for y
    public ProgramVars() {
        input=new HashMap<>();
        envvars=new HashMap<>();
        y=Variable.createOrGetNewVar(VariableType.RESULT,0,this);
    }
    public Collection<Variable> getZinputs(int count) { // Get a collection of Available work variables (z inputs)
        if (count < 0) {
            throw new IllegalArgumentException("Count must be non-negative");
        }
        LinkedList<Variable> res=new LinkedList<>();
        int start=0;
        while(res.size()<count) {
            Optional<Variable> maybekey=Optional.ofNullable(envvars.get(start));
            final int finalStart = start;
            maybekey.ifPresentOrElse((v)->{},
                    ()-> {
                        Variable newVar = Variable.createOrGetNewVar(VariableType.WORK, finalStart,this);
                        res.add(newVar);
                    });
            start++;
        }
        return res;
    }
    public String toString()
    {
        String res="";
        Collection<Variable> inputs=input.values();
        for(Variable var: inputs) {
            res+=String.format("%s=%d ",var.toString(),var.getValue());
        }

        res+="\n";

        Collection<Variable> envs=envvars.values();
        for(Variable env: envs) {
            res+=String.format("%s=%d ",env.toString(),env.getValue());
        }

        res+="\n";

        res+=String.format("%s=%d ",y.toString(),y.getValue());
        return res;
    }
    public Map<Integer, Variable> getInput() {
        return input;
    }
    public Map<Integer, Variable> getEnvvars() {
        return envvars;
    }
    public Variable getY() {
        return y;
    }
    public void reset() {
        for(Variable var:input.values())
            var.setValue(0);
        for(Variable var:envvars.values())
            var.setValue(0);
        y.setValue(0);
    }
    /*public ProgramVars clone() {
        ProgramVars copy = new ProgramVars();
        copy.input = new HashMap<>();
        for (Map.Entry<Integer, Variable> entry : this.input.entrySet()) {
            copy.input.put(entry.getKey(), entry.getValue().clone(copy));
        }
        copy.envvars = new HashMap<>();
        for (Map.Entry<Integer, Variable> entry : this.envvars.entrySet()) {
            copy.envvars.put(entry.getKey(), entry.getValue().clone(copy));
        }
        copy.y = this.y.clone(copy);
        return copy;
    }*/


}