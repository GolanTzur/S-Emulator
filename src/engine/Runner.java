package engine;

import engine.basictypes.FixedLabel;
import engine.basictypes.HasLabel;
import engine.classhierarchy.AbstractInstruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Runner {
    private ArrayList<AbstractInstruction> instructions;
    public Runner(ArrayList<AbstractInstruction> instructions) {
        this.instructions = instructions; // Initialize with the provided instructions
    }
    public HasLabel run() {
        Map<HasLabel, Integer> labelIndices = getIndices();
        int currIndex=0;// Get the indices of labels
        HasLabel nextLabel = null;
        while (currIndex < instructions.size() && instructions.get(currIndex).getLab()!=FixedLabel.EXIT) {
           AbstractInstruction currentInstruction = instructions.get(currIndex);
           nextLabel = currentInstruction.evaluate(); // Evaluate the current instruction
            if (nextLabel==FixedLabel.EMPTY) {
                currIndex++; // Move to the next instruction if no label is returned
            } else {
                Optional<Integer> nextIndex = Optional.ofNullable(labelIndices.get(nextLabel)); // Get the index of the next label
                if (nextIndex.isPresent()) {
                    currIndex = nextIndex.get(); // Jump to the label's index if it exists
                } else {
                    throw new RuntimeException("Label not found: " + nextLabel); // Handle case where label is not found
                }
            }
        }
        return nextLabel;
    }
    private Map<HasLabel,Integer> getIndices() {
        Map<HasLabel, Integer> labelindices = new HashMap<>();
        for (int i = 0; i < instructions.size(); i++) {
            HasLabel label = instructions.get(i).getLab();
            if (!label.equals(FixedLabel.EMPTY)&&!labelindices.containsKey(label)) {
                labelindices.put(label, i); // Store the index of each label
            }
        }
        return labelindices; // Getter for instructions
    }
}
