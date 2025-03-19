# Engaging Video Player

This project is a Java application that utilizes vlcj and Swing to create an engaging video player. The goal is to create a media player that will play media with additional side panels that could be used to display extra content.
![image](https://github.com/user-attachments/assets/7fd40dee-1413-4e7e-b3b8-32c4ca94a4f1)
*screenshot of engagment player tailored for a local business*
# Setup Instructions

## Prerequisites 

While I plan on releasing a "release" version of this project, for now the only way to use is to build from source.
The following are required to install and run this application.
- Unix ( The app installs using the $HOME enviornment variable and it doesn't account for systems that won't have that )
- Java 11 (OpenJDK 11 or higher?)
- VLC 3.x
- Maven 3.6.0 + (for handling dependencies)
- Google Application: `credentials.json` [*OPTIONAL*]

## Installation Steps

1. Clone the repository:

```sh
git clone https://github.com/imawful/jinzo.git
cd jinzo/
```

2. Install the app:

```sh
# ensure you are in the root directory of cloned repo
mvn install
```

3. Define Properties File

This application uses 3 properties:

- tokenStoragePath

Used to store google tokens for the account of the drive owner. Only needed if utilizing Google Drive Service.

- googleCredentialsPath

Used to store the main applications `credentials.json`. Only needed if utilizing Google Drive Service.

- downloadPath

Used to load media such as photos and videos. This path is **required** as without it the app would have no photos or videos to load.
You only need to specify the main folder ex: `$HOME/.jinzo/drive/`. You can set your playlists to grab videos from anywhere but we are using this folder
for easier management.

If you want to define a custom properties file you can place a `applications.properties` file in the `src/main/resources` directory.
The properties file defined in resources should only be used during developement since it should not be included in the JAR. This design
was to allow easier configuration once the application is built. The main properties file is loacated in `$HOME/.jinzo/application.properties`, this file
will be automatically generated for you and will utilize the main apps folder `.jinzo/`. The properties can be modified here but you would be responsible for ensuring
the values exist.

4. Import `credentials.json`

As mentioned before if you wish to utilize the google drive service you need to create a desktop application via googles developer console and download the corresponding `credentials.json` file. Then you must move this file into `$HOME/.jinzo/` (or where ever specified in `application.properties`. 

## How to run the application

1. Compile the application:

```sh
mvn clean compile
```

2. Run the application:

```sh
mvn exec:java
```
On first run the main applications folders will be generated along with any files. You can now store the `credentials.json` file in the main app directory if not done already.

Sequential runs should automatically start the engagment player in fullscreen.

# Features

## Side/Photo Panels

- Side panels can be used to show photos, text or other content alongside the main video. 

## Video Panel

- Video panels plays media content controlled by a Playlist object.

## Engagment Frame 

- JFrame that manages the window containing side panels and video panels.
- Plans for future flexability in order to manage various kinds of engaging layouts!

## Playlist

- Playlist object is responsible for controlling which media to pull from the downloaded videos path. 
- Manages **Media Resource Locators** to be passed to the video panel. 
- There is potential for all kinds of different Playlist types.
- Currently we have a circular queue playlist that will repeatedly play all the vidoes in the queue and circle back once it reaches the end.
- I am working on a good way to get the side panels to utilize playlists so we can show different images either by time or by checking the day.

## Services

- We uses services such as google drive service, to download content from the web to use in our application.
- Services would allow us to dynamically update the content that our engagment player can play.

## Syncs 

- This app currently relies on Java's Timer and TimerTasks to continously call services and update playlists. 
- We attempt to sync the program with drive in by repeadately making calls to download anything new in the drive.
- In the main method we setup a task to call `downloadMedia()` every minute which downloads any new files from the drive.
- We then build our playlist based off what we have downloaded.
- Before I would delete any local files that weren't in the drive but we no longer do that.
- Trying to come up with a better sync system so that we can even add remote control of the player.

# Technical Decisions

## Libraries Used

- vlcj 4.10.1 (vlc bindings)
- apache commons 2.18.0 (file utils)
- imgscalr 4.2 (for rescaling the side panel images)
- scala3-library (currently not in use)
- Junit 5.11 (i need to write tests)

## Architecutral Design

The engagnment player is a JFrame that handles a content panel (JPanel). The content panel uses a border layout and contains 2 side panels and 1 video panel. With the side panels being customizable to allow for any dimensions, photos, or text it makes for an engaging experience while playing videos from the video panel. The side panels can be used to display advertizments, extra content, or any relevant information.

I'm currently trying to modularize as much as I can in the project to allow for the most flexibility. In the future I would like to be able to create different kinds of engagment players but as it is now we only have one implementation. The Playlist class and classes in the Service package are what im trying to focus on the most. At the end of the day for the engament player to function it just needs to be told what videos to play (responsibility of Playlist) if we wanna get new videos automatically without interacting with the app we must create services to do so. The main service we are using now is Google Drive to be able to download videos from a drive and play them locally. With a good design of playlist I beleive this is where most of the flexibility will come in.

# Known Issues


- I'm really trying to focus on getting the main engagement frame to look good on multiple resoultions. I've really only been testing on my laptop which is 1920x1080 however not all screens are this resolution, especially TV screens. So, the few times where I have gotten to test different resolutions I came up with a scaling technique that will scale the side panel images based on the actual displays size and while it works fine I think there still appears to be some parts of the image not showing up properly (edges get trimmed slightly). I'm currently trying to find a way where I can reliably test different resolutions to better the current code I have for scaling.
- Another thing I really need to consider is how we can tell the program what videos and images to display. Currently my solutions have been coded into the application but I think I need to come up with a sort of configuration file so that it can load and display different videos and images on each run. I came across the issue when I had to set up this program on two seperate devices with both of them needing different images. I think I'm going to lean towards command line arguments for now and see if I can get that working.
- I will sometimes get a `java.net.SocketTimeException` or a `java.net.UnknownHostException`, from what I understand this is just my app timing out when trying to reach google drive. I'm not sure if there is anything I can do programatically but I have to make sure that my app is prepared for a failure like this. Before I would get video playback errors because I was deleting videos with the assumption that I would be able to download them again right way, but if I ever time out then the player will have nothing to play.
- There was some cases of exceptions crashing the program due to not finding certain files or directories, I tried to clean it up as much as I could by automatically creating directories if needed and using try catch where ever possible, but if paths aren't loaded in properly from `application.properties` the app could fail.
- Because the app installs using the $HOME enviornment variable there is not really compatability with non Unix systems so we need to change the start up in the Config class.

# Conclusion 

Thank you for checking out my Engaging Video Player project! This project is a work in progress and I'm utilizing Github as a way to share my progress and to practice VC in a professional manner. You are free to contact me with any questions or concerns. As I plan on working more on this project and improving upon it's design, I would appreciate any feedback or requests. Thank you for your time!

- Kevin Barrios
- barrik3@unlv.nevada.edu
