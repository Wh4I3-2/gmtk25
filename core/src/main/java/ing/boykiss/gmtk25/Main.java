package ing.boykiss.gmtk25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.ImGui;

import static com.badlogic.gdx.Gdx.*;
import static com.badlogic.gdx.graphics.GL20.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    public static ImGuiImplGlfw imGuiGlfw;
    public static ImGuiImplGl3 imGuiGl3;
    public static boolean imGuiInitialized;

    public static final int MAX_TPS = 60; // like minecraft :3

    public static final int VIEWPORT_WIDTH = 320;
    public static final int VIEWPORT_HEIGHT = 180;

    public float timePerTick;
    public float timePerFrame;
    private long prevTime;

    private boolean running = true;

    private Viewport backViewport;
    private Stage backStage;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private Image background;

    @Override
    public void create() {
        backViewport = new ScreenViewport();
        backStage = new Stage();
        backStage.setViewport(backViewport);

        background = new Image(new Texture("textures/fill.png"));
        background.setColor(Color.DARK_GRAY);

        backStage.addActor(background);

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        stage = new Stage();
        stage.setViewport(viewport);

        new Thread(() -> {
            long prevTime = System.nanoTime();
            while (running) {
                // This limits the tick rate to our MAX_TPS constant
                long currTime = System.nanoTime();
                if ((currTime - prevTime) / 1_000_000_000f < 1.0f / MAX_TPS) {
                    continue;
                }
                timePerTick = (currTime - prevTime) / 1_000_000_000f;
                prevTime = currTime;

                tick();
            }
        }).start();
    }

    @Override
    public void render() {
        // This is to calculate our FPS
        long currTime = System.nanoTime();
        timePerFrame = (currTime - prevTime) / 1_000_000_000f;
        prevTime = currTime;

        gl.glClear(GL_COLOR_BUFFER_BIT);

        backStage.act();
        backStage.draw();

        stage.act();
        stage.draw();

        renderImGui();
    }

    public void tick() {

    }

    // This should only be running when ImGui has been initialized, which is handled by the lwjgl3 launcher
    // This means that it won't show up on the HTML build
    // We should make sure to remove this in release builds, as ImGui should only be used for dev tools
    public void renderImGui() {
        if (!imGuiInitialized) {
            return;
        }
        imGuiGl3.newFrame();
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        // Insert ImGui UI here
        ImGui.begin("Performance");
        ImGui.text(String.format("TPS: %.2f", 1.0f / timePerTick));
        ImGui.text(String.format("FPS: %.2f", 1.0f / timePerFrame));
        ImGui.end();

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    @Override
    public void resize(int width, int height) {
        backViewport.update(width, height);
        viewport.update(width, height);
        background.setSize(width, height);
        background.setPosition(-width / 2.0f, -height / 2.0f);
    }

    // Called once the program stops
    // Should mostly be used for disposing Disposables
    @Override
    public void dispose() {
        running = false;

        backStage.dispose();
        stage.dispose();

        if (imGuiInitialized) {
            imGuiGl3.shutdown();
            imGuiGlfw.shutdown();
            ImGui.destroyContext();
            imGuiGlfw = null;
            imGuiGl3 = null;
        }
    }
}
