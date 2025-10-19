package servlets;


import entitymanagers.FunctionsManager;
import entitymanagers.ProgramsManager;
import engine.*;
import entitymanagers.UsersManager;
import jakarta.servlet.http.HttpServlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@WebServlet(name = "DebugServlet", urlPatterns = {"/debug"})
public class DebugServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws jakarta.servlet.ServletException, java.io.IOException {
        Properties prop = new Properties();
        prop.load(req.getInputStream());
        String action = prop.getProperty("action");
        req.setAttribute("properties", prop);
        UserInfo user = (UserInfo) req.getSession().getAttribute("currentuser");
        ReentrantReadWriteLock userLock = ((UsersManager) getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName())).getRwLock();

        if(action.equals("debug")) {
            Program program = (Program) req.getSession().getAttribute("currentprogram");

            Program programsource = program;

            int degree=programsource.getProgramDegree();

            ProgramsManager pm = (ProgramsManager) getServletContext().getAttribute(ContextAttributes.PROGRAMS.getAttributeName());
            ProgramInfo pi;
            Program search;

            pi = pm.programExists(programsource.getName());

            if(pi==null){ //If its not main program , its function
                FunctionsManager fm = (FunctionsManager) getServletContext().getAttribute(ContextAttributes.FUNCTIONS.getAttributeName());
                FunctionInfo fi = fm.getFunction(programsource.getName());
                search = fi.func().getProg();
            }
            else
                search = pi.getProgram();

            programsource=search.clone();
            programsource.deployToDegree(programsource.getProgramDegree()-degree);
            req.getSession().setAttribute("programcopy", programsource);

            userLock.readLock().lock();
            int creditsLeft= user.getCreditsLeft();
            userLock.readLock().unlock();

            Debugger debugger=new Debugger(new Runner(program.getInstructions(),creditsLeft));
            debugger.setRunning(true);
            req.getSession().setAttribute("currentdebugger", debugger);
            getServletContext().getRequestDispatcher("/inputs").include(req, resp);
        }
        if(action.equals("step") || action.equals("debug"))
        {
            Debugger debugger = (Debugger) req.getSession().getAttribute("currentdebugger");

            if(debugger==null){
                resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("No active debugger. Start a debug session first.");
                return;
            }

            try {
                int beforeCycles=debugger.getCycleCount();
                debugger.step();
                int afterCycles=debugger.getCycleCount();

                userLock.writeLock().lock();
                user.spendCredits(afterCycles-beforeCycles);
                userLock.writeLock().unlock();

                if (!debugger.isRunning()) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    Program programCopy = (Program) req.getSession().getAttribute("programcopy");
                    req.getSession().setAttribute("currentprogram", programCopy);
                    req.getSession().removeAttribute("programcopy");
                    req.getSession().removeAttribute("currentdebugger");
                    resp.getWriter().print("Finished");
                    return;
                }

            } catch (Exception e) {
                resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST);
                Program programCopy = (Program) req.getSession().getAttribute("programcopy");
                req.getSession().setAttribute("currentprogram", programCopy);
                req.getSession().removeAttribute("programcopy");
                req.getSession().removeAttribute("currentdebugger");
                resp.getWriter().println(e.getMessage());
                return;
            }
            resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_OK);
            resp.getWriter().print(debugger.getCycleCount()+","+ debugger.getCurrentStep());
        }
        else if(action.equals("stop")){
            Program programCopy = (Program) req.getSession().getAttribute("programcopy");
            req.getSession().setAttribute("currentprogram", programCopy);
            req.getSession().removeAttribute("programcopy");
            req.getSession().removeAttribute("currentdebugger");
            resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_OK);
        }
        else if(action.equals("resume")){

            Debugger debugger = (Debugger) req.getSession().getAttribute("currentdebugger");

            if(debugger==null){
                resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("No active debugger. Start a debug session first.");
                return;
            }

            try {
                int beforeCycles=debugger.getCycleCount();
                debugger.resume();
                int afterCycles=debugger.getCycleCount();
                user.spendCredits(afterCycles-beforeCycles);
            } catch (Exception e) {
                resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST);
                Program programCopy = (Program) req.getSession().getAttribute("programcopy");
                req.getSession().setAttribute("currentprogram", programCopy);
                req.getSession().removeAttribute("programcopy");
                req.getSession().removeAttribute("currentdebugger");
                resp.getWriter().println(e.getMessage());
                return;
            }
            resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_OK);
            resp.getWriter().print(debugger.getCycleCount());
        }

    }

}
