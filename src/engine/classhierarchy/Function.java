package engine.classhierarchy;

import engine.*;
import engine.basictypes.*;
import engine.classhierarchy.*;

import java.util.*;

public class Function extends AbstractInstruction {
    Program prog;
    String displayName;
    boolean isEvaluated = false;

    public Function(HasLabel label, Variable var, Program prog, String displayName) {
        super(label, SyntheticType.QUOTE, var);
        this.prog = prog;
        this.displayName = displayName;
    }

    public Function(Variable var, Program prog, String displayName) {
        super(SyntheticType.QUOTE, var);
        this.prog = prog;
        this.displayName = displayName;
    }

    @Override
    public String getChildPart() {
        StringBuilder sb = new StringBuilder();
        if(!(var instanceof ResultVar))
            sb.append(var.toString()+" <- ");
        sb.append(String.format("(%s", displayName));
        Collection<Variable> inputs = prog.getVars().getInput().values();
        inputs.forEach((input) -> sb.append(String.format(",%s", input)));
        sb.append(")");
        return sb.toString();
    }

    public ArrayList<AbstractInstruction> expand(ProgramVars context, Set<HasLabel> progLabels) {
        // ArrayList<Variable> newVars = ;
        /*for (AbstractInstruction instr : prog.getInstructions()) {
            if (instr instanceof ResultVar) {
                ResultVar rv = (ResultVar) v;
                //rv.evaluate();
                rv.getFunction().refreshInputs();
                //rv.getFunction().replaceResultVars(prog.getVars());
                ArrayList<AbstractInstruction> commands = prog.getInstructions();
                rv.getFunction().replaceVars(commands, context);
                rv.getFunction().replaceLabels(commands, progLabels);

            }
        }*/
        refreshInputs();
        //replaceResultVars(prog.getVars());
        ArrayList<AbstractInstruction> commands = prog.getInstructions();
        replaceVars(commands, context);
        replaceLabels(commands, progLabels);
        commands.add(0, new Neutral(lab, context.getY()));
        return commands;
    }

    private void replaceVars(ArrayList<AbstractInstruction> instructions,ProgramVars context) {
        Map<Variable, Variable> varMap = new HashMap<>();
        ArrayList<AbstractInstruction> newInstructions=replaceInputs(prog.getVars(),context,varMap);
        instructions.addAll(0,newInstructions);

        for (int i= newInstructions.size();i<instructions.size();i++) {
            if(!(instructions.get(i) instanceof GotoLabel)) {
                if (!varMap.containsKey(instructions.get(i).getVar())) {
                    varMap.put(instructions.get(i).getVar(), context.getZinputs(1).iterator().next());
                    varMap.get(instructions.get(i).getVar()).setValue(instructions.get(i).getVar().getValue());
                }
                if (instructions.get(i) instanceof HasExtraVar) {
                    HasExtraVar hev = (HasExtraVar) instructions.get(i);
                    if (!varMap.containsKey(hev.getArg())) {
                        varMap.put(hev.getArg(), context.getZinputs(1).iterator().next());
                        varMap.get(hev.getArg()).setValue(hev.getArg().getValue());
                    }
                }
            }
        }
        for (int i= newInstructions.size();i<instructions.size();i++) {
            if(!(instructions.get(i) instanceof GotoLabel)) {
                instructions.get(i).setVar(varMap.get(instructions.get(i).getVar()));
                if (instructions.get(i) instanceof HasExtraVar) {
                    HasExtraVar hev = (HasExtraVar) instructions.get(i);
                    hev.setArg(varMap.get(hev.getArg()));
                }
            }
        }

        Set<Variable> usedVars=varMap.keySet();
        Optional<Variable> yvar=usedVars.stream().filter(v->v.getType()==VariableType.RESULT).findFirst();
        yvar.ifPresent(v -> {
            Variable mapped = varMap.get(v);
            if (mapped != null) {
                instructions.add(new Assignment(var, mapped));
            }
        });
    }

    private void replaceLabels(ArrayList<AbstractInstruction> instructions, Set<HasLabel> allprogramlabels) {
        Map<Label, Label> labelMap = new HashMap<>();
        int nextIndexLabel = 0;
        boolean hasExit = false;

        // Find all labels to replace
        for (int i = 1; i < instructions.size(); i++) {
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

        if (hasExit) {
            Label nextLabel;
            do {
                nextLabel = new Label("L" + nextIndexLabel++);
            } while (allprogramlabels.contains(nextLabel));
            allprogramlabels.add(nextLabel);
            for (int i = 0; i < instructions.size(); i++) {
                if (instructions.get(i) instanceof HasGotoLabel) {
                    HasGotoLabel gotoLabel = (HasGotoLabel) instructions.get(i);
                    if (gotoLabel.getGotolabel() == FixedLabel.EXIT) {
                        gotoLabel.setGotolabel(nextLabel.myClone());
                    }
                }
            }
            instructions.get(instructions.size()-1).setLab(nextLabel);
        }

    }

    @Override
    public HasLabel evaluate() {
        // ArrayList<Variable> newVars = ;
        for (Variable v : prog.getVars().getInput().values()) {
            if (v instanceof ResultVar) {
                ResultVar rv = (ResultVar) v;
                rv.evaluate();
            }
        }
        refreshInputs();
        isEvaluated = true;
        replaceResultVars();
        prog.execute();
        this.getVar().setValue(prog.getVars().getY().getValue());
        return FixedLabel.EMPTY;
    }
    private void replaceResultVars(ProgramVars... progVars)
    {
        Map<ResultVar,Variable> map = new HashMap<>();
        int numArgs=0;
        ArrayList<AbstractInstruction> commands=prog.getInstructions();
        for(int i=0;i<commands.size();i++)
        {
            AbstractInstruction inst=commands.get(i);
            Variable var=inst.getVar();
            if(var instanceof ResultVar) {
                if (progVars.length == 1)
                {
                    map.put((ResultVar) var, progVars[0].getZinputs(1).iterator().next());
                    map.get((ResultVar) var).setValue(var.getValue());
                }
                else
                {
                    map.put((ResultVar) var, Variable.createDummyVar(VariableType.WORK, ++numArgs, var.getValue()));
                }
            }
            if(inst instanceof HasExtraVar&&((HasExtraVar) inst).getArg() instanceof ResultVar)
            {
                Variable extraVar=((HasExtraVar) inst).getArg();
                if (progVars.length == 1)
                {
                    map.put((ResultVar) extraVar, progVars[0].getZinputs(1).iterator().next());
                    map.get((ResultVar) extraVar).setValue(extraVar.getValue());
                }
                else
                {
                    map.put((ResultVar) extraVar, Variable.createDummyVar(VariableType.WORK, ++numArgs, extraVar.getValue()));
                }
            }

        }
        for(int i=0;i<commands.size();i++)
        {
            AbstractInstruction inst=commands.get(i);
            Variable var=inst.getVar();
            if(var instanceof ResultVar)
            {
                Variable newVar=map.get(var);
                inst.setVar(newVar);
            }
            if(inst instanceof HasExtraVar)
            {
                Variable extraVar=((HasExtraVar) inst).getArg();
                if(extraVar instanceof ResultVar)
                {
                    Variable newVar=map.get(extraVar);
                    ((HasExtraVar) inst).setArg(newVar);
                }
            }

        }
    }

    private ArrayList<AbstractInstruction> replaceInputs(ProgramVars progVars,ProgramVars context,Map<Variable,Variable> map)
    {
        ArrayList<AbstractInstruction> commands=new ArrayList<>();
        for(Variable v : progVars.getInput().values())
        {
            if(map.containsKey(v))
                continue;
            if(v instanceof ResultVar)
            {
                ResultVar rv=(ResultVar) v;
                map.put(rv,context.getZinputs(1).iterator().next());
                rv.getFunction().setVar(map.get(rv));
                commands.add(rv.getFunction());
            }
            else
            {
                map.put(v,context.getZinputs(1).iterator().next());
                commands.add(new Assignment(map.get(v),v));
            }
        }
        return commands;
    }
    public Variable getOutputVariable() {
        return prog.getVars().getY();
    }
    public void refreshInputs()
    {
        for(AbstractInstruction instr:prog.getInstructions()){
            if(instr.getVar().getType()==VariableType.INPUT){
                instr.setVar(prog.getVars().getInput().get(instr.getVar().getPosition()));
            }

            if(instr instanceof HasExtraVar){
                HasExtraVar hev=(HasExtraVar) instr;
                if(hev.getArg().getType()==VariableType.INPUT){
                    hev.setArg(prog.getVars().getInput().get(hev.getArg().getPosition()));
                }
            }
        }
    }
    public void updateValues(ArrayList<Integer> values)
    {
        for(AbstractInstruction instr:prog.getInstructions()){
            if(instr.getVar() instanceof ResultVar){
                ((ResultVar)instr.getVar()).getFunction().updateValuesRecursive(values);
            }
            if(instr instanceof HasExtraVar){
                HasExtraVar hev=(HasExtraVar) instr;
                if(hev.getArg() instanceof ResultVar){
                    ((ResultVar)hev.getArg()).getFunction().updateValuesRecursive(values);
                }
            }
            if(instr instanceof Function)
            {
                ((Function)instr).updateValuesRecursive(values);
            }
            if(instr instanceof JumpEqualFunction){
                ((JumpEqualFunction)instr).getFunc().updateValuesRecursive(values);
            }
        }

    }
    private void updateValuesRecursive(ArrayList<Integer> values)
    {
        for(Variable v : prog.getVars().getInput().values())
        {
            if(!(v instanceof ResultVar))
            {
                Optional<Integer> val= Optional.ofNullable(values.get(v.getPosition()-1));
                if(val.isPresent())
                  v.setValue(values.get(v.getPosition()-1));
            }
            else
            {
                ((ResultVar)v).getFunction().updateValuesRecursive(values);
            }
        }

    }

    public Collection<Variable> getUsedVariables() {
        return prog.getVars().getInput().values();
    }
    @Override //Activate with null
    public Function clone(ProgramVars context) {
        if(!(this.var instanceof ResultVar))
            return new Function(var.clone(context),prog.clone(),displayName);
        else
          return new Function(lab.myClone(),((ResultVar)var).clone(context, var.getPosition()),prog.clone(),displayName);

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
