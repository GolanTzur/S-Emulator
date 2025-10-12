package engine;

import engine.classhierarchy.Function;

import java.util.Set;

public class AddFuncDetails {
    UserInfo userUploaded;
    String mainProgramContext;
    Set<FunctionInfo> functions;

    public AddFuncDetails(UserInfo userUploaded, String mainProgramContext, Set<FunctionInfo> functions) {
        this.userUploaded = userUploaded;
        this.mainProgramContext = mainProgramContext;
        this.functions = functions;
    }

    public UserInfo getUserUploaded() {
        return userUploaded;
    }

    public String getMainProgramContext() {
        return mainProgramContext;
    }
    public boolean functionExists(String funcName) {
        return functions.stream().anyMatch(f -> f.func().getProg().getName().equals(funcName));
    }
    public Set<FunctionInfo> getFunctions() {
        return functions;
    }
    public void addFunction(Function func)
    {
     FunctionInfo fi= new FunctionInfo(func, userUploaded.getName(), mainProgramContext);
     functions.add(fi);
     userUploaded.addFunction(fi);
    }
    public FunctionInfo getFunctionInfo(String funcName)
    {
        return functions.stream().filter(f -> f.func().getProg().getName().equals(funcName)).findFirst().orElse(null);
    }
}
