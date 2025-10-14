package servlets;

import engine.Program;
import engine.basictypes.*;
import engine.classhierarchy.AbstractInstruction;
import engine.classhierarchy.HasGotoLabel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@WebServlet(name = "HighlightServlet", urlPatterns = {"/highlight"})
public class HighlightServlet extends HttpServlet {

    // Highlight instructions by label and/or variable
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bylab=request.getParameter("lab");
        String byvar=request.getParameter("var");
        String strpos=request.getParameter("pos");
        int pos;
        try {
            pos = Integer.parseInt(strpos);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid 'pos' parameter");
            return;
        }

        Program program=(Program)request.getSession(false).getAttribute("currentprogram");
        StringBuilder sb=new StringBuilder();
        AbstractInstruction currentInstruction=program.getInstructionAt(pos);

        Set<Integer> positions=new HashSet<>();

        if(bylab!=null && bylab.equals("1")){
            HasLabel labelToHighlight = currentInstruction.getLab();
            Set<Integer> labpositions = program.findLabelsEquals(labelToHighlight);
            Set <Integer> argpositions = new HashSet<>();
            if (currentInstruction instanceof HasGotoLabel)
                argpositions = program.findLabelsEquals(((HasGotoLabel) currentInstruction).getGotolabel());
            labpositions.addAll(argpositions);
            positions.addAll(labpositions);
            }

        if(byvar!=null && byvar.equals("1")){
            Set<Variable> varsToHighlight=program.getAllInvolvedVariables(currentInstruction);
            Set<Integer> varpositions=new HashSet<>();
            for(Variable var:varsToHighlight){;
                varpositions.addAll(program.findVariableUsage(var));
            }
            positions.addAll(varpositions);
        }
        for(Integer p:positions){
            sb.append(p).append(",");
        }
        if(sb.length()>0)
            sb.deleteCharAt(sb.length()-1);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(sb.toString());
    }

    //Highlight instructions by Architecture compatibility
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String arch=request.getParameter("architecture");
        if(arch==null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing or empty 'arch' parameter");
            return;
        }
        Program program=(Program)request.getSession(false).getAttribute("currentprogram");
        StringBuilder sb=new StringBuilder();
        Architecture currentArch=Architecture.valueOf(arch);

        for(AbstractInstruction instr: program.getInstructions()){
            if(instr.getType().getArchitecture().getPrice()>currentArch.getPrice())
                sb.append(instr.getPos()).append(",");
        }

        if(sb.length()>0)
            sb.deleteCharAt(sb.length()-1);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(sb.toString());
    }
}
