package servlets;

import entitymanagers.FunctionsManager;
import entitymanagers.ProgramsManager;
import entitymanagers.UsersManager;
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
import java.util.Properties;


@WebServlet(name = "ProgramsServlet", urlPatterns = {"/programs"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5) // 25 MBaa
public class ProgramsServlet extends HttpServlet {

    // Upload a new program
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
                    ProgramsManager pm;
                    FunctionsManager fm;
                    UsersManager um;
                    synchronized (getServletContext()) {

                        pm = (ProgramsManager) getServletContext().getAttribute(ContextAttributes.PROGRAMS.getAttributeName());
                        fm = (FunctionsManager) getServletContext().getAttribute(ContextAttributes.FUNCTIONS.getAttributeName());
                        um = (UsersManager) getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName());

                        if (pm == null) {
                            pm = ProgramsManager.getInstance();
                            getServletContext().setAttribute(ContextAttributes.PROGRAMS.getAttributeName(), pm);
                        }

                        if (fm == null) {
                            fm = FunctionsManager.getInstance();
                            getServletContext().setAttribute(ContextAttributes.FUNCTIONS.getAttributeName(), fm);
                        }

                        if (um == null) {
                            um = UsersManager.getInstance();
                            getServletContext().setAttribute(ContextAttributes.USERS.getAttributeName(), um);
                        }
                    }
                    if (pm.programExists(sProgram.getName()) != null) {
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        response.getWriter().println("Program already exists");
                        return;
                    }

                    UserInfo userInfo = um.lookForUser(user);
                        AddFuncDetails afd = new AddFuncDetails(userInfo, sProgram.getName(), fm.getFunctions(),fm.getLock(),um.getRwLock());
                        try {
                            Program program = xmlHandler.convertToProgram(sProgram, afd); // here the functions are added
                            program.checkValidity();
                            ProgramInfo pi = new ProgramInfo(program, user);
                            pm.addProgram(pi);
                            um.getRwLock().writeLock().lock();
                            userInfo.addProgram(pi);
                            um.getRwLock().writeLock().unlock();
                        } catch (Exception e) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            response.getWriter().println("Error processing program: " + e.getMessage());
                            return;
                        }



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

    // Get all programs
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProgramsManager pm = (ProgramsManager) getServletContext().getAttribute(ContextAttributes.PROGRAMS.getAttributeName());
        if (pm == null) {
            pm = ProgramsManager.getInstance();
            getServletContext().setAttribute(ContextAttributes.PROGRAMS.getAttributeName(), pm);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
       pm.getRwLock().readLock().lock();
            for (ProgramInfo program : pm.getPrograms()) {
                sb.append("{\"programname\":\"").append(program.getProgramName()).append("\",");
                sb.append("\"owner\":\"").append(program.getUserUploaded()).append("\",");
                sb.append("\"numinstructions\":\"").append(program.getInstructionsCount()).append("\",");
                sb.append("\"degree\":\"").append(program.getDegree()).append("\",");
                sb.append("\"numruns\":\"").append(program.getNumRuns()).append("\",");
                sb.append("\"avgcredits\":\"").append(program.getAvgCreditsPrice()).append("\"},");
            }
        pm.getRwLock().readLock().unlock();

        if (sb.length() == 1)
            sb.deleteCharAt(0);
        else if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(sb.toString());
    }

    //Add run count to program
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Properties prop = new Properties();
        prop.load(request.getInputStream());
        String programName = prop.getProperty("programname");
        String credits = prop.getProperty("credits");
        if (programName != null && credits != null) {
            ProgramsManager pm = (ProgramsManager) getServletContext().getAttribute(ContextAttributes.PROGRAMS.getAttributeName());
            if (pm == null) {
                pm = ProgramsManager.getInstance();
                getServletContext().setAttribute(ContextAttributes.PROGRAMS.getAttributeName(), pm);
            }
            ProgramInfo pi;
            pi = pm.programExists(programName);

            if (pi == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("Program not found");
                return;
            }
            pm.getRwLock().writeLock().lock();
                pi.updateAvgCreditsPrice(Integer.parseInt(credits));
            pm.getRwLock().writeLock().unlock();
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing parameters");
        }

    }
}