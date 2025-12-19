# 🔧 Troubleshooting Guide
## Facial Recognition Based Smart Attendance System

### 📋 Common Issues and Solutions

---

## 🔴 Database Issues

### Issue 1: "Database connection failed"
**Symptoms**: Application cannot connect to MySQL

**Solutions**:
```bash
# 1. Check if MySQL is running
# Windows:
net start MySQL80

# 2. Test connection manually
mysql -u root -p

# 3. Verify credentials in db.properties
db.username=root
db.password=YOUR_ACTUAL_PASSWORD

# 4. Check if port 3306 is available
netstat -an | findstr 3306
```

**Common Causes**:
- MySQL server not running
- Incorrect username/password
- Firewall blocking connection
- Wrong port number

---

### Issue 2: "Access denied for user"
**Solution**:
```sql
# Grant permissions
mysql -u root -p
GRANT ALL PRIVILEGES ON attendance_system.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

---

### Issue 3: "Table doesn't exist"
**Solution**:
```bash
# Let application auto-create tables
1. Delete database: DROP DATABASE attendance_system;
2. Restart application
3. Tables will be auto-created

# Or run schema manually
mysql -u root -p < database_schema.sql
```

---

## 📷 Camera Issues

### Issue 1: "Camera not opened" or "Camera not accessible"
**Solutions**:

**Step 1**: Check if camera is in use
```bash
# Close these applications:
- Zoom, Teams, Skype
- Browser tabs with camera access
- Other webcam applications
```

**Step 2**: Try different camera index
In the code, change:
```java
VideoCapture camera = new VideoCapture(0);
// Try 1, 2, 3... if 0 doesn't work
```

**Step 3**: Verify camera drivers
```bash
# Windows: Check Device Manager
# Look for "Imaging Devices"
# Update drivers if needed
```

---

### Issue 2: "Camera opens but shows black screen"
**Solutions**:
- Check camera lens is not covered
- Restart application
- Restart computer
- Check camera privacy settings (Windows Settings → Privacy → Camera)

---

## 🤖 Face Recognition Issues

### Issue 1: "Trainer file not found"
**Solution**:
```bash
# Train the model first
Main Menu → 3. Train Face Recognition Model

# This creates: trainer/multi.yml
```

---

### Issue 2: "Face not detected during capture"
**Causes & Solutions**:

**Lighting**:
- ✅ Use good lighting (face well-lit)
- ✅ Avoid backlighting
- ✅ Face camera directly

**Distance**:
- ✅ Sit 2-3 feet from camera
- ✅ Ensure full face is visible

**Haar Cascade**:
```bash
# Verify file exists:
src/main/resources/haarcascade_frontalface_default.xml
```

---

### Issue 3: "Face recognized incorrectly" or "Low accuracy"
**Solutions**:

**Immediate Fixes**:
1. Improve lighting conditions
2. Reduce distance to camera
3. Look directly at camera

**Long-term Solutions**:
```bash
# 1. Capture more images
Select: Facial Data Capture
Enter: 100 (instead of default 50)

# 2. Recapture with better lighting
Delete: dataset/<student_name>/
Recapture facial data

# 3. Retrain model
Select: Train Face Recognition Model

# 4. Adjust confidence threshold
In FaceRecognitionAttendanceService.java:
CONFIDENCE_THRESHOLD = 70.0  // Lower = stricter (try 60-80)
```

---

### Issue 4: "Unknown student" even for registered students
**Checklist**:
- ✅ Student has facial data captured?
- ✅ Model trained after capturing data?
- ✅ Lighting similar to capture conditions?
- ✅ Student looking at camera?

**Solution**:
```bash
# View student details
Main Menu → 1. Student Management → 4. View Student Details
# Check "Has Facial Data" and "Image Count"

# If no facial data:
Main Menu → 2. Facial Data Capture

# If facial data exists:
Main Menu → 3. Train Face Recognition Model
```

---

## 🏗️ Build Issues

### Issue 1: "Maven build failed"
**Solutions**:
```bash
# 1. Clean build
mvn clean

# 2. Update dependencies
mvn clean install -U

# 3. Check Java version
java -version
# Should show Java 21

# 4. Set JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-21
mvn clean install
```

---

### Issue 2: "Class not found" errors
**Solution**:
```bash
# Rebuild with dependencies
mvn clean package
mvn dependency:copy-dependencies

# Run with classpath
java -cp "target/classes;target/dependency/*" com.myapp.AttendanceSystem
```

---

### Issue 3: "OpenCV native library not found"
**Solution**:
```bash
# Check pom.xml has:
<dependency>
    <groupId>org.bytedeco</groupId>
    <artifactId>opencv-platform</artifactId>
    <version>4.9.0-1.5.10</version>
</dependency>

# This includes all native libraries
```

---

## 💾 Data Issues

### Issue 1: "Attendance already marked"
**Expected Behavior**: System prevents duplicate attendance

**To Allow Re-marking**:
```sql
# Delete attendance record
DELETE FROM attendance 
WHERE student_id = X 
AND attendance_date = CURDATE() 
AND session_type = 'Full Day';
```

---

### Issue 2: "Student not found"
**Solutions**:
```bash
# 1. Search student
Main Menu → 1. Student Management → 3. Search Student

# 2. Check admission number spelling

# 3. View all students
Main Menu → 1. Student Management → 2. View All Students
```

---

### Issue 3: "Duplicate entry for admission_number"
**Cause**: Admission number already exists

**Solution**:
```bash
# Use unique admission number
# Or search existing student first
```

---

## ⚡ Performance Issues

### Issue 1: "Slow face detection"
**Solutions**:
- Reduce camera resolution
- Close other applications
- Update graphics drivers

---

### Issue 2: "Application freezes during training"
**Expected**: Training takes time

**Normal Duration**:
- 5 students: ~30 seconds
- 10 students: ~1 minute
- 50 students: ~3-5 minutes

**If stuck**:
- Wait 5 minutes
- Check console for errors
- Restart if no progress

---

## 🖥️ Platform-Specific Issues

### Windows Issues

**Camera Permission**:
```
Settings → Privacy → Camera → Allow desktop apps to access camera
```

**MySQL Service**:
```bash
# Start MySQL
net start MySQL80

# Check status
sc query MySQL80
```

---

## 🔍 Debugging Tips

### Enable Detailed Logging
Add to code:
```java
try {
    // your code
} catch (Exception e) {
    e.printStackTrace(); // Shows full error trace
}
```

### Check Database Manually
```sql
-- Connect to MySQL
mysql -u root -p

-- Check database
USE attendance_system;
SHOW TABLES;

-- View data
SELECT * FROM students;
SELECT * FROM attendance ORDER BY created_at DESC LIMIT 10;
SELECT * FROM courses;
```

### Verify File Paths
```bash
# Check these exist:
dataset/                    # For facial images
trainer/                    # For trained models
trainer/multi.yml          # Trained model file
db.properties             # Database config
src/main/resources/haarcascade_frontalface_default.xml
```

---

## 🆘 Emergency Fixes

### Reset Everything
```bash
# 1. Stop application

# 2. Delete database
mysql -u root -p
DROP DATABASE attendance_system;
EXIT;

# 3. Delete training data
rmdir /s trainer
rmdir /s dataset

# 4. Restart application
# Database will be recreated
```

### Fresh Start
```bash
# 1. Backup any important data
# 2. Delete project
# 3. Re-clone/download
# 4. Follow QUICK_START.md
```

---

## 📞 Getting Help

### Information to Collect Before Asking

1. **Error Message** (exact text)
2. **Java Version**: `java -version`
3. **Maven Version**: `mvn -version`
4. **MySQL Version**: `mysql --version`
5. **Operating System**
6. **What you were trying to do**
7. **Steps to reproduce**

### Verification Steps
```bash
# Run system verifier
mvn exec:java -Dexec.mainClass="com.myapp.util.SystemVerifier"

# This checks:
# - Java version
# - OpenCV libraries
# - Camera access
# - Database connection
# - Required files/directories
```

---

## ✅ Prevention Tips

### Best Practices

1. **Always train after adding students**
   - Add 5+ students → Train model

2. **Consistent environment**
   - Capture and recognize in similar lighting
   - Same camera position

3. **Quality captures**
   - Good lighting
   - Face camera directly
   - Capture sufficient images (50+)

4. **Regular backups**
   ```sql
   mysqldump -u root -p attendance_system > backup.sql
   ```

5. **Keep dependencies updated**
   ```bash
   mvn versions:display-dependency-updates
   ```

---

## 🎯 Quick Diagnostic Checklist

Before seeking help, verify:

- [ ] MySQL is running
- [ ] Camera is not in use by other apps
- [ ] db.properties has correct credentials
- [ ] Java 21 is installed and active
- [ ] Maven dependencies are installed
- [ ] Haar Cascade file exists
- [ ] Student has facial data captured
- [ ] Model is trained after capturing data
- [ ] Good lighting conditions
- [ ] Camera is functioning

---

## 💡 Tips for Better Results

### Facial Data Capture
- 📸 Capture 50-100 images per student
- 💡 Use good, consistent lighting
- 👤 Various facial angles
- 🎯 Face clearly visible
- 😊 Natural expressions

### Recognition
- 💡 Similar lighting to capture
- 📏 2-3 feet from camera
- 👁️ Look directly at camera
- ⏱️ Wait 1-2 seconds for recognition
- 🔄 Retrain if multiple new students added

---

## 🔗 Quick Reference Commands

```bash
# Start MySQL
net start MySQL80

# Build project
mvn clean install

# Run application
mvn exec:java -Dexec.mainClass="com.myapp.AttendanceSystem"

# Verify system
mvn exec:java -Dexec.mainClass="com.myapp.util.SystemVerifier"

# Check database
mysql -u root -p attendance_system

# View logs
# Check console output for errors
```

---

**Need More Help?**
- Check PROJECT_README.md for detailed documentation
- Review QUICK_START.md for setup instructions
- Check code comments for implementation details

---

**Last Updated**: December 2025  
**Version**: 1.0
