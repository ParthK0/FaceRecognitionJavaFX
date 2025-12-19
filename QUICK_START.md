# Quick Start Guide
## Facial Recognition Based Smart Attendance System

### 🚀 Quick Setup (5 Minutes)

#### Step 1: Verify Prerequisites
```bash
# Check Java version (should be 21)
java -version

# Check Maven
mvn -version

# Check MySQL
mysql --version
```

#### Step 2: Configure Database
1. Start MySQL server
2. Create/edit `db.properties` in project root:
```properties
db.host=localhost
db.port=3306
db.database=attendance_system
db.username=root
db.password=YOUR_PASSWORD_HERE
db.useSSL=false
db.autoReconnect=true
```

#### Step 3: Build Project
```bash
cd e:\FaceRecognitionJava\facerecognitionjava
mvn clean install
```

#### Step 4: Run Application
```bash
mvn exec:java -Dexec.mainClass="com.myapp.AttendanceSystem"
```

### 📝 First-Time Usage Workflow

#### 1. Register a Student (2 minutes)
- Select: **1. Student Management** → **1. Register New Student**
- Fill in details:
  - Admission Number: `ADM2024001`
  - Roll Number: `CS101`
  - Full Name: `John Doe`
  - Course ID: `1` (Computer Science Engineering)
  - Semester: `1`
  - Academic Year: `2024-2025`
  - Email: `john@example.com`
  - Phone: `1234567890`

#### 2. Capture Facial Data (2 minutes)
- Select: **2. Facial Data Capture**
- Enter Student ID or Admission Number: `ADM2024001`
- Number of images: `50` (press Enter for default)
- Press Enter to start
- **Look at camera and move face slightly for different angles**
- Wait for completion

#### 3. Train Model (1 minute)
- Select: **3. Train Face Recognition Model**
- Press Enter to start training
- Wait for "✓ Model training completed!"

#### 4. Mark Attendance (Live Recognition)
- Select: **4. Mark Attendance (Face Recognition)**
- Enter Course ID: `1`
- Select Session Type: `4` (Full Day)
- Press Enter to start camera
- **Student looks at camera → Attendance marked automatically!**
- Press 'q' to quit

#### 5. View Reports
- Select: **5. View Attendance Reports** → **1. Today's Attendance**
- See all students marked present today

### 🎯 Testing the System

#### Quick Test Scenario:
```
1. Register 2-3 students
2. Capture facial data for each (50 images each)
3. Train the model once
4. Start face recognition
5. Each student looks at camera
6. Check attendance reports
```

### ⚡ Common Commands

**Run Application:**
```bash
mvn exec:java -Dexec.mainClass="com.myapp.AttendanceSystem"
```

**Clean Build:**
```bash
mvn clean install
```

**Run with Specific Java:**
```bash
set JAVA_HOME=C:\Program Files\Java\jdk-21
mvn exec:java -Dexec.mainClass="com.myapp.AttendanceSystem"
```

**Check Database:**
```bash
mysql -u root -p
USE attendance_system;
SELECT * FROM students;
SELECT * FROM attendance;
```

### 📊 Sample Data

The system comes with 5 pre-loaded courses:
1. Computer Science Engineering (CSE101)
2. Information Technology (IT102)
3. Electronics and Communication (ECE103)
4. Master of Business Administration (MBA104)
5. Bachelor of Computer Applications (BCA105)

### 🔍 Troubleshooting Quick Fixes

**Problem**: Camera not opening
**Solution**: 
- Close other camera applications
- Try changing camera index in code (VideoCapture(0) → VideoCapture(1))

**Problem**: Database connection failed
**Solution**:
- Check MySQL is running: `net start MySQL80` (Windows)
- Verify credentials in `db.properties`

**Problem**: Face not recognized
**Solution**:
- Ensure good lighting
- Train model after adding new students
- Capture more images (try 100 instead of 50)

**Problem**: Maven build fails
**Solution**:
```bash
mvn clean
mvn install -U
```

### 📱 Menu Navigation

```
Main Menu
├── 1. Student Management
│   ├── 1. Register New Student ⭐
│   ├── 2. View All Students
│   ├── 3. Search Student
│   ├── 4. View Student Details
│   └── 5. Update Student Information
├── 2. Facial Data Capture ⭐
├── 3. Train Face Recognition Model ⭐
├── 4. Mark Attendance (Face Recognition) ⭐
├── 5. View Attendance Reports
│   ├── 1. Today's Attendance ⭐
│   ├── 2. Student Attendance History
│   ├── 3. Course Attendance Report
│   └── 4. Date-wise Attendance
├── 6. Course Management
├── 7. System Settings
└── 0. Exit
```

⭐ = Most commonly used features

### 💡 Pro Tips

1. **Better Recognition**: Capture images in the same lighting conditions as attendance marking
2. **Multiple Angles**: Move your face slightly while capturing to get different angles
3. **Retrain Often**: Retrain model after adding 5+ new students
4. **Session Types**: Use different session types for morning/afternoon classes
5. **Duplicate Prevention**: System automatically prevents marking attendance twice for same session

### 🎓 For Demonstration/Presentation

1. Have 2-3 volunteers ready
2. Register them beforehand
3. Capture their facial data (takes 2 min each)
4. Train model (takes 1 min)
5. During demo:
   - Show student management features
   - Start face recognition
   - Each volunteer looks at camera
   - Show attendance reports
   - Display attendance percentage

### 📞 Quick Reference

**Project Location**: `e:\FaceRecognitionJava\facerecognitionjava\`
**Main Class**: `com.myapp.AttendanceSystem`
**Database**: `attendance_system`
**Dataset Folder**: `dataset/`
**Trained Model**: `trainer/multi.yml`
**Configuration**: `db.properties`

### ✅ Success Indicators

- ✓ Database initialized successfully
- ✓ N images captured
- ✓ Model training completed
- ✓ Attendance marked successfully for [Name]
- ✓ Face recognized with confidence < 80%

### 📄 Next Steps

After basic setup:
1. Add more students
2. Test different lighting conditions
3. Try different session types
4. Generate various reports
5. Adjust confidence threshold if needed

---

**Need Help?** Check PROJECT_README.md for detailed documentation.
