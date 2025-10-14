package servlets;

import classes.FunctionsManager;
import classes.ProgramsManager;
import classes.UsersManager;
import engine.*;
import engine.UserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Properties;


@WebServlet(name = "UsersServlet", urlPatterns = {"/users"})
public class UsersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        if (action != null && action.equals("login")) {
            String user = request.getParameter("username");
            if (user != null) {
                UsersManager usersManager = (UsersManager) getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName());
                if (usersManager == null) {
                    usersManager = UsersManager.getInstance();
                    getServletContext().setAttribute(ContextAttributes.USERS.getAttributeName(), usersManager);
                }
                synchronized (usersManager) {
                    UserInfo userInfo;
                    UserInfo userToCheck;
                    if ((userInfo = usersManager.lookForUser(user)) == null) {
                        userToCheck = new UserInfo(user);
                        usersManager.addUser(userToCheck);
                    } else
                        userToCheck = userInfo;
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print(user + "," + userToCheck.getCreditsLeft());
                }

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Missing 'username' parameter");
            }
        } else if (action != null && action.equals("showusers")) {
            UsersManager usersManager = (UsersManager) getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName());
            if (usersManager == null) {
                usersManager = UsersManager.getInstance();
                getServletContext().setAttribute(ContextAttributes.USERS.getAttributeName(), usersManager);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            synchronized (usersManager) {
                for (UserInfo ui : usersManager.getUsers()) {
                    sb.append("{\"username\":\"").append(ui.getName()).append("\",");
                    sb.append("\"numfunctions\":\"").append(ui.getFunctionsUploaded().size()).append("\",");
                    sb.append("\"numprograms\":\"").append(ui.getProgramsUploaded().size()).append("\",");
                    sb.append("\"creditsleft\":\"").append(ui.getCreditsLeft()).append("\",");
                    sb.append("\"creditsspent\":\"").append(ui.getCreditsSpent()).append("\",");
                    sb.append("\"numruns\":\"").append(ui.getRunInfos().size()).append("\"},");
                }
            }
            if (sb.length() == 1)
                sb.deleteCharAt(0);
            else if (sb.charAt(sb.length() - 1) == ',') {
                sb.deleteCharAt(sb.length() - 1);
                sb.append("]");
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(sb.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid 'action' parameter");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Properties prop = new Properties();
        prop.load(req.getInputStream());
        String credits = prop.getProperty("credits");
        String user = prop.getProperty("username");
        String action = prop.getProperty("action");
        if (user != null && credits != null && action != null) {
            UserInfo userToUpdate = null;
            UsersManager usersManager = (UsersManager) getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName());
            synchronized (usersManager) {
                if ((userToUpdate = usersManager.lookForUser(user)) == null) {
                    usersManager.addUser(new UserInfo(user));
                }

                switch (action) {
                    case "add":
                        userToUpdate.addCredits(Integer.parseInt(credits));
                        break;
                    case "subtract":
                        try {
                            userToUpdate.subtractCredits(Integer.parseInt(credits));
                        } catch (Exception e) {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().println("Error updating credits: " + e.getMessage());
                            return;
                        }
                        break;
                    default:
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().println("Invalid 'action' parameter");
                        return;
                }
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(userToUpdate.getCreditsLeft());
                return;
            }
        }
        else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Missing 'username' or 'credits' parameter");
        }
    }


    //Set Client Session with the program selected
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String functionname = req.getParameter("programname");
        String user = req.getParameter("username");
        if (functionname == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Missing 'username' or 'functionname' parameter");
            return;
        }

        FunctionsManager functionsManager = (FunctionsManager) getServletContext().getAttribute(ContextAttributes.FUNCTIONS.getAttributeName());
        if (functionsManager == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("FunctionsManager not initialized");
            return;
        }
        ProgramsManager programsManager = (ProgramsManager) getServletContext().getAttribute(ContextAttributes.PROGRAMS.getAttributeName());
        if (programsManager == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("FunctionsManager not initialized");
            return;
        }
        Program programToSet = null;
        synchronized (functionsManager) {
            FunctionInfo functionToSet = null;

            if (functionsManager.functionExists(functionname)) {
                functionToSet = functionsManager.getFunction(functionname);
                programToSet = functionToSet.func().getProg();
            }
        }
        if (programToSet == null) {
            synchronized (programsManager) {
                ProgramInfo programInfoToSet = null;
                if ((programInfoToSet = programsManager.programExists(functionname)) != null) {
                    programToSet = programInfoToSet.getProgram();
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().println("Function/Program not found");
                    return;
                }
            }
        }
                HttpSession session = req.getSession(true);
                session.setAttribute("currentprogram", programToSet.clone());

                UsersManager usersManager = (UsersManager) getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName());
                if (usersManager != null && user != null) {
                    UserInfo userInfo;
                    synchronized (usersManager) {
                        userInfo = usersManager.lookForUser(user);
                    }
                    if (userInfo != null) {
                        session.setAttribute("currentuser", userInfo);
                        resp.setStatus(HttpServletResponse.SC_OK);
                        return;
                    }
                }
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}
