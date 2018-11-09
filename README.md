# AI_PROJECT

CSCI 3154 group project, getting an AI to play DOOM

Setting this up will be different for each person looking to run vizDoom, I'll provide my code and describe how I got it running.

This is assuming IntelliJ is being used, let me know if anyone wants to use eclipse and we'll work through that.

Ensure you have/are using a 64 bit JDK, otherwise you will get an error

## How to install:

  ### 1: Download the repository
  
   i) Put it inside a new project such that everything is contained inside the SRC folder
  
  ### 2: Configure the project structure to use vizdoom.jar
  
   i) Go to `File->Project Structure`
   
   ii) Ensure you're looking at the modules tab, and you choose the dependency tab aswell
   
   iii) Click on the + button on the right hand side of the window
   
   iv) JARs or Directories, then select vizdoom.jar (This has all the external classes inside it (DoomGame.java, Button.java, etc.)
    
  ### 3: Change the paths
  
   i) I'm using `Basic.java` (a pre-provided example) currently. You need to set paths for lines 18, 21, and 26.
   
   ii) Use absolute paths that point directly at the resources needed
    
  ### 4: Change run configurations
  
   i) Go to `Run->Edit Configurations`
   
   ii) Inside of VM options put `-Djava.library.path="C:\Users\Cracker\Desktop\AI_5\src"`
   
   iii) You will need to change the pathing to your SRC folder on your own computer
    
  ### 5: Run!
  
   i) Be careful, I disabled the sound but it still runs some times and it's VERY loud.
    
