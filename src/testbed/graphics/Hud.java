package testbed.graphics;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.system.MemoryUtil;

import testbed.Physics;
import testbed.engine.Window;
import utils.FloatMath;
import utils.Utils;

import static org.lwjgl.system.MemoryUtil.NULL;

public class Hud {

    private static final String FONT_NAME = "BOLD";
    private long vg;
    private NVGColor colour;
    @SuppressWarnings("FieldCanBeLocal")
    private ByteBuffer fontBuffer;
    private DoubleBuffer posx;
    private DoubleBuffer posy;

    public void init() throws Exception {
        this.vg = nvgCreate(NVG_STENCIL_STROKES);
        if (this.vg == NULL) {
            throw new Exception("Could not init nanovg");
        }

        fontBuffer = Utils.ioResourceToByteBuffer("/fonts/OpenSans-Bold.ttf", 150 * 1024);
        int font = nvgCreateFontMem(vg, FONT_NAME, fontBuffer, 0);
        if (font == -1) {
            throw new Exception("Could not add font");
        }
        colour = NVGColor.create();

        posx = MemoryUtil.memAllocDouble(1);
        posy = MemoryUtil.memAllocDouble(1);
    }

    private void clear(int textAreaWidth, int textAreaHeight) {
        glEnable(GL_SCISSOR_TEST);

        // clear all just to be sure
        glScissor(0, 0, textAreaWidth, textAreaHeight);
        glClearColor(1f, 1f, 1f, 0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glDisable(GL_SCISSOR_TEST);
    }

    public void render(Window window, Physics physics, float time) {

        int textAreaWidth = (int) (window.getWidth() * 0.25);
        int textAreaHeight = (int) (window.getHeight() * 0.5);
        int textAreaX = 0;
        int textAreaY = 0;

        clear(textAreaWidth, textAreaHeight);

        glViewport(textAreaX, textAreaY, textAreaWidth, textAreaHeight);

        nvgBeginFrame(vg, textAreaWidth, textAreaHeight, 1);


        // top text
        nvgFontFace(vg, FONT_NAME);
        nvgFontSize(vg, 25.0f);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgFillColor(vg, rgba(30, 30, 30, 255, colour));
        nvgText(vg, textAreaWidth / 2f, 15, "Drone statistics");

        // drone name
        nvgFontFace(vg, FONT_NAME);
        nvgFontSize(vg, 25.0f);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgText(vg, textAreaWidth / 2f, 75, physics.getConfig().getDroneID());

        // categories
        nvgFontFace(vg, FONT_NAME);
        int size = 25;
        nvgFontSize(vg, size);
        nvgFillColor(vg, rgba(30, 30, 30, 255, colour));
        int first = textAreaHeight / 4;
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgText(vg, textAreaWidth / 5f, first, "Pos (m)");
        nvgTextAlign(vg, NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);
        nvgText(vg, textAreaWidth * 7f / 10, first - size, "x:");
        nvgText(vg, textAreaWidth * 7f / 10, first, "y:");
        nvgText(vg, textAreaWidth * 7f / 10, first + size, "z:");

        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        nvgText(vg, textAreaWidth * 7f / 10, first - size, " " + FloatMath.round(physics.getPosition().x, 2));
        nvgText(vg, textAreaWidth * 7f / 10, first, " " + FloatMath.round(physics.getPosition().y, 2));
        nvgText(vg, textAreaWidth * 7f / 10, first + size, " " + FloatMath.round(physics.getPosition().z, 2));


        int second = textAreaHeight * 2 / 4;
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgText(vg, textAreaWidth / 5f, second, "Vel (m/s)");

        nvgTextAlign(vg, NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);
        nvgText(vg, textAreaWidth * 7f / 10, second - size, "x:");
        nvgText(vg, textAreaWidth * 7f / 10, second, "y:");
        nvgText(vg, textAreaWidth * 7f / 10, second + size, "z:");
        nvgText(vg, textAreaWidth * 7f / 10, second + 2 * size, "norm:");


        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        nvgText(vg, textAreaWidth * 7f / 10, second - size, " " + FloatMath.round(physics.getVelocity().x, 2));
        nvgText(vg, textAreaWidth * 7f / 10, second, " " + FloatMath.round(physics.getVelocity().y, 2));
        nvgText(vg, textAreaWidth * 7f / 10, second + size, " " + FloatMath.round(physics.getVelocity().z, 2));
        nvgText(vg, textAreaWidth * 7f / 10, second + 2 * size, " " + FloatMath.round(FloatMath.norm(physics.getVelocity()), 2));


        int third = textAreaHeight * 3 / 4;
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgText(vg, textAreaWidth / 5f, third, "Orient (deg)");
        nvgTextAlign(vg, NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);
        nvgText(vg, textAreaWidth * 7f / 10, third - size, "pitch:");
        nvgText(vg, textAreaWidth * 7f / 10, third, "heading:");
        nvgText(vg, textAreaWidth * 7f / 10, third + size, "roll:");

        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        nvgText(vg, textAreaWidth * 7f / 10, third - size, " " + FloatMath.round(FloatMath.toDegrees(physics.getPitch()), 2));
        nvgText(vg, textAreaWidth * 7f / 10, third, " " + FloatMath.round(FloatMath.toDegrees(physics.getHeading()), 2));
        nvgText(vg, textAreaWidth * 7f / 10, third + size, " " + FloatMath.round(FloatMath.toDegrees(physics.getRoll()), 2));

        // Gele balk
        nvgBeginPath(vg);
        nvgRect(vg, 0, textAreaHeight - 50, textAreaWidth, 50);
        nvgFillColor(vg, rgba(255, 255, 0, 150, colour));
        nvgFill(vg);

        // Team geel text en tijd sinds start
        nvgFontFace(vg, FONT_NAME);
        nvgFontSize(vg, 40.0f);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgFillColor(vg, rgba(30, 30, 30, 255, colour));
        nvgText(vg, textAreaWidth / 4f, textAreaHeight - 25, "Team Geel");

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        nvgText(vg, textAreaWidth * 3f / 4, textAreaHeight - 25, "" + df.format(new Date((int) (time * 1000) + (23 * 60 * 60 * 1000))));

        nvgEndFrame(vg);

        // Restore state
        window.restoreState();
    }


    private NVGColor rgba(int r, int g, int b, int a, NVGColor colour) {
        colour.r(r / 255.0f);
        colour.g(g / 255.0f);
        colour.b(b / 255.0f);
        colour.a(a / 255.0f);

        return colour;
    }

    public void cleanup() {
        nvgDelete(vg);
        if (posx != null) {
            MemoryUtil.memFree(posx);
        }
        if (posy != null) {
            MemoryUtil.memFree(posy);
        }
    }
}