# Running the Facial Recognition Attendance System

## ✅ System Status
The application is now successfully configured and running!

## 🚀 How to Run

### Method 1: Using Maven (Recommended)
```bash
cd e:\FaceRecognitionJava\facerecognitionjava
mvn clean compile exec:java -Dexec.mainClass=com.myapp.AttendanceSystem
```

### Method 2: Build and Run JAR
```bash
mvn clean package
java -jar target/FaceRecognitionJava-1.0-SNAPSHOT.jar
```

## 📋 System Configuration

### Java Version
- **Current**: Java 17.0.16 LTS
- **Configured**: Java 17 (matching system installation)

### Database Configuration
- **Host**: localhost:3306
- **Database**: attendance_system (auto-created)
- **Username**: root
- **Password**: As configured in `db.properties`
- **Connection**: allowPublicKeyRetrieval=true (required for MySQL 8.x)

### Dependencies
- OpenCV 4.9.0 (via JavaCV/Bytedeco)
- MySQL Connector/J 8.3.0
- Maven 3.9+
- JavaFX 20

## 🎯 Main Features

1. **Student Management** - Register new students, update information
2. **Facial Data Capture** - Capture 50 face images per student using webcam
3. **Train Face Recognition Model** - Train LBPH recognizer with captured data
4. **Mark Attendance** - Real-time face recognition for automatic attendance
5. **View Reports** - Today's attendance, student history, course reports
6. **Course Management** - Manage courses and subjects
7. **System Settings** - Configuration and system verification

## 📊 Database Schema

### Tables Created
- **students**: Student information with facial data paths
- **courses**: Course details (code, name, department, credits)
- **attendance**: Attendance records with timestamps and status

### Sample Data
- Pre-loaded with sample courses (CSE101, CSE102, CSE103, CSE104)

## ⚠️ Important Notes

1. **Webcam Required**: For facial data capture and attendance marking
2. **First Time Setup**:
   - Register students (Menu → 1)
   - Capture facial data for each student (Menu → 2)
   - Train the model (Menu → 3)
   - Then mark attendance (Menu → 4)

3. **Face Recognition**:
   - Uses LBPH (Local Binary Patterns Histograms) algorithm
   - Haar Cascade for face detection
   - Requires training before recognition

## 🔧 Troubleshooting

### If compilation fails:
```bash
mvn clean install -U
```

### If database connection fails:
- Verify MySQL is running
- Check `db.properties` credentials
- Ensure `allowPublicKeyRetrieval=true` is in connection URL

### If OpenCV errors occur:
- Ensure webcam is not in use by another application
- Check camera permissions

## 📝 Usage Workflow

1. **Setup Phase**:
   ```
   Start Application → Student Management → Register Students
   ```

2. **Training Phase**:
   ```
   Facial Data Capture → Capture 50 images per student
   Train Model → Wait for training completion
   ```

3. **Operational Phase**:
   ```
   Mark Attendance → Face Recognition runs
   View Reports → Check attendance records
   ```

## 🎓 Academic Project Information

- **Title**: Facial Recognition Based Smart Attendance System
- **Technologies**: Java 17, OpenCV 4.9, MySQL 8.x, Maven
- **Architecture**: Layered (Model-DAO-Service-UI)
- **Face Recognition**: LBPH Algorithm
- **Build Tool**: Maven 3.9+

## 📞 Support

For issues or questions, refer to:
- `PROJECT_README.md` - Complete documentation
- `TROUBLESHOOTING.md` - Common issues and solutions
- `QUICK_START.md` - Quick setup guide
- `FILE_STRUCTURE.md` - Project structure details
