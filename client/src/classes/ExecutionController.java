package classes;

import com.google.gson.Gson;
import engine.Debugger;
import engine.Program;
import engine.ProgramVars;
import engine.Statistics;
import engine.basictypes.Architecture;
import engine.basictypes.Variable;
import engine.classhierarchy.AbstractInstruction;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.*;

import java.util.*;

public class ExecutionController {

    private boolean isMainProgram;
    private final SimpleIntegerProperty creditsprop = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty currdegree = new SimpleIntegerProperty(0);
    private Map<Integer,TextField> inputFields = new HashMap<>();
    private boolean architectureCompatible = true;

    @FXML
    private HBox actionbuttonshbox;

    @FXML
    private ComboBox<Architecture> architectureoptions;


    @FXML
    private Button collapse;

    @FXML
    private Button selectdegree;

    @FXML
    private RadioButton bylabradio;

    @FXML
    private RadioButton byvarradio;

    @FXML
    private TextField degreetext1;

    @FXML
    private Menu file;

    @FXML
    private MenuItem load;

    @FXML
    private MenuItem loadsavedprogram;

    @FXML
    private MenuItem saveprogram;


    @FXML
    private Label currdeg;

    @FXML
    private Button debug;

    @FXML
    private Button expand;

    @FXML
    private TextField fileroute;

    @FXML
    private Button functionselector;

    @FXML
    private Button highlightselection;

    @FXML
    private RadioMenuItem lightMenuitem;

    @FXML
    private RadioMenuItem darkMenuitem;

    @FXML
    private TableView<ObservableAbstractInstruction> instructionstable;

    @FXML
    private TableColumn<ObservableAbstractInstruction, String> instructiontablecycles;

    @FXML
    private TableColumn<ObservableAbstractInstruction, String> instructiontableinstr;

    @FXML
    private TableColumn<ObservableAbstractInstruction, String> instructiontablelabel;

    @FXML
    private TableColumn<ObservableAbstractInstruction, String> instructiontablenumber;

    @FXML
    private TableColumn<ObservableAbstractInstruction, String> instructiontabletype;

    @FXML
    private Label maxdeg;

    @FXML
    private TableView<ObservableAbstractInstruction> commandshistory;

    @FXML
    private TableColumn<ObservableAbstractInstruction, String> commandstablecycles;

    @FXML
    private TableColumn<ObservableAbstractInstruction, String> commandstableinstr;

    @FXML
    private TableColumn<ObservableAbstractInstruction, String> commandstablelabel;

    @FXML
    private TableColumn<ObservableAbstractInstruction, String> commandstablenumber;

    @FXML
    private TableColumn<ObservableAbstractInstruction, String> commandstabletype;


    @FXML
    private Label programinputs;

    @FXML
    private Label programinputs1;

    @FXML
    private Label programname;

    @FXML
    private Label username;

    @FXML
    private Label usercredits;

    @FXML
    private VBox programinputsvbox;

    @FXML
    private VBox programvarsvbox;

    @FXML
    private HBox progcontrolhbox1;

    @FXML
    private HBox progcontrolhbox2;

    @FXML
    private Button reload;

    @FXML
    private Button showstatus;

    @FXML
    private Button prevFunction;

    @FXML
    private Button run;

    @FXML
    private Button resume;

    @FXML
    public void initialize()
    {
        //Set instruction tables format
        setInstructionTableAddFormat(instructiontablenumber,instructiontablelabel,instructiontabletype,instructiontableinstr,instructiontablecycles);
        setInstructionTableAddFormat(commandstablenumber,commandstablelabel,commandstabletype,commandstableinstr,commandstablecycles);

        //Set listener for instruction table selection
        setCommandshistoryListener();

        //degreetext1 should accept only numbers
        degreetext1.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                degreetext1.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        //Set architecture combobox options
        architectureoptions.getItems().addAll(Architecture.values());

        //Get degree for the program
        OkHttpClient client = HttpClientSingleton.getInstance();
        Request requestDegree = new Request.Builder()
                .url("http://localhost:8080/server_war/programcontext?info=degree")
                .method("GET", null)
                .build();
        try {
            String resp = client.newCall(requestDegree).execute().body().string();
            int degree = Integer.parseInt(resp);
            setDegree(degree);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Get instructions for the program
            Request requestInstructions = new Request.Builder()
                    .url("http://localhost:8080/server_war/programcontext?info=instructions")
                    .method("GET", null)
                    .build();
            try {
                String resp = client.newCall(requestInstructions).execute().body().string();
                Gson gson = new Gson();
                ObservableAbstractInstruction[] history = gson.fromJson(resp, ObservableAbstractInstruction[].class);
                instructionstable.getItems().addAll(history);
            } catch (Exception e) {
                e.printStackTrace();
            }

        //Get input variables for the program

        RequestBody body = RequestBody.create(null, new byte[0]);
        Request requestInputs = new Request.Builder()
                .url("http://localhost:8080/server_war/programcontext")
                .method("POST", body)
                .build();
        try {
            String resp = client.newCall(requestInputs).execute().body().string();
            Gson gson = new Gson();
            ObservableProgramVars observableProgramVars = gson.fromJson(resp, ObservableProgramVars.class);
            setInitialInputs(observableProgramVars);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInstructionTableAddFormat(TableColumn<ObservableAbstractInstruction, String>pos,
                                              TableColumn<ObservableAbstractInstruction, String>label,
                                              TableColumn<ObservableAbstractInstruction, String>type,
                                              TableColumn<ObservableAbstractInstruction, String>instr,
                                              TableColumn<ObservableAbstractInstruction, String>cycles) {
        label.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().label()));
        pos.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().pos()));
        type.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().type()));
        instr.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().instruction()));
        cycles.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().cycles()));
    }
    private void setCommandshistoryListener() {
        instructionstable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            commandshistory.getItems().clear();
            if(newSel==null)
                return;
            String selectedPos = newSel.pos();
            OkHttpClient client = HttpClientSingleton.getInstance();
            Request request = new Request.Builder()
                    .url("http://localhost:8080/server_war/programcontext?info=instructionhistory&pos=" + selectedPos)
                    .method("GET", null)
                    .build();
            try {
                String resp = client.newCall(request).execute().body().string();
                Gson gson = new Gson();
                ObservableAbstractInstruction[] history = gson.fromJson(resp, ObservableAbstractInstruction[].class);
                if(history!=null)
                 commandshistory.getItems().addAll(history);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void clearTableSelection() {

        instructionstable.setRowFactory(tv -> new TableRow<ObservableAbstractInstruction>() {
            @Override
            protected void updateItem(ObservableAbstractInstruction instr, boolean empty) {
                super.updateItem(instr, empty);
                setStyle("");
            }

        });
        instructionstable.refresh();
    }



    public void setInitialInfo(String userLogged, String programname, int initialcredits,boolean isMainProgram) {
        this.isMainProgram = isMainProgram;
        this.username.setText(userLogged);
        this.programname.setText(programname);
        this.creditsprop.setValue(initialcredits);
        this.usercredits.textProperty().bind(creditsprop.asString());
    }
    private void setDegree(int degree) {
        this.maxdeg.setText(String.valueOf(degree));
        this.currdegree.setValue(0);
        this.currdeg.textProperty().bind(currdegree.asString());
    }
    private void setInitialInputs(ObservableProgramVars observableProgramVars) {

        boolean fill = false;
        Label previousLabel = null;
        TextField previousTextField = null;

        for (int i=0;i<observableProgramVars.inputVarsNames().length;i++) {
            String var = observableProgramVars.inputVarsNames()[i];
            String pos=var.substring(1,var.length());
            int varPos=Integer.parseInt(pos);

            Label label = new Label(var);
            TextField textField = new TextField();
            textField.setMaxSize(60, 60);

            textField.textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            inputFields.put(varPos,textField);
            if (!fill) {
                fill = true;
                previousLabel = label;
                previousTextField = textField;
            } else {
                HBox hBox = new HBox(10, previousLabel, previousTextField, label, textField); // 10 is spacing between label and field
                hBox.paddingProperty().set(new javafx.geometry.Insets(5, 5, 5, 5));
                programinputsvbox.getChildren().add(hBox);
                fill = false;
            }
        }
        if (fill) {
            HBox hBox = new HBox(10, previousLabel, previousTextField); // 10 is spacing between label and field
            hBox.paddingProperty().set(new javafx.geometry.Insets(5, 5, 5, 5));
            programinputsvbox.getChildren().add(hBox);
        }
    }

    @FXML
    void backToDashboard(ActionEvent event) {
        FXMLLoader dashboardpane = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
        try
        {
        Node dashboardpaneNode = dashboardpane.load();
        DashboardController dashboardController = dashboardpane.getController();
        dashboardController.setUserLogged(this.username.getText(), this.creditsprop.getValue());
        this.instructionstable.getScene().getWindow().hide();
        Stage stage = new Stage();
        stage.setTitle("Dashboard");
        stage.setScene(new Scene((Parent) dashboardpaneNode));
        stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void highlightSelection(ActionEvent actionEvent) {
        ObservableAbstractInstruction selected = instructionstable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Highlight Selection");
            alert.setHeaderText(null);
            alert.setContentText("No instruction selected.");
            alert.showAndWait();
            return;
        }
        String labselected = bylabradio.isSelected() ? "1" : "0";
        String varselected = byvarradio.isSelected() ? "1" : "0";
        OkHttpClient client = HttpClientSingleton.getInstance();
        Request request = new Request.Builder()
                .url("http://localhost:8080/server_war/highlight?var=" + varselected + "&" + "lab=" + labselected + "&pos=" + selected.pos())
                .method("GET", null)
                .build();
        try {
            String resp = client.newCall(request).execute().body().string();
            String [] parts = resp.split(",");
            instructionstable.setRowFactory(tv -> new TableRow<ObservableAbstractInstruction>() {
                @Override
                protected void updateItem(ObservableAbstractInstruction instr, boolean empty) {
                    super.updateItem(instr, empty);
                    if (instr == null || empty) {
                        setStyle("");
                    } else if (Arrays.stream(parts).anyMatch((part) -> part.equals(instr.pos()))) {
                        setStyle("-fx-background-color: yellow;");
                    } else {
                        setStyle("");
                    }
                }
            });
            instructionstable.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void expandProgram(ActionEvent actionEvent) {
        int currdegvalue=currdegree.getValue();
        int maxdegvalue=Integer.parseInt(maxdeg.getText());
        if(currdegvalue==maxdegvalue){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Expand Degree");
            alert.setHeaderText(null);
            alert.setContentText("The program is already at its maximum degree.");
            alert.showAndWait();
            return;
        }

        currdegree.setValue(currdegvalue+1);
        OkHttpClient client = HttpClientSingleton.getInstance();
        Request request = new Request.Builder()
                .url("http://localhost:8080/server_war/expansion?degree="+currdegree.getValue())
                .method("GET", null)
                .build();
        try {
            String resp = client.newCall(request).execute().body().string();
                //Refresh instructions table
                instructionstable.getItems().clear();
                Gson gson = new Gson();
                ObservableAbstractInstruction[] commands = gson.fromJson(resp, ObservableAbstractInstruction[].class);
                instructionstable.getItems().addAll(commands);
                clearTableSelection();
                commandshistory.getItems().clear();
            if(architectureoptions.getSelectionModel().getSelectedItem()!=null){
                markByArcitecture(architectureoptions.getSelectionModel().getSelectedItem());
            }
                } catch (Exception e) {
                    e.printStackTrace();
                }
    }


    @FXML
    public void collapseProgram(ActionEvent actionEvent) {
        int currdegvalue=currdegree.getValue();
        if(currdegvalue==0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Expand Degree");
            alert.setHeaderText(null);
            alert.setContentText("The program cannot be collapsed further.");
            alert.showAndWait();
            return;
        }
        currdegree.setValue(currdegvalue-1);
        OkHttpClient client = HttpClientSingleton.getInstance();
        Request request = new Request.Builder()
                .url("http://localhost:8080/server_war/expansion?degree="+currdegree.getValue())
                .method("GET", null)
                .build();
        try {
            String resp = client.newCall(request).execute().body().string();
            //Refresh instructions table
            instructionstable.getItems().clear();
            Gson gson = new Gson();
            ObservableAbstractInstruction[] commands = gson.fromJson(resp, ObservableAbstractInstruction[].class);
            instructionstable.getItems().addAll(commands);
            clearTableSelection();
            commandshistory.getItems().clear();
            if(architectureoptions.getSelectionModel().getSelectedItem()!=null){
                markByArcitecture(architectureoptions.getSelectionModel().getSelectedItem());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @FXML
    public void expandToDegree(ActionEvent actionEvent) {
        int requesteddegree = Integer.parseInt(degreetext1.getText());
        int maxdegvalue=Integer.parseInt(maxdeg.getText());
        if(requesteddegree>maxdegvalue){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Expand Degree");
            alert.setHeaderText(null);
            alert.setContentText("The program max degree is "+maxdegvalue+".");
            alert.showAndWait();
            return;
        }
        else if(requesteddegree<0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Expand Degree");
            alert.setHeaderText(null);
            alert.setContentText("The program'ss degree must be positive.");
            alert.showAndWait();
            return;
        }

        currdegree.setValue(requesteddegree);
        OkHttpClient client = HttpClientSingleton.getInstance();
        Request request = new Request.Builder()
                .url("http://localhost:8080/server_war/expansion?degree="+currdegree.getValue())
                .method("GET", null)
                .build();
        try {
            String resp = client.newCall(request).execute().body().string();
            //Refresh instructions table
            instructionstable.getItems().clear();
            Gson gson = new Gson();
            ObservableAbstractInstruction[] commands = gson.fromJson(resp, ObservableAbstractInstruction[].class);
            instructionstable.getItems().addAll(commands);
            clearTableSelection();
            commandshistory.getItems().clear();
            if(architectureoptions.getSelectionModel().getSelectedItem()!=null){
                markByArcitecture(architectureoptions.getSelectionModel().getSelectedItem());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @FXML
    public void debugProgram(ActionEvent actionEvent) {
    }
    @FXML
    public void resumeProgram(ActionEvent actionEvent) {
    }
    @FXML
    public void stopdebug(ActionEvent actionEvent) {
    }

    @FXML
    public void runProgram(ActionEvent actionEvent) {
        Architecture selectedArch = architectureoptions.getSelectionModel().getSelectedItem();
        if (!architectureCompatible) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Run Program");
            alert.setHeaderText(null);
            alert.setContentText("The program is not compatible with the selected architecture.");
            alert.showAndWait();
            return;
        } else if (architectureoptions.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Run Program");
            alert.setHeaderText(null);
            alert.setContentText("You must select an architecture.");
            alert.showAndWait();
            return;
        } else if (creditsprop.getValue() <= selectedArch.getPrice()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Run Program");
            alert.setHeaderText(null);
            alert.setContentText("You do not have enough credits to run the program.");
            alert.showAndWait();
            return;
        }
        OkHttpClient client = HttpClientSingleton.getInstance();

        //Subtract credits from user
        MediaType mediaType = MediaType.parse("text/plain");
        String json = "username=" + username.getText() + "\n" + "credits=" + selectedArch.getPrice() + "\n" + "action=subtract";
        RequestBody jsonBody = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url("http://localhost:8080/server_war/users")
                .method("PUT", jsonBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
             if(!response.isSuccessful()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(responseBody);
                alert.setContentText("Please try again later");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //Run program with input variables
        StringBuilder inputBuilder = new StringBuilder();
        for (Map.Entry<Integer, TextField> entry : inputFields.entrySet()) {
            String value = entry.getValue().getText();
            if (value.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("All input fields must be filled.");
                alert.showAndWait();
                return;
            }
            inputBuilder.append(value).append(",");
        }
        if (inputBuilder.length() > 0) {
            inputBuilder.deleteCharAt(inputBuilder.length() - 1); // Remove last comma
        }

        RequestBody body = RequestBody.create("inputs=" + inputBuilder.toString(), mediaType);
        Request requestRun = new Request.Builder()
                .url("http://localhost:8080/server_war/run")
                .method("POST", body)
                .build();
        try {
            Response response = client.newCall(requestRun).execute();
            String resp = response.body().string();
            if (response.isSuccessful()) {
                Gson gson = new Gson();
                ObservableProgramVars runResult = gson.fromJson(resp, ObservableProgramVars.class);

                //Get credits left and update
                Request requestCredits = new Request.Builder()
                        .url("http://localhost:8080/server_war/usercontext?info=credits")
                        .method("GET", null)
                        .build();
                try {
                    String respCredits = client.newCall(requestCredits).execute().body().string();
                    int creditsLeft = Integer.parseInt(respCredits);
                    int cycles = (creditsprop.get() - (creditsLeft+selectedArch.getPrice()));
                    creditsprop.set(creditsLeft);
                    showVariables(cycles, runResult);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(resp);
                alert.setContentText("Please try again later");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showVariables(int cycles, ObservableProgramVars... progVars) {
        HBox hBox = new HBox(10);// 10 is spacing between label and field
        VBox inputVars = new VBox();
        javafx.geometry.Insets insets = new javafx.geometry.Insets(5, 5, 5, 5);
        inputVars.paddingProperty().set(insets);

        for (int i=0;i<progVars[0].inputVarsNames().length;i++) {
            String varName=progVars[0].inputVarsNames()[i];
            String varValue=progVars[0].inputVarsValues()[i];
            Label label = new Label(varName + " = " + varValue);
            inputVars.getChildren().add(label);
            if (progVars.length > 1) { // Compare with previous state if available
                String toCompare = progVars[1].inputVarsValues()[i];
                if (!toCompare.equals(varValue)) {
                    label.setStyle("-fx-text-fill: red;"); // Change text color to red
                }
            }
        }

        VBox workVars = new VBox();
        workVars.paddingProperty().set(insets);

        for (int i=0;i<progVars[0].workVarsNames().length;i++) {
            String varName=progVars[0].workVarsNames()[i];
            String varValue=progVars[0].workVarsValues()[i];
            Label label = new Label(varName + " = " + varValue);
            workVars.getChildren().add(label);
            if (progVars.length > 1) { // Compare with previous state if available
                String toCompare = progVars[1].workVarsValues()[i];
                if (!toCompare.equals(varValue)) {
                    label.setStyle("-fx-text-fill: red;"); // Change text color to red
                }
            }
        }

        Label yLabel = new Label("y = " + progVars[0].result());
        if (progVars.length > 1) {
            String toCompare = progVars[1].result();
            if (!toCompare.equals(progVars[0].result())) {
                yLabel.setStyle("-fx-text-fill: red;"); // Change text color to red
            }
        }
        yLabel.paddingProperty().set(insets);

        Label cyclesLabel = new Label("Cycles = " + cycles);
        cyclesLabel.paddingProperty().set(insets);

        hBox.getChildren().addAll(inputVars, workVars, yLabel, cyclesLabel);
        programvarsvbox.getChildren().clear();
        programvarsvbox.getChildren().add(hBox);
    }



    @FXML
    void selectarchitecture(ActionEvent event) {
        Architecture selected = architectureoptions.getSelectionModel().getSelectedItem();
        markByArcitecture(selected);
    }
    private void markByArcitecture(Architecture architecture) {
        OkHttpClient client = HttpClientSingleton.getInstance();
        RequestBody body = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder()
                .url("http://localhost:8080/server_war/highlight?architecture="+architecture.name())
                .method("POST", body)
                .build();
        try {
            String resp = client.newCall(request).execute().body().string();
            String [] parts = resp.split(",");
            architectureCompatible=true;
            instructionstable.setRowFactory(tv -> new TableRow<ObservableAbstractInstruction>() {
                @Override
                protected void updateItem(ObservableAbstractInstruction instr, boolean empty) {
                    super.updateItem(instr, empty);
                    if (instr == null || empty) {
                        setStyle("");
                    } else if (Arrays.stream(parts).anyMatch((part) -> part.equals(instr.pos()))) {
                        setStyle("-fx-background-color: red;");
                        architectureCompatible=false;
                    } else {
                        setStyle("");
                    }
                }
            });
            instructionstable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
