package dto;

public record ObservableProgramVars(String[] inputVarsNames, String[] inputVarsValues,String[] workVarsNames, String[] workVarsValues,String result) {
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Input Variables:\n");
        for (int i = 0; i < inputVarsNames.length; i++) {
            builder.append(inputVarsNames[i]).append(" = ").append(inputVarsValues[i]).append("\n");
        }
        builder.append("Work Variables:\n");
        for (int i = 0; i < workVarsNames.length; i++) {
            builder.append(workVarsNames[i]).append(" = ").append(workVarsValues[i]).append("\n");
        }
        builder.append("Result: ").append(result).append("\n");
        return builder.toString();
    }
}
