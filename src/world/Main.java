package world;

import engine.Engine;
import engine.IWorldRules;
import utils.Constants;

import java.awt.image.BufferedImage;

public class Main {

    public static void main(String[] args) {
        try {
            //create a world, this will hold the game objects
            IWorldRules worldRules = new TestWorld();
            //create a game engine
            Engine gameEng = new Engine(Constants.TITLE, Constants.WIDTH, Constants.HEIGHT, Constants.VSYNC, worldRules);
            //start the game loop
            gameEng.start();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}