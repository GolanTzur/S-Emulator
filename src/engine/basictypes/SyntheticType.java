package engine.basictypes;

public enum SyntheticType implements AbstractInstructionType {
    Assignment("ASSIGNMENT"),// Increment the value of a variable
    GotoLabel("GOTOLABEL"), // Jump to a specific label
    ZeroVar("ZEROVAR"),
    ConstAssignment("CONSTASSIGNMENT"), // Assign a constant value to a variable
    JumpZero("JUMPZERO"),
    JumpEqualConstant("JUMPEQUALCONSTANT"), // Jump if the value is equal to a constant
    JumpEqualVariable("JUMPEQUALVARIABLE")
    ; // Set a variable to zero

    private final String name;
    SyntheticType(String name) {
        this.name = name; // Initialize the name of the instruction type
    }
    public String getTypeName() {
        return name; // Getter for the name of the instruction type
    }
}
