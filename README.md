How to git and build using Eclipse
==================================
1. Download the [latest version of Eclipse](http://www.eclipse.org/)
2. Download and setup git. I recommend using github's setup guide
3. Download all the required libraries and extract them to somewhere on your drive (I use C:/libs) [log4j](http://logging.apache.org/log4j/1.2/) [Slick](http://slick.cokeandcode.com/), [lwjgl](http://lwjgl.org/), [artemis](http://gamadu.com/artemis/), and both jogg and jorbis.
IMPORTANT: Here is the easiest way to grab the necessary libraries for audio playback: When you download slick, go ahead and grab the full distribution, not just the library! The reason for this is that this is the easiest way to get the compatible jogg and jorbis files required for music playback.
4. Open up git bash cd into your workspace and type 
`git clone https://YOURUSERNAME@github.com/templetonm/Troglodytes.git`
5. Open up Eclipse go to File > New > Java Project and make the project name Troglodytes
6. Right click the project (Troglodytes) on the left hand side and select Properties. Navigate to Java Build Path. Click Add External JARs. Browse to the Libraries you downloaded earlier (log4j, slick, lwgl, artemis, jogg, jorbis)
7. While you're still in the Java Build Path click the drop down for lwgl.jar. Click Native library location and browse to lwgl's native folder for your operating system.
