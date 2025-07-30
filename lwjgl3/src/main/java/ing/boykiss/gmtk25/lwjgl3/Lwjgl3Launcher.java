package ing.boykiss.gmtk25.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import ing.boykiss.gmtk25.Main;

import static com.badlogic.gdx.Gdx.*;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        new Thread(() -> {
            // For whatever reason ImGui doesn't get initialized here if I don't print
            // I decided to just let this black magic stay
            while (true) {
                if (app == null) {
                    System.out.print("");
                    continue;
                }
                if (graphics == null) {
                    System.out.print("");
                    continue;
                }
                app.postRunnable(() -> {
                    Main.imGuiGlfw = new ImGuiImplGlfw();
                    Main.imGuiGl3 = new ImGuiImplGl3();
                    long windowHandle = ((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle();
                    ImGui.createContext();
                    ImGuiIO io = ImGui.getIO();
                    io.setIniFilename(null);
                    io.getFonts().addFontDefault();
                    io.getFonts().build();
                    Main.imGuiGlfw.init(windowHandle, true);
                    Main.imGuiGl3.init("#version 150");
                    Main.imGuiInitialized = true;
                    System.out.println("initialized imgui");
                });
                break;
            }
        }).start();
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("GMTK25");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

        configuration.setWindowedMode(1280, 720);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
