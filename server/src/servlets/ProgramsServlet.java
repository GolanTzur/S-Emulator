package servlets;

import classes.FunctionsManager;
import classes.ProgramsManager;
import classes.UsersManager;
import engine.*;
import engine.jaxbclasses.SProgram;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@WebServlet(name = "ProgramsServlet", urlPatterns = {"/programs"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5) // 25 MBaa
public class ProgramsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Collection<Part> parts = request.getParts();
        String user = request.getParameter("username");
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing username parameter");
            return;
        }

        XMLHandler xmlHandler = XMLHandler.getInstance();
        for (Part part : parts) {
            if (part.getName().equals("file")) {
                SProgram sProgram = xmlHandler.getSProgram(part.getInputStream());
                try {
                    ProgramsManager pm= (ProgramsManager)getServletContext().getAttribute(ContextAttributes.PROGRAMS.getAttributeName());
                    if (pm == null) {
                        pm = ProgramsManager.getInstance();
                        getServletContext().setAttribute(ContextAttributes.PROGRAMS.getAttributeName(), pm);
                    }
                    synchronized (pm) {
                        if (pm.programExists(sProgram.getName()) != null) {
                            response.setStatus(HttpServletResponse.SC_CONFLICT);
                            response.getWriter().println("Program already exists");
                            return;
                        }
                    }
                    FunctionsManager fm= (FunctionsManager)getServletContext().getAttribute(ContextAttributes.FUNCTIONS.getAttributeName());
                    if (fm == null) {
                        fm = FunctionsManager.getInstance();
                        getServletContext().setAttribute(ContextAttributes.FUNCTIONS.getAttributeName(), fm);
                    }
                    UsersManager um= (UsersManager)getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName());
                    if (um == null) {
                        um = UsersManager.getInstance();
                        getServletContext().setAttribute(ContextAttributes.USERS.getAttributeName(), um);
                    }
                    synchronized (getServletContext()) {
                        UserInfo userInfo = um.lookForUser(user);
                        AddFuncDetails afd = new AddFuncDetails(userInfo, sProgram.getName(), fm.getFunctions());
                        Program program = xmlHandler.convertToProgram(sProgram, afd); // here the functions are added
                        program.checkValidity();
                        ProgramInfo pi = new ProgramInfo(program, user);
                        pm.addProgram(pi);
                        userInfo.addProgram(pi);
                    }

                   /* synchronized (fm) {
                        Set<String> functionNames = program.getAllFunctionsUsed();
                        for(String functionName : functionNames) {
                            if (!fm.functionExists(functionName)) {
                                FunctionInfo fi = new FunctionInfo(functionName, user, program.getName());
                                fm.addFunction(fi);
                            }
                            else
                                functionNames.remove(functionName);
                        }
                        StringBuilder funcList = new StringBuilder();
                        for (String funcname :functionNames) {
                            funcList.append(funcname).append(",");
                        }
                        request.setAttribute("functions", funcList.toString());
                        request.setAttribute("program", program.getName());
                        request.setAttribute("action","increaseprograms");
                        getServletContext().getRequestDispatcher("/users").forward(request, response);
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("Error processing program: " + e.getMessage());
                    return;
                }
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProgramsManager pm= (ProgramsManager)getServletContext().getAttribute(ContextAttributes.PROGRAMS.getAttributeName());
        if (pm == null) {
            pm = ProgramsManager.getInstance();
            getServletContext().setAttribute(ContextAttributes.PROGRAMS.getAttributeName(), pm);
        }
       /* if(request.getParameter("programname")!=null)
        {
            String programName=request.getParameter("programname");
            ProgramInfo pi;
            synchronized (pm) {
                pi = pm.programExists(programName);
            }
            if(pi==null)
            {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("Program not found");
                return;
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("{\"programname\":\""+pi.getProgramName()+"\",\"owner\":\""+pi.getUserUploaded()+"\",\"numinstructions\":\""+pi.getInstructionsCount()+"\",\"degree\":\""+pi.getDegree()+"\",\"numruns\":\""+pi.getNumRuns()+"\",\"avgcredits\":\""+pi.getAvgCreditsPrice()+"\"}");
            return;
        }*/
        StringBuilder sb=new StringBuilder();
        sb.append("[");
        synchronized (pm) {
            for (ProgramInfo program : pm.getPrograms()) {
                sb.append("{\"programname\":\"").append(program.getProgramName()).append("\",");
                sb.append("\"owner\":\"").append(program.getUserUploaded()).append("\",");
                sb.append("\"numinstructions\":\"").append(program.getInstructionsCount()).append("\",");
                sb.append("\"degree\":\"").append(program.getDegree()).append("\",");
                sb.append("\"numruns\":\"").append(program.getNumRuns()).append("\",");
                sb.append("\"avgcredits\":\"").append(program.getAvgCreditsPrice()).append("\"},");
            }
        }

        if(sb.length()==1)
            sb.deleteCharAt(0);
        else if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(sb.toString());
    }
}