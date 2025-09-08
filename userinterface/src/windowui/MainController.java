package windowui;
import engine.Program;
import engine.XMLHandler;
import engine.classhierarchy.AbstractInstruction;
import engine.classhierarchy.SyntheticSugar;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;


public class MainController {

 private Program program;
 private Program programcopy;
 private final SimpleIntegerProperty currdegree = new SimpleIntegerProperty(0);
 private final SimpleIntegerProperty maxdegree = new SimpleIntegerProperty(0);
 private String currentFilePath="";

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
 private TableView<?> programhistorytable;

 @FXML
 private TableColumn<?, ?> historytablecycles;

 @FXML
 private TableColumn<?, ?> historytabledegree;

 @FXML
 private TableColumn<?, ?> historytableinput;

 @FXML
 private TableColumn<?, ?> historytablenumber;

 @FXML
 private TableColumn<?, ?> historytableresult;

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
   alert.setTitle("Expand Program Error");
   alert.setHeaderText("Cannot expand further");
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
  programcopy.deployToDegree(currdegree.getValue());
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
   instructionstable.setItems(getInstructions(program));
   commandshistory.getItems().clear();
   programhistorytable.getItems().clear();
   currentFilePath=fileroute.getText();
  } catch (Exception e) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   instructionstable.getItems().clear();
   alert.setTitle("Load Program Error");
   alert.setHeaderText("Could not load the program");
   alert.setContentText(e.getMessage()); // or a custom message
   alert.showAndWait();
  }

 }

 @FXML
 public void initialize() {
  noLoadedProgram();
  setInstructionsTableListener(commandstablelabel, commandstablenumber, commandstablecycles, commandstabletype, commandstableinstr);
  setInstructionsTableListener(instructiontablelabel, instructiontablenumber, instructiontablecycles, instructiontabletype, instructiontableinstr);
  setCommandshistoryListener();
 }


 @FXML
 void loadSavedProgram(ActionEvent event) {

 }

 @FXML
 void resumeProgram(ActionEvent event) {

 }

 @FXML
 void runProgram(ActionEvent event) {

 }

 @FXML
 void saveProgram(ActionEvent event) {

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

 private void setInstructionsTableListener(TableColumn<AbstractInstruction, String> label, TableColumn<AbstractInstruction, Integer> number, TableColumn<AbstractInstruction, Integer> cycles, TableColumn<AbstractInstruction, String> type, TableColumn<AbstractInstruction, String> instr) {
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

   for (int i = history.size() - 1; i >= 0; i--)
    commandshistory.getItems().add(history.get(i));
  });
 }
 public void noLoadedProgram(){
  instructionstable.setPlaceholder(new Label("No program loaded"));
  commandshistory.setPlaceholder(new Label("No instruction selected"));
  programhistorytable.setPlaceholder(new Label("No program loaded"));
  instructionstable.getItems().clear();
  commandshistory.getItems().clear();
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
 }

}
