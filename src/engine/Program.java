package engine;

import engine.basictypes.*;
import engine.classhierarchy.*;


import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.*;

public class Program implements Serializable {
    private final String name;
    private int cycleCount=0;
    private ArrayList<AbstractInstruction> instructions;
    private int programCounter = 0; // Program counter to track execution position
    private ProgramVars vars;

    public Program(String name,ArrayList<AbstractInstruction> instructions,ProgramVars vars) {
        this.name = name;
        this.instructions = instructions;
        for(AbstractInstruction instruction:instructions){
            instruction.setPos(++programCounter);
        }
        this.vars = vars;
    }

    public ProgramVars getVars() {
        return vars;
    }


    public Variable execute() {
        Runner runner= new Runner(instructions/*,vars*/);
        runner.run(true);
        this.cycleCount=runner.getCycleCount();
        return vars.getY();
    }

    public void replaceProgramResultVars(){
        Map<ResultVar,Variable> resultVarMap = new HashMap<>();
    }


    private Set<HasLabel> getallprogramlabels()
    {
        Set<HasLabel> labels = new HashSet<>();
        boolean hasExit=false;
        for (AbstractInstruction instruction : instructions) {
            if(instruction.getLab() instanceof Label) {
                labels.add((Label) instruction.getLab()); // Add label to the set
            }
            if (instruction instanceof HasGotoLabel) {
                HasGotoLabel gotoLabel = (HasGotoLabel) instruction;
                if(gotoLabel.getGotolabel()==FixedLabel.EXIT) {
                   hasExit=true;
                }
            }
        }
        if(hasExit) {
            labels.add(FixedLabel.EXIT);
        }
        return labels; // Returns a set of all labels in the program
    }
    public void deployToDegree(int degree)
    {
        for(int i=0;i<degree;i++) {
            deploy();
        }
    }

    private void deploy()
    {
        Set<HasLabel> allprogramlabels = getallprogramlabels();
        for(int i=0;i<instructions.size();i++){
            AbstractInstruction currentInstruction = instructions.get(i);
            if(currentInstruction instanceof SyntheticSugar)
            {
                AbstractInstruction source;
                ArrayList<AbstractInstruction> expandedInstructions = ((SyntheticSugar) currentInstruction).expand(this.vars);
                for(AbstractInstruction instruction:expandedInstructions){
                    instruction.setPos(++programCounter); // Set position for each expanded instruction
                }
                source=instructions.remove(i);
                expandedInstructions.forEach(instruction->instruction.setSyntheticSource(source));// Set the source for each expanded instruction
                if(source.getLab() instanceof Label) {
                    removeFirstLabelCollisions((Label) source.getLab(), expandedInstructions, allprogramlabels);
                }
                replaceLabels(expandedInstructions, allprogramlabels); // Replace labels in the expanded instructions if needed
                instructions.addAll(i, expandedInstructions); // Replace the synthetic sugar with its expanded instructions
                i+=expandedInstructions.size()-1; // Adjust index to account for the newly added instructions
            } else if (currentInstruction instanceof Function) {
                AbstractInstruction source;

                ArrayList<AbstractInstruction> expandedInstructions = ((Function) currentInstruction).expand(this.vars,allprogramlabels);
                for(AbstractInstruction instruction:expandedInstructions){
                    instruction.setPos(++programCounter); // Set position for each expanded instruction
                }
                source=instructions.remove(i);
                expandedInstructions.forEach(instruction->instruction.setSyntheticSource(source));// Set the source for each expanded instruction
                instructions.addAll(i, expandedInstructions); // Replace the synthetic sugar with its expanded instructions
                i+=expandedInstructions.size()-1; // Adjust index to account for the newly added instructions
            }

        }
    }

    private boolean labelExistsInInstructions(Label label, ArrayList<AbstractInstruction> instructions) {
        for (int i=1; i < instructions.size(); i++) {
            if (label.equals(instructions.get(i).getLab())) {
                return true;
            }
        }
        return false;
    }


private void removeFirstLabelCollisions(Label parentLabel, ArrayList<AbstractInstruction> expandedInstructions, Set<HasLabel> allprogramlabels) {

    Label labelToAssign = parentLabel;
    if (labelExistsInInstructions(parentLabel, expandedInstructions)) {
        int nextIndexLabel = 0;
        Label tempLabel;
        do {
            tempLabel = new Label("L" + nextIndexLabel++);
        } while (allprogramlabels.contains(tempLabel) ||
                labelExistsInInstructions(tempLabel, expandedInstructions));
        labelToAssign = tempLabel;


        for (int j = 1; j < expandedInstructions.size(); j++) {
            AbstractInstruction instr = expandedInstructions.get(j);
            if (instr.getLab() instanceof Label && instr.getLab().equals(parentLabel)) {
                instr.setLab(labelToAssign.myClone());
            }
            if(instr instanceof HasGotoLabel && ((HasGotoLabel) instr).getGotolabel().equals(parentLabel)) {
                if(expandedInstructions.get(0).getLab().equals(parentLabel))
                ((HasGotoLabel) instr).setGotolabel(labelToAssign.myClone());
            }
        }

    }
    }

    private void replaceLabels(ArrayList<AbstractInstruction> instructions, Set<HasLabel> allprogramlabels) {
        Map<Label, Label> labelMap = new HashMap<>();
        int nextIndexLabel = 0;

        // Find all labels to replace
        for (int i=1; i < instructions.size(); i++) {
            if (instructions.get(i).getLab() instanceof Label) {
                Label label = (Label) instructions.get(i).getLab();
                if (allprogramlabels.contains(label)) {
                    Label nextLabel;
                    do {
                        nextLabel = new Label("L" + nextIndexLabel++);
                    } while (allprogramlabels.contains(nextLabel));
                    labelMap.put(label, nextLabel);
                    allprogramlabels.add(nextLabel);
                }
            }
        }
        // Assign new labels to instructions with default labels
        for(int i=0;i<instructions.size();i++){
            if(instructions.get(i).getLab() ==FixedLabel.DEFAULT) {
                Label nextLabel;
                do {
                    nextLabel = new Label("L" + nextIndexLabel++);
                } while (allprogramlabels.contains(nextLabel));
                instructions.get(i).setLab(nextLabel);
                allprogramlabels.add(nextLabel);
                for (int j = 0; j < instructions.size(); j++) {
                    if(j==i) continue; // Skip the current instruction
                    if (instructions.get(j) instanceof HasGotoLabel && ((HasGotoLabel)instructions.get(j)).getGotolabel()==FixedLabel.DEFAULT) {
                        ((HasGotoLabel) instructions.get(j)).setGotolabel(nextLabel.myClone());
                    }
                }
            }
        }

        // Replace labels and goto targets
        for (AbstractInstruction instruction : instructions) {
            if (instruction.getLab() instanceof Label && labelMap.containsKey(instruction.getLab())) {
                instruction.setLab(labelMap.get(instruction.getLab()));
            }
            if (instruction instanceof HasGotoLabel) {
                HasGotoLabel gotoLabel = (HasGotoLabel) instruction;
                if (labelMap.containsKey(gotoLabel.getGotolabel())) {
                    gotoLabel.setGotolabel(labelMap.get(gotoLabel.getGotolabel()));
                }
            }
        }

   }
    public String getName() {
        return name; // Getter for program name
    }
    @Override
    public String toString()
    {
        String res="";
        res+="Program: "+name+"\n";
        res+="Program Inputs: \n";
        for(Variable input : vars.getInput().values()) {
            res += input + " "; // Concatenate string representations of all input variables
        }
        res+="\nProgram Labels: \n";
        boolean showExit=false;
        for(HasLabel label : getallprogramlabels()) {
            if(label instanceof Label) {
                res += label + " "; // Concatenate string representations of all labels
            }
            else {
                showExit=true;
            }
        }
        if (showExit) {
            res += FixedLabel.EXIT + " "; // Concatenate EXIT label if present
        }
        res+="\nProgram Instructions: \n";
        for(AbstractInstruction instruction : instructions) {
            res += instruction.toString() + "\n"; // Concatenate string representations of all instructions
        }
        return res; // Return the string representation of the program
    }
    public ArrayList<AbstractInstruction> getInstructions() {
        return instructions; // Getter for instructions
    }
    public int getProgramDegree(){
        return this.instructions.stream()
                .max((a,b)->{
                    AbstractInstruction instr = a;
                    int adeg=0,bdeg=0;
                    for(int i=0;i<2;i++) {
                        if (instr instanceof Function) {
                            if (instr == a) {
                                adeg = 1+((Function) instr).getDegree();
                            } else {
                                bdeg = 1+((Function) instr).getDegree();
                            }
                        } else if (instr instanceof JumpEqualFunction) {
                            if (instr == a) {
                                adeg = Math.max(((JumpEqualFunction) instr).getFunc().getDegree(), SyntheticType.JUMP_EQUAL_FUNCTION.getDegree());
                            } else {
                                bdeg = Math.max(((JumpEqualFunction) instr).getFunc().getDegree(), SyntheticType.JUMP_EQUAL_FUNCTION.getDegree());
                            }
                        } else if (instr instanceof SyntheticSugar) {
                            if (instr == a) {
                                adeg = ((SyntheticType) ((SyntheticSugar) instr).getType()).getDegree();
                            } else {
                                bdeg = ((SyntheticType) ((SyntheticSugar) instr).getType()).getDegree();
                            }
                        } else {
                            if (instr == a) {
                                adeg = 0;
                            } else {
                                bdeg = 0;
                            }

                            instr = b;
                        }
                    }

                     return Integer.compare(adeg, bdeg);
                }
                ).map(instruction -> {
                    if (instruction instanceof Function) {
                        return 1 + ((Function) instruction).getDegree();
                    } else if (instruction instanceof JumpEqualFunction) {
                        return Math.max(((JumpEqualFunction) instruction).getFunc().getDegree(), SyntheticType.JUMP_EQUAL_FUNCTION.getDegree());
                    } else if (instruction instanceof SyntheticSugar) {
                        return ((SyntheticType) ((SyntheticSugar) instruction).getType()).getDegree();
                    } else {
                        return 0;
                    }
                }).orElse(0);

    }


    public void checkValidity() throws  Exception
    {
        if(instructions==null) {
            throw new Exception("Program has not been initialized.");
        }
                 instructions.stream().filter(instruction->instruction instanceof HasGotoLabel)
                .map(instruction->((HasGotoLabel)instruction).getGotolabel())
                .filter(label -> (!existsLabel(label))&&(label!=FixedLabel.EXIT)) // Filter labels that do not exist in the program and are not EXIT
                .map(HasLabel::getLabel) // Check if all goto labels exist in the program
                .findFirst().ifPresent(label -> {
                    throw new IllegalArgumentException("Program has invalid goto label: " + label);
                }); //All goto labels must exist in the program

    }
    private boolean existsLabel(HasLabel label) {
        return instructions.stream().anyMatch(instruction -> instruction.getLab().equals(label));
    }
    public Collection<Variable> setUserInput()
    {
        Collection<Variable> initialVars = new ArrayList<>();
        this.vars.reset(); // Reset all variables to 0
        Collection<Variable> inputs = vars.getInput().values();
        if(inputs.isEmpty())
            return initialVars; // If there are no inputs, do nothing
        System.out.println("Please enter the values for the following inputs: ");
        for(Variable input : inputs) {
            System.out.print(input+" ");
        }
        System.out.println();
        String userinput;
        try { // Read user input from the console
            do {
                userinput = new BufferedReader(new InputStreamReader(System.in)).readLine();
            } while (!userinput.matches("^[0-9,]+$"));
        }catch (IOException e) {
        throw new RuntimeException("Error reading user input: " + e.getMessage());
    }
        String[] userinputs_splitted = userinput.split(","); // Split the input string by commas
        int [] userinputs_toInt = new int[userinputs_splitted.length]; // Create an array to store the integer values of the inputs
        for(String s : userinputs_splitted) {
            userinputs_toInt[Arrays.asList(userinputs_splitted).indexOf(s)] = Integer.parseInt(s);
        }
        int i=0;
        for(Variable input : inputs) {
            input.setValue(userinputs_toInt[i++]);
            initialVars.add(Variable.createDummyVar(input.getType(),input.getPosition(),input.getValue()));// Create a dummy variable to store the initial value of the input
            if(i>=userinputs_toInt.length) break; // Break if all inputs have been set
        }
        return initialVars;
    }
    /*public Program clone() {
        ArrayList<AbstractInstruction> clonedInstructions = new ArrayList<>();
        ProgramVars vars = new ProgramVars();

        for (AbstractInstruction instruction : this.instructions) {
            if (instruction instanceof Function) {
                Function func = (Function) instruction;
                ArrayList<Variable> inputs = new ArrayList<>(func.getProg().getVars().getInput().values());
                for (Variable var : inputs) {

                }
                clonedInstructions.add(instruction.clone(vars));
            }

            return new Program(this.name, clonedInstructions, vars);

        }
    }*/
    @Override
    public Program clone() {
        // Deep copy of ProgramVars (implement clone if needed)
        ProgramVars clonedVars = this.vars.clone();

        // Deep copy of instructions
        ArrayList<AbstractInstruction> clonedInstructions = new ArrayList<>();
        for (AbstractInstruction instruction : this.instructions) {
            clonedInstructions.add(instruction.clone(clonedVars));
        }

        return new Program(this.name, clonedInstructions, clonedVars);
    }

    public  Set<Integer> findLabelsEquals(HasLabel label)
    {
        Set<Integer> positions = new HashSet<>();
        if(label.equals(FixedLabel.EMPTY))
            return positions;
        for(int i=0;i<instructions.size();i++)
        {
            if(instructions.get(i).getLab().equals(label))
            {
                positions.add(instructions.get(i).getPos());
            }
            if(instructions.get(i) instanceof HasGotoLabel)
            {
                HasGotoLabel hgl = (HasGotoLabel) instructions.get(i);
                if(hgl.getGotolabel().equals(label))
                {
                    positions.add(instructions.get(i).getPos());
                }
            }
        }
        return positions;
    }

    public Set<Integer> findVariableUsage(Variable var)
    {
        Set<Integer> positions = new HashSet<>();
        for(int i=0;i<instructions.size();i++)
        {
            AbstractInstruction instr = instructions.get(i);
            if(instr.getVar() instanceof ResultVar)
            {
                Function searchfunc = ((ResultVar) instr.getVar()).getFunction();

                if((searchVariableRec(var,searchfunc)))
                {
                    positions.add(instr.getPos());
                    continue;
                }
            }
            else if(instr.getVar().toString().equals(var.toString()))
            {
                positions.add(instr.getPos());
                continue;
            }
            if(instr instanceof HasExtraVar)
            {
                HasExtraVar hev = (HasExtraVar) instr;
                if(hev.getArg() instanceof ResultVar)
                {
                    Function searchfunc = ((ResultVar) hev.getArg()).getFunction();
                    if((searchVariableRec(var,searchfunc)))
                    {
                        positions.add(instr.getPos());
                        continue;
                    }
                }
                else if(hev.getArg().toString().equals(var.toString()))
                {
                    positions.add(instr.getPos());
                    continue;
                }
            }
            if(instr instanceof Function)
            {
                Function func = (Function) instr;
                if(searchVariableRec(var,func))
                {
                    positions.add(instr.getPos());
                    continue;
                }
            }
            if(instr instanceof JumpEqualFunction)
            {
                JumpEqualFunction jef = (JumpEqualFunction) instr;
                if(jef.getFunc()!=null)
                {
                    if(searchVariableRec(var,jef.getFunc()))
                    {
                        positions.add(instr.getPos());
                    }
                }
            }
        }
        return positions;
    }

    private boolean searchVariableRec(Variable var,Function func)
    {
     for(Variable input : this.vars.getInput().values())
     {
         if(input instanceof ResultVar)
         {
             if(searchVariableRec(var,((ResultVar) input).getFunction()))
                 return true;
         }
         else if(input.toString().equals(var.toString()))
             return true;
     }
     return false;
    }


    public void updateValues()
    {
         Map<Integer,Integer> values = new HashMap<>();
        for(Map.Entry<Integer, Variable> entry : vars.getInput().entrySet())
        {
            if(!(entry.getValue() instanceof ResultVar))
             values.put(entry.getKey(),entry.getValue().getValue());
        }
        for(AbstractInstruction instr: instructions){
            if(instr.getVar() instanceof ResultVar){
                ((ResultVar)instr.getVar()).getFunction().getProg().updateValuesRecursive(values);
            }
            if(instr instanceof HasExtraVar){
                HasExtraVar hev=(HasExtraVar) instr;
                if(hev.getArg() instanceof ResultVar){
                    ((ResultVar)hev.getArg()).getFunction().getProg().updateValuesRecursive(values);
                }
            }
            if(instr instanceof Function)
            {
                ((Function)instr).getProg().updateValuesRecursive(values);
            }
            if(instr instanceof JumpEqualFunction){
                ((JumpEqualFunction)instr).getFunc().getProg().updateValuesRecursive(values);
            }
        }

    }
    private void updateValuesRecursive(Map<Integer,Integer> values)
    {
        for(Variable v : vars.getInput().values())
        {
            if(!(v instanceof ResultVar))
            {
                Optional<Integer> val= Optional.ofNullable(values.get(v.getPosition()));
                val.ifPresent(v::setValue);
            }
            else
            {
                ((ResultVar)v).getFunction().getProg().updateValuesRecursive(values);
            }
        }
        updateValues();

    }

    public int getCycleCount() {
        return cycleCount;
    }
    public void setCycleCount(int cycleCount) {
        this.cycleCount = cycleCount;
    }

}
