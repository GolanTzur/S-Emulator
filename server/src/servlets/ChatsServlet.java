package servlets;

import entitymanagers.Chat;
import entitymanagers.ChatEntry;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet (name = "ChatsServlet", urlPatterns = {"/chats"})
public class ChatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       String rowCountStr = request.getParameter("rowcount");
        Chat chatmanager;
       synchronized (getServletContext()) {
               chatmanager =(Chat) getServletContext().getAttribute(ContextAttributes.CHAT.getAttributeName());
           if (chatmanager == null) {
               request.getServletContext().setAttribute(ContextAttributes.CHAT.getAttributeName(), Chat.getInstance());
               chatmanager = (Chat) getServletContext().getAttribute(ContextAttributes.CHAT.getAttributeName());
           }
       }
         if (rowCountStr == null) { // Return the all chat
             synchronized (chatmanager) {
                 response.getWriter().print(chatmanager);
             }
         }
         else
         {
                int rowCount;
                try {
                    rowCount = Integer.parseInt(rowCountStr);
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("Invalid 'rowcount' parameter");
                    return;
                }
                synchronized (chatmanager) {
                    try {
                        String latestEntries = chatmanager.getFromRow(rowCount);
                        response.getWriter().print(latestEntries);
                        response.setStatus(HttpServletResponse.SC_OK);
                    } catch (Exception e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().println("Invalid 'rowcount' parameter: " + e.getMessage());
                    }
                }
         }
      }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message = request.getParameter("message");
        String sender = request.getParameter("sender");
        if (message == null || message.trim().isEmpty() || sender == null || sender.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing or empty 'message' parameter");
            return;
        }
        Chat chatmanager = (Chat) getServletContext().getAttribute(ContextAttributes.CHAT.getAttributeName());
        synchronized (getServletContext()) {
            if (chatmanager == null) {
                request.getSession().setAttribute(ContextAttributes.CHAT.getAttributeName(), Chat.getInstance());
                chatmanager = (Chat)getServletContext().getAttribute(ContextAttributes.CHAT.getAttributeName());
            }
        }
            synchronized (chatmanager) {
                ChatEntry newEntry = new ChatEntry(sender, message);
                chatmanager.addEntry(newEntry);
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print("Message added successfully");

    }
}


