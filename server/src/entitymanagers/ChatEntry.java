package entitymanagers;

public record ChatEntry (String sender, String content) {
    public String toString() {
        return "{sender:\""+sender+"\"" + ",content:\"" + content+"\"}";
    }
}
