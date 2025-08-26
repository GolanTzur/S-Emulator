package engine;

import engine.basictypes.FixedLabel;
import engine.basictypes.HasLabel;
import engine.basictypes.Label;
import engine.classhierarchy.AbstractInstruction;
import engine.classhierarchy.HasGotoLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Runner {
    private ArrayList<AbstractInstruction> instructions;
    private ProgramVars context;

    public Runner(ArrayList<AbstractInstruction> instructions, ProgramVars context) {
        this.instructions = instructions; // Initialize with the provided instructions
        this.context = context;
    }

    public HasLabel run() {
        Map<HasLabel, Integer> labelIndices = getIndices();
        int currIndex = 0;// Get the indices of labels
        HasLabel nextLabel = null;
        while (currIndex < instructions.size() && instructions.get(currIndex).getLab() != FixedLabel.EXIT) {
            AbstractInstruction currentInstruction = instructions.get(currIndex);
            nextLabel = currentInstruction.evaluate(context); // Evaluate the current instruction
            if (nextLabel == FixedLabel.EMPTY) {
                currIndex++; // Move to the next instruction if no label is returned
            } else {
                Optional<Integer> nextIndex = Optional.ofNullable(labelIndices.get(nextLabel)); // Get the index of the next label
                if (nextIndex.isPresent()) {
                    currIndex = nextIndex.get(); // Jump to the label's index if it exists
                } else {
                    return nextLabel;
                }
            }
        }
        return nextLabel;
    }

    private Map<HasLabel, Integer> getIndices() {
        replaceDefaultVar(this.instructions);
        Map<HasLabel, Integer> labelindices = new HashMap<>();
        for (int i = 0; i < instructions.size(); i++) {
            HasLabel label = instructions.get(i).getLab();
            if (!label.equals(FixedLabel.EMPTY) && !labelindices.containsKey(label)) {
                labelindices.put(label, i); // Store the index of each label
            }
        }
        return labelindices; // Getter for instructions
    }

    private void replaceDefaultVar(ArrayList<AbstractInstruction> instructions) {
        int nextIndex = 0;
        HasLabel nextLabel = null;
        for (int i = 0; i < instructions.size(); i++) {
            if (instructions.get(i).getLab() == FixedLabel.DEFAULT) {
                do {
                    nextLabel = new Label("L" + nextIndex++); // Create a new label for the default case
                } while (containsLabel(instructions, nextLabel));
                instructions.get(i).setLab(nextLabel); // Set the new label
                for(int j=0;j<instructions.size();j++) {
                    if(j==i) continue; // Skip the current instruction
                    AbstractInstruction instr = instructions.get(j);
                    if (instr instanceof HasGotoLabel && ((HasGotoLabel) instr).getGotolabel().equals(FixedLabel.DEFAULT)) {
                        ((HasGotoLabel) instr).setGotolabel(nextLabel.myClone()); // Clone the label to avoid reference issues
                    }
                }
                break;
            }
        }

    }
    public void replaceFirstLabelAndGoto(ArrayList<AbstractInstruction> instructions) {
        int nextIndex = 0;
        HasLabel newLabel = null;
        HasLabel newGotoLabel = null;
        HasLabel originalLabel = null;
        HasLabel originalGotoLabel = null;

        // Find the first instruction with a Label
        for (AbstractInstruction instr : instructions) {
            if (instr.getLab() instanceof Label) {
                originalLabel = instr.getLab();
                // Generate unused label for instruction label
                do {
                    newLabel = new Label("L" + nextIndex++);
                } while (containsLabel(instructions, newLabel));
                instr.setLab(newLabel);

                // If instruction has gotoLabel, generate unused label for it
                if (instr instanceof HasGotoLabel) {
                    originalGotoLabel = ((HasGotoLabel) instr).getGotolabel();
                    do {
                        newGotoLabel = new Label("L" + nextIndex++);
                    } while (containsLabel(instructions, newGotoLabel));
                    ((HasGotoLabel) instr).setGotolabel(newGotoLabel);
                }
                break; // Only replace the first found
            }
        }

        // Replace all other occurrences of the original label and gotoLabel
        for (AbstractInstruction instr : instructions) {
            if (instr.getLab().equals(originalLabel)) {
                instr.setLab(newLabel);
            }
            if (originalGotoLabel != null && instr instanceof HasGotoLabel) {
                HasGotoLabel gotoInstr = (HasGotoLabel) instr;
                if (gotoInstr.getGotolabel().equals(originalGotoLabel)) {
                    gotoInstr.setGotolabel(newGotoLabel);
                }
            }
        }
    }
    boolean containsLabel(ArrayList<AbstractInstruction> instructions, HasLabel label) {
        for (AbstractInstruction instruction : instructions) {
            if (instruction.getLab().equals(label)) {
                return true; // Check if the label exists in any instruction
            }
        }
        return false; // Return false if the label is not found

    }
}

