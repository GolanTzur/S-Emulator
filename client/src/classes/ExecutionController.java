package classes;

import com.google.gson.Gson;
import engine.Debugger;
import engine.Program;
import engine.Statistics;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.*;

public class ExecutionController {

    //private StringProperty programnameprop=new SimpleStringProperty("");
    //private StringProperty usernameprop=new SimpleStringProperty("");
    private final SimpleIntegerProperty creditsprop = new SimpleIntegerProperty(0);

    private final SimpleIntegerProperty currdegree = new SimpleIntegerProperty(0);


    private Map<Integer,TextField> inputFields = new HashMap<>();


    @FXML
    private HBox actionbuttonshbox;

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
        Request requestInputs = new Request.Builder()
                .url("http://localhost:8080/server_war/programcontext?info=programvars")
                .method("GET", null)
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
                commandshistory.getItems().addAll(history);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



    public void setInitialInfo(String userLogged, String programname, int initialcredits) {
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

    }
    @FXML
    public void expandProgram(ActionEvent actionEvent) {

    }

    @FXML
    public void collapseProgram(ActionEvent actionEvent) {
    }

    @FXML
    public void expandToDegree(ActionEvent actionEvent) {
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
    }
}
