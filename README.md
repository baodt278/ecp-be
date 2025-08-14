# ECP Backend - Electric Company Portal

A comprehensive backend system for managing electric power distribution companies, built with Spring Boot and Java.

## 🔍 Overview

ECP (Electric Company Portal) is a multi-tenant backend system designed to manage electric utility companies, their employees, clients, contracts, billing, and power consumption records. The system provides role-based access control for administrators, company managers, staff, and clients.

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.2.3
- **Language**: Java 17
- **Database**: MariaDB (with JPA/Hibernate)
- **Security**: Spring Security with JWT authentication
- **File Storage**: MinIO (Object Storage)
- **Build Tool**: Maven
- **Additional Libraries**:
  - Thumbnailator (Image processing)
  - Apache Commons Lang3
  - JJWT (JWT tokens)
  - Spring Boot Mail

## 🏗 Architecture

### User Roles
- **ADMIN**: System administrators with full access
- **MANAGER**: Company managers overseeing operations
- **STAFF**: Company staff handling day-to-day operations
- **CLIENT**: End users/customers

### Core Entities
- **Companies**: Electric utility companies
- **Users**: Admin, Employee, Client hierarchical structure
- **Contracts**: Service agreements between companies and clients
- **Bills**: Monthly electricity bills with consumption data
- **Records**: Daily electricity consumption readings
- **Requests**: Service requests and support tickets
- **News**: Company announcements and system news

## 🚀 Features

### User Management
- Multi-role authentication system
- JWT-based security
- Password reset via email
- Avatar upload and management

### Company Operations
- Multi-company support
- Employee management by role
- Contract lifecycle management
- Service request processing workflow

### Billing & Consumption
- Automated bill generation
- Multi-tier pricing structure (Family, Business, Production, etc.)
- Voltage-based pricing (Below 6kV, 6kV-22kV, 22kV-110kV, Above 110kV)
- Consumption analytics and predictions
- Payment tracking

### Request Management
- Client verification requests
- Contract modification requests
- Emergency and maintenance requests
- Multi-stage approval workflow (Pending → Reviewed → Approved/Rejected)

### Analytics & Reporting
- Consumption trend analysis
- Revenue analytics by company
- Predictive consumption modeling
- Monthly and yearly reporting

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MariaDB 10.5+
- MinIO Server (for file storage)
- SMTP Server (for email notifications)

## ⚙️ Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/baodt278/ecp-be.git
cd ecp-be
```

### 2. Database Setup
Create a MariaDB database and update connection details in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/ecp_db
    username: your_username
    password: your_password
```

### 3. MinIO Configuration
Set up MinIO server and configure in `application.yml`:

```yaml
minio:
  url: http://localhost:9000
  access:
    key: your_access_key
    secret: your_secret_key
```

### 4. Email Configuration
Configure SMTP settings for password reset and notifications:

```yaml
spring:
  mail:
    host: your_smtp_host
    port: 587
    username: your_email
    password: your_password
```

### 5. Build and Run
```bash
# Using Maven Wrapper (recommended)
./mvnw clean install
./mvnw spring-boot:run

# Or using Maven directly
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Authentication Endpoints
```
POST /api/admin-login      - Admin authentication
POST /api/client-login     - Client authentication  
POST /api/employee-login   - Employee authentication
POST /api/client-register  - Client registration
POST /api/forgot-password  - Password reset
```

### Admin Endpoints
```
GET  /api/admin/companies       - List all companies
POST /api/admin/create-company  - Create new company
GET  /api/admin/employees       - List company employees
POST /api/admin/create-employee - Create new employee
GET  /api/admin/requests        - View all requests
POST /api/admin/verify-client   - Verify client accounts
```

### Client Endpoints
```
GET  /api/client/info           - Get client profile
POST /api/client/update-info    - Update profile
GET  /api/client/contracts      - View contracts
GET  /api/client/bills          - View bills
POST /api/client/create-request - Submit service request
GET  /api/client/news/system    - System announcements
```

### Employee Endpoints
```
GET  /api/employee/contracts    - View company contracts
POST /api/employee/staff/create-record - Add consumption reading
GET  /api/employee/manager/analyst - View analytics
POST /api/employee/manager/accept-request - Approve requests
```

## 🗃 Database Schema

### Key Tables
- `user` (base table for all user types)
- `admin`, `employee`, `client` (user type tables)
- `company` - Electric utility companies
- `contract` - Service contracts
- `bill` - Monthly bills
- `record` - Daily consumption readings
- `request` - Service requests
- `price` - Pricing tiers
- `news` - Announcements

### Enum Types
- `Role`: ADMIN, MANAGER, STAFF, CLIENT
- `ContractType`: FAMILY, BUSINESS, PRODUCE, PUBLIC_GOV, EDU_MEDIC, COMPLEX
- `BillStatus`: PAID, UNPAID, EXPIRED, PENDING
- `RequestStatus`: PENDING, REVIEWED, APPROVED, REJECTED

## 🔐 Security

- JWT-based authentication with 90-day expiration
- Role-based access control (RBAC)
- Password encryption using BCrypt
- CORS configuration for frontend integration
- Request validation and error handling

## 📧 Email Integration

- Welcome emails for new registrations
- Password reset notifications
- Bill payment reminders
- Service request updates

## 📊 File Management

- MinIO integration for secure file storage
- Avatar uploads for user profiles
- Document attachments for service requests
- Image compression using Thumbnailator

## 🧪 Testing

```bash
# Run tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=EcpApplicationTests
```

## 🚀 Deployment

### Production Configuration
1. Update database configuration for production
2. Configure production MinIO instance
3. Set up production SMTP server
4. Update JWT secret key
5. Configure proper CORS origins

### Docker Deployment (Optional)
```bash
# Build JAR
./mvnw clean package

# Create Dockerfile and build image
docker build -t ecp-backend .

# Run container
docker run -p 8080:8080 ecp-backend
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the GitHub repository
- Contact: doantuanbao2708@gmail.com

## 🏷 Version History

- **v0.0.1-SNAPSHOT**: Initial release with core functionality
  - Multi-tenant company management
  - User authentication and authorization
  - Contract and billing management
  - Service request workflow
  - Basic analytics and reporting

---

**Built with ❤️ for efficient electric utility management**
