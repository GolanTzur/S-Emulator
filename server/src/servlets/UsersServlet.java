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
                    if ((userInfo=usersManager.lookForUser(user)) == null) {
                        userToCheck = new UserInfo(user);
                        usersManager.addUser(userToCheck);
                    }
                    else
                        userToCheck=userInfo;
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print(user+","+userToCheck.getCreditsLeft());
                }

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Missing 'username' parameter");
            }
        }
        else if (action != null && action.equals("showusers")) {
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
            if(sb.length()==1)
                sb.deleteCharAt(0);
            else if (sb.charAt(sb.length() - 1) == ',') {
                sb.deleteCharAt(sb.length() - 1);
                sb.append("]");
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(sb.toString());
        }
        else {
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
        if (user != null && credits != null) {
            UserInfo userToUpdate = null;
            UsersManager usersManager = (UsersManager) getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName());
            synchronized (usersManager) {
                if ((userToUpdate = usersManager.lookForUser(user)) == null) {
                    usersManager.addUser(new UserInfo(user));
                }
                try {
                    userToUpdate.addCredits(Integer.parseInt(credits));
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().print(userToUpdate.getCreditsLeft());
                    return;
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println("Invalid 'credits' parameter");
                }
            }

        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Missing 'username' or 'credits' parameter");
        }
    }


    //When pressing Load button in client
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Properties prop = new Properties();
        prop.load(req.getInputStream());
        String action = prop.getProperty("action");

        //In case request comes from a RequestDispatcher.forward() call
        String otherAttribute=null;
        if((otherAttribute=(String)req.getAttribute("action"))!=null){
            otherAttribute=(String)req.getAttribute("action");
            if(otherAttribute.equals("increaseprograms")){
                action="increaseprograms";
            }
        }

        switch (action) {
            case "increaseprograms":
                String user = req.getParameter("username");
                String funcsToAdd =(String) req.getAttribute("functions");
                String programsToAdd = (String) req.getAttribute("program");
                if (user != null && funcsToAdd != null && programsToAdd != null) {
                    UserInfo userToUpdate = null;
                    UsersManager usersManager = (UsersManager) getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName());
                    synchronized (usersManager) {
                        if ((userToUpdate = usersManager.lookForUser(user)) == null) {
                            usersManager.addUser(new UserInfo(user));
                        }
                        ProgramsManager pm=ProgramsManager.getInstance();
                        userToUpdate.addProgram(pm.programExists(programsToAdd));
                        String[] funcs=funcsToAdd.split(",");
                        FunctionsManager fm=FunctionsManager.getInstance();
                        for(String f:funcs){
                            userToUpdate.addFunction(fm.getFunction(f));
                        }
                    }
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("User programs and functions updated successfully");

                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println("Missing 'username' or 'credits' parameter");
                }
                break;
            case "increaseruns":
                user = prop.getProperty("username");
                String cresditsToReduce = prop.getProperty("credits");
                if (user != null && cresditsToReduce != null) {
                    UserInfo userToUpdate = null;
                    UsersManager usersManager = (UsersManager) getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName());
                    synchronized (usersManager) {
                        if ((userToUpdate = usersManager.lookForUser(user)) == null) {
                            usersManager.addUser(new UserInfo(user));
                        }
                        try {
                            userToUpdate.spendCredits(Integer.parseInt(cresditsToReduce));
                            resp.setStatus(HttpServletResponse.SC_OK);
                            resp.getWriter().println(userToUpdate.getCreditsLeft());
                        } catch (NumberFormatException e) {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().println("Invalid 'credits' parameter");
                        }
                    }
                }
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("Invalid 'action' parameter");
                break;

        }
    }
}
