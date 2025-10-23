package servlets;

import com.google.gson.Gson;
import engine.ProgramVars;
import engine.RunInfo;
import engine.UserInfo;
import engine.basictypes.Architecture;
import engine.basictypes.Variable;
import engine.basictypes.VariableType;
import entitymanagers.UsersManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import helperclasses.ObservableProgramVars;

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
        userInfo = um.lookForUser(user);

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
                sb.append("\"results\":").append(ProgramContextServlet.serializeprogramVars(ri.getResults())).append(",");
                sb.append("\"cycles\":").append(ri.getCycles()).append(",");
                sb.append("\"degree\":").append(ri.getDegree());
                sb.append("},");
            }
        }
        if (sb.length() == 1)
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
        String resultStr = prop.getProperty("results");
        String cyclesStr = prop.getProperty("cycles");
        String degreeStr = prop.getProperty("degree");
        String isMainStr = prop.getProperty("isMainProgram");

        if (user == null || runname == null || arch == null || resultStr == null || cyclesStr == null || degreeStr == null || isMainStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing parameters");
            return;
        }

        UsersManager um = (UsersManager) getServletContext().getAttribute(ContextAttributes.USERS.getAttributeName());

        UserInfo userInfo = um.lookForUser(user);
        boolean isMain = Boolean.parseBoolean(isMainStr);
        int cycles;
        int degree;
        ProgramVars result;
        try {
            cycles = Integer.parseInt(cyclesStr);
            degree = Integer.parseInt(degreeStr);
            Gson gson = new Gson();
            ObservableProgramVars tempVars = gson.fromJson(resultStr, ObservableProgramVars.class);
            result = deserializeprogramVars(tempVars);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid number format for cycles or degree");
            return;
        }
        um.getRwLock().writeLock().lock();
        userInfo.addRunInfo(new RunInfo(isMain, runname, Architecture.valueOf(arch), result, cycles, degree));
        um.getRwLock().writeLock().unlock();
        response.setStatus(HttpServletResponse.SC_OK);

    }

    public ProgramVars deserializeprogramVars(ObservableProgramVars programVars) {
        ProgramVars vars = new ProgramVars();
        int inputlength=programVars.inputVarsNames().length;
        for (int i=0;i<inputlength;i++) {

            int pos=Integer.parseInt(programVars.inputVarsNames()[i].substring(1));
            int value=Integer.parseInt(programVars.inputVarsValues()[i]);

            vars.getInput().put(pos,Variable.createDummyVar(VariableType.INPUT,pos,value));
        }

        int worklength=programVars.workVarsNames().length;
        for (int i=0;i<worklength;i++) {
            int pos = Integer.parseInt(programVars.workVarsNames()[i].substring(1));
            int value = Integer.parseInt(programVars.workVarsValues()[i]);
            vars.getEnvvars().put(pos, Variable.createDummyVar(VariableType.WORK,pos,value));
        }

        vars.getY().setValue(Integer.parseInt(programVars.result()));
        return vars;
    }
}

