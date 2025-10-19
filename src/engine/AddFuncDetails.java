package engine;

import engine.classhierarchy.Function;

import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AddFuncDetails {
    private final UserInfo userUploaded;
    private final String mainProgramContext;
    private final Set<FunctionInfo> functions;
    private final ReentrantReadWriteLock funclocker;
    private final ReentrantReadWriteLock userlocker;

    public AddFuncDetails(UserInfo userUploaded, String mainProgramContext, Set<FunctionInfo> functions, ReentrantReadWriteLock funclocker,ReentrantReadWriteLock userlocker) {
        this.userUploaded = userUploaded;
        this.mainProgramContext = mainProgramContext;
        this.functions = functions;
        this.funclocker = funclocker;
        this.userlocker = funclocker;
    }


    public boolean functionExists(String funcName) {
        funclocker.readLock().lock();
        try {
            return functions.stream().anyMatch(f -> f.func().getProg().getName().equals(funcName));
        } finally {
            funclocker.readLock().unlock();
        }
    }
    public Set<FunctionInfo> getFunctions() {
        funclocker.readLock().lock();
        try {
            return functions;
        } finally {
            funclocker.readLock().unlock();
        }
    }
    public void addFunction(Function func)
    {
     FunctionInfo fi= new FunctionInfo(func, userUploaded.getName(), mainProgramContext);

     funclocker.writeLock().lock();
     functions.add(fi);
     funclocker.writeLock().unlock();

     userlocker.writeLock().lock();
     userUploaded.addFunction(fi);
     userlocker.writeLock().unlock();
    }
    public FunctionInfo getFunctionInfo(String funcName)
    {
        funclocker.readLock().lock();
        try{
        return functions.stream().filter(f -> f.func().getProg().getName().equals(funcName)).findFirst().orElse(null);
        } finally {
            funclocker.readLock().unlock();
        }
    }
}
