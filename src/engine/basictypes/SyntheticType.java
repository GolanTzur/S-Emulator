package engine.basictypes;

public enum SyntheticType implements AbstractInstructionType {
    ASSIGNMENT("ASSIGNMENT",2,4,Architecture.III),// Increment the value of a variable
    GOTO_LABEL("GOTOLABEL",1,1,Architecture.II), // Jump to a specific label
    ZERO_VARIABLE("ZEROVAR",1,1,Architecture.II),
    CONSTANT_ASSIGNMENT("CONSTASSIGNMENT",1,2,Architecture.II), // Assign a constant value to a variable
    JUMP_ZERO("JUMPZERO",2,2,Architecture.III),
    JUMP_EQUAL_CONSTANT("JUMPEQUALCONSTANT",3,2,Architecture.III), // Jump if the value is equal to a constant
    JUMP_EQUAL_VARIABLE("JUMPEQUALVARIABLE",3,2,Architecture.III),
    JUMP_EQUAL_FUNCTION("JUMPEQUALFUNCTION",3,6,Architecture.IV), // Jump if the value is equal to a function result
    QUOTE("QUOTE",1,5,Architecture.IV); // Assign the value of one variable to another
    //Cycles and Degree expected to rise - quote and jump equal function
    ; // Set a variable to zero

    private final String name;
    private final int degree;
    private final int cycles;
    private final Architecture architecture;

    SyntheticType(String name,int degree, int cycles, Architecture architecture) {
        this.name = name; // Initialize the name of the instruction type
        this.degree = degree;
        this.cycles = cycles; // Initialize the number of cycles for the instruction type
        this.architecture = architecture;
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
    public Architecture getArchitecture() {
        return architecture;
    }
}
