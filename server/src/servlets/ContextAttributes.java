package servlets;

public enum ContextAttributes {
    USERS("AllUsers"),
    PROGRAMS("AllPrograms"),
    FUNCTIONS("AllFunctions"),
    CHAT("AllChats");

    private final String attributeName;

    ContextAttributes(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return attributeName;
    }
}
