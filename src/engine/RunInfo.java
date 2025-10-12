package engine;

import engine.basictypes.Architecture;

public class RunInfo {
    private final boolean isMain;
    private final String name;
    private final Architecture arch;
    private final int result;
    private final int cycles;
    private final int degree;
    public RunInfo(boolean isMain, String name, Architecture arch, int result, int cycles,int degree) {
        this.isMain = isMain;
        this.name = name;
        this.arch = arch;
        this.result = result;
        this.cycles = cycles;
        this.degree = degree;
    }
    public boolean isMain() {
        return isMain;
    }
    public String getName() {
            return  name;
    }
    public Architecture getArch() {
        return arch;
    }
    public int getResult() {
        return result;
    }
    public int getCycles() {
        return cycles;
    }
    public int getDegree() {
        return degree;
    }
}
