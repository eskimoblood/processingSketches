package audioreactive;

import codeanticode.glgraphics.*;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import javax.media.opengl.GL;
import java.util.ArrayList;

public class GLSLNoiseInput extends PApplet {

    private Audio audio;

    PeasyCam cam;


    static public void main(String args[]) {
        PApplet.main(new String[]{ /* "--present", */"audioreactive.GLSLNoiseInput"});
    }

    private int colorCnt = 0;


    private int gradientLength = 10;

    private int[] colors;

    private GLSLShader shader;


    private GLTexture tex;

    private PGraphics texImage;

    private GLModel model;

    /*
   * Uniforms used in the shader, controls by p5 slider form {@link Controls}
    */
    float baseRadius = 50;

    protected int ageDump = 1;

    protected float preScaleY = 1;

    protected float preTranslateX = 0;

    protected float preTranslateY = 0;

    protected float noiseScaleX = 0;

    protected float noiseScaleY = 0;

    protected float noiseScaleZ = 0;

    private float duration = 1;

    private float cnt = 1;

    protected float damp = .2f;

    protected float L00_0 = 0.3783264f;
    protected float L00_1 = 0.4260425f;
    protected float L00_2 = 0.4504587f;

    protected float L1m1_0 = 0.2887813f;
    protected float L1m1_1 = 0.3586803f;
    protected float L1m1_2 = 0.4147053f;

    float L10_0 = 0.0379030f;
    float L10_1 = 0.0295216f;
    float L10_2 = 0.0098567f;

    float L11_0 = -0.1033028f;
    float L11_1 = -0.1031690f;
    float L11_2 = -0.0884924f;

    float L2m2_0 = -0.0621750f;
    float L2m2_1 = -0.0554432f;
    float L2m2_2 = -0.0396779f;

    float L2m1_0 = 0.0077820f;
    float L2m1_1 = -0.0148312f;
    float L2m1_2 = -0.0471301f;


    float L20_0 = -0.0935561f;
    float L20_1 = -0.1254260f;
    float L20_2 = -0.1525629f;

    float L21_0 = -0.0572703f;
    float L21_1 = -0.0572703f;
    float L21_2 = -0.0363410f;

    float L22_0 = 0.0203348f;
    float L22_1 = -0.0044201f;
    float L22_2 = -0.0452180f;

    protected float widthX = 0.05f, widthY = 0.05f, widthZ = 0.05f, widthQ = 0.05f, widthW = 0.05f, widthL = 0.05f;

    protected float peak_hold_time;

    protected float border = 1;

    protected float wi = 10;

    public void setup() {
        size(1024, 720, GLConstants.GLGRAPHICS);

        audio = new Audio(this);

        Controls.startControls(this);

        initShader();
        initRaster();
        cam = new PeasyCam(this, 100);
        cam.setMinimumDistance(1);
        cam.setMaximumDistance(1500);

    }


    private void initRaster() {
        ArrayList<PVector> vertices = new ArrayList<PVector>();
        ArrayList<Integer> indices = new ArrayList<Integer>();

        float numU = 1, numV = 1;
        float cnt = numU / audio.getPeakSize();

        int size = audio.getPeakSize();
        println(1.0 / size);
        for (int i = 0; i <= size; i++) {
            for (int j = 0; j <= size; j++) {
                float x = 1.0f / size * i;
                float y = 1.0f / size * j;

                vertices.add(new PVector(x, y));


                if (i < size && j < size) {
                    int s = size + 1;

                    int p1 = i * s + j;
                    int p2 = (i + 1) * s + j;
                    int p3 = i * s + j + 1;
                    int p4 = (i + 1) * s + j + 1;

                    indices.add(p1);
                    indices.add(p2);
                    indices.add(p3);

                    indices.add(p2);
                    indices.add(p4);
                    indices.add(p3);

                }
            }
        }


        int[] in = new int[indices.size()];

        for (int i = 0; i < indices.size(); i++) {

            in[i] = indices.get(i);
        }

        model = new GLModel(this, vertices, TRIANGLES, GLModel.STATIC);
        model.initIndices(in.length);
        model.updateIndices(in);
    }

    private void initShader() {
        shader = new GLSLShader(this, "shader/inputNoiseVert.glsl", "shader/inputNoiseFrag.glsl");
        tex = new GLTexture(this);
        tex.init(audio.getPeakSize(), audio.getPeakSize());
        println(audio.getPeakSize());
    }

    public void draw() {


        background(50);
        setAudioDataToGraphic();
        audio.updatePeaks();
        updateShader();
    }

    private void updateShader() {
        GLGraphics renderer = (GLGraphics) g;
        GL gl = renderer.gl;
        renderer.beginGL();
        lights();
        //  ambient(120);
        // The light is drawn after applying the translation and
        // rotation trasnformations, so it always shines on the
        // same side of the torus.

        gl.glDisable(GL.GL_COLOR_MATERIAL);
        // gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE);
        gl.glColor3f(0.7f, 0.7f, 0.7f);

        float mat_specular[] = {0.7f, 0.7f, 0.7f, 1.0f};
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, mat_specular, 0); // specular


        float mat_shininess[] = {1};
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, mat_shininess, 0); // specular


        float mat_diffuseb[] = {0.9f, 0.9f, 0.9f, 1.0f};
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuseb, 0);

        float mat_diffuse[] = {0.9f, 0.9f, 0.9f, 1.0f};
        gl.glMaterialfv(GL.GL_BACK, GL.GL_DIFFUSE, mat_diffuse, 0);

        float light0_diffuse[] = {0.5f, 0.5f, 0.5f, 1};
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_DIFFUSE, light0_diffuse, 0);

        float light0_ambient[] = {0.8f, 0.8f, 0.8f, 1};
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_AMBIENT, light0_ambient, 0);
        gl.glEnable(GL.GL_LIGHT0);
        shader.start();


        shader.setFloatUniform("BaseRadius", baseRadius);
        shader.setFloatUniform("duration", duration);
        shader.setFloatUniform("cnt", cnt);
        shader.setFloatUniform("border", border);
        shader.setFloatUniform("width", wi);
        shader.setIntUniform("permTexture", 0);

        shader.setVecUniform("LightPos", 0.0f, 10.0f, 0.0f);
        shader.setVecUniform("BaseColor", 1, 1, 1);
        shader.setFloatUniform("MixRatio", 0.5f);
        shader.setIntUniform("EnvMap", 10);

        shader.setVecUniform("LightPosition", 0.0f, 10.0f, 0.0f);
        shader.setFloatUniform("ScaleFactor", 1.0f);
        shader.setFloatUniform("far", 700.0f);

        shader.setVecUniform("L00", L00_0, L00_1, L00_2);
        shader.setVecUniform("L1m1", L1m1_0, L1m1_1, L1m1_2);
        shader.setVecUniform("L10", L10_0, L10_1, L10_2);
        shader.setVecUniform("L11", L11_0, L11_1, L11_2 * .75f);
        shader.setVecUniform("L2m2", L2m2_0, L2m2_1, L2m2_2 * .75f);
        shader.setVecUniform("L2m1", L2m1_0, L2m1_1, L2m1_2 * .75f);
        shader.setVecUniform("L20", L20_0, L20_1, L20_2 * .5f);
        shader.setVecUniform("L21", L21_0, L21_1, L21_2 * .5f);
        shader.setVecUniform("L22", L22_0, L22_1, L22_2 * .5f);


        shader.setFloatUniform("widthX", widthX);
        shader.setFloatUniform("widthY", widthY);
        shader.setFloatUniform("widthZ", widthZ);
        shader.setFloatUniform("widthQ", widthQ);
        shader.setFloatUniform("widthW", widthW);
        shader.setFloatUniform("widthL", widthL);

        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(tex.getTextureTarget(), tex.getTextureID());


        renderer.model(model);

        shader.stop();
        renderer.endGL();
    }


    void setAudioDataToGraphic() {
        tex.loadPixels();

        int peakSize = audio.getPeakSize();
        int half = peakSize / 2;
        float[] peaks = audio.getPeaks();

        for (int i = peakSize - 1; i > 0; i--) {
            for (int j = 0; j < peakSize; j++) {
                int color = tex.pixels[(i - 1) * peakSize + j];
                tex.pixels[i * peakSize + j] = color;
            }

        }
//        for (int i = 0; i < half; i++) {
//            for (int j = 0; j < peakSize; j++) {
//                int color = tex.pixels[(i + 1) * peakSize + j];
//                tex.pixels[(i) * peakSize + j] = color;
//            }
//
//        }
        for (int i = 0; i < peakSize; i++) {
            float peak = peaks[i] / 1f;

            tex.pixels[i] = color(peak);
            //tex.pixels[half * peakSize + i] = color(peak);
        }

        tex.loadTexture();
    }


}
