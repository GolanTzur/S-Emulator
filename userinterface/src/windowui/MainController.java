package windowui;
import engine.*;
import engine.basictypes.HasLabel;
import engine.basictypes.Variable;
import engine.classhierarchy.*;
import engine.jaxbclasses.SInstructions;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;


public class MainController {

 private Program program;
 private Program programcopy;
 private final SimpleIntegerProperty currdegree = new SimpleIntegerProperty(0);
 private final SimpleIntegerProperty maxdegree = new SimpleIntegerProperty(0);
 private boolean loadedFromFile;
 private Map<Integer, TextField> inputFields = new HashMap<>();
 private Debugger debugger;


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
 private Button run;

 @FXML
 private Button resume;


 @FXML
 private Button stop;

 @FXML
 void collapseProgram(ActionEvent event) {
  if (currdegree.get() == 0) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Collapse Program Error");
   alert.setHeaderText("Cannot collapse further");
   alert.showAndWait();
   return;
  }
  currdegree.setValue(currdegree.getValue() - 1);
  programcopy = program.clone();
  programcopy.deployToDegree(currdegree.getValue());
  instructionstable.setItems(getInstructions(programcopy));
  clearTableSelection();
  commandshistory.getItems().clear();
 }

 private void startDebugging() {
  debug.setText("Step");
  run.setVisible(false);
  reload.setVisible(false);
  progcontrolhbox2.setVisible(false);
  functionselector.setVisible(false);
  programvarsvbox.getChildren().clear();
  debugger = new Debugger(new Runner(programcopy.getInstructions()));
  instructionstable.getSelectionModel().clearAndSelect(0);
  debugger.setRunning(true);
 }

 private void stopDebugging() {
  debug.setText("Debug");
  run.setVisible(true);
  reload.setVisible(true);
  progcontrolhbox2.setVisible(true);
  //functionselector.setVisible(true);
  //programvarsvbox.getChildren().clear();
  debugger = null;
  instructionstable.getSelectionModel().clearAndSelect(-1);
  clearTableSelection();
  programcopy = program.clone();
  programcopy.deployToDegree(currdegree.getValue());
 }


 @FXML
 void debugProgram(ActionEvent event) {

  if (programcopy == null) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Debug Program Error");
   alert.setHeaderText("No program loaded");
   alert.showAndWait();
   return;
  }

  if (debugger != null && !debugger.isRunning())
   return;

  if (debugger == null) {
   startDebugging();
   try {
    loadVariablesToProgramCopy();
    //programcopy.updateValues();
   } catch (Exception e) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Debug Program Error");
    alert.setHeaderText("Could not load input variables");
    alert.setContentText(e.getMessage()); // or a custom message
    alert.showAndWait();
   }
  } else {
   ProgramVars before = programcopy.getVars().clone();
   debugger.step();
   int nextIndex = debugger.getCurrentStep(); // Assumes this method exists
   instructionstable.getSelectionModel().clearAndSelect(nextIndex);
   showVariables(debugger.getCycleCount(), before);
   if (!debugger.isRunning()) {
    stopDebugging();
    programvarsvbox.getChildren().clear();
   }
  }

 }

 @FXML
 void expandProgram(ActionEvent event) {

  if (currdegree.get() == maxdegree.get()) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Expand Program Error");
   alert.setHeaderText("Cannot expand further");
   alert.showAndWait();
   return;
  }

  currdegree.setValue(currdegree.getValue() + 1);
  programcopy = program.clone();
  programcopy.deployToDegree(currdegree.getValue());
  instructionstable.setItems(getInstructions(programcopy));
  clearTableSelection();
  commandshistory.getItems().clear();
 }

 @FXML
 void highlightSelection(ActionEvent event) {

  if (!bylabradio.isSelected() && !byvarradio.isSelected()) {
   return;
  }

  AbstractInstruction selected = instructionstable.getSelectionModel().getSelectedItem();

  if (selected == null) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Highlight Selection Error");
   alert.setHeaderText("No instruction selected");
   alert.showAndWait();
   return;
  }
  Set<Integer> labelPositions = new HashSet<>();
  Set<Integer> varPositions = new HashSet<>();

  if (bylabradio.isSelected()) {
   HasLabel labelToHighlight = selected.getLab();
   Set<Integer> positions = programcopy.findLabelsEquals(labelToHighlight);
   Set<Integer> argpositions = new HashSet<>();

   if (selected instanceof HasGotoLabel)
    argpositions = programcopy.findLabelsEquals(((HasGotoLabel) selected).getGotolabel());
   positions.addAll(argpositions);
   labelPositions.addAll(positions);
  }
  if (byvarradio.isSelected()) {
   if (!(selected instanceof GotoLabel)) {
    Set<Variable> varsToHighlight = programcopy.getAllInvolvedVariables(selected);
    for (Variable var : varsToHighlight) {
     Set<Integer> positions = programcopy.findVariableUsage(var);
     varPositions.addAll(positions);
    }

       /*
       Variable varToHighlight = selected.getVar();
       Set<Integer> positions = programcopy.findVariableUsage(varToHighlight);
       if (selected instanceof HasExtraVar)
        positions.addAll(programcopy.findVariableUsage(((HasExtraVar) selected).getArg()));

       varPositions.addAll(positions);*/

   }
  }
  Set<Integer> allPositions = new HashSet<>();
  allPositions.addAll(labelPositions);
  allPositions.addAll(varPositions);

  instructionstable.setRowFactory(tv -> new TableRow<AbstractInstruction>() {
   @Override
   protected void updateItem(AbstractInstruction instr, boolean empty) {
    super.updateItem(instr, empty);
    if (instr == null || empty) {
     setStyle("");
    } else if (allPositions.contains(instr.getPos())) {
     setStyle("-fx-background-color: yellow;");
    } else {
     setStyle("");
    }
   }
  });

  instructionstable.refresh();

 }


 @FXML
 void loadProgram(ActionEvent event) {

  FileChooser fileChooser = new FileChooser();
  fileChooser.setTitle("Open Program File");
  File file = fileChooser.showOpenDialog(load.getParentPopup().getOwnerWindow());
  // Check if a file was selected
  if ((!fileroute.getText().isEmpty()) && fileroute.getText().equals(file.getAbsolutePath())) {
   return;
  }

  XMLHandler xmlhandler = XMLHandler.getInstance();
  try {

   // Load the program from the selected file
   program = xmlhandler.loadProgram(file.getAbsolutePath());
   program.checkValidity();

   waitForLoadProgram();

   programcopy = program.clone();

   programLoaded();
   currdegree.set(0);
   maxdegree.set(program.getProgramDegree());
   setInputVariables(program);
   fileroute.setText(file.getAbsolutePath());
   loadedFromFile = false;

   Alert alert = new Alert(Alert.AlertType.INFORMATION);
   alert.setTitle("Load Program");
   alert.setHeaderText(String.format("Program %s loaded successfully.", program.getName()));
   alert.showAndWait();

  } catch (Exception e) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Load Program Error");
   alert.setHeaderText("Could not load the program");
   alert.setContentText(e.getMessage()); // or a custom message
   e.printStackTrace();
   alert.showAndWait();
  }

 }

 private void waitForLoadProgram() {
  Alert loadalert = new Alert(Alert.AlertType.INFORMATION);
  loadalert.setTitle("Load Program");
  ProgressBar progressBar = new ProgressBar();
  Label progressLabel = new Label("Steps: 0 of 5");

  VBox vbox = new VBox(10, progressBar, progressLabel);
  vbox.setPadding(new Insets(10));

  loadalert.getDialogPane().setContent(vbox);
  loadalert.setWidth(300);
  loadalert.setHeaderText("Loading program, please wait...");
  progressBar.setPrefWidth(250);

  Task<Void> task = new Task<>() {
   @Override
   protected Void call() throws Exception {
    // Simulate work
    for (int i = 1; i <= 5; i++) {
     Thread.sleep(500); // pretend to do work
     updateProgress(i, 5);
     updateMessage("Step " + i + " of 5");
    }
    return null;
   }

  };

  progressBar.progressProperty().bind(task.progressProperty());
  progressLabel.textProperty().bind(task.messageProperty());
  task.setOnSucceeded(e -> loadalert.close());
  task.setOnFailed(e -> loadalert.close());
  task.setOnCancelled(e -> loadalert.close());

  new Thread(task).start();
  loadalert.showAndWait();

 }

 @FXML
 public void initialize() {
  functionselector.setVisible(false);
  reload.setVisible(false);
  showstatus.setVisible(false);

  noLoadedProgram();
  setInstructionsTableAddFormat(commandstablelabel, commandstablenumber, commandstablecycles, commandstabletype, commandstableinstr);
  setInstructionsTableAddFormat(instructiontablelabel, instructiontablenumber, instructiontablecycles, instructiontabletype, instructiontableinstr);
  setProgramHistoryAddFormat(historytablenumber, historytabledegree, historytablecycles, historytableinput, historytableresult);
  setCommandshistoryListener();

  degreetext1.textProperty().addListener((obs, oldValue, newValue) -> {
   if (!newValue.matches("\\d*")) {
    degreetext1.setText(newValue.replaceAll("[^\\d]", ""));
   }
  });
 }

 @FXML
 void loadSavedProgram(ActionEvent event) {
  if (loadedFromFile) {
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

   waitForLoadProgram();

   commandshistory.getItems().clear();
   programhistorytable.getItems().clear();
   programLoaded();

   instructionstable.getItems().clear();
   instructionstable.setItems(getInstructions(programcopy));
   loadedFromFile = true;

   currdegree.set(program.getProgramDegree() - programcopy.getProgramDegree());
   maxdegree.set(program.getProgramDegree());


   setInputVariables(program);
   Alert alert = new Alert(Alert.AlertType.INFORMATION);
   alert.setTitle("Load Program from file");
   alert.setHeaderText(String.format("Program %s with degree %d/%d loaded successfully.", program.getName(), currdegree.getValue(), maxdegree.getValue()));
   alert.showAndWait();

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
  if (debugger == null || !debugger.isRunning())
   return;

  ProgramVars before = programcopy.getVars().clone();
  debugger.resume();
  int nextIndex = debugger.getCurrentStep(); // Assumes this method exists
  if (nextIndex >= 0)
   instructionstable.getSelectionModel().clearAndSelect(nextIndex);
  else
   instructionstable.getSelectionModel().clearAndSelect(-1);
  showVariables(debugger.getCycleCount(), before);
  if (!debugger.isRunning())
   stopDebugging();
 }


 @FXML
 void runProgram(ActionEvent event) {
  Collection<Variable> initialInputs;
  try {
   initialInputs = loadVariablesToProgramCopy();
   //programcopy.updateValues();
  } catch (Exception e) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Run Program Error");
   alert.setHeaderText("Could not load input variables");
   alert.setContentText(e.getMessage()); // or a custom message
   alert.showAndWait();
   return;
  }
  programcopy.execute();
  Statistics stats = new Statistics(currdegree.getValue(), initialInputs, programcopy.getCycleCount(), programcopy.getVars().clone());
  //stats.appendStatistics();
  programhistorytable.getItems().add(stats);
  showVariables(programcopy.getCycleCount());
  programcopy = program.clone();
  programcopy.deployToDegree(currdegree.getValue());
 }

 @FXML
 void saveProgram(ActionEvent event) {
  if (program == null || programcopy == null) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Save Program Error");
   alert.setHeaderText("No program loaded");
   alert.showAndWait();
   return;
  }

  ProgramState state = new ProgramState(program, programcopy);

  try {
   state.saveProgramState();
   Alert alert = new Alert(Alert.AlertType.INFORMATION);
   alert.setTitle("Save Program");
   alert.setHeaderText(String.format("Program %s with degree %d/%d saved successfully.", program.getName(), currdegree.getValue(), maxdegree.getValue()));
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
 void expandToDegree(ActionEvent event) {
  if (degreetext1.getText().isEmpty()) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Expand to Degree Error");
   alert.setHeaderText("Degree field is empty");
   alert.showAndWait();
   return;
  }
  int targetDegree;
  try {
   targetDegree = Integer.parseInt(degreetext1.getText());
  } catch (NumberFormatException e) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Expand to Degree Error");
   alert.setHeaderText("Degree field is not a valid integer");
   alert.showAndWait();
   return;
  }

  if (targetDegree < 0 || targetDegree > maxdegree.get()) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setTitle("Expand to Degree Error");
   alert.setHeaderText(String.format("Degree must be between 0 and %d", maxdegree.get()));
   alert.showAndWait();
   return;
  }

  currdegree.setValue(targetDegree);
  programcopy = program.clone();
  programcopy.deployToDegree(currdegree.getValue());
  clearTableSelection();
  instructionstable.setItems(getInstructions(programcopy));
  commandshistory.getItems().clear();
 }

 @FXML
 void selectFunction(ActionEvent event) {

 }

 @FXML
 void stopdebug(ActionEvent event) {
  if (debugger == null || !debugger.isRunning())
   return;
  stopDebugging();
  programvarsvbox.getChildren().clear();
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
          new SimpleStringProperty((cellData.getValue() instanceof SyntheticSugar || cellData.getValue() instanceof Function) ? "S" : "B")
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
           cellData.getValue().getVariables().forEach((var) -> {
            ;
            sb.append(var.toString()).append("=").append(var.getValue()).append(",");
           });
           if (!sb.isEmpty()) sb.deleteCharAt(sb.length() - 1);
           return new SimpleStringProperty(sb.toString());
          }
  );
  result.setCellValueFactory(cellData ->
          new SimpleIntegerProperty(cellData.getValue().getResults().getY().getValue()).asObject()
  );

 }

 private void clearTableSelection() {

  instructionstable.setRowFactory(tv -> new TableRow<AbstractInstruction>() {
   @Override
   protected void updateItem(AbstractInstruction instr, boolean empty) {
    super.updateItem(instr, empty);
    setStyle("");
   }

  });
  instructionstable.refresh();
 }

 public void setCommandshistoryListener() {
  instructionstable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
   commandshistory.getItems().clear();
   ArrayList<AbstractInstruction> history = new ArrayList<>();
   AbstractInstruction prev = newSel;
   if (prev == null) return; // No selection
   AbstractInstruction next;
   while ((next = prev.getSource()) != null) {
    history.add(next);
    prev = next;
   }
   commandshistory.getItems().addAll(history);
  });
 }

 public void noLoadedProgram() {
  instructionstable.setPlaceholder(new Label("No program loaded"));
  commandshistory.setPlaceholder(new Label("No instruction selected"));
  programhistorytable.setPlaceholder(new Label("No program loaded"));

  fileroute.setEditable(false);
  instructionstable.getItems().clear();
  commandshistory.getItems().clear();
  programhistorytable.getItems().clear();
  inputFields.clear();
  loadedFromFile = false;
  program = null;
  programcopy = null;
  progcontrolhbox1.setVisible(false);
  progcontrolhbox2.setVisible(false);
  actionbuttonshbox.setVisible(false);
  reload.setVisible(false);
  showstatus.setVisible(false);
  saveprogram.setVisible(false);
  currdeg.textProperty().unbind();
  maxdeg.textProperty().unbind();
  currdeg.setText("0");
  maxdeg.setText("0");
 }

 public void programLoaded() {

  progcontrolhbox1.setVisible(true);
  progcontrolhbox2.setVisible(true);
  actionbuttonshbox.setVisible(true);
  reload.setVisible(true);
  showstatus.setVisible(true);
  saveprogram.setVisible(true);

  currdeg.textProperty().bind(currdegree.asString());
  maxdeg.textProperty().bind(maxdegree.asString());
  clearTableSelection();
  instructionstable.setItems(getInstructions(program));
  commandshistory.getItems().clear();
  programhistorytable.getItems().clear();
  inputFields.clear();
  programvarsvbox.getChildren().clear();
  Statistics.reset();
 }

 public void setInputVariables(Program program) {
  programinputsvbox.getChildren().clear();
  if (program == null) return;

  Collection<Variable> inputs = program.getVars().getInput().values();
  if (inputs.isEmpty()) {
   programinputs.setText("No input variables");
   return;
  }

  boolean fill = false;
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

 public Collection<Variable> loadVariablesToProgramCopy() throws Exception {
  Collection<Variable> variables = new ArrayList<>();
  if (programcopy == null) return variables;

  for (Map.Entry<Integer, TextField> entry : inputFields.entrySet()) {
   Integer varPos = entry.getKey();
   TextField textField = entry.getValue();
   String text = textField.getText();
   if (text == null) {
    throw new Exception("Input variable at position " + varPos + " is empty.");
   }

   if (text.isEmpty()) {
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

 public void showVariables(int cycles, ProgramVars... compareTo) {
  HBox hBox = new HBox(10);// 10 is spacing between label and field
  VBox inputVars = new VBox();
  javafx.geometry.Insets insets = new javafx.geometry.Insets(5, 5, 5, 5);
  inputVars.paddingProperty().set(insets);

  for (Variable var : programcopy.getVars().getInput().values()) {
   Label label = new Label(var.toString() + " = " + var.getValue());
   inputVars.getChildren().add(label);
   if (compareTo.length > 0) {
    Variable toCompare = compareTo[0].getInput().get(var.getPosition());
    if (toCompare != null && toCompare.getValue() != var.getValue()) {
     label.setStyle("-fx-text-fill: red;"); // Change text color to red
    }
   }
  }

  VBox workVars = new VBox();
  workVars.paddingProperty().set(insets);

  for (Variable var : programcopy.getVars().getEnvvars().values()) {
   Label label = new Label(var.toString() + " = " + var.getValue());
   workVars.getChildren().add(label);
   if (compareTo.length > 0) {
    Variable toCompare = compareTo[0].getEnvvars().get(var.getPosition());
    if (toCompare != null && toCompare.getValue() != var.getValue()) {
     label.setStyle("-fx-text-fill: red;"); // Change text color to red
    }
   }
  }

  Label yLabel = new Label("y = " + programcopy.getVars().getY().getValue());
  if (compareTo.length > 0) {
   Variable toCompare = compareTo[0].getY();
   if (toCompare != null && toCompare.getValue() != programcopy.getVars().getY().getValue()) {
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
 void reloadFromHistory(ActionEvent event) {
    Statistics stats = programhistorytable.getSelectionModel().selectedItemProperty().getValue();
    if (stats == null) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Reload from History Error");
        alert.setHeaderText("No execution selected");
        alert.showAndWait();
        return;
    }
    Iterator<Variable> inputs = stats.getVariables().iterator();
  for(Map.Entry<Integer, TextField> entry : inputFields.entrySet()) {
   TextField textField= entry.getValue();
   Integer val= inputs.next().getValue();
   textField.setText(val.toString());
  }

  int itemDegree= stats.getDegree();
  if(itemDegree!=currdegree.getValue()) {
   currdegree.setValue(itemDegree);
   programcopy = program.clone();
   programcopy.deployToDegree(currdegree.getValue());
   instructionstable.setItems(getInstructions(programcopy));
   clearTableSelection();
   commandshistory.getItems().clear();
  }

  programvarsvbox.getChildren().clear();
 }


 @FXML
 void showStatus(ActionEvent event) {
  Alert alert = new Alert(Alert.AlertType.INFORMATION);
  alert.setTitle("Run Status");
  Statistics stats = programhistorytable.getSelectionModel().selectedItemProperty().getValue();
  if (stats == null) {
   alert.setHeaderText("No execution selected");
   alert.showAndWait();
   return;
  }
  alert.setHeaderText("Program Variables: \n"+stats.getResults().toString());
  alert.showAndWait();
 }
}
