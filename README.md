##EMR integration for ultrasound scanners on standard Android platform
==============================================================================

### How to build and run
1. Download sources with Maven (pom.xml - Maven - Download sources)
2. Add .jars in libs folder to project dependencies.
  * IntelliJ: Project structure - Modules - CapgeminiEMR - Dependencies - Add - Select all jar-files
in the libs folder.
3. Build and run the CapgeminiEMR module. The application will install, but will exit right away.
4. Build and run the VScanLauncher module.
5. To login, use username "a" with blank password

### Known issues
* Android 4.4 (KitKat) doesn't like the gallery to be refreshed and will either show
an empty gallery or will force application to crash
* Unable to fully clear the SQLite database without reinstalling application
* Can't read nested XML properly