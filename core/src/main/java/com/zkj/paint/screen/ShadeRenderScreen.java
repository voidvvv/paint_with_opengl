package com.zkj.paint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zkj.paint.shader.GoldTextureRender;
import com.zkj.paint.shader.ShaderRender;

import java.util.ArrayList;
import java.util.List;

public class ShadeRenderScreen implements Screen {
    private List<ShaderRender> shaderRenders = new ArrayList<>();
    private Stage stage;
    private Skin skin;
    private SelectBox<ShaderRender> shaderSelectBox;
    private ShaderRender currentShaderRender;
    private Table uiTable;
    Camera camera;
    TextArea ta ;
    @Override
    public void show() {
        // load all shader
        initShaders();

        // Initialize stage and viewport
        stage = new Stage(new ScreenViewport());

        // Load skin (assuming uiskin.json is in assets/ui/)
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Create UI table
        uiTable = new Table(skin);
        uiTable.setFillParent(false);
        uiTable.top().left();
        uiTable.setPosition(10, Gdx.graphics.getHeight() - 10);

        // Create shader select box
        shaderSelectBox = new SelectBox<>(skin);
        shaderSelectBox.setItems(shaderRenders.toArray(new ShaderRender[0]));
        shaderSelectBox.setWidth(250);

        // Add change listener to select box
        shaderSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentShaderRender = shaderSelectBox.getSelected();
            }
        });

        // Add select box to table
        uiTable.add(shaderSelectBox).pad(10);

        // Add table to stage
        stage.addActor(uiTable);

        // Set initial shader render
        if (!shaderRenders.isEmpty()) {
            currentShaderRender = shaderRenders.get(0);
            shaderSelectBox.setSelected(currentShaderRender);
        }
        ta = new TextArea("", skin){
            @Override
            public void act(float delta) {
                this.setText(shaderRenders.size() + "");
            }
        };
        uiTable.add(ta);
        // Set input processor
        Gdx.input.setInputProcessor(stage);
        OrthographicCamera localCamera = new OrthographicCamera();
        localCamera.setToOrtho(false);
        this.camera = localCamera;
    }

    private void initShaders() {
        // 添加镀金效果的渲染器
        try {
            // 加载着色器文件
            String goldVertexShader = Gdx.files.internal("shader/demo2/demo.vert").readString();
            String goldFragmentShader = Gdx.files.internal("shader/demo2/demo.frag").readString();

            // 创建着色器程序
            ShaderProgram goldShaderProgram = new ShaderProgram(goldVertexShader, goldFragmentShader);

            // 检查着色器程序是否编译成功
            if (!goldShaderProgram.isCompiled()) {
                Gdx.app.error("Gold Shader", "Shader compilation failed: " + goldShaderProgram.getLog());
            } else {
                // 加载测试纹理
                Texture testTexture;
                try {
                    testTexture = new Texture(Gdx.files.internal("libgdx.png"));
                } catch (Exception e) {
                    // 如果没有默认纹理，创建一个简单的彩色纹理
                    Pixmap pixmap = new Pixmap(256, 256, Pixmap.Format.RGBA8888);
                    pixmap.setColor(0.2f, 0.4f, 0.8f, 1.0f);
                    pixmap.fill();
                    pixmap.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    pixmap.drawLine(0, 0, 255, 255);
                    pixmap.drawLine(255, 0, 0, 255);
                    testTexture = new Texture(pixmap);
                    pixmap.dispose();
                }

                // 创建镀金纹理渲染器
                GoldTextureRender goldTextureRender = new GoldTextureRender(goldShaderProgram, testTexture);
                goldTextureRender.setName("Gold Texture Shader");
                shaderRenders.add(goldTextureRender);
            }
        } catch (Exception e) {
            Gdx.app.error("Gold Shader", "Error creating gold texture shader: " + e.getMessage());
        }

        // 加载原始的颜色渐变着色器
        try {
            String vertexShader = Gdx.files.internal("shader/test_color/test_color.vert").readString();
            String fragmentShader = Gdx.files.internal("shader/test_color/test_color.frag").readString();

            // 创建着色器程序
            ShaderProgram shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

            // 检查着色器程序是否编译成功
            if (!shaderProgram.isCompiled()) {
                Gdx.app.error("Shader", "Shader compilation failed: " + shaderProgram.getLog());
                return;
            }

            // 创建着色器渲染实例
            ShaderRender colorGradientRender = new ShaderRender(shaderProgram) {
                // VBO实现，提高性能
                private int vboId; // VBO对象ID
                private boolean vboInitialized = false;

                @Override
                public void create() {
                    // 初始化VBO
                    initVBO();
                }

                private void initVBO() {
                    if (vboInitialized) return;

                    // 创建VBO
                    vboId = Gdx.gl.glGenBuffer();

                    // 定义标准化设备坐标的全屏四边形顶点
                    float[] vertices = {
                        -1.0f, -1.0f,
                        1.0f, -1.0f,
                        -1.0f, 1.0f,
                        1.0f, 1.0f
                    };

                    // 绑定VBO并上传数据到GPU
                    Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboId);
                    Gdx.gl.glBufferData(
                        GL20.GL_ARRAY_BUFFER,
                        vertices.length * Float.BYTES,
                        BufferUtils.newFloatBuffer(vertices.length).put(vertices).flip(),
                        GL20.GL_STATIC_DRAW // 数据不会频繁变化
                    );

                    // 解绑VBO
                    Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);

                    vboInitialized = true;
                }

                @Override
                public void render(float delta, Matrix4 pro) {
                    getShaderProgram().begin();

                    // 设置uniform变量
                    getShaderProgram().setUniformf("u_time", Gdx.graphics.getFrameId() * 0.1f);
                    getShaderProgram().setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    // 使用单位矩阵，因为我们使用标准化设备坐标
                    getShaderProgram().setUniformMatrix("u_projTrans", new Matrix4());

                    // 启用顶点属性
                    int positionLocation = getShaderProgram().getAttributeLocation("a_position");
                    getShaderProgram().enableVertexAttribute(positionLocation);

                    // 绑定VBO并设置顶点属性指针
                    Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboId);
                    Gdx.gl.glVertexAttribPointer(
                        positionLocation,    // 属性位置
                        2,                   // 每个属性的组件数
                        GL20.GL_FLOAT,       // 数据类型
                        false,               // 是否归一化
                        0,                   // 步长
                        0                    // VBO中的偏移量（因为只有位置数据）
                    );

                    // 绘制四边形
                    Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4);

                    // 禁用顶点属性并解绑VBO
                    getShaderProgram().disableVertexAttribute(positionLocation);
                    Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);

                    getShaderProgram().end();
                }

                @Override
                public void dispose() {
                    super.dispose();
                    // 当着色器渲染器被销毁时清理VBO
                    if (vboInitialized) {
                        int[] vbos = {vboId};
                        Gdx.gl.glDeleteBuffer(vboId);
                        vboInitialized = false;
                    }
                }
            };

            colorGradientRender.setName("Color Gradient Shader");
            shaderRenders.add(colorGradientRender);
        } catch (Exception e) {
            Gdx.app.error("Gradient Shader", "Error creating color gradient shader: " + e.getMessage());
        }

        // 创建所有着色器
        createShaders();
    }

    private void createShaders() {
        for (ShaderRender shaderRender : shaderRenders) {
            shaderRender.create();
        }
    }

    @Override
    public void render(float delta) {
        // 清除屏幕
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.9f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 渲染选中的着色器
        if (currentShaderRender != null) {
            currentShaderRender.render(delta, this.camera.combined);
            if (GL20.GL_NO_ERROR != Gdx.gl.glGetError()) {
                System.out.println(Gdx.gl.glGetError());
            }

        }

        // 绘制UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        uiTable.setPosition(10, height - 10);
        this.camera.update();
    }

    @Override
    public void pause() {
        // 不需要特定的暂停逻辑
    }

    @Override
    public void resume() {
        // 不需要特定的恢复逻辑
    }

    @Override
    public void hide() {
        // 当屏幕隐藏时移除输入处理器
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        // 销毁所有资源
        stage.dispose();
        skin.dispose();

        // 销毁所有着色器渲染器（这也会销毁VBO）
        for (ShaderRender render : shaderRenders) {
            render.dispose();
        }
    }
}
