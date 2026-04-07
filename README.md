# Real Time Chat Application — Backend

A real-time chat backend built with Spring Boot 3, WebSocket (STOMP/SockJS), JWT-based authentication, and PostgreSQL.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.4.0 |
| Language | Java 17 |
| Real-Time | WebSocket + STOMP (SockJS) |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Mapping | ModelMapper 3.2.2 |
| Validation | Jakarta Bean Validation |
| API Docs | Springdoc OpenAPI 2.8.3 (Swagger UI) |
| Build Tool | Maven |
| Utilities | Lombok |

---

## Project Structure

```
src/main/java/com/chat_application/
├── RealTimeChatApplication.java
├── AuthFilter/
│   ├── JwtAuthFilter.java
│   └── WebSecurityConfig.java
├── WebSocket/
│   └── WebSocketConfig.java
├── config/
│   └── MapperConfig.java
├── advices/
│   ├── ApiError.java
│   ├── ApiResponse.java
│   ├── GlobalExceptionHandler.java
│   └── GlobalResponseHandler.java
├── controllers/
│   ├── AttachmentController.java
│   ├── ChatController.java
│   ├── ChatParticipantController.java
│   ├── FriendshipController.java
│   ├── InvitationController.java
│   ├── MessageController.java
│   ├── MessageReadController.java
│   ├── NotificationController.java
│   └── UserController.java
├── services/                  # Interface + Impl for each domain
├── repositories/
├── entity/
│   └── enums/
├── dto/
├── exception/
│   ├── ResourceNotFoundException.java
│   └── UnAuthorisedException.java
└── util/
    └── AppUtils.java
```

---

## Configuration

`src/main/resources/application.properties`

```properties
spring.application.name=Realtime-Chat
server.port=8090

spring.datasource.url=jdbc:postgresql://localhost:5432/ChatDb
spring.datasource.username=postgres
spring.datasource.password=<your_password>

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secretKey=<your_secret_key>
```

> **Note:** Do not commit real credentials or the JWT secret to version control.

---

## Getting Started

### Prerequisites

- Java 17+
- Maven
- PostgreSQL

### Steps

1. Create the database:
   ```sql
   CREATE DATABASE ChatDb;
   ```

2. Update `application.properties` with your database credentials and a JWT secret key.

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

Server starts on **http://localhost:8090**.

---

## API Endpoints

All endpoints are under `/api/v1/users`. Endpoints marked 🔓 are public (no auth required).

### User / Auth

| Method | Endpoint | Description |
|---|---|---|
| POST 🔓 | `/signup` | Register a new user |
| POST 🔓 | `/login` | Login — returns access token; sets refresh token as HttpOnly cookie |
| POST | `/logout` | Clears the refresh token cookie |
| POST 🔓 | `/refresh` | Get a new access token using the refresh token cookie |
| PUT | `/update` | Update user profile |
| DELETE | `/delete` | Delete account |
| PATCH | `/deactivate` | Deactivate account |
| PATCH | `/activate` | Activate account |
| PATCH | `/updateOnlineStatus` | Update online status |
| GET | `/getLastSeen` | Get last-seen timestamp |
| PATCH | `/updateLastSeen` | Update last-seen timestamp |
| GET | `/searchUser/{name}` | Search users by name |
| PATCH | `/blockUser/{friendId}` | Block a user |
| PATCH | `/unblockUser/{friendId}` | Unblock a user |
| GET | `/getAllUsers` | Get all users |

### Friendships

| Method | Endpoint | Description |
|---|---|---|
| GET | `/getAllFriends` | Get friends list |
| GET | `/getAllPendingRequests` | Get incoming pending friend requests |
| POST | `/sendFriendRequest/{friendId}` | Send a friend request |
| PATCH | `/acceptFriendRequest/{requestedId}` | Accept a friend request |
| DELETE | `/removeFriend/{friendId}` | Remove a friend |
| PATCH | `/cancelRequest/{requesterId}` | Cancel a sent friend request |
| GET | `/getFriendshipStatus/{friendId}` | Get friendship status with a user |
| GET | `/getAllBlockedUsers` | Get list of blocked users |
| GET | `/getFriendRequestSentUsers` | Get users you have sent requests to |

### Chats

| Method | Endpoint | Description |
|---|---|---|
| POST | `/createChat` | Create a chat |
| GET | `/getChatByName/{chatName}` | Search chats by name |
| GET | `/getAllChats/{page}/{size}` | Get paginated list of chats |
| DELETE | `/deleteChat/{chatId}` | Delete a chat |
| DELETE | `/leaveChat/{chatId}` | Leave a chat |

### Chat Participants

| Method | Endpoint | Description |
|---|---|---|
| PATCH | `/addParticipant/{chatId}/{userId}` | Add a participant to a chat |
| DELETE | `/removeParticipant/{chatId}/{userId}` | Remove a participant from a chat |
| GET | `/getAllParticipants/{chatId}` | Get all participants in a chat |
| GET | `/isUserParticipant/{chatId}/{userId}` | Check if a user is a participant |
| GET | `/isUserAdmin/{chatId}/{userId}` | Check if a user is an admin |
| PATCH | `/updateParticipantRole/{chatId}/{userId}` | Update a participant's role |

### Messages

| Method | Endpoint | Description |
|---|---|---|
| POST | `/createMessage` | Send a message |
| GET | `/getChat/{chatId}/{page}/{size}` | Get paginated messages for a chat |
| DELETE | `/deleteForMe/{messageId}` | Delete a message for yourself |
| DELETE | `/deleteForEveryone/{messageId}` | Delete a message for all participants |
| DELETE | `/deleteAll/{chatId}` | Delete all messages in a chat |
| PATCH | `/markMessageDelivered/{messageId}` | Mark a message as delivered |

### Message Read Receipts

| Method | Endpoint | Description |
|---|---|---|
| PATCH | `/markAsRead/{messageId}` | Mark a message as read |
| GET | `/getUnreadCount/{messageId}` | Get unread count for a message |
| GET | `/getReadByMessage/{messageId}` | Get list of users who read a message |
| PATCH | `/markAllAsReadInChat/{chatId}` | Mark all messages in a chat as read |
| GET | `/hasUserReadMessage/{messageId}/{userId}` | Check if a user has read a message |

### Attachments

| Method | Endpoint | Description |
|---|---|---|
| POST | `/addAttachment/{messageId}` | Add an attachment to a message |
| DELETE | `/removeAttachment/{attachmentId}` | Remove an attachment |
| GET | `/getAttachmentById/{attachmentId}` | Get an attachment by ID |
| GET | `/getAllAttachmentsForMessage/{messageId}` | Get all attachments for a message |

### Invitations

| Method | Endpoint | Description |
|---|---|---|
| POST | `/sendInvitation/{receiverId}/{chatId}` | Send a group invite to a user |
| PATCH | `/acceptInvitation/{invitationId}` | Accept an invitation |
| PATCH | `/rejectInvitation/{invitationId}` | Reject an invitation |

### Notifications

| Method | Endpoint | Description |
|---|---|---|
| POST | `/createNotification/` | Create a notification |
| GET | `/getNotifications/{page}/{size}` | Get paginated notifications |
| PATCH | `/markAsRead/{notificationId}` | Mark a notification as read |
| GET | `/getUnreadNotificationsCount` | Get unread notification count |
| DELETE | `/deleteNotification/{notificationId}` | Delete a notification |
| DELETE | `/deleteAllNotification` | Delete all notifications |

---

## WebSocket

STOMP endpoint (via SockJS):
```
/ws-chat
```

| Prefix | Purpose |
|---|---|
| `/app` | Application destination prefix (`setApplicationDestinationPrefixes`) |
| `/topic` | Simple broker destination (`enableSimpleBroker`) |
| `/queue` | Simple broker destination (`enableSimpleBroker`) |
| `/user` | User destination prefix (`setUserDestinationPrefix`) |

---

## Security

- Stateless sessions (`SessionCreationPolicy.STATELESS`)
- JWT access token via `Authorization` header
- Refresh token stored as an HttpOnly cookie
- BCrypt password encoding
- Public paths: `/api/v1/users/signup`, `/api/v1/users/login`, `/api/v1/users/refresh`, `/ws-chat/**`, `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`

---

## API Documentation

Swagger UI: `http://localhost:8090/swagger-ui.html`

OpenAPI spec: `http://localhost:8090/v3/api-docs`

---

## Enums

| Enum | Values |
|---|---|
| `ChatType` | `ONE_TO_ONE`, `GROUP` |
| `ChatRole` | `ADMIN`, `MEMBER` |
| `FriendStatus` | `ACCEPTED`, `BLOCKED`, `PENDING` |
| `InvitationStatus` | `PENDING`, `ACCEPTED`, `REJECTED` |
| `MessageType` | `TEXT`, `VOICE`, `IMAGE`, `VIDEO`, `PDF` |
| `MessageStatus` | `VISIBLE`, `DELETE_FOR_ME` |
| `MessageSendStatus` | `PENDING`, `DELIVERED`, `SENT`, `FAILED` |
| `NotificationType` | `NEW_MESSAGE`, `FRIEND_REQUEST`, `GROUP_INVITE` |
| `OnlineStatus` | `ONLINE`, `OFFLINE` |
| `UserRole` | `ADMIN`, `GENERAL` |
| `UserStatus` | `BLOCKED`, `ACTIVE`, `DELETED` |
| `AttachmentType` | `IMAGE`, `VIDEO`, `PDF` |
