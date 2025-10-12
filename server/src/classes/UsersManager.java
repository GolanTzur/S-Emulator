package classes;

import engine.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class UsersManager {
    private static UsersManager instance = null;
    private List<UserInfo> users;

    private UsersManager() {
        this.users = new ArrayList<>();
    }
    public List<UserInfo> getUsers() {
        return users;
    }
    public UserInfo lookForUser(String name) {
        for (UserInfo u : users) {
            if (u.getName().equals(name)) {
                return u;
            }
        }
        return null;
    }

    public synchronized void addUser(UserInfo user) {
        users.add(user);
    }

    public static UsersManager getInstance() {
        if (instance == null) {
            instance = new UsersManager();
        }
        return instance;
    }
}
