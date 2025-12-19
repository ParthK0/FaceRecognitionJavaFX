# Facial Recognition Based Smart Attendance System

A professional-grade academic project that implements an automated attendance system using facial recognition technology, built with Java, OpenCV, MySQL, and Maven.

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Module Details](#module-details)

## 🎯 Overview

This system simulates a real-world institutional attendance solution that uses facial recognition to automatically mark student attendance. The system captures student facial data during registration, trains a machine learning model, and then uses real-time face recognition to identify students and mark their attendance in a MySQL database.

## ✨ Features

### Student Management Module
- Register students with complete academic profiles
  - Auto-generated student ID
  - Full name, admission number, roll number
  - Course enrollment, semester, academic year
  - Contact information (email, phone)
- Search and view student details
- Update student information
- Track facial data status

### Facial Data Capture Module
- Real-time camera integration using OpenCV
- Automated face detection
- Capture multiple facial images per student
- Progress tracking during capture
- Quality validation

### Face Recognition Module
- Live video processing
- Real-time face detection and recognition
- Confidence threshold-based identification
- Visual feedback (colored bounding boxes)
- Cooldown period to prevent duplicate entries

### Attendance Management Module
- Automatic attendance marking upon recognition
- Duplicate prevention (same date + session)
- Multiple session types (Morning, Afternoon, Evening, Full Day)
- Attendance status tracking (Present, Absent, Late, Excused)
- Comprehensive attendance reports

### Database Features
- Relational database design with foreign key constraints
- Three main tables: students, courses, attendance
- Data integrity and validation
- Indexed queries for performance
- Sample course data included

## 🛠 Technology Stack

- **Programming Language**: Java 21 (LTS)
- **Computer Vision**: OpenCV 4.9.0 (via JavaCV/Bytedeco)
- **Database**: MySQL 8.0+
- **Build Tool**: Maven 3.9+
- **JDBC**: MySQL Connector/J 8.3.0
- **Face Recognition**: LBPH (Local Binary Patterns Histograms)

## 🏗 System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  (Console-based Menu System - AttendanceSystem.java)        │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────┴──────────────────────────────────────┐
│                    Service Layer                             │
│  - StudentService                                            │
│  - AttendanceService                                         │
│  - FacialDataCaptureService                                  │
│  - FaceRecognitionAttendanceService                          │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────┴──────────────────────────────────────┐
│                    Data Access Layer (DAO)                   │
│  - StudentDAO                                                │
│  - CourseDAO                                                 │
│  - AttendanceDAO                                             │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────┴──────────────────────────────────────┐
│                    Database Layer                            │
│  MySQL Database (attendance_system)                          │
│  - students table                                            │
│  - courses table                                             │
│  - attendance table                                          │
└─────────────────────────────────────────────────────────────┘
```

## 📦 Installation

### Prerequisites
1. **JDK 21** (LTS) - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)
2. **Maven 3.9+** - Download from [Apache Maven](https://maven.apache.org/download.cgi)
3. **MySQL 8.0+** - Download from [MySQL](https://dev.mysql.com/downloads/mysql/)
4. **Webcam** - Required for facial data capture and recognition

### Steps

1. **Clone or download the project**
   ```bash
   git clone <repository-url>
   cd FaceRecognitionJava/facerecognitionjava
   ```

2. **Install Maven dependencies**
   ```bash
   mvn clean install
   ```

3. **Configure database connection**
   - Edit `db.properties` file in the project root:
   ```properties
   db.host=localhost
   db.port=3306
   db.database=attendance_system
   db.username=root
   db.password=your_password
   db.useSSL=false
   db.autoReconnect=true
   ```

## 💾 Database Setup

### Automatic Setup (Recommended)
The system automatically creates the database and tables on first run. Simply:
1. Ensure MySQL server is running
2. Run the application
3. Database will be initialized automatically

### Manual Setup (Optional)
If you prefer manual setup:
```bash
mysql -u root -p < database_schema.sql
```

### Database Schema

**courses table**
- course_id (PK, AUTO_INCREMENT)
- course_code (UNIQUE)
- course_name
- department
- credits
- timestamps

**students table**
- student_id (PK, AUTO_INCREMENT)
- admission_number (UNIQUE)
- roll_number
- full_name
- course_id (FK → courses)
- semester
- academic_year
- email, phone
- facial_data_path
- is_active
- timestamps

**attendance table**
- attendance_id (PK, AUTO_INCREMENT)
- student_id (FK → students)
- course_id (FK → courses)
- attendance_date
- attendance_time
- session_type (ENUM: Morning, Afternoon, Evening, Full Day)
- status (ENUM: Present, Absent, Late, Excused)
- marked_by
- remarks
- created_at
- UNIQUE constraint on (student_id, course_id, date, session_type)

## 🚀 Usage

### Running the Application

```bash
mvn exec:java -Dexec.mainClass="com.myapp.AttendanceSystem"
```

Or compile and run:
```bash
mvn clean package
java -cp target/classes:target/dependency/* com.myapp.AttendanceSystem
```

### Workflow

1. **Initial Setup**
   - Start the application
   - Database will be initialized automatically
   - Sample courses are created

2. **Register Students**
   - Select "Student Management" → "Register New Student"
   - Enter student details
   - System creates dataset directory

3. **Capture Facial Data**
   - Select "Facial Data Capture"
   - Enter student ID or admission number
   - System captures 50 images (default)
   - Images saved to `dataset/<student_name>/`

4. **Train Model**
   - Select "Train Face Recognition Model"
   - System processes all facial data
   - Creates trained model file: `trainer/multi.yml`

5. **Mark Attendance**
   - Select "Mark Attendance (Face Recognition)"
   - Choose course and session type
   - System starts camera
   - Recognized students are automatically marked present
   - Duplicate prevention is automatic

6. **View Reports**
   - Today's attendance summary
   - Student-wise attendance history
   - Course-wise attendance report
   - Date-wise attendance

## 📁 Project Structure

```
facerecognitionjava/
├── src/
│   └── main/
│       ├── java/com/myapp/
│       │   ├── model/              # Entity classes
│       │   │   ├── Student.java
│       │   │   ├── Course.java
│       │   │   └── Attendance.java
│       │   ├── dao/                # Data Access Objects
│       │   │   ├── StudentDAO.java
│       │   │   ├── CourseDAO.java
│       │   │   └── AttendanceDAO.java
│       │   ├── service/            # Business Logic
│       │   │   ├── StudentService.java
│       │   │   ├── AttendanceService.java
│       │   │   ├── FacialDataCaptureService.java
│       │   │   └── FaceRecognitionAttendanceService.java
│       │   ├── config/             # Configuration
│       │   │   └── DatabaseConfig.java
│       │   ├── util/               # Utilities
│       │   │   ├── DatabaseConnection.java
│       │   │   └── Util.java
│       │   ├── AttendanceSystem.java    # Main application
│       │   ├── TrainerMulti.java        # Model training
│       │   └── (other legacy files)
│       └── resources/
│           └── haarcascade_frontalface_default.xml
├── dataset/                # Facial images storage
├── trainer/               # Trained models
├── pom.xml               # Maven configuration
├── database_schema.sql   # Database schema
├── db.properties         # Database configuration
└── README.md            # This file
```

## 📚 Module Details

### Student Management Module
**File**: `StudentService.java`
- CRUD operations for students
- Validation and business rules
- Facial data path management
- Integration with database

### Facial Data Capture Module
**File**: `FacialDataCaptureService.java`
- OpenCV camera integration
- Haar Cascade face detection
- Image capture with progress tracking
- Quality validation

### Face Recognition Module
**File**: `FaceRecognitionAttendanceService.java`
- LBPH face recognizer
- Real-time video processing
- Confidence-based matching
- Visual feedback system

### Attendance Management Module
**File**: `AttendanceService.java`
- Attendance marking with validations
- Duplicate prevention
- Session management
- Report generation

## 🔧 Configuration

### Database Configuration
Edit `db.properties`:
```properties
db.host=localhost          # MySQL server host
db.port=3306              # MySQL server port
db.database=attendance_system  # Database name
db.username=root          # Database username
db.password=              # Database password
```

### Recognition Parameters
In `FaceRecognitionAttendanceService.java`:
```java
CONFIDENCE_THRESHOLD = 80.0  // Lower = stricter matching
RECOGNITION_COOLDOWN = 5000  // ms between recognitions
```

### Capture Parameters
In `FacialDataCaptureService.java`:
```java
DEFAULT_IMAGE_COUNT = 50     // Images per student
```

## 📊 Sample Usage Scenarios

### Scenario 1: New Semester Setup
1. Add new courses (if needed)
2. Register all students
3. Capture facial data for each student
4. Train the model once
5. Start daily attendance

### Scenario 2: Daily Attendance
1. Start application
2. Select "Mark Attendance"
3. Choose course and session
4. Students look at camera
5. Attendance marked automatically
6. View reports at end of day

## 🐛 Troubleshooting

### Camera not opening
- Check camera permissions
- Ensure no other application is using the camera
- Try changing camera index in code (0, 1, 2, etc.)

### Database connection error
- Verify MySQL is running
- Check credentials in `db.properties`
- Ensure database exists

### Recognition not working
- Train model after adding new students
- Ensure good lighting conditions
- Check confidence threshold settings

### Low recognition accuracy
- Capture more images (increase count)
- Improve lighting conditions
- Ensure face is clearly visible
- Retrain the model

## 📝 Notes

- **Java 21 LTS**: This project uses the latest LTS version of Java
- **Maven Dependencies**: All dependencies are managed via Maven
- **Data Integrity**: Foreign key constraints ensure referential integrity
- **Security**: Consider implementing authentication for production use
- **Performance**: Indexed database queries for optimal performance
- **Scalability**: Modular design allows easy extension

## 🎓 Academic Value

This project demonstrates:
- Real-world application development
- Integration of multiple technologies
- Database design and management
- Computer vision implementation
- Object-oriented programming
- Layered architecture pattern
- Clean code practices

## 📄 License

This is an academic project. Feel free to use and modify for educational purposes.

## 🤝 Contributing

This is an academic project. Suggestions and improvements are welcome.

---

**Version**: 1.0  
**Last Updated**: December 2025  
**Java Version**: 21 (LTS)  
**Build Tool**: Maven 3.9+
