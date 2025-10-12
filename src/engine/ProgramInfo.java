package engine;

public class ProgramInfo {
    private final Program program;
    private final String userUploaded;
    private int avgCreditsPrice;
    private int numRuns;

    public ProgramInfo(Program program, String userUploaded) {
        this.program = program;
        this.userUploaded = userUploaded;
        this.avgCreditsPrice = 0;
        this.numRuns = 0;
    }
    public String getUserUploaded() {
        return userUploaded;
    }
    public String getProgramName() {
        return program.getName();
    }
    public int getInstructionsCount() {
        return program.getInstructions().size();
    }
    public int getDegree() {
        return program.getProgramDegree();
    }
    public int getAvgCreditsPrice() {
        return avgCreditsPrice;
    }
    public int getNumRuns() {
        return numRuns;
    }
    public Program getProgram() {
        return program;
    }
    public void updateAvgCreditsPrice(int newPrice) {
        avgCreditsPrice = (avgCreditsPrice * numRuns + newPrice) / (numRuns + 1);
        numRuns++;
    }

}
