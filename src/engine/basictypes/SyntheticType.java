package engine.basictypes;

public enum SyntheticType implements AbstractInstructionType {
    ASSIGNMENT("ASSIGNMENT",3,4),// Increment the value of a variable
    GOTO_LABEL("GOTOLABEL",2,1), // Jump to a specific label
    ZERO_VAR("ZEROVAR",2,1),
    CONST_ASSIGNMENT("CONSTASSIGNMENT",2,2), // Assign a constant value to a variable
    JUMP_ZERO("JUMPZERO",3,2),
    JUMP_EQUAL_CONSTANT("JUMPEQUALCONSTANT",4,2), // Jump if the value is equal to a constant
    JUMP_EQUAL_VARIABLE("JUMPEQUALVARIABLE",4,2)
    ; // Set a variable to zero

    private final String name;
    private final int degree;
    private final int cycles;

    SyntheticType(String name,int degree, int cycles) {
        this.name = name; // Initialize the name of the instruction type
        this.degree = degree;
        this.cycles = cycles; // Initialize the number of cycles for the instruction type
    }
    public String getTypeName() {
        return name; // Getter for the name of the instruction type
    }
    public int getDegree() {
        return degree; // Getter for the degree of the instruction type
    }
    public int getCycles() {
        return cycles; // Getter for the number of cycles of the instruction type
    }
}
