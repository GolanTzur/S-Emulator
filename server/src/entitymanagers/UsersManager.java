package entitymanagers;

import engine.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UsersManager {
    private static UsersManager instance = null;
    private final List<UserInfo> users;
    private final ReentrantReadWriteLock rwLock;

    private UsersManager() {
        rwLock = new ReentrantReadWriteLock();
        this.users = new ArrayList<>();
    }

    public ReentrantReadWriteLock getRwLock() {
        return rwLock;
    }

    public List<UserInfo> getUsers() {
        rwLock.readLock().lock();
        try {
            return users;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public UserInfo lookForUser(String name) {
        rwLock.readLock().lock();
        try {
            for (UserInfo u : users) {
                if (u.getName().equals(name)) {
                    return u;
                }
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return null;
    }

    public void addUser(UserInfo user) {
        rwLock.writeLock().lock();
        users.add(user);
        rwLock.writeLock().unlock();
    }

    public static synchronized UsersManager getInstance() {
        if (instance == null) {
            instance = new UsersManager();
        }
        return instance;
    }
}
