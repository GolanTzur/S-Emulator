package engine;

import engine.basictypes.FixedLabel;
import engine.basictypes.HasLabel;
import engine.basictypes.Label;
import engine.classhierarchy.AbstractInstruction;
import engine.classhierarchy.Function;
import engine.classhierarchy.HasGotoLabel;
import engine.classhierarchy.JumpEqualFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Runner {
    private ArrayList<AbstractInstruction> instructions;

    private int currIndexdebug = 0;
    private Map<HasLabel,Integer> labelIndicesdebug;
    private HasLabel nextLabeldebug = null;
    private int cycleCountdebug = 0;
    private boolean finisheddebug = false;


    public void reset() {
        this.currIndexdebug = 0;
        this.cycleCountdebug = 0;
        this.labelIndicesdebug = getIndices();
        this.nextLabeldebug = null;
        this.finisheddebug = false;
    }

    private int cycleCount = 0;

    public Runner(ArrayList<AbstractInstruction> instructions/*, ProgramVars context*/) {
        this.instructions = instructions; // Initialize with the provided instructions
        reset();
        //this.context = context;
    }
    public int getCurrIndexdebug()
    {
        return currIndexdebug;
    }

    public HasLabel run(boolean countCycles) {
        Map<HasLabel, Integer> labelIndices = getIndices();
        int currIndex = 0;// Get the indices of labels
        HasLabel nextLabel = null;
        while (currIndex < instructions.size() && instructions.get(currIndex).getLab() != FixedLabel.EXIT) {
            AbstractInstruction currentInstruction = instructions.get(currIndex);
            if(countCycles){
                this.cycleCount+=currentInstruction.getType().getCycles();
            }
            nextLabel = currentInstruction.evaluate(/*context*/); // Evaluate the current instruction
            if(countCycles) {
                if (currentInstruction instanceof Function)
                    this.cycleCount += ((Function) currentInstruction).getCycles();
                else if (currentInstruction instanceof JumpEqualFunction)
                    this.cycleCount += ((JumpEqualFunction) currentInstruction).getFunc().getCycles();
            }
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

    public HasLabel step() {

        if (currIndexdebug >= instructions.size() || instructions.get(currIndexdebug).getLab() == FixedLabel.EXIT) {
            this.finisheddebug = true;
            return nextLabeldebug;
        }

        AbstractInstruction currentInstruction = instructions.get(currIndexdebug);
        this.cycleCountdebug += currentInstruction.getType().getCycles();

        nextLabeldebug = currentInstruction.evaluate();

        if (currentInstruction instanceof Function)
            this.cycleCountdebug += ((Function) currentInstruction).getCycles();
        else if (currentInstruction instanceof JumpEqualFunction)
            this.cycleCountdebug += ((JumpEqualFunction) currentInstruction).getFunc().getCycles();

        if (nextLabeldebug == FixedLabel.EMPTY) {
            currIndexdebug++;
        } else {
            Optional<Integer> nextIdx = Optional.ofNullable(labelIndicesdebug.get(nextLabeldebug));
            if (nextIdx.isPresent()) {
                currIndexdebug = nextIdx.get();
            } else {
                this.finisheddebug = true;
                return nextLabeldebug;
            }
        }
        return nextLabeldebug;
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

    public boolean isFinisheddebug() {
        return finisheddebug;
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
    public int getCycleCount() {
        return cycleCount;
    }
    public int getCycleCountdebug() {
        return cycleCountdebug;
    }
}

