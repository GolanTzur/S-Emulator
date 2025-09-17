package engine.classhierarchy;

import engine.*;
import engine.basictypes.*;
import engine.classhierarchy.*;

import java.util.*;

public class Function extends AbstractInstruction {
    Program prog;
    String displayName;
    boolean isEvaluated=false;

    public Function(HasLabel label,Variable var ,Program prog, String displayName) {
        super(label, SyntheticType.QUOTE, var);
        this.prog = prog;
        this.displayName = displayName;
    }
    public Function(Variable var,Program prog,String displayName) {
        super(SyntheticType.QUOTE, var);
        this.prog = prog;
        this.displayName = displayName;
    }

    @Override
    public String getChildPart() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("(%s", displayName));
    Collection<Variable> inputs = prog.getVars().getInput().values();
    inputs.forEach((input)-> sb.append(String.format(",%s", input)));
    sb.append(")");
    return sb.toString();
    }

    public ArrayList<AbstractInstruction> expand(ProgramVars context, Set<HasLabel> progLabels) {
        ArrayList<AbstractInstruction> commands = prog.getInstructions();
        commands.add(0,new Neutral(lab,context.getY()));
        replaceVars(commands, context);
        replaceLabels(commands, progLabels);
        return commands;
    }

    private void replaceVars(ArrayList<AbstractInstruction> instructions, ProgramVars context) {
        Map<Variable,Variable> varMap = new HashMap<>();
        int sizeinnerinputs=0;
        int indexAddassign=1;

        for(Variable v:prog.getVars().getInput().values()){
            /*if(!(v instanceof ResultVar))*/{
                sizeinnerinputs++;
            }
        }

        int sizeZinputs=sizeinnerinputs+prog.getVars().getEnvvars().size()+1;
        Collection<Variable> newVars = context.getZinputs(sizeZinputs);
        Iterator<Variable> it = newVars.iterator();

        for(Variable v:prog.getVars().getInput().values()){
            Variable newVar = it.next();
            if(v instanceof ResultVar)
            {
                ResultVar resultVar = (ResultVar)v;
                varMap.put(v,newVar);
                instructions.add(indexAddassign++,new Function(newVar,resultVar.getFunction().prog,resultVar.getFunction().displayName));
            }
            else {
                varMap.put(v, newVar);
                instructions.add(indexAddassign++, new Assignment(newVar, v));
            }
        }
        for(Variable v:prog.getVars().getEnvvars().values()){
            varMap.put(v,it.next());
        }
        varMap.put(prog.getVars().getY(),it.next());

        // Find all variables to replace
        for (int i=0; i < instructions.size(); i++) {

                instructions.get(i).setVar(varMap.get(instructions.get(i).getVar()));
                if(instructions.get(i) instanceof HasExtraVar) {
                    HasExtraVar hasSecondVar = (HasExtraVar) instructions.get(i);
                    hasSecondVar.setArg(varMap.get(hasSecondVar.getArg()));
                }
        }

    }

    private void replaceLabels(ArrayList<AbstractInstruction> instructions, Set<HasLabel> allprogramlabels) {
        Map<Label, Label> labelMap = new HashMap<>();
        int nextIndexLabel = 0;
        boolean hasExit=false;

        // Find all labels to replace
        for (int i=1; i < instructions.size(); i++) {
            if (instructions.get(i).getLab() instanceof Label) {
                Label label = (Label) instructions.get(i).getLab();
                if (allprogramlabels.contains(label)) {
                    Label nextLabel;
                    do {
                        nextLabel = new Label("L" + nextIndexLabel++);
                    } while (allprogramlabels.contains(nextLabel));
                    labelMap.put(label, nextLabel);
                    allprogramlabels.add(nextLabel);
                }
            } else if (instructions.get(i) instanceof HasGotoLabel) {
                HasGotoLabel gotoLabel = (HasGotoLabel) instructions.get(i);
                if (gotoLabel.getGotolabel() == FixedLabel.EXIT) {
                    hasExit = true;
                }
            }
        }

        // Assign new labels to instructions with default labels
        /*for(int i=0;i<instructions.size();i++){
            if(instructions.get(i).getLab()==FixedLabel.DEFAULT) {
                Label nextLabel;
                do {
                    nextLabel = new Label("L" + nextIndexLabel++);
                } while (allprogramlabels.contains(nextLabel));
                instructions.get(i).setLab(nextLabel);
                allprogramlabels.add(nextLabel);
                for (int j = 0; j < instructions.size(); j++) {
                    if(j==i) continue; // Skip the current instruction
                    if (instructions.get(j) instanceof HasGotoLabel && ((HasGotoLabel)instructions.get(j)).getGotolabel()==FixedLabel.DEFAULT) {
                        ((HasGotoLabel) instructions.get(j)).setGotolabel(nextLabel.myClone());
                    }
                }
            }
        }*/

        // Replace labels and goto targets
        for (AbstractInstruction instruction : instructions) {
            if (instruction.getLab() instanceof Label && labelMap.containsKey(instruction.getLab())) {
                instruction.setLab(labelMap.get(instruction.getLab()));
            }
            if (instruction instanceof HasGotoLabel) {
                HasGotoLabel gotoLabel = (HasGotoLabel) instruction;
                if (labelMap.containsKey(gotoLabel.getGotolabel())) {
                    gotoLabel.setGotolabel(labelMap.get(gotoLabel.getGotolabel()));
                }
            }
        }

        if(hasExit){
            Label nextLabel;
            do {
                nextLabel = new Label("L" + nextIndexLabel++);
            } while (allprogramlabels.contains(nextLabel));
            allprogramlabels.add(nextLabel);
            for(int i=0;i<instructions.size();i++){
                if(instructions.get(i) instanceof HasGotoLabel){
                    HasGotoLabel gotoLabel=(HasGotoLabel) instructions.get(i);
                    if(gotoLabel.getGotolabel()==FixedLabel.EXIT){
                        gotoLabel.setGotolabel(nextLabel.myClone());
                    }
                }
            }
            instructions.add(new Assignment(var,prog.getVars().getY()));
        }
        else
            instructions.add(new Neutral(prog.getVars().getY()));

    }

    @Override
    public HasLabel evaluate() {
        super.evaluate(); //Calculate result vars if there are any
        prog.execute();
        new Assignment(var, prog.getVars().getY()).evaluate();
        isEvaluated=true;
        return FixedLabel.EMPTY;
    }
    public Variable getOutputVariable() {
        return prog.getVars().getY();
    }
    public Collection<Variable> getUsedVariables() {
        return prog.getVars().getInput().values();
    }
    @Override //Activate with null
    public Function clone(ProgramVars context) {
        return new Function(lab.myClone(),var.clone(context) ,prog.clone(), displayName);
    }
    public int getCycles() {
        return prog.getCycleCount(); // Function call overhead
    }
    public int getDegree(){
        return prog.getProgramDegree()+SyntheticType.QUOTE.getDegree();
    }

    public Program getProg() {
        return prog;
    }
    public boolean isEvaluated() {
        return isEvaluated;
    }


}
