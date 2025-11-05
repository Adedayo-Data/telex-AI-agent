# telex-AI-agent
Stage 3 tast at HNG internship.

# 🎯 Interview Question Generator Agent

An AI-powered agent that generates customized interview questions for any job role using Google's Gemini AI. Built for integration with the Telex.im platform using the A2A (Agent-to-Agent) protocol.

This AI agent assists users in preparing for job interviews by generating role-specific interview questions. When a user provides a job title (e.g., "Java Developer", "Product Manager"), the agent returns 12 well-structured interview questions organized into three categories:

- 🛠️ **Technical Questions** (4 questions) - Role-specific technical skills and knowledge
- 🤝 **Behavioral Questions** (4 questions) - Soft skills, teamwork, and problem-solving
- 🧠 **Situational Questions** (4 questions) - Real-world scenarios and decision-making


## 🛠️ Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.1.5
- **Build Tool**: Maven
- **AI Service**: Google Gemini API (gemini-1.5-flash-8b)
- **Protocol**: JSON-RPC 2.0 / A2A
- **Deployment**: Render (or Railway/Heroku)

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17 or higher**
  ```bash
  java -version
  ```

- **Maven 3.6 or higher**
  ```bash
  mvn -version
  ```

- **Gemini API Key** (Free tier available)
    - Get yours at: [Google AI Studio](https://aistudio.google.com/app/apikey)

---

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/interview-question-agent.git
cd interview-question-agent
```

### 2. Install Dependencies

```bash
mvn clean install
```

---

## ⚙️ Configuration

### 1. Set Up Environment Variables

Create an `application.properties` file in `src/main/resources/`:

```properties
# Server Configuration
server.port=8080

# Gemini API Key (REQUIRED)
GEMINI-API-KEY=your_actual_gemini_api_key_here

# Logging
logging.level.root=INFO
logging.level.com.hng.telexAgent=DEBUG
```

### 2. Get Your Gemini API Key

1. Visit [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Click "Create API Key"
3. Copy the key (format: `AIzaSyC...`)
4. Replace `your_actual_gemini_api_key_here` in `application.properties`

## 💻 Running Locally

### Start the Application

```bash
mvn spring-boot:run
```

You should see:
```
=================================
✅ Interview Agent is Running!
🌐 Visit: http://localhost:8080/api/agent/health
=================================
```

### Verify It's Running

Open your browser or use curl:
```bash
curl http://localhost:8080/api/agent/health
```

Expected response:
```json
{
  "status": "healthy",
  "agent": "Interview Question Generator",
  "version": "1.0"
}
```

---

## 📚 API Documentation

### Base URL
```
Local: http://localhost:8080
Production: https://your-app.onrender.com
```

---

### 1. Health Check

**Endpoint**: `GET /api/agent/health`

**Response**:
```json
{
  "status": "healthy",
  "agent": "Interview Question Generator",
  "version": "1.0"
}
```

---

### 2. A2A Message Handler (Main Endpoint)

**Endpoint**: `POST /api/agent/message`

**Headers**:
```
Content-Type: application/json
```

**Request Body** (A2A Protocol):
```json
{
  "jsonrpc": "2.0",
  "id": "unique-request-id",
  "method": "message/send",
  "params": {
    "message": {
      "role": "user",
      "parts": [
        {
          "kind": "text",
          "text": "Generate questions for a Java Developer"
        }
      ],
      "messageId": "msg-123"
    },
    "configuration": {
      "blocking": true,
      "acceptedOutputModes": ["text/plain"]
    }
  }
}
```

**Response** (Success):
```json
{
  "jsonrpc": "2.0",
  "id": "unique-request-id",
  "result": {
    "message": {
      "kind": "message",
      "role": "assistant",
      "parts": [
        {
          "kind": "text",
          "text": "🎯 Interview Questions for: Java Developer\n\n🛠️ **Technical Questions**\n1. ..."
        }
      ]
    }
  }
}
```

**Response** (Error):
```json
{
  "jsonrpc": "2.0",
  "id": "unique-request-id",
  "error": {
    "code": -32603,
    "message": "Failed to generate questions: [error details]"
  }
}
```

---

### 3. Supported Job Roles

The agent can generate questions for **any job role**, including:

- Software Engineers (Java, Python, JavaScript, etc.)
- Frontend/Backend Developers
- DevOps Engineers
- Product Managers
- Data Scientists
- Mobile Developers (iOS, Android, Flutter, React Native)
- QA Engineers
- UI/UX Designers
- And many more!

**Example requests**:
- "Get me questions for a Senior Python Developer"
- "Generate interview questions for a Product Manager"
- "Interview questions for a React Developer"

---

## 🌐 Deployment

### Deploy to Render (Recommended - Free)

1. **Create Render Account**: [render.com](https://render.com)

2. **Create New Web Service**:
    - Connect your GitHub repository
    - Select "Java" environment
    - Build command: `./mvnw clean package -DskipTests`
    - Start command: `java -jar target/telex-agent-0.0.1-SNAPSHOT.jar`

3. **Set Environment Variable**:
    - Key: `GEMINI-API-KEY`
    - Value: Your Gemini API key

4. **Deploy**: Click "Create Web Service"

5. **Get Your Public URL**:
   ```
   https://your-app-name.onrender.com
   ```

## 🧪 Testing

### Using cURL

**Test Health Endpoint**:
```bash
curl http://localhost:8080/api/agent/health
```

**Test Question Generation** (Blocking Mode):
```bash
curl -X POST http://localhost:8080/api/agent/message \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": "test-001",
    "method": "message/send",
    "params": {
      "message": {
        "role": "user",
        "parts": [
          {
            "kind": "text",
            "text": "Generate questions for a Backend Developer"
          }
        ],
        "messageId": "msg-test-001"
      },
      "configuration": {
        "blocking": true,
        "acceptedOutputModes": ["text/plain"]
      }
    }
  }'
```

### Using Postman

1. Create a new POST request
2. URL: `http://localhost:8080/api/agent/message`
3. Headers: `Content-Type: application/json`
4. Body: Use the JSON from the cURL example above
5. Click "Send"

---

## 🔗 Telex.im Integration

### Workflow Configuration

Create a workflow in Telex.im with this configuration:

```json
{
  "active": true,
  "category": "utilities",
  "description": "An AI agent that generates custom interview questions.",
  "id": "interview-question-agent-001",
  "name": "Interview Question Generator",
  "nodes": [
    {
      "id": "interview_agent_node",
      "name": "Interview Question Agent",
      "parameters": {
        "blocking": true,
        "timeout": 30000
      },
      "type": "a2a/generic-a2a-node",
      "typeVersion": 1,
      "url": "https://your-app.onrender.com/api/agent/message"
    }
  ],
  "short_description": "Generates interview questions for any job role."
}
```

### Integration Steps

1. **Deploy your agent** and get the public URL
2. **Create workflow** in Telex.im
3. **Set the URL** to your `/api/agent/message` endpoint
4. **Enable blocking mode** (`"blocking": true`)
5. **Test** by sending messages in Telex.im

### Usage in Telex.im

Users can interact with your agent by sending messages like:
- "Get me questions for a Java Developer"
- "Generate interview questions for a Product Manager"
- "Interview questions for React Developer"

---

## 🐛 Troubleshooting

### Issue: 404 Not Found Error from Gemini API

**Error**: `models/gemini-1.5-flash is not found`

**Solution**: Update the model name in `GeminiService.java`:
```java
private final String geminiApiUrl =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-8b:generateContent?key=";
```

---

### Issue: Empty Response or "Please provide a job role"

**Cause**: Text extraction not handling nested data arrays

**Solution**: Use the updated `extractLatestUserMessage()` method that searches both top-level parts and nested data arrays.

---

### Issue: Timeout Errors in Telex.im

**Symptoms**: "Error while streaming" in logs

**Cause**: Using blocking mode but taking too long, or non-blocking mode without webhook implementation

**Solution**:
- If `blocking: true` - Increase timeout to 60000ms
- If `blocking: false` - Implement webhook response pattern

---

### Issue: API Key Invalid

**Error**: 401 Unauthorized or 403 Forbidden

**Solution**:
1. Verify API key is correct
2. Check for extra spaces or line breaks
3. Regenerate key at [Google AI Studio](https://aistudio.google.com/app/apikey)
4. Ensure environment variable is set correctly

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👥 Authors

- **Your Name** - Adedayo Theophilus Adedeji (https://github.com/Adedayo-Data)

---

## 🔮 Future Enhancements

Potential features for future versions:

- [ ] Support for multiple languages
- [ ] Question difficulty levels (Junior, Mid, Senior)
- [ ] Save generated questions to database
- [ ] Rate limiting and caching
- [ ] Answer evaluation feature
- [ ] Company-specific questions
- [ ] Integration with job posting APIs

---
