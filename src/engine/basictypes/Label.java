// src/engine/basictypes/Label.java
package engine.basictypes;

public class Label implements HasLabel {
    private final String label;

    public Label(String label) { this.label = label; }

    @Override
    public String getLabel() { return this.label; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HasLabel)) return false;
        String otherLabel = ((HasLabel) obj).getLabel();
        return label == null ? otherLabel == null : label.equals(otherLabel);
    }

    @Override
    public int hashCode() {
        return label == null ? 0 : label.hashCode();
    }
    @Override
    public String toString() {
        return label;
    }
}