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

import static com.badlogic.gdx.Gdx.*;
import static com.badlogic.gdx.graphics.GL20.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    public static final int MAX_TPS = 60; // like minecraft :3

    public static final int VIEWPORT_WIDTH = 320;
    public static final int VIEWPORT_HEIGHT = 180;

    public float timePerTick;
    public float timePerFrame;
    private long prevTime;

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
    }

    @Override
    public void render() {
        // This limits the tick rate to our MAX_TPS constant
        long currTime = System.nanoTime();
        if ((currTime - prevTime) / 1_000_000_000f >= 1.0f / MAX_TPS) {
            tick();
            timePerTick = (currTime - prevTime) / 1_000_000_000f;
            prevTime = currTime;
        }

        // This is to calculate our FPS
        timePerFrame = (currTime - prevTime) / 1_000_000_000f;
        prevTime = currTime;

        gl.glClear(GL_COLOR_BUFFER_BIT);

        backStage.act();
        backStage.draw();

        stage.act();
        stage.draw();
    }

    public void tick() {

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
        backStage.dispose();
        stage.dispose();
    }
}
