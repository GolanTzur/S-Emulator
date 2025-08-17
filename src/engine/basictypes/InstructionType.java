package engine.basictypes;

public enum InstructionType implements AbstractInstructionType {
    Increase("INCREASE"),
    Decrease("DECREASE"),
    Assignment("ASSIGNMENT"), // Increment the value of a variable
    Neutral("NEUTRAL"), // No operation, used for empty labels
    Jumpnotzero("JUMPNOTZERO"), // Jump if the value is not zero
    Gotolabel("GOTOLABEL"), // Jump to a specific label
    Zerovar("ZEROVAR"); // Set a variable to zero variable to zero

    private final String name;
    InstructionType(String name) {
        this.name = name; // Initialize the name of the instruction type
    }
    public String getTypeName() {
        return name; // Getter for the name of the instruction type
    }
}
