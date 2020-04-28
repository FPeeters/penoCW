import testbed.engine.Engine;
import testbed.engine.IWorldRules;
import testbed.gui.ConfigSetupGUI;
import utils.Constants;


public class Main {

    public static void main(String[] args) {
        try {
            ConfigSetupGUI gui = new ConfigSetupGUI();
            IWorldRules worldRules = gui.showDialog();

            //create a game engine
            Engine gameEng = new Engine(Constants.TITLE, Constants.VSYNC, worldRules);
            //start the game loop
            gameEng.start();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}