package entitymanagers;

import java.util.ArrayList;
import java.util.List;

public class Chat { // Singleton class to manage chat entries
    private final List <ChatEntry> entries;
    private static Chat instance;
    private Chat() {
        entries = new ArrayList<ChatEntry>();
    }
    public static Chat getInstance() {
        if (instance == null) {
            instance = new Chat();
        }
        return instance;
    }

    public void addEntry(ChatEntry entry)
    {
        entries.add(entry);
    }

    public List<ChatEntry> getEntries()
    {
        return entries;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (ChatEntry entry : entries) {
            sb.append(entry.toString()).append(",");
        }
        if(sb.length()>1 && sb.charAt(sb.length()-1)==',')
            sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }
    public String getFromRow(int rowCount)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        try {
            for (int i = rowCount; i < entries.size(); i++) {
                sb.append(entries.get(i).toString()).append(",");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new  IllegalArgumentException("Index out of bounds!");
        }
        if(sb.length()>1 && sb.charAt(sb.length()-1)==',')
            sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }
}
