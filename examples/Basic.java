package examples;

import vizdoom.*;
import sbbj_tpg.*;

import java.io.IOException;
import java.util.*;
import java.lang.*;

public class Basic {

    final private static int SCREEN_SIZE = 307202;

    public static void main (String[] args) throws IOException {

        System.out.println("\n\nBASIC EXAMPLE\n");

        // Create DoomGame instance. It will run the game and communicate with you.
        DoomGame game = new DoomGame();

        // Sets path to vizdoom engine executive which will be spawned as a separate process. Default is "./vizdoom".
        game.setViZDoomPath("C:\\Users\\Cracker\\Desktop\\AI_5\\src\\vizdoom");

        // Sets path to doom2 iwad resource file which contains the actual doom game-> Default is "./doom2.wad".
        game.setDoomGamePath("C:\\Users\\Cracker\\Desktop\\AI_5\\src\\scenarios\\freedoom2.wad");
        //game.setDoomGamePath("../../bin/doom2.wad");   // Not provided with environment due to licences.

        // Sets path to additional resources iwad file which is basically your scenario iwad.
        // If not specified default doom2 maps will be used and it's pretty much useles... unless you want to play doom.
        game.setDoomScenarioPath("C:\\Users\\Cracker\\Desktop\\AI_5\\src\\scenarios\\defend_the_center.wad");

        // Set map to start (scenario .wad files can contain many maps).
        game.setDoomMap("map01");

        // Sets resolution. Default is 320X240
        game.setScreenResolution(ScreenResolution.RES_320X240);

        // Sets the screen buffer format. Not used here but now you can change it. Defalut is CRCGCB.
        game.setScreenFormat(ScreenFormat.RGB24);

        // Sets other rendering options
        game.setRenderHud(true);
        game.setRenderCrosshair(false);
        game.setRenderWeapon(true);
        game.setRenderDecals(false);
        game.setRenderParticles(false);
        game.setRenderEffectsSprites(false);
        game.setRenderMessages(false);
        game.setRenderCorpses(false);

        // Adds buttons that will be allowed.
        Button[] availableButtons = new Button [] {Button.TURN_LEFT, Button.TURN_RIGHT, Button.ATTACK};
        game.setAvailableButtons(availableButtons);

        // Returns table of available Buttons.
        // Button[] availableButtons = game.getAvailableButtons();

        // Adds game variables that will be included in state.
        game.addAvailableGameVariable(GameVariable.AMMO2);
        // game.setAvailableGameVariables is also available.

        // Returns table of available GameVariables.
        // GameVariable[] availableGameVariables = game.getAvailableGameVariables();

        // Causes episodes to finish after 200 tics (actions)
        // game.setEpisodeTimeout(200);

        // Makes episodes start after 10 tics (~after raising the weapon)
        game.setEpisodeStartTime(10);

        // Makes the window appear (turned on by default)
        game.setWindowVisible(true);


        // Sets ViZDoom mode (PLAYER, ASYNC_PLAYER, SPECTATOR, ASYNC_SPECTATOR, PLAYER mode is default)
        game.setMode(Mode.PLAYER);

        // Enables engine output to console.
        //game.setConsoleEnabled(true);

        // Initialize the game. Further configuration won't take any effect from now on.
        game.init();

        // Define some actions. Each list entry corresponds to declared buttons:
        // MOVE_LEFT, MOVE_RIGHT, ATTACK
        // more combinations are naturally possible but only 3 are included for transparency when watching.
        List<double[]> actions = new ArrayList<>();
        actions.add(new double[] {1, 0, 0});
        actions.add(new double[] {0, 1, 0});
        actions.add(new double[] {0, 0, 1});

        // Example Code execution when interacting with any API:

        // Create a TPG instance with the parameters file and training flag
        TPGAlgorithm tpgAlgorithm = new TPGAlgorithm("C:\\Users\\Cracker\\Desktop\\AI_5\\src\\parameters.arg", "learn");

        // Grab the TPG learning interface from the wrapper object
        TPGLearn tpg = tpgAlgorithm.getTPGLearn();

        // Get the action pool from the API and give it to TPG in the form of a long array (long[])
        tpg.setActions( new long[] {1L, 2L, 3L} );

        // Run the initialize method to create Team/Learner populations and prep for beginning learning
        tpg.initialize();

        // Create a variable for holding reward
        double reward;

        //input features array



        // Create a variable for the number of iterations
        int numberOfIterations = 100;

        // Keep a count of the number of games to play (learning dimensions)
        int gamesToPlay = 1;

        // Main Learning Loop
        for( int i=0; i < numberOfIterations; i++ )
        {
            for( int j=0; j < gamesToPlay; j++ )
            {
                // Let every Team play the current game once
                while( tpg.remainingTeams() > 0 )
                {

                    // Reset the reward to 0.
                    reward = 0.0;

                    // This while loop would normally be while( game.episode_still_running() ), but
                    // I don't have a game to simulate for you, so here I'm simply saying that each game
                    // runs for 10 "frames" before offering reward and moving to the next Team.



                    while( !game.isEpisodeFinished() )
                    {

                        int action = (int)tpg.participate(getFeatures(game.getState().screenBuffer)) - 1;

                        //System.out.println("Action chosen: " + action + " J = " + j + " I = " + i);

                        reward += game.makeAction(actions.get(action));

                    }

                    // Reward the current Team. This automatically rotates the current Team.
                    // The "game" string should be unique to the game the Team just played.
                    // In single-game learning just make it static, but when you move on to
                    // playing multiple games, you'll need to make sure the labels are correct.
                    tpg.reward( "game", reward );

                    game.newEpisode();
                }
            }

            // Print the current top 10 Team population outcomes and some simple environment values
            tpg.printStats(10);

            // Tell TPG to Perform Selection
            tpg.selection();

            // Tell TPG to Reproduce and Mutate with the current Teams
            tpg.generateNewTeams();

            // Reset TPG so it increases the generation count and finds the new Root Teams
            tpg.nextEpoch();

        }

        tpg.close();
        // It will be done automatically in destructor but after close You can init it again with different settings.
        game.close();
    }

    public static double[] getFeatures(byte buffer[]){


        double[] inputFeatures = new double[buffer.length/3];

        int counter = 0;

        //iterate over screen buffer
        for(int x = 0; x < buffer.length; x += 3){

            double rgb = ((buffer[x]&0x0FF)<<16) | ((buffer[x+1]&0x0FF)<<8) | ((buffer[x+2])&0x0FF);

            if(counter >= SCREEN_SIZE - 1){
                //System.out.println(counter + "\n");
            } else {
                inputFeatures[counter++] = rgb;
            }

        }

        return inputFeatures;

    }

}