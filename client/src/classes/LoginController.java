package classes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import okhttp3.*;

import java.net.http.HttpRequest;

public class LoginController {

    @FXML
    private Button loginbutton;

    @FXML
    private TextField logintext;

    DashboardController dashboardController = null;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    void loginbuttonPressed(ActionEvent event) {
        if(logintext.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("Username cannot be empty.");
            alert.showAndWait();
            return;
        }
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("http://localhost:8080/server_war/users?action=login&username="+logintext.getText())
                .method("GET",null)
                .build();
        try {
            Response resp=client.newCall(request).execute();
            if (!resp.isSuccessful()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText(null);
                alert.setContentText("Login failed. Please try again.");
                alert.showAndWait();
                return;
            }
            String[] parts = resp.body().string().split(",");
            if (parts.length != 2) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText(null);
                alert.setContentText("Login failed. Please try again.");
                alert.showAndWait();
                return;
            }
            dashboardController.setUserLogged(parts[0], Integer.parseInt(parts[1]));
            dashboardController.setDashboardScene();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("Login failed. Please try again.");
            alert.showAndWait();
        }

    }

}