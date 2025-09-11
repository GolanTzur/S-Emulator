package engine.classhierarchy;

import engine.ProgramVars;
import engine.basictypes.HasLabel;

public interface Evaluable {
     HasLabel evaluate(/*ProgramVars context*/); //Indicates whether to Jump or not
}
