package entitymanagers;

import engine.FunctionInfo;

import java.util.HashSet;
import java.util.Set;

public class FunctionsManager {
    private static FunctionsManager instance = null;
    private Set<FunctionInfo> functions;

    private FunctionsManager() {
        this.functions = new HashSet<>();
    }
    public Set<FunctionInfo> getFunctions() {
        return functions;
    }

    /*public boolean functionExists(String functionName) {
        for (FunctionInfo fi : functions) {
            if (fi.func().getProg().getName().equals(functionName)) {
                return true;
            }
        }
        return false;
    }*/
    public FunctionInfo getFunction(String functionName) {
        for (FunctionInfo fi : functions) {
            if (fi.func().getProg().getName().equals(functionName)) {
                return fi;
            }
        }
        return null;
    }

    public synchronized void addFunction(FunctionInfo pi) {
        functions.add(pi);
    }

    public static FunctionsManager getInstance() {
        if (instance == null) {
            instance = new FunctionsManager();
        }
        return instance;
    }
}

