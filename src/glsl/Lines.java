package glsl;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import controlP5.*;
import processing.core.PApplet;
import processing.core.PVector;
import toxi.geom.AABB;
import toxi.geom.Matrix4x4;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.LaplacianSmooth;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.SphericalHarmonics;
import toxi.geom.mesh.SurfaceMeshBuilder;
import toxi.geom.mesh.Vertex;
import toxi.geom.mesh.WETriangleMesh;
import toxi.geom.mesh.subdiv.MidpointSubdivision;
import toxi.geom.mesh.subdiv.SubdivisionStrategy;
import toxi.processing.ToxiclibsSupport;
import toxi.volume.MeshLatticeBuilder;
import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLSLShader;
import codeanticode.glgraphics.GLTexture;

import peasy.*;


public class Lines extends PApplet {

    /**
     * <p>
     * This example demonstrates the MeshVoxelizer utility to turn a given
     * triangle mesh into a volumetric representation for further manipulation.
     * E.g. This is useful for some digital fabrication tasks when only a shell
     * with a physical wall thickness is desired rather than a completely
     * solid/filled polygon model. Other use cases incl. experimentation with
     * VolumetricBrushes to drill holes into models etc.
     * </p>
     * <p/>
     * <p>
     * The MeshVoxelizer class is currently still in ongoing development, so any
     * feature requests/ideas/help is appreciated.
     * </p>
     * <p/>
     * <p>
     * <strong>Usage:</strong>
     * <ul>
     * <li>v: voxelize current mesh (see details in function comment)</li>
     * <li>l: apply laplacian mesh smooth</li>
     * <li>w: wireframe on/off</li>
     * <li>n: show normals on/off</li>
     * <li>r: reset mesh</li>
     * <li>-/=: adjust zoom</li>
     * </ul>
     * </p>
     */

    /*
      * Copyright (c) 2010 Karsten Schmidt
      *
      * This library is free software; you can redistribute it and/or modify it
      * under the terms of the GNU Lesser General Public License as published by
      * the Free Software Foundation; either version 2.1 of the License, or (at
      * your option) any later version.
      *
      * http://creativecommons.org/licenses/LGPL/2.1/
      *
      * This library is distributed in the hope that it will be useful, but
      * WITHOUT ANY WARRANTY; without even the implied warranty of
      * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
      * General Public License for more details.
      *
      * You should have received a copy of the GNU Lesser General Public License
      * along with this library; if not, write to the Free Software Foundation,
      * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
      */

    SubdivisionStrategy subdiv = new MidpointSubdivision();
    ToxiclibsSupport gfx;
    WETriangleMesh mesh;
    Matrix4x4 normalMap = new Matrix4x4().translateSelf(128, 128, 128)
            .scaleSelf(127);

    boolean isWireframe;
    float currZoom = 1.25f;

    boolean showNormals;

    GLSLShader shader;
    GLTexture tex0, texNoise;
    GLModel model;


    private PApplet p;
    protected float sliderValue1;
    protected float sliderValue2;
    protected float sliderValue3;
    protected float sliderValue4;
    protected float sliderValue5;
    protected float sliderValue6;
    protected float sliderValue7;
    protected float sliderValue8;
    protected boolean createMesh;
    protected float widthX = 0.05f, widthY = 0.05f, widthZ = 0.05f, widthQ = 0.05f, widthW = 0.05f, widthL = 0.05f;


    PeasyCam cam;

    public void setup() {
        size(600, 600, GLConstants.GLGRAPHICS);
        cam = new PeasyCam(this, 100);
//        cam.setMinimumDistance(50);
//        cam.setMaximumDistance(500);
        loadStrings("shinyfrag.glsl");
        initOpenGL();
        gfx = new ToxiclibsSupport(this);
        p = this;
        initMesh();
        initGUI();
    }


    public void draw() {
        if (createMesh) {
            initMesh();
            createMesh = false;
        }
        background(0);
//        translate(width / 2, height / 2, 0);
//        rotateX(mouseY * 0.01f);
//        rotateY(mouseX * 0.01f);
        scale(currZoom);
        if (!isWireframe) {
            fill(255);
            noStroke();
            lights();
        } else {
            gfx.origin(new Vec3D(), 100);
            noFill();
            stroke(0);
        }
        // gfx.meshNormalMapped(mesh, !isWireframe, showNormals ? 10 : 0);

        // runOpenGL();
        GLGraphics renderer = (GLGraphics) g;
        renderer.beginGL();
                lights();
        shader.start(); // Enabling shader.

        // spherical harmonics data
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
        // environment mapping data
        shader.setVecUniform("LightPos", 0.0f, 10.0f, 0.0f);
        shader.setVecUniform("BaseColor", 1, 1, 1);
        shader.setFloatUniform("MixRatio", 0.5f);
        shader.setIntUniform("EnvMap", 10);

        shader.setFloatUniform("widthX", widthX);
        shader.setFloatUniform("widthY", widthY);
        shader.setFloatUniform("widthZ", widthZ);
        shader.setFloatUniform("widthQ", widthQ);
        shader.setFloatUniform("widthW", widthW);
        shader.setFloatUniform("widthL", widthL);

        // gfx.mesh(mesh, true);
        renderer.model(model);
        // model.render();

        shader.stop();

        renderer.endGL();
    }

    // creates a simple cube mesh and applies displacement subdivision
    // on all edges for several iterations
    public void initMesh() {
        float[] m = new float[8];
        m[0] = (int) sliderValue1;
        m[1] = (int) sliderValue2;
        m[2] = (int) sliderValue3;
        m[3] = (int) sliderValue4;
        m[4] = (int) sliderValue5;
        m[5] = (int) sliderValue6;
        m[6] = (int) sliderValue7;
        m[7] = (int) sliderValue8;

//        float[] m = new float[8];
//		for (int i = 0; i < 8; i++) {
//			m[i] = (int) random(9);
//		}

        SurfaceMeshBuilder b = new SurfaceMeshBuilder(new SphericalHarmonics(m));
        Mesh3D tmpMesh = b.createMesh(null, 380, 80);
        mesh = new WETriangleMesh();
        mesh.addMesh(tmpMesh);
        mesh.computeFaceNormals();
        mesh.faceOutwards();
        mesh.computeVertexNormals();

        createModel();
    }

    public void voxelizeMesh() {
        // voxelize and then add stuff tomesh

        mesh = MeshLatticeBuilder.build(mesh, 60, 3.5f);

        createModel();
    }

    public void keyPressed() {
        if (key == 'w') {
            isWireframe = !isWireframe;
        }
        if (key == 'l') {
            new LaplacianSmooth().filter(mesh, 1);
            createModel();
        }
        if (key == '-') {
            currZoom -= 0.1f;
        }
        if (key == '=') {
            currZoom += 0.1f;
        }
        if (key == 'v') {
            voxelizeMesh();
        }
        if (key == 'n') {
            showNormals = !showNormals;
        }
        if (key == 'r') {
            initMesh();
        }
        if (key == 's') {
             save("lattice_" + (System.currentTimeMillis() / 1000) + ".png");
        }
    }



    public void createModel() {
        ArrayList vertices = new ArrayList();
        ArrayList normals = new ArrayList();
        ArrayList texcoords = new ArrayList();
        createToxicModelData(mesh, vertices, normals, texcoords);
        // create model data
        // model = new GLModel(this, vertices.size(), TRIANGLES,
        // GLModel.STATIC);
        int spacing = 4;
        mesh.computeVertexNormals();
        float[] verts = mesh.getMeshAsVertexArray();
        int numV = verts.length / spacing; // The vertices array from the mesh
        // object
        // has a spacing of 4.
        float[] norms = mesh.getVertexNormalsAsArray();


        model = new GLModel(p, numV, TRIANGLES, GLModel.STATIC);
        model.beginUpdateVertices();
        model.initNormals();
        model.beginUpdateNormals();
        for (int i = 0; i < numV; i++) {
            int count = i * 4;
            model.updateVertex(i, verts[count], verts[count + 1],
                    verts[count + 2]);

            model.updateNormal(i, norms[count], norms[count + 1],
                    norms[count + 2]);

        }
        model.endUpdateVertices();
        model.endUpdateNormals();

        model.initTextures(1);
        model.setTexture(0, tex0);

        // Setting the texture coordinates.
        model.updateTexCoords(0, texcoords);

    }

    /**
     * convert Toxis TriangleMesh to GLModel data
     */
    public void createToxicModelData(WETriangleMesh mesh, List vertices,
                                     ArrayList normals, ArrayList texcoords) {
        int num = mesh.faces.size();// mesh.getNumFaces();
        mesh.computeVertexNormals();
        Vec3D center = mesh.computeCentroid();
        AABB boundingBox = mesh.getBoundingBox();
        Vec3D boundingMin = boundingBox.getMin();
        Vec3D boundingMax = boundingBox.getMax();

        float minX = boundingMin.x;
        float maxX = boundingMax.x;
        float cellWidth = maxX - minX;

        for (int i = 0; i < num; i++) {
            Face f = mesh.faces.get(i);
            // get vertices and normals from face
            Vertex vertex0 = f.a;
            Vertex vertex1 = f.b;
            Vertex vertex2 = f.c;
            Vec3D normal0 = f.a.normal;
            Vec3D normal1 = f.b.normal;
            Vec3D normal2 = f.c.normal;
            // convert to PVector and add to the arrayLists
            vertices.add(new PVector(vertex0.x, vertex0.y, vertex0.z));
            vertices.add(new PVector(vertex1.x, vertex1.y, vertex1.z));
            vertices.add(new PVector(vertex2.x, vertex2.y, vertex2.z));
            normals.add(new PVector(normal0.x, normal0.y, normal0.z));
            normals.add(new PVector(normal1.x, normal1.y, normal1.z));
            normals.add(new PVector(normal2.x, normal2.y, normal2.z));

            // create uv coords

            // vertex 0
            float tmpCellX0 = (maxX - vertex0.x) / cellWidth;
            float dx0 = center.x - vertex0.x;
            float dy0 = center.y - vertex0.y;
            float dz0 = center.z - vertex0.z;
            PVector texTmp0 = new PVector(dx0, dy0, dz0);
            texTmp0.normalize();
            float uCoord0 = asin(texTmp0.x) / PI + .5f;
            float vCoord0 = asin(texTmp0.y) / PI + .5f;
            PVector tex0 = new PVector(uCoord0, vCoord0, 0);
            texcoords.add(tex0);

            // vertex 1
            float tmpCellX1 = (maxX - vertex1.x) / cellWidth;
            float dx1 = center.x - vertex1.x;
            float dy1 = center.y - vertex1.y;
            float dz1 = center.z - vertex1.z;
            PVector texTmp1 = new PVector(dx1, dy1, dz1);
            texTmp1.normalize();
            float uCoord1 = asin(texTmp1.x) / PI + .5f;
            float vCoord1 = asin(texTmp1.y) / PI + .5f;
            PVector tex1 = new PVector(uCoord1, vCoord1, 0);
            texcoords.add(tex1);

            // vertex 2
            float tmpCellX2 = (maxX - vertex2.x) / cellWidth;
            float dx2 = center.x - vertex2.x;
            float dy2 = center.y - vertex2.y;
            float dz2 = center.z - vertex2.z;
            PVector texTmp2 = new PVector(dx2, dy2, dz2);
            texTmp2.normalize();
            float uCoord2 = asin(texTmp2.x) / PI + .5f;
            float vCoord2 = asin(texTmp2.y) / PI + .5f;
            PVector tex2 = new PVector(uCoord2, vCoord2, 0);
            texcoords.add(tex2);
        }
    }

    float L00_0 = 0.85f;// 0.78908;
    float L00_1 = 0.85f;// 0.43710;
    float L00_2 = 0.8f;// 0.54161;

    float L1m1_0 = 0.85f;// 0.4376419;
    float L1m1_1 = 0.85f;// 0.5579443;
    float L1m1_2 = 0.8f;// 0.7024107;

    float L10_0 = 0.85f;// -0.1020717;
    float L10_1 = 0.85f;// -0.1824865;
    float L10_2 = 0.8f;// -0.2749662;

    float L11_0 = 0.4543814f;
    float L11_1 = 0.3750162f;
    float L11_2 = 0.1968642f;

    float L2m2_0 = 0.1841687f;
    float L2m2_1 = 0.1396696f;
    float L2m2_2 = 0.0491580f;

    float L2m1_0 = -0.1417495f;
    float L2m1_1 = -0.2186370f;
    float L2m1_2 = -0.3132702f;

    float L20_0 = -0.3890121f;
    float L20_1 = -0.4033574f;
    float L20_2 = -0.3639718f;

    float L21_0 = 0.0872238f;
    float L21_1 = 0.0744587f;
    float L21_2 = 0.0353051f;

    float L22_0 = 0.6662600f;
    float L22_1 = 0.6706794f;
    float L22_2 = 0.5246173f;

    public void initOpenGL() {
        GLGraphics renderer = (GLGraphics) g;
        renderer.beginGL();
        GL gl = renderer.gl;

        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_BLEND);
        gl.glEnable(GL.GL_COLOR_MATERIAL);
        gl.glEnable(GL.GL_POINT_SMOOTH);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        // glu.gluPerspective(50.0, 1.0, 5.0, 1500.0); //50
        // ((PGraphicsOpenGL) g).endGL();
        // gl.glEnable(GL.GL_CULL_FACE);
        // gl.glCullFace(GL.GL_BACK);
        gl.glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);

        gl.glHint(GL.GL_POINT_SMOOTH, GL.GL_NICEST);
        gl.glHint(GL.GL_LINE_SMOOTH, GL.GL_NICEST);
        gl.glHint(GL.GL_POLYGON_SMOOTH, GL.GL_NICEST);

        gl.glEnable(GL.GL_POINT_SMOOTH);
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL.GL_POLYGON_SMOOTH);

        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        renderer.endGL();

        tex0 = new GLTexture(this, "1211.jpg");

        // shader
        shader = new GLSLShader(this, "shinyvert.glsl", "shinyfrag.glsl");
    }

    float M_Spec_r = .7f;
    float M_Spec_g = .7f;
    float M_Spec_b = .7f;

    float M_Shin_r = .7f;
    float M_Shin_g = .7f;
    float M_Shin_b = .7f;

    float M_Diff_r = .7f;
    float M_Diff_g = .7f;
    float M_Diff_b = .7f;

    float M_Amb_r = .7f;
    float M_Amb_g = .7f;
    float M_Amb_b = .7f;


    public void runOpenGL() {
        GLGraphics renderer = (GLGraphics) g;
        GL gl = renderer.gl;
        // renderer.beginGL();

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
    }

    static public void main(String args[]) {
        PApplet.main(new String[]{"--bgcolor=#ffffff",
                "glsl.Lines"});
    }

    private void initGUI() {

        ControlP5 controlP5 = new ControlP5(this);
        controlP5.setAutoDraw(false);
        ControlWindow controlWindow = controlP5.addControlWindow("controlP5window", 100, 100, 400, 600);
        controlWindow.hideCoordinates();

        controlWindow.setBackground(color(40));
        controlP5.addSlider("sliderValue1", 0, 20, 5, 40, 100, 10).setTab(controlWindow, "form");
        controlP5.addSlider("sliderValue2", 0, 20, 5, 60, 100, 10).setTab(controlWindow, "form");
        controlP5.addSlider("sliderValue3", 0, 20, 5, 80, 100, 10).setTab(controlWindow, "form");
        controlP5.addSlider("sliderValue4", 0, 20, 5, 100, 100, 10).setTab(controlWindow, "form");
        controlP5.addSlider("sliderValue5", 0, 20, 5, 120, 100, 10).setTab(controlWindow, "form");
        controlP5.addSlider("sliderValue6", 0, 20, 5, 140, 100, 10).setTab(controlWindow, "form");
        controlP5.addSlider("sliderValue7", 0, 20, 5, 160, 100, 10).setTab(controlWindow, "form");
        controlP5.addSlider("sliderValue8", 0, 20, 5, 180, 100, 10).setTab(controlWindow, "form");


        controlP5.addSlider("L00_0", 0, 1, 0, 40, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L00_1", 0, 1, 0, 60, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L00_2", 0, 1, 0, 80, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L1m1_0", 0, 1, 0, 120, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L1m1_1", 0, 1, 0, 140, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L1m1_2", 0, 1, 0, 160, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L10_0", 0, 1, 0, 180, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L10_1", 0, 1, 0, 200, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L10_2", 0, 1, 0, 220, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L11_0", 0, 1, 0, 240, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L11_1", 0, 1, 0, 260, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L11_2", 0, 1, 0, 280, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L2m1_0", 0, 1, 0, 300, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L2m1_1", 0, 1, 0, 320, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L2m1_2", 0, 1, 0, 340, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L20_0", 0, 1, 0, 360, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L20_1", 0, 1, 0, 380, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L20_2", 0, 1, 0, 400, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L21_0", 0, 1, 0, 420, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L21_1", 0, 1, 0, 440, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L21_2", 0, 1, 0, 460, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("L22_0", 0, 1, 0, 480, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L22_1", 0, 1, 0, 500, 100, 10).setTab(controlWindow, "color");
        controlP5.addSlider("L22_2", 0, 1, 0, 520, 100, 10).setTab(controlWindow, "color");

        controlP5.addSlider("widthX", 0, 1f, 0, 40, 300, 10).setTab(controlWindow, "pattern");
        controlP5.addSlider("widthY", 0, 1f, 0, 60, 300, 10).setTab(controlWindow, "pattern");
        controlP5.addSlider("widthZ", 0, 1f, 0, 80, 300, 10).setTab(controlWindow, "pattern");
        controlP5.addSlider("widthQ", 0, 100f, 0, 100, 300, 10).setTab(controlWindow, "pattern");
        controlP5.addSlider("widthW", 0, 5f, 0, 120, 300, 10).setTab(controlWindow, "pattern");
        controlP5.addSlider("widthL", -1f, 1f, 0, 120, 300, 10).setTab(controlWindow, "pattern");

        controlP5.addListener(new ControlListener() {
            public void controlEvent(ControlEvent controlEvent) {
                if (controlEvent.controller().getTab().name().equals("form")) {
                    createMesh = true;
                }
            }
        });
    }


}
