package servlets;

import entitymanagers.FunctionsManager;
import engine.FunctionInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "FunctionsServlet", urlPatterns = {"/functions"})
public class FunctionsServlet extends HttpServlet {


    // Get list of all functions
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FunctionsManager fm= (FunctionsManager) getServletContext().getAttribute(ContextAttributes.FUNCTIONS.getAttributeName());
        synchronized (getServletContext()) {
            if (fm == null) {
                fm = FunctionsManager.getInstance();
                getServletContext().setAttribute(ContextAttributes.FUNCTIONS.getAttributeName(), fm);
            }
        }
        StringBuilder sb=new StringBuilder();
        sb.append("[");

        fm.getLock().readLock().lock();
        for (FunctionInfo function : fm.getFunctions()) {
            sb.append("{\"funcname\":\"").append(function.func().getProg().getName()).append("\",");
            sb.append("\"mainprogramname\":\"").append(function.mainProgramContext()).append("\",");
            sb.append("\"owner\":\"").append(function.userUploaded()).append("\",");
            sb.append("\"numinstructions\":\"").append(function.func().getProg().getInstructions().size()).append("\",");
            sb.append("\"degree\":\"").append(function.func().getProg().getProgramDegree()).append("\"},");
        }
        fm.getLock().readLock().unlock();

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
