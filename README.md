# Blue Chat 🦜

## Android-Study-Jams
This project was submitted by me during faciliating Android App Development using Kotlin under Android Study Jams in my Campus [GDSC-GCE].

<b> Problem Statement: </b>

One of the challenges people face every day is unavailability of internet and wifi everywhere.
Technology plays a vital role in day-to-day life activities which in turn made great changes in many work fields and out of them Mobile Application is one of the major developments.
Mobile Application can be used effectively for this job as they are widely used and are known for easy access.

<b> Proposed Solution : </b>

This project proposes a “Bluetooth Chat App” named <b>Blue Chat </b>. Its features include you can use this app to chat with other person nearby without troubling him/her to connect to wifi.
It uses BluetoothAdapter API to get all the available devices and paired devices nearby. Upon clicking on devices, it enable you to chat to other devices.


Currently the app lets you connect one device at a time resulting you can chat to one device at a time.


<img width="200" alt="sampleimages" src="https://raw.githubusercontent.com/ambasta-shalu/blue-chat/master/App-Snapshot/snapshot1.jpg"> <img width="200" alt="sampleimages" src="https://raw.githubusercontent.com/ambasta-shalu/blue-chat/master/App-Snapshot/snapshot2.jpg"> <img width="200" alt="sampleimages" src="https://raw.githubusercontent.com/ambasta-shalu/blue-chat/master/App-Snapshot/snapshot3.jpg"> <img width="200" alt="sampleimages" src="https://raw.githubusercontent.com/ambasta-shalu/blue-chat/master/App-Snapshot/snapshot4.jpg">

<b> Functionality & Concepts used : </b>

- The App has a very simple and interactive interface which helps the user select devices.Following are few android concepts used to achieve the functionalities in app : 
- Constraint Layout : Most of the activities in the app uses a flexible constraint layout, which is easy to handle for different screen sizes.
- Simple & Easy Views Design : Use of familiar audience EditText with hints and interactive buttons made it easier for users move back and forth from one activity to another. App also uses App Navigation to switch between different screens.
- RecyclerView : To present the list of different devices nearby, I used the efficient recyclerview.
- BluetoothServer, BluetoothServerSocket : The interface for BluetoothSocket is similar to that of TCP sockets -Socket and ServerSocket. On the server side, I used a BluetoothServerSocket to create a listening server socket.
- LiveData & Room Database : I am also using LiveData to update & observe any changes in the BluetoothDevices Activity and update it to local databases using Room.

<b> Application Link & Future Scope : </b>

The app is currently in the Alpha testing phase with a limited no. of users.
You can access the app : <a href="https://github.com/ambasta-shalu/blue-chat/blob/master/app-debug.apk"> Here 🤓</a>

Once the app is fully tested and functional, I will plan to launch it on Google Play Store :)
