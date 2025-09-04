package engine.basictypes;

//import java.util.Optional;

import java.io.Serializable;

public interface HasLabel extends Serializable {
    String getLabel();
    HasLabel myClone(); // Returns a clone of the object with the same label

    String toString() ;

    default boolean equals(HasLabel hl){
        if (this == hl) return true; // Check if same object
        if (hl == null) return false; // Check if hl is null
        String thisLabel = this.getLabel(); // Get label of this object
        String otherLabel = hl.getLabel(); // Get label of hl
        return thisLabel == null ? otherLabel == null : thisLabel.equals(otherLabel); // Compare labels
    }// Returns the label associated with the object
}
