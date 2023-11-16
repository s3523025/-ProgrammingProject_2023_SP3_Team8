<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="Classes.Message" %>
<%@ page import="Classes.Group" %>
<%@ page import="Classes.Channel" %>
<%@ page import="java.util.List" %>
<%@ page import="Classes.ChatService" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    // Get the current group ID and channel ID from the request parameters
    String groupIdParam = request.getParameter("groupId");
    String channelIdParam = request.getParameter("channelId");
    String chatTitle = "Placeholder Chat Name";

    //used to make the date pretty
    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


    int groupId = 0;
    int channelId = 0;
    if (groupIdParam != null && !groupIdParam.isEmpty()) {
        groupId = Integer.parseInt(groupIdParam);
    }
    if (channelIdParam != null && !channelIdParam.isEmpty()) {
        channelId = Integer.parseInt(channelIdParam);
    }
    // Get the list of messages for the selected channel from the ChatService class
    List<Message> messages = null;
    List<Group> groups = null;
    List<Channel> channels = null;

    int userIdValue = 0;
    String userId = (String) session.getAttribute("userId");
    if (userId != null && !userId.isEmpty()) {
        userIdValue = Integer.parseInt(userId);
    } else {
        response.sendRedirect("login");
    }

    ChatService chatService = new ChatService();
    try {

        groups = chatService.getGroups(userIdValue);

        // Set default values for groupId and channelId if they are not set in the request.
        if (groupIdParam == null || groupIdParam.isEmpty()) {
            groupId = groups.get(0).getId();
        }
        channels = chatService.getChannels(groupId);
        if(channelIdParam == null || channelIdParam.isEmpty()) {
            channelId = channels.get(0).getChannelId();
        }

        messages = chatService.getMessages(groupId, channelId);
        chatTitle = chatService.getChatTitle(groupId, channelId);

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
%>

<html>
<head>
    <title>Chat</title>
    <jsp:include page="head.jsp" />
</head>
<body>
<div class="main-container flex items-stretch justify-stretch">
    <jsp:include page="sidebar.jsp" />
    <div>
        <!-- Include the group members' page within an iframe -->
        <jsp:include page="ChannelMembers.jsp" />
    </div>
    <div class="main-content flex flex-col grow p-8">
        <div id="chat-title" class="flex content-center items-end mx-2 ">
            <div id="chat-name" class="text-2xl"><%= chatTitle %></div>
            <div id="chat-info" class="mx-4 text-base">

            </div>

        </div>
        <div id="chat-box" class="p-2 pt-4 overflow-scroll">
            <% for (Message message : messages) {
                String isSender = "receiver"; // Default value if "userName" is not found

                String userName = (String) message.getSenderName();
                // Retrieve "userName" from the session
                String currentUser = (String) session.getAttribute("userName");
                if (userName != null) {
                    // Compare the session "userName" with the message sender name
                    if (userName.equals(currentUser)) {
                        isSender = "sender";
                    }
                }

                //if the message text is blank, skip it
                if (message.getMessageText().equals("")) {
                    continue;
                }                Date date = inputDateFormat.parse(message.getCreatedAt().toString());
                // Create a SimpleDateFormat object for the desired output format
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("h:mma");
                String formattedTimestamp = outputDateFormat.format(date);






            %>
            <div class="chat-message w-full flex my-6 <%= isSender %>">
                <div class="sender-img text-center mx-4">
                    <img src="https://chatterboxavatarstorage.blob.core.windows.net/blob/<%= userName %>" alt="sender image">
                </div>
                <div class="message-content">
                    <div class="message-info flex mx-2">
                        <div class="message-sender-name text-white mr-2"><%= userName %></div>
                        <div class="message-stats text-slate-400 text-xs italic" style="line-height: 24px;"> @ <%= formattedTimestamp %></div>
                    </div>
                    <div class="message-text text-white mx-2">
                        <%= message.getMessageText() %>
                    </div>
                </div>
            </div>
            <% } %>
        </div>
        <div id="chat-control" >
            <form id="chat-form" action="send-message" method="post">
                <input type="hidden" name="groupId" value="<%= groupId %>">
                <input type="hidden" name="channelId" value="<%= channelId %>">
                <label>
                    <input type="text" id="chat-msg-input" name="messageText" placeholder="Enter your message here...">
                </label>
                <input type="submit"  id="submit-chat-msg" value=">">
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
                <li class="text-white mt-2"><a href="${pageContext.request.contextPath}/login">Login</a></li>
                <li class="text-white mt-2"><a href="${pageContext.request.contextPath}/Profile.jsp">Profile</a></li>
                <li class="text-white mt-2"><a href="${pageContext.request.contextPath}/Groups.jsp">Groups</a></li>
                <li class="text-white mt-2">____________</li>
                <li class="text-white mt-2"><a href="${pageContext.request.contextPath}/Channels.jsp?groupId=9">Example Channel - Test Group 2</a></li>
                <li class="text-white mt-2"><a href="${pageContext.request.contextPath}/Chat.jsp?groupId=9&channelId=14">Example Chat - TG 2 CH 1</a></li>
            </ul>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/chat.js"></script>
<script>
    function scrollChatToBottom() {
        let chatBox = document.getElementById('chat-box');
        chatBox.scrollTop = chatBox.scrollHeight;
    }

    // initial load of messages
    fetchNewMessages(<%= groupId %>, <%= channelId%>, "${pageContext.request.contextPath}");



    // fetch new messages every 10 seconds
    setInterval(function() {
        //count the number of messages
        let msg = document.getElementsByClassName("chat-message");
        console.log("msg length: " + msg.length);
        let msgCount = msg.length;

        fetchNewMessages(<%= groupId %>, <%= channelId%>, "${pageContext.request.contextPath}", msgCount);
    }, 10000);
    $(document).ready(function () {



        scrollChatToBottom();


        // Capture the form submission event
        $("#chat-form").submit(function (event) {
            event.preventDefault(); // Prevent the default form submission

            let context = "${pageContext.request.contextPath}";
            let _url = "";
            if (!(context == null || context === "undefined" || typeof context === "undefined" || context === "")) {
                _url = context + "/send-message";
            } else {
                _url = "/send-message";
            }
            // Handle the form submission with an AJAX request
            $.ajax({
                type: "POST", // or "GET" depending on your requirements
                url: _url,
                data: $("#chat-form").serialize(), // Serialize the form data
                success: function (response) {
                    console.log("AJAX Request Success: " + response);
                    scrollChatToBottom();
                    // Fetch the new messages after the form submission
                    fetchNewMessages(<%= groupId %>, <%= channelId%>, "${pageContext.request.contextPath}");
                    scrollChatToBottom();
                },
                error: function (error) {
                    console.log("AJAX Request Failed: " + response);
                }
            });

            // Clear the input field
            $("#chat-msg-input").val("");
        });
    });
</script>

</body>
</html>