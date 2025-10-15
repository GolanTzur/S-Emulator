package servlets;

import engine.*;
import engine.basictypes.Variable;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "RunServlet", urlPatterns = {"/run"})
public class RunServlet extends HttpServlet {

   @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     Program program=(Program)request.getSession(false).getAttribute("currentprogram");
     Program sourceprog;
     sourceprog=program.clone();

     getServletContext().getRequestDispatcher("/inputs").include(request,response);
     UserInfo user=(UserInfo)request.getSession(false).getAttribute("currentuser");
     synchronized (user) {
         try {
             program.execute(user.getCreditsLeft());
             user.spendCredits(program.getCycleCount());
         } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println(e.getMessage());
                user.spendCredits(user.getCreditsLeft()); // remove all credits on error
                request.getSession(false).setAttribute("currentprogram",sourceprog);
                return;
         }
     }
     getServletContext().getRequestDispatcher("/programcontext").forward(request,response);
     request.getSession(false).setAttribute("currentprogram",sourceprog);
     response.setStatus(HttpServletResponse.SC_OK);
   }
}
