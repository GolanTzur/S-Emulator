package servlets;

import engine.RunInfo;
import engine.UserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import classes.*;

import java.io.IOException;

@WebServlet(name = "UserHistoryServlet", urlPatterns = {"/userhistory"})
public class UserHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = request.getParameter("username");
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing username parameter");
            return;
        }

        UsersManager um = (UsersManager) getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName());
        if (um == null) {
            um = UsersManager.getInstance();
            getServletContext().setAttribute(ContextAttributes.USERS.getAttributeName(), um);
        }
        UserInfo userInfo;
        synchronized (um) {
            userInfo = um.lookForUser(user);
        }
        if (userInfo == null) {
            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("User not found");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        synchronized (userInfo) {
            for (RunInfo ri : userInfo.getRunInfos()) {
                sb.append("{");
                sb.append("\"isMain\":\"").append(ri.isMain()).append("\",");
                sb.append("\"name\":\"").append(ri.getName()).append("\",");
                sb.append("\"arch\":\"").append(ri.getArch()).append("\",");
                sb.append("\"result\":\"").append(ri.getResult()).append("\",");
                sb.append("\"cycles\":\"").append(ri.getCycles()).append("\"");
                sb.append("\"degree\":\"").append(ri.getDegree()).append("\"");
                sb.append("},");
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
