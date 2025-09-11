package engine;

import engine.basictypes.*;
import engine.classhierarchy.*;
import engine.jaxbclasses.SInstruction;
import engine.jaxbclasses.SInstructionArgument;
import engine.jaxbclasses.SProgram;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static engine.basictypes.SyntheticType.*;
import static engine.basictypes.InstructionType.*;

public class XMLHandler { // Singleton class to handle XML operations
    private static XMLHandler xhandler;
    private XMLHandler() {
        // Private constructor to prevent instantiation
    }
    public static XMLHandler getInstance() {
        if (xhandler == null) {
            xhandler = new XMLHandler();
        }
        return xhandler;
    }
    public Program loadProgram(String fileName) throws Exception {
        File file= getInputFile(fileName); // Loads the program from the XML file
        if (file == null) {
            throw new IllegalArgumentException("File not found or invalid file type: " + fileName);
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            SProgram sprogram = getSProgram(inputStream); // Gets the SProgram object from the XML file
            return convertToProgram(sprogram); // Converts SProgram to Program object

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error loading program from file: " + fileName, e);
        }

    }
    public File getInputFile(String fileName) {
        if(fileName.endsWith(".xml")==false) {
            return null;
        }
        File res= new File(fileName); // Returns the input XML file
        if (!res.exists()) {
           return null;
        }
        return res;
    }
    public SProgram getSProgram(InputStream file) {
        if (file == null) {
            return null; // Returns the SProgram object from the XML file
        }
        try {
            JAXBContext jc = JAXBContext.newInstance("engine.jaxbclasses");
            return (SProgram) jc.createUnmarshaller().unmarshal(file);
        } catch(JAXBException e)
        {
            e.printStackTrace();
            return null; // Handle JAXBException
        }
    }
    public Program convertToProgram(SProgram sprogram) throws Exception {
        if (sprogram == null) {
            return null; // Converts SProgram to Program object
        }
        if (sprogram.getSInstructions() == null || sprogram.getSInstructions().getSInstruction().isEmpty()) {
            return null; // Check if instructions are present
        }
        String name = sprogram.getName();
        ProgramVars progVars = new ProgramVars();
        List<SInstruction> sInstructions = sprogram.getSInstructions().getSInstruction();
        ArrayList<AbstractInstruction> instructions = new ArrayList<AbstractInstruction>();

        for (SInstruction sin : sInstructions) {
            AbstractInstructionType type=getType(sin.getType(), sin.getName());
            Variable var= loadVariable(sin.getSVariable(),progVars);
            HasLabel label = loadLabel(sin.getSLabel());
            if (label == FixedLabel.EXIT) {
                throw new IllegalArgumentException("Invalid label: " + sin.getSLabel());
            }
            switch(type)
            {
                case ASSIGNMENT:
                    String assign = lookforValue("assignedVariable", sin.getSInstructionArguments().getSInstructionArgument());
                    Variable assignvar= loadVariable(assign,progVars);
                    instructions.add(new Assignment(label,var, assignvar));
                    break;
                case CONSTANT_ASSIGNMENT:
                     String const_assign = lookforValue("constantValue", sin.getSInstructionArguments().getSInstructionArgument());
                     if(!const_assign.matches("\\d+")|| Integer.parseInt(const_assign) < 0) {
                         throw new IllegalArgumentException("Invalid constant value: " + const_assign);
                     }
                     instructions.add(new ConstAssignment(label, var, Integer.parseInt(const_assign)));
                    break;
                case INCREASE:
                    instructions.add(new Increase(label, var));
                    break;
                case DECREASE:
                    instructions.add(new Decrease(label, var));
                    break;
                case NEUTRAL:
                    instructions.add(new Neutral(label, var));
                    break;
                case JUMP_NOT_ZERO:
                    String jumpNotZero = lookforValue("JNZLabel", sin.getSInstructionArguments().getSInstructionArgument());
                    HasLabel gotoLabel_jnz = loadLabel(jumpNotZero);
                    instructions.add(new JumpNotZero(label, var, gotoLabel_jnz));
                    break;
                case JUMP_ZERO:
                    String jumpZero = lookforValue("JZLabel", sin.getSInstructionArguments().getSInstructionArgument());
                    HasLabel gotoLabel_jz = loadLabel(jumpZero);
                    instructions.add(new JumpZero(label, var, gotoLabel_jz));
                    break;
                case GOTO_LABEL:
                    String gotoLabel = lookforValue("gotoLabel", sin.getSInstructionArguments().getSInstructionArgument());
                    HasLabel gotoLabel_label = loadLabel(gotoLabel);
                    Variable dummyVar = Variable.createDummyVar(VariableType.WORK, var.getPosition(), 0);
                    instructions.add(new GotoLabel(label, dummyVar, gotoLabel_label));
                    break;
                case ZERO_VARIABLE:
                    instructions.add(new ZeroVar(label, var));
                    break;
                case JUMP_EQUAL_CONSTANT:
                    String jump_equal_const = lookforValue("constantValue", sin.getSInstructionArguments().getSInstructionArgument());
                    if(!jump_equal_const.matches("\\d+")|| Integer.parseInt(jump_equal_const) < 0) {
                        throw new IllegalArgumentException("Invalid constant value: " + jump_equal_const);
                    }
                    String jumpequalconst_label= lookforValue("JEConstantLabel", sin.getSInstructionArguments().getSInstructionArgument());
                    HasLabel gotoLabel_je_const = loadLabel(jumpequalconst_label);
                    instructions.add(new JumpEqualConstant(label, var, Integer.parseInt(jump_equal_const), gotoLabel_je_const));
                    break;
                case JUMP_EQUAL_VARIABLE:
                    String jump_equal_var = lookforValue("variableName", sin.getSInstructionArguments().getSInstructionArgument());
                    Variable jump_equal_var_var = loadVariable(jump_equal_var,progVars);
                    String jumpequalvar_label= lookforValue("JEVariableLabel", sin.getSInstructionArguments().getSInstructionArgument());
                    HasLabel gotoLabel_je_var = loadLabel(jumpequalvar_label);
                    instructions.add(new JumpEqualVariable(label, var, jump_equal_var_var, gotoLabel_je_var));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown instruction type: " + sin.getType());
            }
        }
        return new Program(name, instructions,progVars); // Returns a new Program object with the given name and instructions
    }
    public AbstractInstructionType getType(String typeName,String opName) throws IllegalArgumentException {

        if(typeName.equals("basic")) {
            try {
                return InstructionType.valueOf(opName);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid synthetic instruction type: " + opName);
            }
        }
        else if(typeName.equals("synthetic")) {
            try {
                return SyntheticType.valueOf(opName);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid synthetic instruction type: " + opName);
            }
        }
        else {
            throw new IllegalArgumentException("Unknown instruction type: " + typeName);
        }

    }
    public Variable loadVariable(String varName,ProgramVars context) throws IllegalArgumentException {
        if (!isValidVariableName(varName)) {
            throw new IllegalArgumentException("Invalid variable name: " + varName);
        }
        if(varName.startsWith("x"))
           return Variable.createOrGetNewVar(VariableType.INPUT,varName.charAt(1)-'0',context);
        if(varName.startsWith("z"))
            return Variable.createOrGetNewVar(VariableType.WORK,varName.charAt(1)-'0',context);
        return Variable.createOrGetNewVar(VariableType.RESULT,0,context);
    }
    private boolean isValidVariableName(String varName) {
        return varName != null && varName.matches("^(x\\d+|z\\d+|y)+$"); // Checks if the variable name is valid
    }
    private HasLabel loadLabel(String labelName) throws IllegalArgumentException {
        if (labelName == null||labelName.equals("")) {
            return FixedLabel.EMPTY; // Returns an empty label
        }

        if (labelName.toLowerCase().equals("exit") ) {
            return FixedLabel.EXIT;
        }

        return new Label(labelName); // Returns a new label with the given name
    }
    private String lookforValue(String field,List<SInstructionArgument> instructions) { // Looks for a specific field in the instruction arguments
        return instructions.stream().filter(instr-> instr.getName().equals(field))
                .findFirst()
                .map((instr) -> instr.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Field " + field + " not found in instruction arguments"));
    }

}
