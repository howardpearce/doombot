package examples;

import vizdoom.*;
import sbbj_tpg.*;
import java.util.*;
import java.lang.*;

public class Basic {

    public static void main (String[] args) {

        System.out.println("\n\nBASIC EXAMPLE\n");

        // Create DoomGame instance. It will run the game and communicate with you.
        DoomGame game = new DoomGame();

        // Sets path to vizdoom engine executive which will be spawned as a separate process. Default is "./vizdoom".
        game.setViZDoomPath("C:\\Users\\Howard Pearce\\Desktop\\AI_V4\\src\\vizdoom");

        // Sets path to doom2 iwad resource file which contains the actual doom game-> Default is "./doom2.wad".
        game.setDoomGamePath("C:\\Users\\Howard Pearce\\Desktop\\AI_V4\\src\\scenarios\\freedoom2.wad");
        //game.setDoomGamePath("../../bin/doom2.wad");   // Not provided with environment due to licences.

        // Sets path to additional resources iwad file which is basically your scenario iwad.
        // If not specified default doom2 maps will be used and it's pretty much useles... unless you want to play doom.
        game.setDoomScenarioPath("C:\\Users\\Howard Pearce\\Desktop\\AI_V4\\src\\scenarios\\basic.wad");

        // Set map to start (scenario .wad files can contain many maps).
        game.setDoomMap("map01");

        // Sets resolution. Default is 320X240
        game.setScreenResolution(ScreenResolution.RES_640X480);

        // Sets the screen buffer format. Not used here but now you can change it. Defalut is CRCGCB.
        game.setScreenFormat(ScreenFormat.RGB24);

        // Sets other rendering options
        game.setRenderHud(false);
        game.setRenderCrosshair(false);
        game.setRenderWeapon(true);
        game.setRenderDecals(false);
        game.setRenderParticles(false);
        game.setRenderEffectsSprites(false);
        game.setRenderMessages(false);
        game.setRenderCorpses(false);

        // Adds buttons that will be allowed.
        Button[] availableButtons = new Button [] {Button.MOVE_LEFT, Button.MOVE_RIGHT, Button.ATTACK};
        game.setAvailableButtons(availableButtons);
        // game.addAvailableButton(Button.MOVE_LEFT); // Appends to available buttons.
        // game.addAvailableButton(Button.MOVE_RIGHT);
        // game.addAvailableButton(Button.ATTACK);

        // Returns table of available Buttons.
        // Button[] availableButtons = game.getAvailableButtons();

        // Adds game variables that will be included in state.
        game.addAvailableGameVariable(GameVariable.AMMO2);
        // game.setAvailableGameVariables is also available.

        // Returns table of available GameVariables.
        // GameVariable[] availableGameVariables = game.getAvailableGameVariables();

        // Causes episodes to finish after 200 tics (actions)
        game.setEpisodeTimeout(200);

        // Makes episodes start after 10 tics (~after raising the weapon)
        game.setEpisodeStartTime(10);

        // Makes the window appear (turned on by default)
        game.setWindowVisible(true);

        // Turns on the sound. (turned off by default)
        game.setSoundEnabled(true);

        // Sets ViZDoom mode (PLAYER, ASYNC_PLAYER, SPECTATOR, ASYNC_SPECTATOR, PLAYER mode is default)
        game.setMode(Mode.PLAYER);

        // Enables engine output to console.
        //game.setConsoleEnabled(true);

        // Initialize the game. Further configuration won't take any effect from now on.
        game.init();

        // Define some actions. Each list entry corresponds to declared buttons:
        // MOVE_LEFT, MOVE_RIGHT, ATTACK
        // more combinations are naturally possible but only 3 are included for transparency when watching.
        List<double[]> actions = new ArrayList<double[]>();
        actions.add(new double[] {1, 0, 1});
        actions.add(new double[] {0, 1, 1});
        actions.add(new double[] {0, 0, 1});

        // Example Code execution when interacting with any API:

        // Create a TPG instance with the parameters file and training flag
        TPGAlgorithm tpgAlgorithm = new TPGAlgorithm("C:\\Users\\Howard Pearce\\Desktop\\AI_V4\\src\\parameters.arg", "learn");

        // Grab the TPG learning interface from the wrapper object
        TPGLearn tpg = tpgAlgorithm.getTPGLearn();

        // Get the action pool from the API and give it to TPG in the form of a long array (long[])
        tpg.setActions( new long[] {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L} );

        // Run the initialize method to create Team/Learner populations and prep for beginning learning
        tpg.initialize();

        // Create a variable for holding reward
        double reward = 0.0;

        // Create an array for holding features
        double[] inputFeatures = null;

        // Create a variable for the number of iterations
        int numberOfIterations = 1000;

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
                    while( game.isEpisodeFinished() )
                    {

                        // Convert the gameState to a double[] somehow. This is a 5 feature space. A very small frame.
                        inputFeatures = new double[]{1.0, 2.1, 3.2, 4.3, 5.4};

                        // Accumulate the reward by getting TPG to play. TPG receives the input features,
                        // then returns an action label, which is enacted on the environment. The environment
                        // then returns a reward which can be applied immediately or stored for later use,
                        // depending on what you want the algorithm to do.
                        reward += game.makeAction(actions.get((int)tpg.participate( inputFeatures ) - 1));


                    }

                    // Reward the current Team. This automatically rotates the current Team.
                    // The "game" string should be unique to the game the Team just played.
                    // In single-game learning just make it static, but when you move on to
                    // playing multiple games, you'll need to make sure the labels are correct.
                    tpg.reward( "game", reward );
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

        // It will be done automatically in destructor but after close You can init it again with different settings.
        game.close();
    }

    public static double actOnEnvironment( long action )
    {
        // Return the action multiplied by 1000 * a value in [0.0,1.0) for a simple reward simulation.
        // Adjust this reward to see varying TPG growth behaviour. For example, setting this to an
        // action multiplied by some constant will see TPG learning to spit out large actions all the time.
        return action * 1000 * Math.random();
    }

}
