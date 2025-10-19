package entitymanagers;

import engine.ProgramInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ProgramsManager {

    private final ReentrantReadWriteLock rwLock;
    private static ProgramsManager instance = null;
    private final List<ProgramInfo> programs;

    private ProgramsManager() {
        this.programs = new ArrayList<>();
        rwLock = new ReentrantReadWriteLock();
    }
    public List<ProgramInfo> getPrograms() {
        return programs;
    }

    public ReentrantReadWriteLock getRwLock() {
        return rwLock;
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
