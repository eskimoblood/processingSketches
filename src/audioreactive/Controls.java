package audioreactive;

import controlP5.ControlP5;
import controlP5.ControlWindow;
import controlP5.Slider;

/**
 * Created by IntelliJ IDEA.
 * User: akoeberle
 * <p/>
 * Render Slider in separate window
 */
public class Controls {

    public static void startControls(GLSLNoiseInput p) {
        ControlP5 controlP5;

        ControlWindow controlWindow;

        Slider baseRadiusSlider;

        Slider noiseScaleXSlider;
        controlP5 = new ControlP5(p);
        controlP5.setAutoDraw(false);
        controlWindow = controlP5.addControlWindow("controlP5window", 100, 100, 400, 650);
        controlWindow.hideCoordinates();

        controlWindow.setBackground(p.color(40));

        baseRadiusSlider = controlP5.addSlider("baseRadius", 0, 500, p.baseRadius, 10, 10, 100, 10);
        baseRadiusSlider.setWindow(controlWindow);

        noiseScaleXSlider = controlP5.addSlider("damp", 0, 3, p.noiseScaleX, 10, 50, 100, 10);
        noiseScaleXSlider.setWindow(controlWindow);

        baseRadiusSlider = controlP5.addSlider("duration", 0, 2000, p.noiseScaleY, 10, 90, 100, 10);
        baseRadiusSlider.setWindow(controlWindow);

        baseRadiusSlider = controlP5.addSlider("cnt", .5f, 10, p.noiseScaleZ, 10, 130, 100, 10);
        baseRadiusSlider.setWindow(controlWindow);

        baseRadiusSlider = controlP5.addSlider("border", 0, 100, p.border, 10, 170, 100, 10);
        baseRadiusSlider.setWindow(controlWindow);

        baseRadiusSlider = controlP5.addSlider("wi", 0, 300, p.wi, 10, 210, 100, 10);
        baseRadiusSlider.setWindow(controlWindow);

        baseRadiusSlider = controlP5.addSlider("peakholdtime", 0, 5, p.peak_hold_time, 10, 250, 100, 10);
        baseRadiusSlider.setWindow(controlWindow);

        baseRadiusSlider = controlP5.addSlider("ageDump", 0, 10, p.ageDump, 10, 290, 100, 10);
        baseRadiusSlider.setWindow(controlWindow);

        baseRadiusSlider = controlP5.addSlider("preScaleY", 0, 10, p.preScaleY, 10, 330, 100, 10);
        baseRadiusSlider.setWindow(controlWindow);

        baseRadiusSlider = controlP5.addSlider("preTranslateX", -10, 10, p.preTranslateX, 10, 370, 100, 10);
        baseRadiusSlider.setWindow(controlWindow);

        baseRadiusSlider = controlP5.addSlider("preTranslateY", -10, 10, p.preTranslateY, 10, 410, 100, 10);
        baseRadiusSlider.setWindow(controlWindow);

        controlP5.addSlider("L00_0", -1, 1, 0, 40, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L00_1", -1, 1, 0, 60, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L00_2", -1, 1, 0, 80, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L1m1_0", -1, 1, 0, 120, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L1m1_1", -1, 1, 0, 140, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L1m1_2", -1, 1, 0, 160, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L10_0", -1, 1, 0, 180, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L10_1", -1, 1, 0, 200, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L10_2", -1, 1, 0, 220, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L11_0", -1, 1, 0, 240, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L11_1", -1, 1, 0, 260, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L11_2", -1, 1, 0, 280, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L2m1_0", -1, 1, 0, 300, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L2m1_1", -1, 1, 0, 320, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L2m1_2", -1, 1, 0, 340, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L20_0", -1, 1, 0, 360, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L20_1", -1, 1, 0, 380, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L20_2", -1, 1, 0, 400, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L21_0", -1, 1, 0, 420, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L21_1", -1, 1, 0, 440, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L21_2", -1, 1, 0, 460, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L22_0", -1, 1, 0, 480, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L22_1", -1, 1, 0, 500, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L22_2", -1, 1, 0, 520, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("widthX", 0, 1f, 0, 40, 300, 10).setTab(controlWindow, "pattern");
        controlP5.addSlider("widthY", 0, 1f, 0, 60, 300, 10).setTab(controlWindow, "pattern");
        controlP5.addSlider("widthZ", 0, 1f, 0, 80, 300, 10).setTab(controlWindow, "pattern");
        controlP5.addSlider("widthQ", 0, 100f, 0, 100, 300, 10).setTab(controlWindow, "pattern");
        controlP5.addSlider("widthW", 0, 5f, 0, 120, 300, 10).setTab(controlWindow, "pattern");
        controlP5.addSlider("widthL", -1f, 1f, 0, 120, 300, 10).setTab(controlWindow, "pattern");

    }
}
