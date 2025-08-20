import engine.Program;
import engine.ProgramVars;
import engine.XMLHandler;
import engine.classhierarchy.AbstractInstruction;
import engine.classhierarchy.*;
import engine.basictypes.*;
import engine.jaxbclasses.SProgram;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
//        Program p=new Program("TestProgram",new ArrayList<AbstractInstruction>(List.of(
//           new Increase(new Label("L0"),Variable.createOrGetNewVar(VariableType.INPUT,1)),
//           new Assignment(new Label("L1"),ProgramVars.y,ProgramVars.input.get(1))
//        ) ));

//        Program p=new Program("TestProgram",new ArrayList<AbstractInstruction>(List.of(
//                new Increase(new Label("L0"),Variable.createOrGetNewVar(VariableType.INPUT,1)),
//                new Assignment(new Label("L1"),ProgramVars.y,Variable.createOrGetNewVar(VariableType.INPUT,1)),
//                new ConstAssignment(ProgramVars.y,5))));
/*
        System.out.println("Cycles: "+p.getProgramCycles()+" Degree: "+p.getProgramDegree());
        System.out.println("Program valid: " + p.checkValidity());
        System.out.println(p);
        p.deployToDegree(2);
        System.out.println(p);
        p.execute();
        System.out.println(ProgramVars.MytoString());*/

        XMLHandler xmlHandler = XMLHandler.getInstance();
        File file = xmlHandler.getInputFile("C:\\Users\\Golan\\Desktop\\xmls\\badic.xml");
        try {
            InputStream is = new FileInputStream(file);
            SProgram sProgram = xmlHandler.getProgram(is);
            System.out.println(sProgram);
        }catch (FileNotFoundException e){}








    }

}