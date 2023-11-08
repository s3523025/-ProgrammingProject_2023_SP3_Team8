<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="Classes.Channel" %>
<%@ page import="java.util.List" %>
<%@ page import="Classes.ChatService" %>
<%@ page import="java.sql.SQLException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    // Get the current group ID from the request parameter
    String groupIdParam = request.getParameter("groupId");
    int groupId = Integer.parseInt(groupIdParam); // Assume groupId is valid

    // Get the list of channels for the selected group from the ChatService class
    List<Channel> channels = null;
    ChatService chatService = new ChatService();
    try {
        channels = chatService.getChannels(groupId);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }

    // Janky logout check if no user ID
    String userId = (String) session.getAttribute("userId");
    if (!(userId != null && !userId.isEmpty())) {
        response.sendRedirect("login");
    }
%>

<html>
<head>
    <title>User Profile</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/dashboard.css">
    <jsp:include page="head.jsp" />
</head>
<body>
<div class="main-container flex items-stretch justify-stretch">
    <jsp:include page="sidebar.jsp" />
    <div class="main-content flex flex-col grow p-8">
        <div id="chat-title" class="flex content-center items-end mx-2 ">
            <div id="chat-name" class="text-2xl">Channels</div>
        </div>
        <div id="chat-control" class="mx-2">

            <% for (Channel channel : channels) { %>
            <li class="text-white mt-2"><a href="Chat.jsp?groupId=<%= groupId %>&channelId=<%= channel.getChannelId() %>"><%= channel.getChannelName() %></a></li>
            <% } %>

            <h2 class="text-white text-xl mt-8">Add a New Channel</h2>
            <form action="add-channel" method="post">
                <input type="hidden" name="groupId" value="<%= groupId %>">
                <label>
                <input type="text" name="channelName" placeholder="Channel Name">
            </label>
                <input type="submit" value="Add">
            </form>
        </div>
    </div>
    <div>
        <c:import url="inviteUser.jsp" />
    </div>
    <div class="info-bar">
        <div class="m-4">
            <h1 class="text-white text-xl mt-4 font-bold">Dev Links</h1>
            <ul>
                <li class="text-white mt-2"><a href="${pageContext.request.contextPath}/login?action=logout">Login</a></li>
                <li class="text-white mt-2"><a href="${pageContext.request.contextPath}/Profile.jsp">Profile</a></li>
                <li class="text-white mt-2"><a href="${pageContext.request.contextPath}/Groups.jsp">Groups</a></li>
                <li class="text-white mt-2">____________</li>
                <li class="text-white mt-2"><a href="${pageContext.request.contextPath}/Channels.jsp?groupId=9">Example Channel - Test Group 2</a></li>
                <li class="text-white mt-2"><a href="${pageContext.request.contextPath}/Chat.jsp?groupId=9&channelId=14">Example Chat - TG 2 CH 1</a></li>
            </ul>
        </div>
    </div>
</div>
</body>
</html>

