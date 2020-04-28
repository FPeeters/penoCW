package testbed.engine;

import utils.IO.MouseInput;

/**
 * Set of functions every world object needs
 */
public interface IWorldRules {

    void init(Window window, Engine engine);

    void input(Window window, MouseInput mouseInput);

    void update(float interval, MouseInput mouseInput);

    void render(Window window);

    void cleanup();

    void endSimulation();
}