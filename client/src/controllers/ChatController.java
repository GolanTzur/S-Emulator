package controllers;
import com.google.gson.Gson;
import dto.ObservableChatEntry;
import dto.ObservableUserInfo;
import httpclient.HttpClientSingleton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;



public class ChatController {

    private String currentUser;
    private ExecutorService usersListUpdater;
    private ExecutorService messageReceiver;
    private List<ObservableChatEntry> chatEntries;

    @FXML
    private TextArea chat;

    @FXML
    private TextArea message;

    @FXML
    private Button send;

    @FXML
    private Label username;

    @FXML
    private ListView<String> userslist;

    public void initialize()
    {
        //Get all messages from server and display them in chat area
        OkHttpClient client = HttpClientSingleton.getInstance();
        Request request = new Request.Builder()
                .url("http://localhost:8080/server_war/chats")
                .get()
                .build();
        try
        {
            String responseBody = client.newCall(request).execute().body().string();
            Gson gson = new Gson();

            Type listType = new TypeToken<ArrayList<ObservableChatEntry>>(){}.getType();
            chatEntries = gson.fromJson(responseBody, listType);
            updateChatArea();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //Start background thread to fetch new messages periodically
        messageReceiver = Executors.newSingleThreadExecutor();
        messageReceiver.submit(() -> {
            while (true) {
                try {
                    Thread.sleep(2000); // Poll every 2 seconds
                    fetchNewMessages(chatEntries.size());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        //Start background thread to fetch users list periodically
        usersListUpdater = Executors.newSingleThreadExecutor();
        Runnable usersListTask = () -> {
            while (true) {
                try {
                    Thread.sleep(5000); // Update every 5 seconds
                    fetchUsersList();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        };
        usersListUpdater.submit(usersListTask);

    }
    private void updateChatArea(int...rownumber)
    {
        StringBuilder chatContent = new StringBuilder();
        if(rownumber.length == 0) {
            for (ObservableChatEntry entry : chatEntries) {
                chatContent.append(entry.sender()).append(": ").append(entry.content()).append("\n");
            }
            chat.setText(chatContent.toString());
        }
        else {
            int rowNum = rownumber[0];
            for (int i = rowNum; i < chatEntries.size(); i++) {
                ObservableChatEntry entry = chatEntries.get(i);
                chatContent.append(entry.sender()).append(": ").append(entry.content()).append("\n");
            }
            chat.appendText(chatContent.toString());
        }
    }
    private void fetchNewMessages(int currentRowCount)
    {
        OkHttpClient client = HttpClientSingleton.getInstance();
        Request request = new Request.Builder()
                .url("http://localhost:8080/server_war/chats?rowcount=" + currentRowCount)
                .build();
        try
        {
            String responseBody = client.newCall(request).execute().body().string();
            Gson gson = new Gson();

            Type listType = new TypeToken<ArrayList<ObservableChatEntry>>(){}.getType();
            List<ObservableChatEntry> newEntries = gson.fromJson(responseBody, listType);
            if(!newEntries.isEmpty())
            {
                Platform.runLater(()-> {
                    chatEntries.addAll(newEntries);
                    updateChatArea(currentRowCount);
                });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void fetchUsersList()
    {
        OkHttpClient client = HttpClientSingleton.getInstance();
        Request request = new Request.Builder()
                .url("http://localhost:8080/server_war/users?action=showusers")
                .build();
        try {
            String responseBody = client.newCall(request).execute().body().string();
            Gson gson = new Gson();
            ObservableUserInfo[] users = gson.fromJson(responseBody, ObservableUserInfo[].class);
            Platform.runLater(() ->{
                userslist.getItems().clear();
                userslist.getItems().addAll(List.of(users).stream().map(ObservableUserInfo::username).toList());
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setCurrentUser(String username)
    {
        this.currentUser=username;
        this.username.setText(username);
    }



    @FXML
    void sendMessage(ActionEvent event) {
        String msgContent = message.getText().trim();
        if(msgContent.isEmpty())
            return;
        OkHttpClient client = HttpClientSingleton.getInstance();
        Request request = new Request.Builder()
                .url("http://localhost:8080/server_war/chats")
                .post(new okhttp3.FormBody.Builder()
                        .add("sender", currentUser)
                        .add("message", msgContent)
                        .build())
                .build();
        try
        {
            client.newCall(request).execute();
            message.clear();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    // Add this method to your controller
    private void shutdownThreads() {
        if (usersListUpdater != null && !usersListUpdater.isShutdown()) {
            usersListUpdater.shutdownNow();
        }
        if (messageReceiver != null && !messageReceiver.isShutdown()) {
            messageReceiver.shutdownNow();
        }
    }

    @FXML
    public void onClose() {
        shutdownThreads();
    }

}
