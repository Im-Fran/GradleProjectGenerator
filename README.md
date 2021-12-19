# GradleProjectGenerator
A project generator using Compose by JetBrains with a GUI

# How to build
Download/Clone the repository and then run the following command:
> Before running the command make sure you have installed JDK 16 or newer
If you're on windows
```shell
gradlew.bat package
```

If you're on Linux/macOS
```sh
./gradlew package
```

After that you will find the executable files in the path `build/compose/binaries/main/`, for macos you should use the one inside the `app` folder
