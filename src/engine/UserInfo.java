package engine;

import engine.classhierarchy.Function;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    private final String name;
    private final List<Program> programsUploaded;
    private final List<Function> functionsUploaded;
    private final List<RunInfo> runInfos;
    private int creditsLeft;
    private int creditsSpent;

    public UserInfo(String name) {
        this.name = name;
        this.creditsLeft = 0;
        this.creditsSpent = 0;
        programsUploaded = new ArrayList<>();
        functionsUploaded = new ArrayList<>();
        runInfos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public List<Program> getProgramsUploaded() {
        return programsUploaded;
    }
    public List<Function> getFunctionsUploaded() {
        return functionsUploaded;
    }
    public List<RunInfo> getRunInfos() {
        return  runInfos;
    }
    public int getCreditsLeft() {
        return creditsLeft;
    }
    public int getCreditsSpent() {
        return creditsSpent;
    }
    public void addProgram(Program p) {
        programsUploaded.add(p);
    }
    public void addFunction(Function f) {
        functionsUploaded.add(f);
    }
    public void addRunInfo(RunInfo r) {
        runInfos.add(r);
    }
    public void addCredits(int c) {
        creditsLeft += c;
    }
    public void spendCredits(int c) {
        if (c > creditsLeft) {
            throw new RuntimeException("Not enough credits");
        }
        creditsLeft -= c;
        creditsSpent += c;
    }

}
