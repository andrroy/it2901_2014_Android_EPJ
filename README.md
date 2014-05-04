##EMR integration for ultrasound scanners on standard Android platform
==============================================================================

### How to build and run

#### Prerequisites
* **Android Support Library** and **Android Support Repository**. These can be downloaded using the Android SDK Manager.
* Gradle

#### Build
1. Import the project using Gradle as external model.
2. Add .jars in libs folder to project dependencies.
  * Using IntelliJ: Project structure - Modules - CapgeminiEMR - Dependencies - Add - Select all jar-files
in the libs folder.
3. Run the EMRService module.
4. Run the VScanLauncher module.
5. Run the CapgeminiEMR module.
6. When prompted to setup the application, the following setup config can be used: http://royrvik.org/settings.xml


### Known issues
* Android 4.4 (KitKat) doesn't like the gallery to be refreshed and will either show
an empty gallery or will force application to crash
* SQLCipher might be unstable on certain devices
* Stacktrace printed with Log.w when providing invalid source in setup.
* Stacktrace printed with Log.w when providing wrong password on Login.
