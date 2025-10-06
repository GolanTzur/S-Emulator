package engine;

import engine.classhierarchy.Function;

/*public class FunctionInfo {
    private final Function func;
    private final String userUploaded;
    private final String mainProgramContext;

    public FunctionInfo(Function func, String userUploaded, String mainProgramContext) {
        this.func = func;
        this.userUploaded = userUploaded;
        this.mainProgramContext = mainProgramContext;
    }

    public String getUserUploaded() {
        return userUploaded;
    }
    public String getMainProgramContext() {
        return mainProgramContext;
    }
    public Function getFunction() {
        return func;
    }

}*/
public record FunctionInfo(Function func, String userUploaded, String mainProgramContext) {}

