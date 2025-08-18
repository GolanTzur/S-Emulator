import engine.Program;
import engine.ProgramVars;
import engine.classhierarchy.AbstractInstruction;
import engine.classhierarchy.*;
import engine.basictypes.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ProgramVars.input.put(1, new Variable(VariableType.INPUT,1));
        Program p=new Program("TestProgram",new ArrayList<AbstractInstruction>(List.of(
           new Increase(new Label("L0"),ProgramVars.input.get(1)),
           new Assignment(new Label("L1"),ProgramVars.y,ProgramVars.input.get(1))
        ) ));
        System.out.println("Cycles: "+p.getProgramCycles()+"Degree: "+p.getProgramDegree());
        System.out.println(p);
        p.deployToDegree(1);
        System.out.println(p);
        p.deployToDegree(1);
        System.out.println(p);
        int result = p.execute().getValue();
        System.out.println("Result: " + result);
    }

}