package windowui;
import engine.Program;
import engine.ProgramState;
import engine.Statistics;
import engine.XMLHandler;
import engine.basictypes.Variable;
import engine.classhierarchy.AbstractInstruction;
import engine.classhierarchy.SyntheticSugar;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class MainController {

 private Program program;
 private Program programcopy;
 private final SimpleIntegerProperty currdegree = new SimpleIntegerProperty(0);
 private final SimpleIntegerProperty maxdegree = new SimpleIntegerProperty(0);
 private String currentFilePath ="";
 private boolean loadedFromFile;
 private Map<Integer, TextField> inputFields = new HashMap<>();

 @FXML
 private Button collapse;

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
 private Button load;

 @FXML
 private Button loadsavedprogram;

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
 private VBox programinputsvbox;

 @FXML
 private VBox programvarsvbox;

 @FXML
 private Button rerun;

 @FXML
 private Button resume;

 @FXML
 private Button run;

 @FXML
 private Button saveprogram;

 @FXML
 private Button stop;

 @FXML
 void collapseProgram(ActionEvent event) {
  if(currdegree.get() == 0) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Collapse Program Error");
   alert.setHeaderText("Cannot collapse further");
   alert.showAndWait();
   return;
  }
  currdegree.setValue(currdegree.getValue()-1);
  programcopy= program.clone();
  programcopy.deployToDegree(currdegree.getValue());
  instructionstable.setItems(getInstructions(programcopy));
  commandshistory.getItems().clear();
 }

 @FXML
 void debugProgram(ActionEvent event) {

 }

 @FXML
 void expandProgram(ActionEvent event) {
  if(currdegree.get() == maxdegree.get()) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Expand Program Error");
   alert.setHeaderText("Cannot expand further");
   alert.showAndWait();
   return;
  }
  currdegree.setValue(currdegree.getValue()+1);
  programcopy.deployToDegree(1);
  instructionstable.setItems(getInstructions(programcopy));
  commandshistory.getItems().clear();

 }

 @FXML
 void highlightSelection(ActionEvent event) {

 }

 @FXML
 void loadProgram(ActionEvent event) {
  if(fileroute.getText().equals(currentFilePath)) {
    return;
  }

  XMLHandler xmlhandler = XMLHandler.getInstance();
  try {

   program = xmlhandler.loadProgram(fileroute.getText());
   program.checkValidity();
   programcopy = program.clone();

   programLoaded();
   currdegree.set(0);
   maxdegree.set(program.getProgramDegree());
   setInputVariables(program);

   loadedFromFile=false;
   currentFilePath=fileroute.getText();

  } catch (Exception e) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Load Program Error");
   alert.setHeaderText("Could not load the program");
   alert.setContentText(e.getMessage()); // or a custom message
   alert.showAndWait();
  }

 }

 @FXML
 public void initialize() {
  noLoadedProgram();
  setInstructionsTableAddFormat(commandstablelabel, commandstablenumber, commandstablecycles, commandstabletype, commandstableinstr);
  setInstructionsTableAddFormat(instructiontablelabel, instructiontablenumber, instructiontablecycles, instructiontabletype, instructiontableinstr);
  setProgramHistoryAddFormat(historytablenumber, historytabledegree, historytablecycles, historytableinput, historytableresult);
  setCommandshistoryListener();
 }

 @FXML
 void loadSavedProgram(ActionEvent event) {
    if(loadedFromFile) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Load Program from file");
        alert.setHeaderText("Program already loaded from file");
        alert.showAndWait();
     return;
    }
    try {
     ProgramState toLoad = ProgramState.loadProgramState();
     program = toLoad.getOrigin();
     programcopy = toLoad.getCopy();

     commandshistory.getItems().clear();
     programhistorytable.getItems().clear();
     programLoaded();

     instructionstable.getItems().clear();
     instructionstable.setItems(getInstructions(programcopy));
     loadedFromFile=true;

     currdegree.set(program.getProgramDegree()-programcopy.getProgramDegree());
     maxdegree.set(program.getProgramDegree());


     setInputVariables(program);
     Alert alert = new Alert(Alert.AlertType.INFORMATION);
     alert.setTitle("Load Program from file");
     alert.setHeaderText(String.format("Program %s with degree %d/%d loaded successfully.",program.getName(),currdegree.getValue(),maxdegree.getValue()));
     alert.showAndWait();
     currentFilePath="";
     fileroute.setText("");

    } catch (Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Load Program from file Error");
        alert.setHeaderText("Could not load the program");
        alert.setContentText(e.getMessage()); // or a custom message
        alert.showAndWait();
    }

 }

 @FXML
 void resumeProgram(ActionEvent event) {

 }

 @FXML
 void runProgram(ActionEvent event) {
  Collection<Variable>initialInputs;
  try {
   initialInputs = loadVariablesToProgramCopy();
  } catch (Exception e) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Run Program Error");
   alert.setHeaderText("Could not load input variables");
   alert.setContentText(e.getMessage()); // or a custom message
   alert.showAndWait();
   return;
  }
  programcopy.execute();
  Statistics stats=new Statistics(currdegree.getValue(),initialInputs,programcopy.getCycleCount(),programcopy.getVars().clone());
  stats.appendStatistics();
  programhistorytable.getItems().add(stats);
  showVariables();
 }

 @FXML
 void saveProgram(ActionEvent event) {
    if(program==null || programcopy==null){
     Alert alert = new Alert(Alert.AlertType.ERROR);
     alert.setTitle("Save Program Error");
     alert.setHeaderText("No program loaded");
     alert.showAndWait();
     return;
    }
    ProgramState state=new ProgramState(program,programcopy);
    try {
     state.saveProgramState();
     Alert alert = new Alert(Alert.AlertType.INFORMATION);
     alert.setTitle("Save Program");
     alert.setHeaderText(String.format("Program %s with degree %d/%d saved successfully.",program.getName(),currdegree.getValue(),maxdegree.getValue()));
     alert.showAndWait();
    } catch (Exception e) {
     Alert alert = new Alert(Alert.AlertType.ERROR);
     alert.setTitle("Save Program Error");
     alert.setHeaderText("Could not save the program");
     alert.setContentText(e.getMessage()); // or a custom message
     alert.showAndWait();
    }
 }

 @FXML
 void selectFunction(ActionEvent event) {

 }

 @FXML
 void stopdebug(ActionEvent event) {

 }

 public ObservableList<AbstractInstruction> getInstructions(Program program) {
  ObservableList<AbstractInstruction> instructions = FXCollections.observableArrayList();
  if (program == null) return instructions;
  instructions.addAll(program.getInstructions());
  return instructions;
 }

 private void setInstructionsTableAddFormat(TableColumn<AbstractInstruction, String> label, TableColumn<AbstractInstruction, Integer> number, TableColumn<AbstractInstruction, Integer> cycles, TableColumn<AbstractInstruction, String> type, TableColumn<AbstractInstruction, String> instr) {
  // Define how each column gets its value
  label.setCellValueFactory(cellData ->
          new SimpleStringProperty(cellData.getValue().getLab().toString())
  );

  number.setCellValueFactory(cellData ->
          new SimpleIntegerProperty(cellData.getValue().getPos()).asObject()
  );

  cycles.setCellValueFactory(cellData ->
          new SimpleIntegerProperty(cellData.getValue().getType().getCycles()).asObject()
  );

  type.setCellValueFactory(cellData ->
          new SimpleStringProperty(cellData.getValue() instanceof SyntheticSugar ? "S" : "B")
  );
  instr.setCellValueFactory(cellData ->
          new SimpleStringProperty(cellData.getValue().getChildPart())
  );

 }

 private void setProgramHistoryAddFormat(TableColumn<Statistics, Integer> number, TableColumn<Statistics, Integer> degree, TableColumn<Statistics, Integer> cycles, TableColumn<Statistics, String> input, TableColumn<Statistics, Integer> result) {
  // Define how each column gets its value

  number.setCellValueFactory(cellData ->
          new SimpleIntegerProperty(cellData.getValue().getId()).asObject()
  );

  degree.setCellValueFactory(cellData ->
          new SimpleIntegerProperty(cellData.getValue().getDegree()).asObject()
  );

  cycles.setCellValueFactory(cellData ->
          new SimpleIntegerProperty(cellData.getValue().getCycles()).asObject()
  );

  input.setCellValueFactory(cellData -> {
           StringBuilder sb = new StringBuilder();
           cellData.getValue().getVariables().forEach((var) -> {;
            sb.append(var.toString()).append("=").append(var.getValue()).append(",");
           });
           if(!sb.isEmpty()) sb.deleteCharAt(sb.length()-1);
           return new SimpleStringProperty(sb.toString());
          }
  );
  result.setCellValueFactory(cellData ->
          new SimpleIntegerProperty(cellData.getValue().getResults().getY().getValue()).asObject()
  );

 }

 public void setCommandshistoryListener() {
  instructionstable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
   commandshistory.getItems().clear();
   ArrayList<AbstractInstruction> history = new ArrayList<>();
   AbstractInstruction prev = newSel;
    if (prev == null) return; // No selection
   AbstractInstruction next;
   while ((next = prev.getSource()) != null)
   {
    history.add(next);
    prev = next;
   }
   commandshistory.getItems().addAll(history);
  });
 }
 public void noLoadedProgram(){
  instructionstable.setPlaceholder(new Label("No program loaded"));
  commandshistory.setPlaceholder(new Label("No instruction selected"));
  programhistorytable.setPlaceholder(new Label("No program loaded"));
  instructionstable.getItems().clear();
  commandshistory.getItems().clear();
  programhistorytable.getItems().clear();
  inputFields.clear();
  loadedFromFile=false;
  program=null;
  programcopy=null;
  expand.setVisible(false);
  collapse.setVisible(false);
  run.setVisible(false);
  debug.setVisible(false);
  stop.setVisible(false);
  resume.setVisible(false);
  rerun.setVisible(false);
  saveprogram.setVisible(false);
  functionselector.setVisible(false);
  highlightselection.setVisible(false);
  currdeg.textProperty().unbind();
  maxdeg.textProperty().unbind();
  currdeg.setText("0");
  maxdeg.setText("0");
 }
 public void programLoaded()
 {
  expand.setVisible(true);
  collapse.setVisible(true);
  run.setVisible(true);
  debug.setVisible(true);
  stop.setVisible(true);
  resume.setVisible(true);
  rerun.setVisible(true);
  saveprogram.setVisible(true);
  functionselector.setVisible(true);
  highlightselection.setVisible(true);
  currdeg.textProperty().bind(currdegree.asString());
  maxdeg.textProperty().bind(maxdegree.asString());
  instructionstable.setItems(getInstructions(program));
  commandshistory.getItems().clear();
  programhistorytable.getItems().clear();
  inputFields.clear();
  programvarsvbox.getChildren().clear();
  Statistics.reset();
 }
 public void setInputVariables(Program program) {
    programinputsvbox.getChildren().clear();
    if(program==null) return;

    Collection<Variable> inputs = program.getVars().getInput().values();
    if(inputs.isEmpty()) {
     programinputs.setText("No input variables");
     return;
    }

  boolean fill=false;
  Label previousLabel = null;
  TextField previousTextField = null;

  for (Map.Entry<Integer, Variable> entry : program.getVars().getInput().entrySet()) {
   Integer varPos = entry.getKey();
   Variable var = entry.getValue();

   Label label = new Label(var.toString());
   TextField textField = new TextField();
   textField.setMaxSize(60, 60);

   textField.textProperty().addListener((obs, oldValue, newValue) -> {
    if (!newValue.matches("\\d*")) {
     textField.setText(newValue.replaceAll("[^\\d]", ""));
    }
   });

   inputFields.put(varPos, textField);
    if(!fill) {
     fill=true;
     previousLabel = label;
     previousTextField = textField;
    }
    else {
     HBox hBox = new HBox(10,previousLabel,previousTextField, label,textField); // 10 is spacing between label and field
     hBox.paddingProperty().set(new javafx.geometry.Insets(5, 5, 5, 5));
     programinputsvbox.getChildren().add(hBox);
     fill=false;
    }
  }
    if(fill) {
     HBox hBox = new HBox(10, previousLabel, previousTextField); // 10 is spacing between label and field
     hBox.paddingProperty().set(new javafx.geometry.Insets(5, 5, 5, 5));
     programinputsvbox.getChildren().add(hBox);
    }
 }

 public Collection<Variable> loadVariablesToProgramCopy() throws Exception {
    Collection<Variable> variables = new ArrayList<>();
    if(programcopy==null) return variables;
    for (Map.Entry<Integer, TextField> entry : inputFields.entrySet()) {
     Integer varPos = entry.getKey();
     TextField textField = entry.getValue();
     String text = textField.getText();
     if (text == null) {
      throw new Exception("Input variable at position " + varPos + " is empty.");
     }

     if(text.isEmpty()) {
      text = "0";
     }

     try {
      int value = Integer.parseInt(text);
      Variable toUpdate = programcopy.getVars().getInput().get(varPos);
      toUpdate.setValue(value);
      variables.add(Variable.createDummyVar(toUpdate.getType(), toUpdate.getPosition(), toUpdate.getValue()));
     } catch (NumberFormatException e) {
      throw new Exception("Input variable at position " + varPos + " is not a valid integer.");
     }
    }
  return variables;
 }
 public void showVariables()
 {
     HBox hBox = new HBox(10);// 10 is spacing between label and field
     VBox inputVars = new VBox();
     javafx.geometry.Insets insets = new javafx.geometry.Insets(5, 5, 5, 5);
     inputVars.paddingProperty().set(insets);

    for(Variable var:programcopy.getVars().getInput().values()) {
     Label label = new Label(var.toString()+" = "+var.getValue());
     inputVars.getChildren().add(label);
    }
    VBox workVars = new VBox();
    workVars.paddingProperty().set(insets);

    for (Variable var : programcopy.getVars().getEnvvars().values()) {
     Label label = new Label(var.toString()+" = "+var.getValue());
     workVars.getChildren().add(label);
    }

    Label yLabel= new Label("y = "+programcopy.getVars().getY().getValue());
    yLabel.paddingProperty().set(insets);

    Label cyclesLabel= new Label("Cycles = "+programcopy.getCycleCount());
    cyclesLabel.paddingProperty().set(insets);

    hBox.getChildren().addAll(inputVars,workVars,yLabel,cyclesLabel);
    programvarsvbox.getChildren().clear();
    programvarsvbox.getChildren().add(hBox);
 }


}
