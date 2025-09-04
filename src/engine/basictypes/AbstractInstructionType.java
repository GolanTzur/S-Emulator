package engine.basictypes;

import java.io.Serializable;

public interface AbstractInstructionType extends Serializable {
    String getTypeName();
    int getCycles(); // Returns the degree of the instruction type, which is 1 for basic instruction types
}
