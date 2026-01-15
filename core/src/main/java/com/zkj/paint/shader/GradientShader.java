package com.zkj.paint.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.BufferUtils;

public class GradientShader implements Shader {
    private int vboId; // VBO对象ID
    private boolean vboInitialized = false;
    ShaderProgram shaderProgram;
    // 定义标准化设备坐标的全屏四边形顶点
    float[] vertices = {
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    };
    @Override
    public void create() {
        // 初始化VBO
        initVBO();
        String vertexShader = Gdx.files.internal("shader/test_color/test_color.vert").readString();
        String fragmentShader = Gdx.files.internal("shader/test_color/test_color.frag").readString();

        // 创建着色器程序
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

        // 检查着色器程序是否编译成功
        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("Shader", "Shader compilation failed: " + shaderProgram.getLog());
            return;
        }
    }

    private void initVBO() {
        if (vboInitialized) return;

        // 创建VBO
        vboId = Gdx.gl.glGenBuffer();



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

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    @Override
    public void render(float delta, Texture texture, float x, float y, float width, float height, Matrix4 pro) {
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
        // 当着色器渲染器被销毁时清理VBO
        if (vboInitialized) {
            int[] vbos = {vboId};
            Gdx.gl.glDeleteBuffer(vboId);
            vboInitialized = false;
        }
    }
}
