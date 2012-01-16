How to git and build using Eclipse
==================================
1. Download the [latest version of Eclipse](http://www.eclipse.org/)
2. Download and setup git. I recommend using github's setup guide
3. Download all the required libraries and extract them to somewhere on your drive (I use C:/libs) [log4j](http://logging.apache.org/log4j/1.2/) [Slick](http://slick.cokeandcode.com/), [lwjgl](http://lwjgl.org/) and [artemis](http://gamadu.com/artemis/)
4. Create a folder in your Eclipse workspace named Troglodytes
5. Open up git bash cd into that folder and type 
`git clone https://coxry@github.com/templetonm/Troglodytes.git`
6. Rename the resulting folder (which should be Troglodytes) to src
7. Open up Eclipse go to File > New > Java Project and make the project name Troglodytes
8. Right click the project (Troglodytes) on the left hand side and select Properties. Navigate to Java Build Path. Click Add External JARs. Browse to the Libraries you downloaded earlier (log4j, slick, lwgl, artemis)
9. While you're still in the Java Build Path click the drop down for lwgl.jar. Click Native library location and browse to lwgl's native folder for your operating system.
