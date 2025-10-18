package entitymanagers;

import engine.ProgramInfo;

import java.util.ArrayList;
import java.util.List;

public class ProgramsManager {
    private static ProgramsManager instance = null;
    private List<ProgramInfo> programs;

    private ProgramsManager() {
        this.programs = new ArrayList<>();
    }
    public List<ProgramInfo> getPrograms() {
        return programs;
    }

    public synchronized void addProgram(ProgramInfo pi) {
        programs.add(pi);
    }

    public ProgramInfo programExists(String programName) {
        for (ProgramInfo pi : programs) {
            if (pi.getProgramName().equals(programName)) {
                return pi;
            }
        }
        return null;
    }

    public static ProgramsManager getInstance() {
        if (instance == null) {
            instance = new ProgramsManager();
        }
        return instance;
    }
}
