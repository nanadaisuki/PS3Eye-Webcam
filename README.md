# PS3Eye-Webcam
Can called by opencv.

This program only provides streaming function. To start this program, you need to install java, unzip PS3Eye_GUI.zip, and put those jars in the lib folder. 

PS3Eye_GUI.zip: https://github.com/diwi/PS3Eye
 
  
Example address: 
  http://localhost:8887/
  
Example call: 
  cv::VideoCapture("http://localhost:8887/", 0);
  
Fill in this address into the OpenCV program to get the video stream.
