package engine.basictypes;

public enum InstructionType implements AbstractInstructionType {
    Increase("INCREASE",1),
    Decrease("DECREASE",1),
    Neutral("NEUTRAL",0),
    Jumpnotzero("JUMPNOTZERO",2);

    private final String name;
    private final int cycles; // Number of cycles for the instruction type
    InstructionType(String name,int cycles) {
        this.name = name; // Initialize the name of the instruction type
        this.cycles = cycles; // Initialize the number of cycles for the instruction type
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
}
