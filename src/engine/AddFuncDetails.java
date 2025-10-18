package engine;

import engine.classhierarchy.Function;

import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AddFuncDetails {
    private final UserInfo userUploaded;
    private final String mainProgramContext;
    private final Set<FunctionInfo> functions;
    private final ReentrantReadWriteLock locker;

    public AddFuncDetails(UserInfo userUploaded, String mainProgramContext, Set<FunctionInfo> functions, ReentrantReadWriteLock locker) {
        this.userUploaded = userUploaded;
        this.mainProgramContext = mainProgramContext;
        this.functions = functions;
        this.locker = locker;
    }


    public boolean functionExists(String funcName) {
        locker.readLock().lock();
        try {
            return functions.stream().anyMatch(f -> f.func().getProg().getName().equals(funcName));
        } finally {
            locker.readLock().unlock();
        }
    }
    public Set<FunctionInfo> getFunctions() {
        locker.readLock().lock();
        try {
            return functions;
        } finally {
            locker.readLock().unlock();
        }
    }
    public void addFunction(Function func)
    {
     FunctionInfo fi= new FunctionInfo(func, userUploaded.getName(), mainProgramContext);
     locker.writeLock().lock();
     functions.add(fi);
     locker.writeLock().unlock();
     userUploaded.addFunction(fi);
    }
    public FunctionInfo getFunctionInfo(String funcName)
    {
        locker.readLock().lock();
        try{
        return functions.stream().filter(f -> f.func().getProg().getName().equals(funcName)).findFirst().orElse(null);
        } finally {
            locker.readLock().unlock();
        }
    }
}
