package engine;

import engine.basictypes.Label;
import engine.basictypes.Variable;
import engine.basictypes.VariableType;

import java.util.*;

public class ProgramVars {
    public static Map<Integer, Variable> input=new HashMap<>();
    public static Map<Integer, Variable> envvars=new HashMap<>();
    public static Variable y=new Variable(0, 0,VariableType.RESULT);

    public static Collection<Variable> getZinputs(int count) { // Get a collection of Available work variables (z inputs)
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
                        Variable newVar = new Variable(VariableType.WORK, finalStart);
                        envvars.put(finalStart, newVar);
                        res.add(newVar);
                    });
            start++;
        }
        return res;
    }

}