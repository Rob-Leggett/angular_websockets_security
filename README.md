# Angular WebSocket Security (Modernized)

[![CI](https://github.com/Rob-Leggett/angular_websockets_security/actions/workflows/ci.yml/badge.svg)](https://github.com/Rob-Leggett/angular_websockets_security/actions/workflows/ci.yml)
[![Security Scan](https://github.com/Rob-Leggett/angular_websockets_security/actions/workflows/security.yml/badge.svg)](https://github.com/Rob-Leggett/angular_websockets_security/actions/workflows/security.yml)

A modern full-stack application demonstrating **secure WebSocket communication** with **token-based authentication** using Spring Boot 3.4 and Angular 19.

## 🔐 Security Features

This application implements a complete token-based security model for both REST API and WebSocket connections:

### REST API Security
- **JWT Token Authentication** - Stateless, token-based auth
- **X-AUTH-TOKEN Header** - Custom header for token transport
- **BCrypt Password Encoding** - Secure password storage
- **CORS Configuration** - Controlled cross-origin access

### WebSocket Security (CRITICAL)
- **Token Validation on CONNECT** - `WebSocketTokenInterceptor` validates JWT tokens passed in STOMP headers
- **Message-Level Security** - CONNECT, MESSAGE, SUBSCRIBE require authentication
- **SecurityContext Integration** - Authenticated user available in WebSocket handlers
- **User-Specific Subscriptions** - Secure per-user notification channels

## 🏗️ Architecture

```
┌─────────────────┐     HTTP + WebSocket      ┌─────────────────────┐
│                 │  ─────────────────────>   │                     │
│  Angular 19     │     X-AUTH-TOKEN          │  Spring Boot 3.4    │
│  Frontend       │  <─────────────────────   │  Backend            │
│                 │                           │                     │
└─────────────────┘                           └─────────────────────┘
        │                                              │
        │  STOMP over SockJS                          │
        │  X-AUTH-TOKEN in headers                    │
        └──────────────────────────────────────────────┘
```

## 📦 Technology Stack

| Component | Version |
|-----------|---------|
| Java | 21 LTS |
| Spring Boot | 3.4.1 |
| Spring Security | 6.4.x |
| Angular | 19.x |
| Node.js | 20+ LTS |
| JWT (jjwt) | 0.12.6 |
| H2 Database | In-memory |

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Node.js 20+
- Maven 3.9+

### Run the Backend

```bash
cd backend
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Run the Frontend

```bash
cd frontend
npm install
npm start
```

The frontend will start on `http://localhost:4200` with proxy to backend.

### Default Login Credentials

- **Email:** `user@example.com`
- **Password:** `password`

## 🔒 Security Implementation Details

### Authentication Flow

1. Client sends `POST /api/authentication/login` with `Authorization: Basic <base64>` header
2. Server validates credentials against database (BCrypt)
3. Server generates JWT token signed with HMAC-SHA256
4. Server returns token in `X-AUTH-TOKEN` response header
5. Client stores token in sessionStorage
6. All subsequent requests include `X-AUTH-TOKEN` header

### WebSocket Authentication Flow

1. Client connects to `/stomp` endpoint via SockJS
2. STOMP CONNECT frame includes `X-AUTH-TOKEN` in headers
3. `WebSocketTokenInterceptor` extracts and validates token
4. If valid, sets `Authentication` in `SecurityContext`
5. Subscription requests are authorized against configured rules
6. Only authenticated users can subscribe to `/user/notifications`

### Key Security Classes

| Class | Purpose |
|-------|---------|
| `JwtTokenProvider` | Creates and validates JWT tokens |
| `JwtAuthenticationFilter` | Validates tokens on HTTP requests |
| `WebSocketTokenInterceptor` | Validates tokens on WebSocket CONNECT |
| `WebSocketSecurityConfig` | Configures message-level security rules |
| `SecurityConfig` | Main Spring Security configuration |

## 📁 Project Structure

```
├── backend/                     # Spring Boot application
│   ├── src/main/java/au/com/example/
│   │   ├── config/             # Configuration classes
│   │   ├── controller/         # REST and WebSocket controllers
│   │   ├── model/              # JPA entities
│   │   ├── repository/         # Spring Data repositories
│   │   └── security/           # Security components
│   └── src/main/resources/
│       ├── application.yml     # Application configuration
│       └── data.sql            # Initial data
│
├── frontend/                    # Angular application
│   └── src/app/
│       ├── core/
│       │   ├── auth/           # Authentication service & guard
│       │   ├── interceptors/   # HTTP interceptor
│       │   └── websocket/      # WebSocket service
│       └── features/           # Feature components
│
└── (legacy modules)            # Original AngularJS code (deprecated)
```

## 🧪 Testing

### Backend Tests

```bash
cd backend
mvn test
```

### Frontend Build

```bash
cd frontend
npm run build
```

## 📖 API Endpoints

### Authentication
- `POST /api/authentication/login` - Login with Basic Auth
- `POST /api/authentication/logout` - Logout

### User
- `GET /api/user` - Get current user details

### Customers (Protected)
- `GET /api/customers` - List all customers
- `GET /api/customers/search?query=` - Search customers
- `POST /api/customers` - Create customer
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer

### WebSocket
- `CONNECT /stomp` - WebSocket endpoint (SockJS)
- `SUBSCRIBE /user/notifications` - User notifications (requires auth)

## 🔄 Migration from Original

This is a complete rewrite of the original AngularJS + Spring 4 application:

| Original | Modernized |
|----------|-----------|
| AngularJS 1.4 | Angular 19 |
| Spring 4 + Spring Security 4 | Spring Boot 3.4 + Spring Security 6 |
| Gulp 3 | Angular CLI |
| Custom HMAC tokens | JWT (jjwt) |
| WAR deployment | Embedded Tomcat JAR |
| javax.* APIs | jakarta.* APIs |

### Security Preserved
- ✅ Token-based stateless authentication
- ✅ X-AUTH-TOKEN header convention
- ✅ WebSocket token validation on CONNECT
- ✅ Message-level security for STOMP
- ✅ Per-user notification subscriptions

## 📝 License

MIT License
