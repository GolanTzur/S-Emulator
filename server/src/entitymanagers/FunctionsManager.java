package entitymanagers;

import engine.FunctionInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;


//Addition is performed via AddFuncDetails class
public class FunctionsManager {
    private final ReentrantReadWriteLock rwLock;
    private static FunctionsManager instance = null;
    private final Set<FunctionInfo> functions;

    private FunctionsManager() {
        this.functions = new HashSet<>();
        rwLock = new ReentrantReadWriteLock();
    }
    public Set<FunctionInfo> getFunctions()
    {
        rwLock.readLock().lock();
        try {
            return functions;
        }
        finally {
            rwLock.readLock().unlock();
        }
    }

    public FunctionInfo getFunction(String functionName) {
        rwLock.readLock().lock();
        try {
            for (FunctionInfo fi : functions) {
                if (fi.func().getProg().getName().equals(functionName)) {
                    return fi;
                }
            }
        }
        finally {
            rwLock.readLock().unlock();
        }
        return null;
    }

    public ReentrantReadWriteLock getLock() {
        return rwLock;
    }

    public static synchronized FunctionsManager getInstance() {
        if (instance == null) {
            instance = new FunctionsManager();
        }
        return instance;
    }
}

