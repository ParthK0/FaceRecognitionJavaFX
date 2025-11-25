# FaceRecognitionJavaFX

Minimal JavaFX + Bytedeco OpenCV face recognition sample.

## Build

From PowerShell in project root:

```powershell
cd E:\FaceRecognitionJava\facerecognitionjava
mvn -DskipTests package
mvn dependency:copy-dependencies -DincludeScope=runtime
```

## Run the JavaFX Camera UI

Make sure the JavaFX jars were copied to `target\dependency` then run:

```powershell
java --module-path "target\dependency" --add-modules=javafx.controls,javafx.fxml,javafx.swing -cp "target\FaceRecognitionJava-1.0-SNAPSHOT.jar;target\dependency\*" com.myapp.CameraUI
```

Notes:
- If you add new training images under `dataset/<name>/`, run the multi-trainer:

```powershell
java -cp "target\FaceRecognitionJava-1.0-SNAPSHOT.jar;target\dependency\*" com.myapp.TrainerMulti
```

- The trainer writes `trainer/multi.yml` and `trainer/labels.txt`. These are ignored by `.gitignore`.
- If the app cannot find a valid Haar cascade, it will attempt to download one at runtime (network required).

## Git / GitHub

If Git is not installed on your machine, install it from https://git-scm.com/download/win.

Once Git is installed, run these commands in PowerShell from the project folder (replace placeholders):

```powershell
cd E:\FaceRecognitionJava\facerecognitionjava
# if you haven't done this already
git init
git config user.email "you@example.com"
git config user.name "Your Name"

# add and commit
git add .
git commit -m "Initial commit - JavaFX Face Recognition Login System"

# create repo on GitHub (https://github.com/new) named: FaceRecognitionJavaFX
# then add remote and push
git remote add origin https://github.com/YOUR-USERNAME/FaceRecognitionJavaFX.git
git branch -M main
git push -u origin main
```

If you want, share your GitHub repo URL (or your GitHub username) and I will give the exact `git remote add` command.
