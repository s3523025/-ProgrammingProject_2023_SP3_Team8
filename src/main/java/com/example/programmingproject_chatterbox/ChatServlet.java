package com.example.programmingproject_chatterbox;

import Classes.ChatService;
import Classes.Group;
import Classes.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ChatServlet extends HttpServlet {
	
	private ChatService chatService;
	
	public ChatServlet() {
		chatService = new ChatService();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		
		if (action.equals("createGroup")) {
			String groupName = request.getParameter("groupName");
			Group group = null;
			try {
				group = chatService.createGroup(groupName);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			request.setAttribute("group", group);
			request.getRequestDispatcher("/group.jsp").forward(request, response);
		} else if (action.equals("getGroups")) {
			List<Group> groups = null;
			try {
				groups = chatService.getGroups();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			request.setAttribute("groups", groups);
			request.getRequestDispatcher("/groups.jsp").forward(request, response);
		} else if (action.equals("sendMessage")) {
			int groupId = Integer.parseInt(request.getParameter("groupId"));
			String messageText = request.getParameter("messageText");
			try {
				chatService.sendMessage(groupId, messageText);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			response.sendRedirect("/group.jsp?groupId=" + groupId);
		} else if (action.equals("getMessages")) {
			int groupId = Integer.parseInt(request.getParameter("groupId"));
			int channelId = Integer.parseInt(request.getParameter("channelId"));
			List<Message> messages = null;
			try {
				messages = chatService.getMessages(groupId, channelId);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			request.setAttribute("messages", messages);
			request.getRequestDispatcher("/messages.jsp").forward(request, response);
		} else {
			response.setStatus(404);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
