LeapRecorder
============

A small and simple utility for recording and saving Leap Motion data in JSON format. No installation or internet connection required.


##Features
Record and save the following data:
- The id of each frame.
- Hand information (id, direction, palm position, palm normal, palm velocity, hand basis, left or right hand)
- Finger information (id, direction, tip position, tip velocity, bones)
- Bone information (type, length, width, direction, centre, next joint, previous joint, basis)

A small GUI that always stays on top of other windows to keep it easily accessible when using, e.g., the Visualiser.

For more information regarding the different information, refer to the [Leap Motion API](https://developer.leapmotion.com/documentation/skeletal/java/api/Leap_Classes.html).


##Instructions
- Start LeapRecorder by double-clicking the .jar file.
- Press "Start/Reset" to start recording, pressing it again will clear the recorded data.
- Press "Stop" to stop recording.
- Use "Save All" to save all information, or "Save w/o bones" to exclude bone information (which uses a lot of space).
- Use "Open directory" to open the folder where all data is saved.


##Requirements
- [Java 7](http://java.com/en/download/windows_manual.jsp?locale=en)
- Windows (has not been tried on other OSs)

---
Copyright &copy; 2014 Jakob Hjelm (Komposten)
 