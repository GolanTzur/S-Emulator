package servlets;

import entitymanagers.FunctionsManager;
import entitymanagers.ProgramsManager;
import engine.*;
import entitymanagers.UsersManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@WebServlet(name = "RunServlet", urlPatterns = {"/run"})
public class RunServlet extends HttpServlet {


    // Execute the current program, first save a copy from servlet context
   @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       Program programsource=(Program)request.getSession(false).getAttribute("currentprogram");
       Program program=programsource;

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
       getServletContext().getRequestDispatcher("/inputs").include(request,response);
       UserInfo user=(UserInfo)request.getSession(false).getAttribute("currentuser");
       ReentrantReadWriteLock userLock=((UsersManager) getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName())).getRwLock();
       try {
           userLock.readLock().lock();
           int creditsLeft=user.getCreditsLeft();
           userLock.readLock().unlock();

           program.execute(creditsLeft);

           userLock.writeLock().lock();
           user.spendCredits(program.getCycleCount());
           userLock.writeLock().unlock();
       } catch (Exception e) {
           response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
           response.getWriter().println(e.getMessage());
           request.getSession(false).setAttribute("currentprogram",programsource);
           return;
       }

     getServletContext().getRequestDispatcher("/programcontext").forward(request,response);
     request.getSession(false).setAttribute("currentprogram",programsource);
     response.setStatus(HttpServletResponse.SC_OK);
   }
}
