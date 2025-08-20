package engine;

import engine.basictypes.*;
import engine.classhierarchy.AbstractInstruction;
import engine.classhierarchy.HasGotoLabel;
import engine.classhierarchy.SyntheticSugar;
import engine.classhierarchy.ZeroVar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Program {
    private final String name;
    private ArrayList<AbstractInstruction> instructions;
    private int programCounter = 0; // Program counter to track execution position

    public Program(String name,ArrayList<AbstractInstruction> instructions) {
        this.name = name;
        this.instructions = instructions;
        for(AbstractInstruction instruction:instructions){
            instruction.setPos(++programCounter);
        }
    }
    public Variable execute() {
        new Runner(instructions).run();
        return ProgramVars.y;
    }
    private Set<Label> getallprogramlabels()
    {
        Set<Label> labels = new java.util.HashSet<>();
        for (AbstractInstruction instruction : instructions) {
            if(instruction.getLab() instanceof Label) {
                labels.add((Label) instruction.getLab()); // Add label to the set
            }
        }
        return labels; // Returns a set of all labels in the program
    }
    public void deployToDegree(int degree)
    {
        for(int i=1;i<degree;i++) {
            deploy();
        }
    }

    private void deploy()
    {
        Set<Label> allprogramlabels = getallprogramlabels();
        for(int i=0;i<instructions.size();i++){
            AbstractInstruction currentInstruction = instructions.get(i);
            if(currentInstruction instanceof SyntheticSugar)
            {
                AbstractInstruction source;
                ArrayList<AbstractInstruction> expandedInstructions = ((SyntheticSugar) currentInstruction).expand();
                for(AbstractInstruction instruction:expandedInstructions){
                    instruction.setPos(++programCounter); // Set position for each expanded instruction
                }
                source=instructions.remove(i);
                expandedInstructions.forEach(instruction->instruction.setSyntheticSource(source));// Set the source for each expanded instruction
                //allprogramlabels.remove(source.getLab());
                if(source.getLab() instanceof Label) {
                    removeFirstLabelCollisions((Label) source.getLab(), expandedInstructions, allprogramlabels);
                }// Remove label collisions in the expanded instructions
                replaceLabels(expandedInstructions, allprogramlabels); // Replace labels in the expanded instructions if needed
                instructions.addAll(i, expandedInstructions); // Replace the synthetic sugar with its expanded instructions

                i += expandedInstructions.size()-1; // Adjust index to account for added instructions
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


private void removeFirstLabelCollisions(Label parentLabel, ArrayList<AbstractInstruction> expandedInstructions, Set<Label> allprogramlabels) {

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
                if(expandedInstructions.get(0).getLab().equals(parentLabel)) {}
                ((HasGotoLabel) instr).setGotolabel(labelToAssign.myClone());
            }
        }

        for (int j = 1; j < expandedInstructions.size(); j++) {
            AbstractInstruction instr = expandedInstructions.get(j);
            if (instr.getLab() instanceof Label && instr.getLab().equals(parentLabel)) {
                instr.setLab(labelToAssign.myClone());
            }
            if(instr instanceof HasGotoLabel && ((HasGotoLabel) instr).getGotolabel().equals(parentLabel)) {
                if(expandedInstructions.get(0).getLab().equals(parentLabel)) {}
                ((HasGotoLabel) instr).setGotolabel(labelToAssign.myClone());
            }
        }
    }
    }

    private void replaceLabels(ArrayList<AbstractInstruction> instructions, Set<Label> allprogramlabels) {
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
        for(AbstractInstruction instruction : instructions) {
            res += instruction.toString() + "\n"; // Concatenate string representations of all instructions
        }
        return res; // Return the string representation of the program
    }
    public ArrayList<AbstractInstruction> getInstructions() {
        return instructions; // Getter for instructions
    }
    public int getProgramDegree(){
        return this.instructions.stream().filter(instruction->instruction instanceof SyntheticSugar)
                .max((a,b)->{
                   int adeg= ((SyntheticType)((SyntheticSugar)a).getType()).getDegree();
                   int bdeg= ((SyntheticType)((SyntheticSugar)b).getType()).getDegree();
                     return Integer.compare(adeg, bdeg);
                }).map(instruction -> ((SyntheticType)((SyntheticSugar)instruction).getType()).getDegree()).orElse(1); // Returns the maximum degree of synthetic sugars in the program);
    }
    public int getProgramCycles(){
        return this.instructions.stream().mapToInt((instruction) -> instruction.getType().getCycles())
                .sum(); // Returns the total cycles of all instructions in the program
    }
    public boolean checkValidity()
    {
        if(instructions==null) {
            return true; // Program is invalid if it has no instructions
        }
        return instructions.stream().filter(instruction->instruction instanceof HasGotoLabel)
                .map(instruction->((HasGotoLabel)instruction).getGotolabel())
                .allMatch(label -> existsLabel(label)); // Check if all goto labels exist in the program
    }
    private boolean existsLabel(HasLabel label) {
        return instructions.stream().anyMatch(instruction -> instruction.getLab().equals(label));
    }


}
