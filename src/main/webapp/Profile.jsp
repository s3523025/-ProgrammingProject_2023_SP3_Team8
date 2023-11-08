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




%>
<html>
<head>
    <title>User Profile</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/dashboard.css">
    <script src="https://kit.fontawesome.com/9c30b9a3ff.js" crossorigin="anonymous"></script>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Audiowide&family=Baloo+2:wght@700&family=Noto+Sans:wght@400;600;700&display=swap" rel="stylesheet">
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
                <input type="text" class="p-1 w-80" value="${sessionScope.userName}">
            </label>
            <div class="text-white mt-2">First Name</div>
            <label>
                <input type="text" class="p-1 w-80" value="${sessionScope.firstName}">
            </label>
            <div class="text-white mt-2">Last Name</div>
            <label>
                <input type="text" class="p-1 w-80" value="${sessionScope.lastName}">
            </label>
            <div class="text-white mt-2">Email</div>
            <label>
                <input type="text" class="p-1 w-80" value="${sessionScope.email}">
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
