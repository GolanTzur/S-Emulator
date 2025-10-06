package engine.basictypes;

import javafx.scene.shape.Arc;

public enum InstructionType implements AbstractInstructionType {
    INCREASE("INCREASE",1),
    DECREASE("DECREASE",1),
    NEUTRAL("NEUTRAL",0),
    JUMP_NOT_ZERO("JUMPNOTZERO",2);

    private final String name;
    private final int cycles; // Number of cycles for the instruction type
    private final Architecture architecture;
    InstructionType(String name,int cycles) {
        this.name = name; // Initialize the name of the instruction type
        this.cycles = cycles; // Initialize the number of cycles for the instruction type
        this.architecture = Architecture.I;
    }
    public String getTypeName() {
        return name; // Getter for the name of the instruction type
    }
    public int getDegree() {
        return 1; // Default degree for basic instruction types
    }
    public int getCycles() {
        return cycles; // Getter for the number of cycles of the instruction type
    }
    public Architecture getArchitecture() {
        return architecture;
    }
}
