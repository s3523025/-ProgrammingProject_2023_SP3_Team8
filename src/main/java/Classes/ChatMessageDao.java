package Classes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatMessageDao {
	
	private final Database database;
	
	public ChatMessageDao() {
		database = new Database();
	}
	
	public void saveGroup(Group group, int creatorID) throws SQLException {
		Connection connection = database.getConnection();
		try {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO GroupDB (GroupName) VALUES (?)");
			PreparedStatement statementCreator = connection.prepareStatement("INSERT INTO GroupMembershipDB (GroupID, GroupUserID, GroupRole) VALUES (?,?,?)");
			statement.setString(1, group.getName());
			statement.executeUpdate();
			statementCreator.setInt(1, database.getGroupID(group.getName()));
			statementCreator.setInt(2, creatorID);
			statementCreator.setInt(3, 1);
			statementCreator.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			database.closeConnection(connection);
		}
	}
	
	public List<Group> getGroups(int userID) throws SQLException {
		Connection connection = database.getConnection();
		try {
			// Step 1: Execute the initial query to get group IDs associated with the userID
			PreparedStatement initialQuery = connection.prepareStatement("SELECT GroupID FROM GroupMembershipDB WHERE GroupUserID = ?");
			initialQuery.setInt(1, userID);
			ResultSet initialResult = initialQuery.executeQuery();
			List<Integer> groupIDs = new ArrayList<>();
			
			while (initialResult.next()) {
				groupIDs.add(initialResult.getInt("GroupID"));
			}
			
			// Step 2: Execute a query for each group ID to get group information
			List<Group> groups = new ArrayList<>();
			for (int groupID : groupIDs) {
				PreparedStatement groupQuery = connection.prepareStatement("SELECT * FROM GroupDB WHERE GroupID = ?");
				groupQuery.setInt(1, groupID);
				ResultSet groupResult = groupQuery.executeQuery();
				
				if (groupResult.next()) {
					Group group = new Group();
					group.setId(groupResult.getInt("GroupID"));
					group.setName(groupResult.getString("GroupName"));
					groups.add(group);
				}
			}
			
			return groups;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			database.closeConnection(connection);
		}
	}
	public List<Group> getPrivateGroups(int userID) throws SQLException {
		Connection connection = database.getConnection();
		try {
			// Step 1: Execute the initial query to get group IDs associated with the userID
			PreparedStatement initialQuery = connection.prepareStatement("SELECT PrivateGroupID FROM PrivateGroupMembershipDB WHERE PrivateGroupUserID = ?");
			initialQuery.setInt(1, userID);
			ResultSet initialResult = initialQuery.executeQuery();
			List<Integer> groupIDs = new ArrayList<>();

			while (initialResult.next()) {
				groupIDs.add(initialResult.getInt("PrivateGroupID"));
			}

			// Step 2: Execute a query for each group ID to get group information
			List<Group> groups = new ArrayList<>();
			for (int groupID : groupIDs) {
				PreparedStatement groupQuery = connection.prepareStatement("SELECT * FROM PrivateGroupDB WHERE PrivateGroupID = ?");
				groupQuery.setInt(1, groupID);
				ResultSet groupResult = groupQuery.executeQuery();

				if (groupResult.next()) {
					Group group = new Group();
					group.setId(groupResult.getInt("PrivateGroupID"));
					group.setName(groupResult.getString("PrivateGroupName"));
					groups.add(group);
				}
			}

			return groups;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			database.closeConnection(connection);
		}
	}
	
	public void saveMessage(Message message) throws SQLException {
		Connection connection = database.getConnection();
		try {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO ChatMessageDB (groupID, channelID, senderID, recipientID, messageText) VALUES (?, ?, ?, ?, ?)");
			statement.setInt(1, message.getGroupId());
			statement.setInt(2, message.getChannelId());
			statement.setInt(3, message.getSenderId());
			statement.setInt(4, message.getRecipientId());
			statement.setString(5, message.getMessageText());
			//statement.setTimestamp(5, new Timestamp(message.getCreatedAt().getTimestamp()));
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			database.closeConnection(connection);
		}
	}
	
	public List<Message> getMessages(int groupId, int channelId) throws SQLException {
		Connection connection = database.getConnection();
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		try {
			// SQL Join to return the username of the sender
			statement = connection.prepareStatement("SELECT u.Username, c.* FROM ChatMessageDB AS c JOIN UserDB AS u ON u.UserId = c.senderId WHERE groupID = ? AND ChannelID = ?");
			//statement = connection.prepareStatement("SELECT * FROM ChatMessageDB WHERE groupID = ? AND ChannelID = ?");
			statement.setInt(1, groupId);
			statement.setInt(2, channelId);
			resultSet = statement.executeQuery();
			List<Message> messages = new ArrayList<>();
			while (resultSet.next()) {
				Message message = new Message();
				message.setId(resultSet.getInt("messageID"));
				message.setGroupId(resultSet.getInt("groupID"));
				message.setChannelId(resultSet.getInt("ChannelID"));
				message.setSenderId(resultSet.getInt("senderID"));
				message.setSenderName(resultSet.getString("Username"));
				message.setRecipientId(resultSet.getInt("recipientID"));
				message.setMessageText(resultSet.getString("messageText"));
				message.setCreatedAt(resultSet.getTimestamp("createdDate"));
				messages.add(message);
			}
			return messages;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			database.closeConnection(connection);
		}
	}

	public String getChatTitle(int groupId, int channelId) throws SQLException {
		Connection connection = database.getConnection();
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("SELECT ChannelName FROM ChannelDB WHERE groupID = ? AND ChannelID = ?");
			statement.setInt(1, groupId);
			statement.setInt(2, channelId);
			resultSet = statement.executeQuery();
			String chatTitle = "";
			while (resultSet.next()) {
				chatTitle = resultSet.getString("ChannelName");
			}
			return chatTitle;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			database.closeConnection(connection);
		}

	}
	
	public List<Channel> getChannels(int groupId) throws SQLException {
		Connection connection = database.getConnection();
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM ChannelDB WHERE GroupID = ?");
			statement.setInt(1, groupId);
			ResultSet resultSet = statement.executeQuery();
			List<Channel> channels = new ArrayList<>();
			while (resultSet.next()) {
				Channel channel = new Channel();
				channel.setId(resultSet.getInt("channelID"));
				channel.setGroupId(resultSet.getInt("groupID"));
				channel.setChannelName(resultSet.getString("channelName"));
				channels.add(channel);
			}
			return channels;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			database.closeConnection(connection);
		}
	}
	

	public void saveChannel(Channel channel, int creatorID) throws SQLException {
		Connection connection = database.getConnection();
		
		try {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO ChannelDB (ChannelName, GroupID) VALUES (?, ?)");
			PreparedStatement statementCreator = connection.prepareStatement
					("INSERT INTO ChannelMembershipDB (ChannelID, UserID, ChannelRole) VALUES (?, ?, ?)");
			statement.setString(1, channel.getChannelName());
			statement.setInt(2, channel.getGroupId());
			statement.executeUpdate();
			statementCreator.setInt(1, database.getChannelID(channel.getChannelName()));
			statementCreator.setInt(2, creatorID);
			statementCreator.setInt(3, 1);
			statementCreator.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			database.closeConnection(connection);
		}
	}

	//Check session data to confirm f current user is a admin for the channel
	public boolean isChannelAdmin(int channelID, int userID) throws SQLException {
		Connection connection = database.getConnection();
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT ChannelRole FROM ChannelMembershipDB WHERE ChannelID = ? AND UserID = ?");
			statement.setInt(1, channelID);
			statement.setInt(2, userID);
			ResultSet resultSet = statement.executeQuery();
			int role = 0;
			while (resultSet.next()) {
				role = resultSet.getInt("ChannelRole");
			}
            return role == 1;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			database.closeConnection(connection);
		}
	}
	public boolean isGroupAdmin(int groupID, int userID) throws SQLException {
		Connection connection = database.getConnection();
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT GroupRole FROM GroupMembershipDB WHERE GroupID = ? AND GroupUserID = ?");
			statement.setInt(1, groupID);
			statement.setInt(2, userID);
			ResultSet resultSet = statement.executeQuery();
			int role = 0;
			while (resultSet.next()) {
				role = resultSet.getInt("GroupRole");
			}
            return role == 1;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			database.closeConnection(connection);
		}
	}

	public void savePrivateGroup(Group group, int creatorID, int userID) throws SQLException {
		Connection connection = database.getConnection();
		try {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO PrivateGroupDB (PrivateGroupName) VALUES (?)");
			PreparedStatement statementCreator = connection.prepareStatement("INSERT INTO PrivateGroupMembershipDB (PrivateGroupID, PrivateGroupUserID, PrivateGroupUserID2) VALUES (?,?,?)");

			statement.setString(1, group.getName());
			statement.executeUpdate();
			statementCreator.setInt(1, database.getPrivateGroupID(group.getName()));
			statementCreator.setInt(2, creatorID);
			statementCreator.setInt(3, userID);
			statementCreator.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			database.closeConnection(connection);
		}
	}

	public List<Message> getPrivateMessages(int privateGroupId) throws SQLException {
		Connection connection = database.getConnection();
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		try {
			// SQL Join to return the username of the sender
			statement = connection.prepareStatement("SELECT u.Username, c.* FROM PrivateChatMessageDB AS c JOIN UserDB AS u ON u.UserId = c.senderId WHERE PrivateGroupID = ?");
			//statement = connection.prepareStatement("SELECT * FROM ChatMessageDB WHERE groupID = ? AND ChannelID = ?");
			statement.setInt(1, privateGroupId);
			resultSet = statement.executeQuery();
			List<Message> messages = new ArrayList<>();
			while (resultSet.next()) {
				Message message = new Message();
				message.setId(resultSet.getInt("PrivateMessageID"));
				message.setGroupId(resultSet.getInt("PrivateGroupID"));
				message.setSenderId(resultSet.getInt("senderID"));
				message.setSenderName(resultSet.getString("Username"));
				message.setMessageText(resultSet.getString("messageText"));
				message.setCreatedAt(resultSet.getTimestamp("createdDate"));
				messages.add(message);
			}
			return messages;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			database.closeConnection(connection);
		}
	}

	public void savePrivateMessage(PrivateMessage message) throws SQLException {
		Connection connection = database.getConnection();
		try {

			PreparedStatement statement = connection.prepareStatement("INSERT INTO PrivateChatMessageDB (PrivateGroupID, senderID, messageText) VALUES (?, ?, ?)");
			statement.setInt(1, message.getGroupId());
			statement.setInt(2, message.getSenderId());
			statement.setString(3, message.getMessageText());
			//statement.setTimestamp(5, new Timestamp(message.getCreatedAt().getTimestamp()));
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			database.closeConnection(connection);
		}
	}


    public boolean isFriend(int currentUserID, int friendUserId) throws SQLException {
		Connection connection = database.getConnection();
		//check friend table for a row with both user ids
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM FriendDB WHERE UserID = ? AND FriendUserID = ?");
			statement.setInt(1, currentUserID);
			statement.setInt(2, friendUserId);
			ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
		} catch (SQLException e){
			throw new RuntimeException(e);
		} finally {
			database.closeConnection(connection);
		}
	}
}
