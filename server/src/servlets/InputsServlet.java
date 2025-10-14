package servlets;

import engine.Program;
import engine.basictypes.Variable;
import jakarta.servlet.http.HttpServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "InputsServlet", urlPatterns = {"/inputs"})
public class InputsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Program currentProgram = (Program) req.getSession(false).getAttribute("currentprogram");
        Properties prop = new Properties();
        prop.load(req.getInputStream());
        String inputs = prop.getProperty("inputs");
        Integer[] inputArray = inputs.split(",").length > 0 ? new Integer[inputs.split(",").length] : new Integer[0];
        for (int i = 0; i < inputs.split(",").length; i++) {
            inputArray[i] = Integer.parseInt(inputs.split(",")[i]);
        }
        Collection<Variable> variables = currentProgram.getVars().getInput().values();
        Iterator<Variable> varIterator = variables.iterator();

        for(int i=0;i<inputArray.length;i++){
            if(varIterator.hasNext()){
                Variable var=varIterator.next();
                var.setValue(inputArray[i]);
            }
            else
                break;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        //resp.getWriter().println("RunServlet is not yet implemented.");
    }
}
