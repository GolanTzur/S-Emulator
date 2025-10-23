package dto;

public class ObservableRunInfo {
    private final boolean isMain;
    private final String name;
    private final Architecture arch;
    private final ObservableProgramVars results;
    private final int cycles;
    private final int degree;
    public ObservableRunInfo(boolean isMain, String name, Architecture arch, ObservableProgramVars results, int cycles, int degree) {
        this.isMain = isMain;
        this.name = name;
        this.arch = arch;
        this.results = results;
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
    public ObservableProgramVars getResults() {
        return results;
    }
    public int getCycles() {
        return cycles;
    }
    public int getDegree() {
        return degree;
    }
}