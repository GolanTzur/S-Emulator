package engine.basictypes;

import java.util.Optional;

public interface HasLabel {
    String getLabel();

    @Override
    String toString() ;

    default boolean equals(HasLabel hl){
        if (this == hl) return true; // Check if same object
        if (hl == null) return false; // Check if hl is null
        String thisLabel = this.getLabel(); // Get label of this object
        String otherLabel = hl.getLabel(); // Get label of hl
        return thisLabel == null ? otherLabel == null : thisLabel.equals(otherLabel); // Compare labels
    }// Returns the label associated with the object
    default HasLabel clone(){
        if (this instanceof Label) {
            return new Label(this.getLabel());
        } else {
            return FixedLabel.valueOf(this.getLabel().toUpperCase());
        }
    }


}
