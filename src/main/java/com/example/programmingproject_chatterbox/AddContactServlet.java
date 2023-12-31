package com.example.programmingproject_chatterbox;

import Classes.Database;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
@WebServlet(name = "AddContactServlet", urlPatterns = { "/add-contact" })
public class AddContactServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Invite user functionality

        Database database = new Database();
        HttpSession session = request.getSession();
        String userName = request.getParameter("UserName");
        if (request.getParameter("UserIDtoChange") != null) {
            String userNameFromUserID = database.getUsername(Integer.parseInt(request.getParameter("UserIDtoChange")));

            userName = userNameFromUserID;
        }

        String currentUser = (String) session.getAttribute("userName");

        database.addFriend(userName, currentUser);
        response.sendRedirect("Profile.jsp");
        }

        // Redirect or display success message


    }

