package servlets;

import entitymanagers.FunctionsManager;
import entitymanagers.ProgramsManager;
import engine.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ExpansionServlet", urlPatterns = {"/expansion"})
public class ExpansionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws jakarta.servlet.ServletException, java.io.IOException {
        Program prog= (Program) request.getSession(false).getAttribute("currentprogram");
        String progName=prog.getName();
        String degreeParam = request.getParameter("degree");
        if (degreeParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing 'degree' parameter");
            return;
        }
        int degree;
        try {
            degree = Integer.parseInt(degreeParam);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid 'degree' parameter");
            return;
        }
        ProgramsManager pm = (ProgramsManager) getServletContext().getAttribute(ContextAttributes.PROGRAMS.getAttributeName());
        ProgramInfo pi;
        Program search;
        pi = pm.programExists(progName);

            if(pi==null){ //If its not main program , its function
                FunctionsManager fm = (FunctionsManager) getServletContext().getAttribute(ContextAttributes.FUNCTIONS.getAttributeName());
                FunctionInfo fi = fm.getFunction(progName);
                search = fi.func().getProg();
            }
            else
                search = pi.getProgram();

            prog=search.clone();
            prog.deployToDegree(degree);
            request.getSession(false).setAttribute("currentprogram", prog);
            getServletContext().getRequestDispatcher("/programcontext?info=instructions").forward(request, response);
    }
}
