package engine.basictypes;

public enum VariableType {

    INPUT{
        public String getRepresentation(int number) {
            return "x" + number;
        }
    },
    RESULT{
        public String getRepresentation(int number) {
            return "y";
        }
    },
    WORK{
        public String getRepresentation(int number) {
            return "z" + number;
        }
    };

     public abstract String getRepresentation(int number);
}
