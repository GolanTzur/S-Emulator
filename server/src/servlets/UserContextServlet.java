package servlets;

import engine.UserInfo;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "UserContextServlet", urlPatterns = {"/usercontext"})
public class UserContextServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws jakarta.servlet.ServletException, java.io.IOException {
        String info = request.getParameter("info");
        UserInfo username = (UserInfo) request.getSession(false).getAttribute("currentuser");

        if (info == null) {
            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing 'info' parameter");
            return;
        }
        if (info.equals("username")) {
                String usernameStr= username.getName();
                response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_OK);
                response.getWriter().print(usernameStr);
                return;
        } else if (info.equals("credits")) {
            String creditsStr= username.getCreditsLeft()+"";
            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_OK);
            response.getWriter().print(creditsStr);
            return;
        } else {
            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid 'info' parameter");
        }
    }

}
