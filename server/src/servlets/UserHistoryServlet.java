package servlets;

import engine.RunInfo;
import engine.UserInfo;
import engine.basictypes.Architecture;
import entitymanagers.UsersManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import entitymanagers.*;

import java.io.IOException;
import java.util.Properties;

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
                sb.append("\"isMain\":").append(ri.isMain()).append(",");
                sb.append("\"name\":\"").append(ri.getName()).append("\",");
                sb.append("\"arch\":\"").append(ri.getArch()).append("\",");
                sb.append("\"result\":").append(ri.getResult()).append(",");
                sb.append("\"cycles\":").append(ri.getCycles()).append(",");
                sb.append("\"degree\":").append(ri.getDegree());
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
        response.getWriter().print(sb.toString());
    }

    //Add runinfo to user history
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Properties prop = new Properties();
        prop.load(request.getInputStream());
        String user = prop.getProperty("username");
        String runname = prop.getProperty("programname");
        String arch = prop.getProperty("architecture");
        String resultStr = prop.getProperty("result");
        String cyclesStr = prop.getProperty("cycles");
        String degreeStr = prop.getProperty("degree");
        String isMainStr = prop.getProperty("isMainProgram");

        if(user==null || runname==null || arch==null || resultStr==null || cyclesStr==null || degreeStr==null || isMainStr==null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing parameters");
            return;
        }

        UsersManager um = (UsersManager) getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName());
        synchronized (um) {
            UserInfo userInfo = um.lookForUser(user);
            boolean isMain = Boolean.parseBoolean(isMainStr);
            int cycles;
            int degree;
            int result;
            try {
                cycles = Integer.parseInt(cyclesStr);
                degree = Integer.parseInt(degreeStr);
                result = Integer.parseInt(resultStr);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Invalid number format for cycles or degree");
                return;
            }
            userInfo.addRunInfo(new RunInfo(isMain, runname, Architecture.valueOf(arch), result, cycles, degree));
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }



    }
}
