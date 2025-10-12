package classes;

import engine.Debugger;
import engine.Program;
import engine.Statistics;
import engine.basictypes.Variable;
import engine.classhierarchy.AbstractInstruction;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.*;

public class ExecutionController {

    //private StringProperty programnameprop=new SimpleStringProperty("");
    //private StringProperty usernameprop=new SimpleStringProperty("");
    private final SimpleIntegerProperty creditsprop = new SimpleIntegerProperty(0);

    private final SimpleIntegerProperty currdegree = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty maxdegree = new SimpleIntegerProperty(0);

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
    private TableView<AbstractInstruction> instructionstable;

    @FXML
    private TableColumn<AbstractInstruction, Integer> instructiontablecycles;

    @FXML
    private TableColumn<AbstractInstruction, String> instructiontableinstr;

    @FXML
    private TableColumn<AbstractInstruction, String> instructiontablelabel;

    @FXML
    private TableColumn<AbstractInstruction, Integer> instructiontablenumber;

    @FXML
    private TableColumn<AbstractInstruction, String> instructiontabletype;

    @FXML
    private Label maxdeg;

    @FXML
    private TableView<Statistics> programhistorytable;

    @FXML
    private TableColumn<Statistics, Integer> historytablecycles;

    @FXML
    private TableColumn<Statistics, Integer> historytabledegree;

    @FXML
    private TableColumn<Statistics, String> historytableinput;

    @FXML
    private TableColumn<Statistics, Integer> historytablenumber;

    @FXML
    private TableColumn<Statistics, Integer> historytableresult;

    @FXML
    private TableView<AbstractInstruction> commandshistory;

    @FXML
    private TableColumn<AbstractInstruction, Integer> commandstablecycles;

    @FXML
    private TableColumn<AbstractInstruction, String> commandstableinstr;

    @FXML
    private TableColumn<AbstractInstruction, String> commandstablelabel;

    @FXML
    private TableColumn<AbstractInstruction, Integer> commandstablenumber;

    @FXML
    private TableColumn<AbstractInstruction, String> commandstabletype;


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

    public void setInitialInfo(String userLogged, String programname, int initialcredits) {
        this.username.setText(userLogged);
        this.programname.setText(programname);
        this.creditsprop.setValue(initialcredits);

        this.currdeg.textProperty().bind(currdegree.asString());
        this.maxdeg.textProperty().bind(maxdegree.asString());
    }


}
