package entitymanagers;

import engine.ProgramInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ProgramsManager {

    private ReentrantReadWriteLock rwLock;
    private static ProgramsManager instance = null;
    private final List<ProgramInfo> programs;

    private ProgramsManager() {
        this.programs = new ArrayList<>();
    }
    public List<ProgramInfo> getPrograms() {
        rwLock = new ReentrantReadWriteLock();
        return programs;
    }

    public void addProgram(ProgramInfo pi) {
        rwLock.writeLock().lock();
        try {
            programs.add(pi);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public ProgramInfo programExists(String programName) {
        rwLock.readLock().lock();
        try {
        for (ProgramInfo pi : programs) {
            if (pi.getProgramName().equals(programName)) {
                return pi;
            }
        }
        } finally {
            rwLock.readLock().unlock();
        }
        return null;
    }

    public static synchronized ProgramsManager getInstance() {
        if (instance == null) {
            instance = new ProgramsManager();
        }
        return instance;
    }
}
