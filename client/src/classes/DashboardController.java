package classes;

import com.google.gson.Gson;
import engine.RunInfo;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DashboardController {
    private final StringProperty usernameproperty = new SimpleStringProperty("");
    private final StringProperty creditsproperty = new SimpleStringProperty("");
    private String userHistoryShow=null;

    private ScheduledExecutorService usersscheduler;
    private ScheduledExecutorService programsscheduler;
    private ScheduledExecutorService functionsscheduler;
    private ScheduledExecutorService historyscheduler;

    @FXML
    private TableView<ObservableFunctionInfo> allfunctions;

    @FXML
    private TableColumn<ObservableFunctionInfo,String> allfunctionsdegree;

    @FXML
    private TableColumn<ObservableFunctionInfo,String> allfunctionsinstructions;

    @FXML
    private TableColumn<ObservableFunctionInfo,String> allfunctionsmainprogram;

    @FXML
    private TableColumn<ObservableFunctionInfo,String> allfunctionsname;

    @FXML
    private TableColumn<ObservableFunctionInfo,String> allfunctionsowner;

    @FXML
    private TableView<ObservableProgramInfo> allprograms;

    @FXML
    private TableColumn<ObservableProgramInfo, String> allprogramsavgcredits;

    @FXML
    private TableColumn<ObservableProgramInfo, String> allprogramsdegree;

    @FXML
    private TableColumn<ObservableProgramInfo, String> allprogramsinstructions;

    @FXML
    private TableColumn<ObservableProgramInfo, String> allprogramsname;

    @FXML
    private TableColumn<ObservableProgramInfo, String> allprogramsowner;

    @FXML
    private TableColumn<ObservableProgramInfo,String> allprogramsruns;

    @FXML
    private TableView<ObservableUserInfo> allusers;

    @FXML
    private TableColumn<ObservableUserInfo, String> alluserscredits;

    @FXML
    private TableColumn<ObservableUserInfo,String> allusersfunctions;

    @FXML
    private TableColumn<ObservableUserInfo,String> allusersname;

    @FXML
    private TableColumn<ObservableUserInfo,String> allusersprograms;

    @FXML
    private TableColumn<ObservableUserInfo,String> allusersruns;

    @FXML
    private TableColumn<ObservableUserInfo,String> allusersspent;

    @FXML
    private Button chargebutton;

    @FXML
    private TextField chargecredits;

    @FXML
    private Label credits;

    @FXML
    private AnchorPane anchor00;

    @FXML
    private AnchorPane anchor01;

    @FXML
    private AnchorPane anchor10;

    @FXML
    private AnchorPane anchor11;

    @FXML
    private Button deselectuser;

    @FXML
    private TextField fileroute;

    @FXML
    private Button load;

    @FXML
    private Button selectFunction;

    @FXML
    private Button selectProgram;

    @FXML
    private TableView<RunInfo> userhistory;

    @FXML
    private TableColumn<RunInfo, String> userhistoryarchitecture;

    @FXML
    private TableColumn<RunInfo,Integer> userhistorycycles;

    @FXML
    private TableColumn<RunInfo,Integer> userhistorydegree;

    @FXML
    private TableColumn<RunInfo,String> userhistorymainprogram;

    @FXML
    private TableColumn<RunInfo,String> userhistoryname;

    @FXML
    private TableColumn<RunInfo,Integer> userhistorynumber;

    @FXML
    private TableColumn<RunInfo,Integer> userhistoryresult;

    @FXML
    private Label userhistorytext;

    @FXML
    private HBox usermenuhbox;

    @FXML
    private Label alluserstext;

    @FXML
    private Label allfunctionstext;

    @FXML
    private Label allmainprogramstext;

    @FXML
    private GridPane maingrid;

    @FXML
    private Label username;

    private void setUsersTableAddFormat(TableColumn<ObservableUserInfo, String> username,
                                       TableColumn<ObservableUserInfo, String> programs,
                                       TableColumn<ObservableUserInfo, String> functions,
                                       TableColumn<ObservableUserInfo, String> runs,
                                       TableColumn<ObservableUserInfo, String> credits,
                                       TableColumn<ObservableUserInfo, String> spent) {
        // Define how each column gets its value
        username.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().username())
        );

        programs.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().numprograms()))
        );

        functions.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().numfunctions()))
        );

        runs.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().numruns()))
        );
        credits.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().creditsleft()))
        );
        spent.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().creditsspent()))
        );

    }
    private void setProgramsTableAddFormat(TableColumn<ObservableProgramInfo, String> name,
                                           TableColumn<ObservableProgramInfo, String> owner,
                                           TableColumn<ObservableProgramInfo, String> instructions,
                                           TableColumn<ObservableProgramInfo, String> degree,
                                           TableColumn<ObservableProgramInfo, String> avgcredits,
                                           TableColumn<ObservableProgramInfo, String> runs) {
        // Define how each column gets its value
        name.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().programname())
        );

        owner.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().owner())
        );

        instructions.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().numinstructions()))
        );

        degree.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().degree()))
        );
        avgcredits.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().avgcredits()))
        );
        runs.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().numruns()))
        );

    }
    private void setFunctionsTableAddFormat(TableColumn<ObservableFunctionInfo, String> name,
                                           TableColumn<ObservableFunctionInfo, String> owner,
                                           TableColumn<ObservableFunctionInfo, String> mainprogram,
                                           TableColumn<ObservableFunctionInfo, String> instructions,
                                           TableColumn<ObservableFunctionInfo, String> degree) {
        // Define how each column gets its value
        name.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().funcname())
        );

        owner.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().owner())
        );

        mainprogram.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().mainprogramname())
        );

        instructions.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().numinstructions()))
        );
        degree.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().degree()))
        );

    }
    private void setUserHistoryTableAddFormat(TableColumn<RunInfo, Integer> number,
                                           TableColumn<RunInfo, String> name,
                                           TableColumn<RunInfo, String> mainprogram,
                                           TableColumn<RunInfo, String> architecture,
                                           TableColumn<RunInfo, Integer> result,
                                           TableColumn<RunInfo, Integer> cycles,
                                           TableColumn<RunInfo, Integer> degree) {
        // Define how each column gets its value
        number.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(userhistory.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );

        name.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName())
        );

        mainprogram.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isMain() ? "Yes" : "No")
        );

        architecture.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getArch().toString())
        );
        result.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getResult()).asObject()
        );
        cycles.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCycles()).asObject()
        );
        degree.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getDegree()).asObject()
        );

    }

    public void setUserSelectedListener() {
            allusers.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                        if (newSel != null)
                          this.userHistoryShow = newSel.username();
                    }
            );
    }


    @FXML
    public void initialize() {
        setLoginScene();
        fileroute.setEditable(false);
        setUsersTableAddFormat(allusersname,allusersprograms,allusersfunctions,allusersruns,alluserscredits,allusersspent);
        setProgramsTableAddFormat(allprogramsname,allprogramsowner,allprogramsinstructions,allprogramsdegree,allprogramsavgcredits,allprogramsruns);
        setFunctionsTableAddFormat(allfunctionsname,allfunctionsowner,allfunctionsmainprogram,allfunctionsinstructions,allfunctionsdegree);
        setUserHistoryTableAddFormat(userhistorynumber,userhistoryname,userhistorymainprogram,userhistoryarchitecture,userhistoryresult,userhistorycycles,userhistorydegree);
        setUserSelectedListener();

        //All users table update thread
        usersscheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable showusers = () -> {
            try {
                OkHttpClient CLIENT = new OkHttpClient();
                Request req = new Request.Builder()
                        .url("http://localhost:8080/server_war/users?action=showusers")
                        .get()
                        .build();
                   CLIENT.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {


                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            ObservableUserInfo[] users = gson.fromJson(responseBody, ObservableUserInfo[].class);
                            javafx.application.Platform.runLater(() -> {

                                // Save current selection
                                ObservableUserInfo selected = allusers.getSelectionModel().getSelectedItem();
                                String selectedName = selected != null ? selected.username() : null;

                                allusers.getItems().clear();
                                if(users!=null&&users.length>0)
                                 allusers.getItems().addAll(users);

                                if (selectedName != null) {
                                    for (ObservableUserInfo u : allusers.getItems()) {
                                        if (u.username().equals(selectedName)) {
                                            allusers.getSelectionModel().select(u);
                                            break;
                                        }
                                    }
                                }
                            });
                         }
                    }

                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Run immediately, then every 5 seconds
        usersscheduler.scheduleAtFixedRate(showusers, 0, 5, TimeUnit.SECONDS);


        //All programs table update thread
        programsscheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable showprograms = () -> {
            try {
                OkHttpClient CLIENT = new OkHttpClient();
                Request req = new Request.Builder()
                        .url("http://localhost:8080/server_war/programs")
                        .get()
                        .build();
                CLIENT.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {


                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            ObservableProgramInfo[] programs = gson.fromJson(responseBody, ObservableProgramInfo[].class);
                            javafx.application.Platform.runLater(() -> {

                                // Save current selection
                                ObservableProgramInfo selected = allprograms.getSelectionModel().getSelectedItem();
                                String selectedName = selected != null ? selected.programname() : null;

                                allprograms.getItems().clear();
                                if (programs != null&&programs.length>0)
                                 allprograms.getItems().addAll(programs);

                                if (selectedName != null) {
                                    for (ObservableProgramInfo p : allprograms.getItems()) {
                                        if (p.programname().equals(selectedName)) {
                                            allprograms.getSelectionModel().select(p);
                                            break;
                                        }
                                    }
                                }
                            });
                        }
                    }

                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Run immediately, then every 5 seconds
        programsscheduler.scheduleAtFixedRate(showprograms, 0, 5, TimeUnit.SECONDS);

        //All functions table update thread
        functionsscheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable showfunctions = () -> {
            try {
                OkHttpClient CLIENT = new OkHttpClient();
                Request req = new Request.Builder()
                        .url("http://localhost:8080/server_war/functions")
                        .get()
                        .build();
                CLIENT.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            ObservableFunctionInfo[] functions = gson.fromJson(responseBody, ObservableFunctionInfo[].class);
                            javafx.application.Platform.runLater(() -> {

                                // Save current selection
                                ObservableFunctionInfo selected = allfunctions.getSelectionModel().getSelectedItem();
                                String selectedName = selected != null ? selected.funcname() : null;

                                allfunctions.getItems().clear();
                                if(functions!=null&&functions.length>0)
                                    allfunctions.getItems().addAll(functions);

                                if (selectedName != null) {
                                    for (ObservableFunctionInfo f : allfunctions.getItems()) {
                                        if (f.funcname().equals(selectedName)) {
                                            allfunctions.getSelectionModel().select(f);
                                            break;
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        // Run immediately, then every 5 seconds
        functionsscheduler.scheduleAtFixedRate(showfunctions, 0, 5, TimeUnit.SECONDS);

    }


    public void setUserLogged(String userLogged,int initialcredits) {
        usernameproperty.set(userLogged);
        creditsproperty.set(String.valueOf(initialcredits)+"");
        username.textProperty().bind(usernameproperty);
        credits.textProperty().bind(creditsproperty);
        setDashboardScene();
    }

    public void setDashboardScene() { //A user is selected
            maingrid.getChildren().clear();
            maingrid.getChildren().addAll(anchor00,anchor01,anchor10,anchor11);
            usermenuhbox.setVisible(true);
            this.userHistoryShow=usernameproperty.get();

            //User history table update thread (in initialize userHistoryShow is null so it doesn't run)
            historyscheduler = Executors.newSingleThreadScheduledExecutor();
            Runnable showhistory = () -> {
                try {
                        OkHttpClient CLIENT = new OkHttpClient();
                        Request req = new Request.Builder()
                                .url("http://localhost:8080/server_war/userhistory?username=" + userHistoryShow)
                                .get()
                                .build();
                        CLIENT.newCall(req).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseBody = response.body().string();
                                if (response.isSuccessful()) {
                                    Gson gson = new Gson();
                                    RunInfo[] runs = gson.fromJson(responseBody, RunInfo[].class);
                                    javafx.application.Platform.runLater(() -> {
                                        userhistory.getItems().clear();
                                        if(runs!=null&&runs.length>0)
                                         userhistory.getItems().addAll(runs);
                                    });
                                }
                            }
                        });

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            };
            // Run immediately, then every 5 seconds
            historyscheduler.scheduleAtFixedRate(showhistory, 0, 5, TimeUnit.SECONDS);
    }

    private void setLoginScene() {
        try {
            maingrid.getChildren().clear();
            usermenuhbox.setVisible(false);
            FXMLLoader loginpane = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Node loginpaneNode = loginpane.load();
            LoginController loginController = loginpane.getController();
            loginController.setDashboardController(this);
            maingrid.add(loginpaneNode, 0, 0);
            GridPane.setColumnSpan(loginpaneNode, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void chargePressed(ActionEvent event) {
        String user = usernameproperty.get();
        int creditsToAdd;
        try {
            creditsToAdd = Integer.parseInt(chargecredits.getText());
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            String json = "username=" + user + "\n"+"credits=" + creditsToAdd + "\n"+"action=add";
            RequestBody jsonBody = RequestBody.create(json, mediaType);
            Request request = new Request.Builder()
                    .url("http://localhost:8080/server_war/users")
                    .method("PUT", jsonBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    creditsproperty.set(String.valueOf(responseBody) + "");
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(responseBody);
                    alert.setContentText("Please try again later");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format");
            return;

        }
    }

    @FXML
    void deselectuserPressed(ActionEvent event) {
            this.userHistoryShow = usernameproperty.get();
            allusers.getSelectionModel().clearSelection();
    }

    @FXML
    void loadPressed(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Program File");
        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        // Check if a file was selected
        if (file == null) {
            return; // User cancelled the file chooser
        }
        String filename = file.getName();
        if (!filename.toLowerCase().endsWith(".xml"))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid file type");
            alert.setContentText("Please select a .xml file");
            alert.showAndWait();
            return;
        }
        if ((!fileroute.getText().isEmpty()) && fileroute.getText().equals(file.getAbsolutePath())) {
            return;
        }
        fileroute.setText(file.getAbsolutePath());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, mediaType))
                .build();
        Request request = new Request.Builder()
                .url("http://localhost:8080/server_war/programs?username=" + usernameproperty.get())
                .method("POST", body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            if (response.isSuccessful()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Program loaded successfully");
                alert.setContentText("You can now select it from the list of programs");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(responseBody);
                alert.setContentText("Please try again later");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        private void moveToExecutionScene(String progname,boolean isMainProgram) {
            OkHttpClient client = HttpClientSingleton.getInstance();
            RequestBody body = RequestBody.create(null, new byte[0]);
            try {
            String encodedProgname = URLEncoder.encode(progname, "UTF-8");
            Request request = new Request.Builder()
                    .url("http://localhost:8080/server_war/users?programname=" + encodedProgname+"&username="+usernameproperty.get())
                    .method("POST", body)
                    .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Could not set program for execution");
                    alert.setContentText("Please try again later");
                    alert.showAndWait();
                    return;
                }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
            FXMLLoader executionpane = new FXMLLoader(getClass().getResource("/fxml/execution.fxml"));
            Node executionpaneNode = executionpane.load();
            ExecutionController executionController = executionpane.getController();
            executionController.setInitialInfo(usernameproperty.get(), progname, Integer.parseInt(creditsproperty.get()),isMainProgram);
            this.maingrid.getScene().getWindow().hide();
            usersscheduler.shutdown();
            historyscheduler.shutdown();
            programsscheduler.shutdown();
            functionsscheduler.shutdown();
            Stage stage = new Stage();
            stage.setTitle("Execution - S-Emulator");
            stage.setScene(new Scene((Parent) executionpaneNode));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    void selectFunctionPressed(ActionEvent event) {
        ObservableFunctionInfo selectedFunction = allfunctions.getSelectionModel().getSelectedItem();
        if (selectedFunction == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No function selected");
            alert.setContentText("Please select a function from the list");
            alert.showAndWait();
            return;
        }
        moveToExecutionScene(selectedFunction.funcname(),false);
    }

    @FXML
    void selectProgramPressed(ActionEvent event) {
        ObservableProgramInfo selectedProgram = allprograms.getSelectionModel().getSelectedItem();
        if (selectedProgram == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No program selected");
            alert.setContentText("Please select a program from the list");
            alert.showAndWait();
            return;
        }
        moveToExecutionScene(selectedProgram.programname(),true);
    }

}

