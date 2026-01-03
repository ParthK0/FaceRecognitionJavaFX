package com.myapp.gui;

import com.myapp.dao.FaceEmbeddingDAO;
import com.myapp.dao.RecognitionLogDAO;
import com.myapp.dao.StudentDAO;
import com.myapp.service.DeepLearningAttendanceService;
import com.myapp.ml.DeepLearningRecognizer;
import com.myapp.ml.DeepLearningTrainer;
import com.myapp.model.Attendance;
import com.myapp.model.Student;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Deep Learning Face Recognition GUI Application
 * Modern JavaFX-based interface for the face recognition attendance system
 */
public class DeepLearningGUI extends Application {
    
    private Stage primaryStage;
    private BorderPane mainLayout;
    private StackPane contentArea;
    
    private StudentDAO studentDAO;
    private FaceEmbeddingDAO embeddingDAO;
    private RecognitionLogDAO logDAO;
    
    private TextArea outputArea;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("🎓 Deep Learning Face Recognition Attendance System");
        
        // Initialize DAOs
        studentDAO = new StudentDAO();
        embeddingDAO = new FaceEmbeddingDAO();
        logDAO = new RecognitionLogDAO();
        
        // Create main layout
        mainLayout = new BorderPane();
        
        // Create and set components
        mainLayout.setTop(createHeader());
        mainLayout.setLeft(createSidebar());
        mainLayout.setCenter(createContentArea());
        mainLayout.setBottom(createStatusBar());
        
        // Create scene
        Scene scene = new Scene(mainLayout, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Show home view by default
        showHomeView();
    }

    /**
     * Create header section
     */
    private VBox createHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, #2C3E50, #3498DB); " +
                       "-fx-padding: 20; -fx-spacing: 5;");
        
        Label titleLabel = new Label("🎓 Deep Learning Face Recognition");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.WHITE);
        
        Label subtitleLabel = new Label("Smart Attendance System with DNN + FaceNet");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        header.setAlignment(Pos.CENTER);
        
        return header;
    }

    /**
     * Create sidebar navigation
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setStyle("-fx-background-color: #34495E; -fx-padding: 15;");
        sidebar.setPrefWidth(200);
        
        Label navLabel = new Label("NAVIGATION");
        navLabel.setTextFill(Color.LIGHTGRAY);
        navLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        Button btnHome = createNavButton("🏠 Home");
        Button btnTrain = createNavButton("🎓 Train Students");
        Button btnRecognize = createNavButton("📹 Live Recognition");
        Button btnAttendance = createNavButton("✅ Mark Attendance");
        Button btnAnalytics = createNavButton("📊 Analytics");
        Button btnStudents = createNavButton("👥 Students");
        Button btnLogs = createNavButton("📝 Logs");
        
        // Add action handlers
        btnHome.setOnAction(e -> showHomeView());
        btnTrain.setOnAction(e -> showTrainingView());
        btnRecognize.setOnAction(e -> showRecognitionView());
        btnAttendance.setOnAction(e -> showAttendanceView());
        btnAnalytics.setOnAction(e -> showAnalyticsView());
        btnStudents.setOnAction(e -> showStudentsView());
        btnLogs.setOnAction(e -> showLogsView());
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        Button btnExit = createNavButton("❌ Exit");
        btnExit.setOnAction(e -> Platform.exit());
        btnExit.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
        
        sidebar.getChildren().addAll(navLabel, new Separator(),
            btnHome, btnTrain, btnRecognize, btnAttendance, 
            btnAnalytics, btnStudents, btnLogs, spacer, btnExit);
        
        return sidebar;
    }

    /**
     * Create main content area
     */
    private StackPane createContentArea() {
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #ECF0F1; -fx-padding: 20;");
        return contentArea;
    }

    /**
     * Create status bar
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setStyle("-fx-background-color: #2C3E50; -fx-padding: 10;");
        statusBar.setAlignment(Pos.CENTER_LEFT);
        
        Label statusLabel = new Label("Status: Ready");
        statusLabel.setTextFill(Color.LIGHTGRAY);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label versionLabel = new Label("v2.0 - Deep Learning Edition");
        versionLabel.setTextFill(Color.LIGHTGRAY);
        
        statusBar.getChildren().addAll(statusLabel, spacer, versionLabel);
        
        return statusBar;
    }

    /**
     * Create navigation button
     */
    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; " +
                    "-fx-font-size: 14; -fx-padding: 10;");
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #2C3E50; -fx-text-fill: white; " +
            "-fx-font-size: 14; -fx-padding: 10;"));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: white; " +
            "-fx-font-size: 14; -fx-padding: 10;"));
        return btn;
    }

    /**
     * Show home view
     */
    private void showHomeView() {
        VBox view = new VBox(20);
        view.setAlignment(Pos.TOP_CENTER);
        view.setPadding(new Insets(20));
        
        Label welcomeLabel = new Label("Welcome to Deep Learning Face Recognition System");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Statistics cards
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        
        try {
            int totalStudents = studentDAO.getAllStudents().size();
            int totalEmbeddings = embeddingDAO.getTotalEmbeddingCount();
            int studentsWithEmbeddings = embeddingDAO.getUniqueStudentCount();
            
            VBox card1 = createStatCard("👥 Total Students", String.valueOf(totalStudents), "#3498DB");
            VBox card2 = createStatCard("🧠 Trained Students", String.valueOf(studentsWithEmbeddings), "#2ECC71");
            VBox card3 = createStatCard("📊 Total Embeddings", String.valueOf(totalEmbeddings), "#9B59B6");
            
            statsBox.getChildren().addAll(card1, card2, card3);
        } catch (SQLException e) {
            statsBox.getChildren().add(new Label("Error loading statistics: " + e.getMessage()));
        }
        
        // Quick actions
        Label actionsLabel = new Label("Quick Actions");
        actionsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        GridPane actionsGrid = new GridPane();
        actionsGrid.setHgap(15);
        actionsGrid.setVgap(15);
        actionsGrid.setAlignment(Pos.CENTER);
        
        Button btnQuickTrain = createActionButton("🎓 Train All Students", "Train all students with facial data");
        Button btnQuickRecognize = createActionButton("📹 Start Recognition", "Start live face recognition");
        Button btnQuickAttendance = createActionButton("✅ Mark Attendance", "Mark attendance automatically");
        Button btnQuickView = createActionButton("📊 View Analytics", "View system analytics");
        
        btnQuickTrain.setOnAction(e -> showTrainingView());
        btnQuickRecognize.setOnAction(e -> showRecognitionView());
        btnQuickAttendance.setOnAction(e -> showAttendanceView());
        btnQuickView.setOnAction(e -> showAnalyticsView());
        
        actionsGrid.add(btnQuickTrain, 0, 0);
        actionsGrid.add(btnQuickRecognize, 1, 0);
        actionsGrid.add(btnQuickAttendance, 0, 1);
        actionsGrid.add(btnQuickView, 1, 1);
        
        view.getChildren().addAll(welcomeLabel, statsBox, new Separator(), actionsLabel, actionsGrid);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    /**
     * Show training view
     */
    private void showTrainingView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label titleLabel = new Label("🎓 Train Face Recognition Models");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        
        // Train all section
        VBox trainAllBox = createSectionBox("Train All Students");
        Button btnTrainAll = new Button("🚀 Train All Students");
        btnTrainAll.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; " +
                           "-fx-font-size: 14; -fx-padding: 10 20;");
        Label trainAllDesc = new Label("Train all students with facial data using deep learning models");
        trainAllDesc.setWrapText(true);
        trainAllBox.getChildren().addAll(trainAllDesc, btnTrainAll);
        
        btnTrainAll.setOnAction(e -> trainAllStudents());
        
        // Train specific section
        VBox trainSpecificBox = createSectionBox("Train Specific Student");
        
        Label trainSpecificDesc = new Label("Enter Student ID to train. Dataset path is optional (auto-detected from database).");
        trainSpecificDesc.setWrapText(true);
        trainSpecificDesc.setStyle("-fx-font-size: 11; -fx-text-fill: gray;");
        
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        
        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Student ID");
        studentIdField.setPrefWidth(100);
        
        TextField datasetPathField = new TextField();
        datasetPathField.setPromptText("(Optional) Custom dataset path - leave empty for auto-detect");
        HBox.setHgrow(datasetPathField, Priority.ALWAYS);
        
        Button btnBrowse = new Button("📁");
        btnBrowse.setTooltip(new javafx.scene.control.Tooltip("Browse for custom dataset directory"));
        btnBrowse.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Dataset Directory");
            File selectedDir = chooser.showDialog(primaryStage);
            if (selectedDir != null) {
                datasetPathField.setText(selectedDir.getAbsolutePath());
            }
        });
        
        Button btnTrainSpecific = new Button("🎯 Train Student");
        btnTrainSpecific.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-padding: 5 15;");
        
        VBox studentBox = new VBox(5);
        HBox studentRow = new HBox(10);
        studentRow.setAlignment(Pos.CENTER_LEFT);
        studentRow.getChildren().addAll(new Label("Student ID:"), studentIdField, btnTrainSpecific);
        
        HBox datasetRow = new HBox(10);
        datasetRow.setAlignment(Pos.CENTER_LEFT);
        datasetRow.getChildren().addAll(new Label("Dataset Path:"), datasetPathField, btnBrowse);
        
        studentBox.getChildren().addAll(studentRow, datasetRow);
        trainSpecificBox.getChildren().addAll(trainSpecificDesc, studentBox);
        
        btnTrainSpecific.setOnAction(e -> {
            try {
                int studentId = Integer.parseInt(studentIdField.getText());
                String datasetPath = datasetPathField.getText();
                trainSpecificStudent(studentId, datasetPath);
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid student ID", Alert.AlertType.ERROR);
            }
        });
        
        // Output area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);
        outputArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12;");
        VBox.setVgrow(outputArea, Priority.ALWAYS);
        
        view.getChildren().addAll(titleLabel, trainAllBox, trainSpecificBox, 
                                 new Label("Training Output:"), outputArea);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    /**
     * Show recognition view
     */
    private void showRecognitionView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.TOP_CENTER);
        
        Label titleLabel = new Label("📹 Live Face Recognition");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        
        Label descLabel = new Label("Start real-time face recognition from camera");
        descLabel.setWrapText(true);
        
        Button btnStartRecognition = new Button("🎥 Start Recognition");
        btnStartRecognition.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; " +
                                    "-fx-font-size: 16; -fx-padding: 15 30;");
        
        btnStartRecognition.setOnAction(e -> startRecognition());
        
        Label infoLabel = new Label("ℹ️ Press 'q' in the camera window to stop recognition");
        infoLabel.setStyle("-fx-font-style: italic;");
        
        view.getChildren().addAll(titleLabel, descLabel, btnStartRecognition, infoLabel);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    /**
     * Show attendance marking view
     */
    private void showAttendanceView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label titleLabel = new Label("✅ Mark Attendance with Face Recognition");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        
        VBox formBox = createSectionBox("Attendance Session Settings");
        
        HBox courseBox = new HBox(10);
        courseBox.setAlignment(Pos.CENTER_LEFT);
        Label courseLabel = new Label("Course ID:");
        TextField courseIdField = new TextField();
        courseIdField.setPromptText("Enter course ID");
        courseIdField.setPrefWidth(150);
        courseBox.getChildren().addAll(courseLabel, courseIdField);
        
        HBox sessionBox = new HBox(10);
        sessionBox.setAlignment(Pos.CENTER_LEFT);
        Label sessionLabel = new Label("Session:");
        ComboBox<String> sessionCombo = new ComboBox<>();
        sessionCombo.getItems().addAll("Morning", "Afternoon", "Evening", "Full Day");
        sessionCombo.setValue("Morning");
        sessionBox.getChildren().addAll(sessionLabel, sessionCombo);
        
        Button btnStartAttendance = new Button("🚀 Start Attendance Marking");
        btnStartAttendance.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; " +
                                   "-fx-font-size: 14; -fx-padding: 10 20;");
        
        formBox.getChildren().addAll(courseBox, sessionBox, btnStartAttendance);
        
        btnStartAttendance.setOnAction(e -> {
            try {
                int courseId = Integer.parseInt(courseIdField.getText());
                Attendance.SessionType sessionType = Attendance.SessionType.valueOf(
                    sessionCombo.getValue().toUpperCase().replace(" ", "_"));
                startAttendanceMarking(courseId, sessionType);
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid course ID", Alert.AlertType.ERROR);
            }
        });
        
        view.getChildren().addAll(titleLabel, formBox);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    /**
     * Show analytics view
     */
    private void showAnalyticsView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label titleLabel = new Label("📊 System Analytics");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        
        // Load statistics
        try {
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            
            Map<String, Object> stats = logDAO.getRecognitionStatistics(startOfDay, endOfDay);
            
            GridPane statsGrid = new GridPane();
            statsGrid.setHgap(20);
            statsGrid.setVgap(20);
            statsGrid.setAlignment(Pos.CENTER);
            
            VBox card1 = createStatCard("Total Attempts", stats.get("total_attempts").toString(), "#3498DB");
            VBox card2 = createStatCard("Successful", stats.get("successful").toString(), "#2ECC71");
            VBox card3 = createStatCard("Failed", stats.get("failed").toString(), "#E74C3C");
            VBox card4 = createStatCard("Unknown", stats.get("unknown").toString(), "#95A5A6");
            
            float avgConf = ((Number) stats.get("avg_confidence")).floatValue();
            VBox card5 = createStatCard("Avg Confidence", String.format("%.2f%%", avgConf * 100), "#9B59B6");
            
            statsGrid.add(card1, 0, 0);
            statsGrid.add(card2, 1, 0);
            statsGrid.add(card3, 2, 0);
            statsGrid.add(card4, 0, 1);
            statsGrid.add(card5, 1, 1);
            
            view.getChildren().addAll(titleLabel, new Label("Today's Recognition Statistics"), statsGrid);
            
        } catch (SQLException e) {
            Label errorLabel = new Label("Error loading analytics: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            view.getChildren().addAll(titleLabel, errorLabel);
        }
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    /**
     * Show students view
     */
    private void showStudentsView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label titleLabel = new Label("👥 Students Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        
        TableView<Student> table = new TableView<>();
        
        TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(
            data.getValue().getStudentId()).asObject());
        
        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getFullName()));
        
        TableColumn<Student, String> admissionCol = new TableColumn<>("Admission No");
        admissionCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getAdmissionNumber()));
        
        TableColumn<Student, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().isActive() ? "Active" : "Inactive"));
        
        table.getColumns().addAll(idCol, nameCol, admissionCol, statusCol);
        
        try {
            List<Student> students = studentDAO.getAllStudents();
            table.getItems().addAll(students);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load students: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        
        VBox.setVgrow(table, Priority.ALWAYS);
        view.getChildren().addAll(titleLabel, table);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    /**
     * Show logs view
     */
    private void showLogsView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label titleLabel = new Label("📝 Recognition Logs");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        
        HBox controlsBox = new HBox(10);
        Label limitLabel = new Label("Show last:");
        TextField limitField = new TextField("50");
        limitField.setPrefWidth(80);
        Button btnRefresh = new Button("🔄 Refresh");
        controlsBox.getChildren().addAll(limitLabel, limitField, btnRefresh);
        
        TextArea logsArea = new TextArea();
        logsArea.setEditable(false);
        logsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11;");
        VBox.setVgrow(logsArea, Priority.ALWAYS);
        
        btnRefresh.setOnAction(e -> {
            try {
                int limit = Integer.parseInt(limitField.getText());
                List<Map<String, Object>> logs = logDAO.getRecentLogs(limit);
                
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("%-20s %-25s %-12s %-15s%n", 
                    "Timestamp", "Student", "Result", "Confidence"));
                sb.append("─".repeat(75)).append("\n");
                
                for (Map<String, Object> log : logs) {
                    String timestamp = log.get("recognition_timestamp").toString();
                    String student = log.get("student_name") != null ? 
                                   log.get("student_name").toString() : "Unknown";
                    String result = log.get("recognition_result").toString();
                    String confidence = String.format("%.2f%%", 
                                      ((Number) log.get("confidence_score")).floatValue() * 100);
                    
                    sb.append(String.format("%-20s %-25s %-12s %-15s%n", 
                        timestamp, student, result, confidence));
                }
                
                logsArea.setText(sb.toString());
            } catch (Exception ex) {
                logsArea.setText("Error loading logs: " + ex.getMessage());
            }
        });
        
        // Load initially
        btnRefresh.fire();
        
        view.getChildren().addAll(titleLabel, controlsBox, logsArea);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }

    // Helper methods

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; " +
                     "-fx-padding: 20; -fx-min-width: 200;");
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        valueLabel.setTextFill(Color.WHITE);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        titleLabel.setTextFill(Color.WHITE);
        
        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }

    private Button createActionButton(String title, String description) {
        VBox content = new VBox(5);
        content.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 11; -fx-text-fill: gray;");
        descLabel.setWrapText(true);
        
        content.getChildren().addAll(titleLabel, descLabel);
        
        Button btn = new Button();
        btn.setGraphic(content);
        btn.setPrefSize(250, 80);
        btn.setStyle("-fx-background-color: white; -fx-border-color: #BDC3C7; " +
                    "-fx-border-radius: 5; -fx-background-radius: 5;");
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #ECF0F1; -fx-border-color: #3498DB; " +
            "-fx-border-radius: 5; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: white; -fx-border-color: #BDC3C7; " +
            "-fx-border-radius: 5; -fx-background-radius: 5;"));
        
        return btn;
    }

    private VBox createSectionBox(String title) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Separator separator = new Separator();
        
        box.getChildren().addAll(titleLabel, separator);
        return box;
    }

    // Action methods

    private void trainAllStudents() {
        new Thread(() -> {
            try {
                Platform.runLater(() -> {
                    if (outputArea != null) {
                        outputArea.setText("Starting training...\n");
                    }
                });
                
                DeepLearningTrainer trainer = new DeepLearningTrainer();
                trainer.trainAllStudents();
                trainer.close();
                
                Platform.runLater(() -> {
                    showAlert("Success", "Training completed successfully!", Alert.AlertType.INFORMATION);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert("Error", "Training failed: " + e.getMessage(), Alert.AlertType.ERROR);
                });
            }
        }).start();
    }

    private void trainSpecificStudent(int studentId, String datasetPath) {
        new Thread(() -> {
            try {
                Platform.runLater(() -> {
                    if (outputArea != null) {
                        outputArea.setText("Starting training for student " + studentId + "...\n");
                    }
                });
                
                DeepLearningTrainer trainer = new DeepLearningTrainer();
                // Pass null or empty path to let trainer auto-determine the path
                String actualPath = (datasetPath == null || datasetPath.trim().isEmpty()) ? null : datasetPath;
                boolean success = trainer.trainStudent(studentId, actualPath);
                trainer.close();
                
                Platform.runLater(() -> {
                    if (success) {
                        showAlert("Success", "Training completed!", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Error", "Training failed. Check console for details.", Alert.AlertType.ERROR);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert("Error", "Training failed: " + e.getMessage(), Alert.AlertType.ERROR);
                });
            }
        }).start();
    }

    private void startRecognition() {
        new Thread(() -> {
            try {
                DeepLearningRecognizer recognizer = new DeepLearningRecognizer();
                recognizer.startRealtimeRecognition();
                recognizer.close();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert("Error", "Recognition failed: " + e.getMessage(), Alert.AlertType.ERROR);
                });
            }
        }).start();
    }

    private void startAttendanceMarking(int courseId, Attendance.SessionType sessionType) {
        new Thread(() -> {
            try {
                DeepLearningAttendanceService service = new DeepLearningAttendanceService();
                service.startAttendanceRecognition(courseId, sessionType);
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert("Error", "Attendance marking failed: " + e.getMessage(), Alert.AlertType.ERROR);
                });
            }
        }).start();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
