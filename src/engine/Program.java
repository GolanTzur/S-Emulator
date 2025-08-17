package engine;

import engine.basictypes.Label;
import engine.basictypes.Variable;
import engine.classhierarchy.AbstractInstruction;
import engine.classhierarchy.HasGotoLabel;
import engine.classhierarchy.SyntheticSugar;

import java.util.ArrayList;
import java.util.Set;

public class Program {
    private final String name;
    private ArrayList<AbstractInstruction> instructions;

    public Program(String name,ArrayList<AbstractInstruction> instructions) {
        this.name = name;
        this.instructions = instructions;
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
        for(int i=0;i<instructions.size();i++) {
            deploy();
        }
    }

    public void deploy()
    {
        Set<Label> allprogramlabels = getallprogramlabels();
        for(int i=0;i<instructions.size();i++){
            AbstractInstruction currentInstruction = instructions.get(i);
            if(currentInstruction instanceof SyntheticSugar)
            {
                ArrayList<AbstractInstruction> expandedInstructions = ((SyntheticSugar) currentInstruction).expand();
                instructions.remove(i);
                replaceLabels(expandedInstructions, allprogramlabels); // Replace labels in the expanded instructions if needed
                instructions.addAll(i, expandedInstructions); // Replace the synthetic sugar with its expanded instructions
                i += expandedInstructions.size() - 1; // Adjust index to account for added instructions
            }
        }
    }
    private void replaceLabels(ArrayList<AbstractInstruction> instructions, Set<Label> allprogramlabels) {
        int nextIndexLabel=1;
        Label nextLabel=new Label("L"+nextIndexLabel); //Searching for available labels in the program

        for (AbstractInstruction instruction : instructions) {
            if (instruction.getLab() instanceof Label) {
                Label label = (Label) instruction.getLab();
                if (allprogramlabels.contains(label)) {
                    while (!allprogramlabels.contains(nextLabel)) {
                        nextIndexLabel++; // Increment the label index until an unused label is found
                        nextLabel = new Label("L" + nextIndexLabel); // Create a new label
                    }
                    instruction.setLab(nextLabel); // Replace the label in the instruction

                    for (AbstractInstruction instr : instructions) {
                        if (instr.getLab().equals(label)) {
                            instr.setLab(nextLabel); // Update all instructions with the same label
                        }
                        if(instr instanceof HasGotoLabel){
                            HasGotoLabel gotoLabel = (HasGotoLabel) instr;
                            if (gotoLabel.getGotolabel().equals(label)) {
                                gotoLabel.setGotolabel(nextLabel); // Update the gotolabel if it matches the current label
                            }
                        }
                    }
                    allprogramlabels.add(nextLabel);
                    nextIndexLabel++;
                }
            }
        }
    }
    public String getName() {
        return name; // Getter for program name
    }

}
