// src/engine/basictypes/FixedLabel.java
package engine.basictypes;

public enum FixedLabel implements HasLabel {
    EXIT("exit"),
    EMPTY("");

    private final String label;

    FixedLabel(String label) {
        this.label = label;
    }

    public String getString() {
        return label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public HasLabel myClone(){
        return this;
    }

    // Optional: custom label-based equality
    public boolean equalsLabel(HasLabel other) {
        if (other == null) return false;
        String otherLabel = other.getLabel();
        return label == null ? otherLabel == null : label.equals(otherLabel);
    }

    @Override
    public String toString() {
        return label;
    }
}