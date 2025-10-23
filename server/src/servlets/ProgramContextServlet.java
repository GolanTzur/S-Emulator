package servlets;

import engine.Program;
import engine.ProgramVars;
import engine.basictypes.InstructionType;
import engine.basictypes.Variable;
import engine.classhierarchy.AbstractInstruction;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProgramContextServlet", urlPatterns = {"/programcontext"})
public class ProgramContextServlet extends HttpServlet {

    // Get program info like degree , instructions list , instruction history
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws jakarta.servlet.ServletException, java.io.IOException {

        Program program = (Program) request.getSession(false).getAttribute("currentprogram");

        String info = request.getParameter("info");
        if (info != null && info.equals("degree")) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(program.getProgramDegree());
            return;
        } else if (info != null && info.equals("instructions")) {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (AbstractInstruction instr : program.getInstructions()) {
                builder.append("{\"pos\":\"").append(instr.getPos()).append("\",");
                builder.append("\"label\":\"").append(instr.getLab()).append("\",");
                builder.append("\"type\":\"");
                if (instr.getType() instanceof InstructionType)
                    builder.append("B");
                else
                    builder.append("S");
                builder.append("\",");
                builder.append("\"architecture\":\"").append(instr.getType().getArchitecture().name()).append("\",");
                builder.append("\"instruction\":\"").append(instr.getChildPart()).append("\",");
                builder.append("\"cycles\":\"").append(instr.getType().getCycles()).append("\"},");
            }
            if (builder.length() == 1) {
                builder.deleteCharAt(0);
            } else {
                builder.deleteCharAt(builder.length() - 1);
                builder.append("]");
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(builder.toString());
            return;
        } else if (info != null && info.equals("instructionhistory")) {
            String indexsearch = request.getParameter("pos");
            if (indexsearch == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Missing 'index' parameter");
                return;
            }
            int pos;
            try {
                pos = Integer.parseInt(indexsearch);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Invalid 'index' parameter");
                return;
            }


            AbstractInstruction source = null;
            source = program.getInstructionAt(pos);

            if (source == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("Instruction not found");
                return;
            }
            source = source.getSource();

            StringBuilder builder = new StringBuilder();
            builder.append("[");
            while (source != null) {
                builder.append("{\"pos\":\"").append(source.getPos()).append("\",");
                builder.append("\"label\":\"").append(source.getLab()).append("\",");
                builder.append("\"type\":\"");
                if (source.getType() instanceof InstructionType)
                    builder.append("B");
                else
                    builder.append("S");
                builder.append("\",");
                builder.append("\"architecture\":\"").append(source.getType().getArchitecture().name()).append("\",");
                builder.append("\"instruction\":\"").append(source.getChildPart()).append("\",");
                builder.append("\"cycles\":\"").append(source.getType().getCycles()).append("\"},");
                source = source.getSource();
            }
            if (builder.length() == 1) {
                builder.deleteCharAt(0);
            } else {
                builder.deleteCharAt(builder.length() - 1);
                builder.append("]");
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(builder.toString());
            return;
        }else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid 'info' parameter");
            return;
        }
    }

    //program vars will be accessed with post method
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws jakarta.servlet.ServletException, java.io.IOException {
        Program program = (Program) request.getSession(false).getAttribute("currentprogram");
        ProgramVars programVars = program.getVars();
        String result = serializeprogramVars(programVars);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(result);
        return;
    }
    public static String serializeprogramVars(ProgramVars programVars) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"inputVarsNames\":[");
        for (Integer pos : programVars.getInput().keySet()) {
            builder.append("\"x").append(pos).append("\",");
        }
        if (builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("],");
        builder.append("\"inputVarsValues\":[");
        for (Variable var : programVars.getInput().values()) {
            builder.append(var.getValue()).append(",");
        }
        if (builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("],");
        builder.append("\"workVarsNames\":[");
        for (Integer pos : programVars.getEnvvars().keySet()) {
            builder.append("\"z").append(pos).append("\",");
        }
        if (builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("],");
        builder.append("\"workVarsValues\":[");
        for (Variable var : programVars.getEnvvars().values()) {
            builder.append(var.getValue()).append(",");
        }
        if (builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("],");
        builder.append("\"result\":" + programVars.getY().getValue());
        builder.append("}");
        return builder.toString();
    }

}



