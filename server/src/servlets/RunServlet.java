package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@WebServlet(name = "RunServlet", urlPatterns = {"/run"})
public class RunServlet extends HttpServlet {
@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String s=request.getParameter("a");
        response.setContentType("text/plain");
        if (s==null)
            response.getWriter().write("RunServlet is working!");
        else
        response.getWriter().write("RunServlet is working!"+s);
    }

}
