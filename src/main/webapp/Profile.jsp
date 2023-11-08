<%@ page import="Classes.Group" %>
<%@ page import="java.util.List" %>
<%@ page import="Classes.ChatService" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="Classes.Database" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    // Get the list of groups from the ChatService class
    Database database = new Database();
    int userID = database.getUserID((String) session.getAttribute("userName"));
	System.out.println(userID);
    List<Group> groups = null;
    ChatService chatService = new ChatService();
    try {
        groups = chatService.getGroups(userID);
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
    <jsp:include page="head.jsp" />
</head>
<body>
<div class="main-container flex items-stretch justify-stretch">
    <jsp:include page="sidebar.jsp" />
    <div class="main-content flex flex-col grow p-8">
        <div id="chat-title" class="flex content-center items-end mx-2 ">
            <div id="chat-name" class="text-2xl">Edit Profile</div>
        </div>
        <div id="chat-control" class="mx-2">
            <div class="text-white text-xl mt-4 font-bold">User info</div>
            <div class="text-white mt-2">Username</div>
            <label>
                <input type="text" class="p-1 w-50" value="${sessionScope.userName}">
            </label>
            <div class="text-white mt-2">First Name</div>
            <label>
                <input type="text" class="p-1 w-50" value="${sessionScope.firstName}">
            </label>
            <div class="text-white mt-2">Last Name</div>
            <label>
                <input type="text" class="p-1 w-50" value="${sessionScope.lastName}">
            </label>
            <div class="text-white mt-2">Email</div>
            <label>
                <input type="text" class="p-1 w-50" value="${sessionScope.email}">
            </label>
            <div class="btn-div">Save Changes</div>
        </div>
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
