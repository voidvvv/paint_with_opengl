package com.zkj.paint.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.BufferUtils;

public class PlatinumShader implements Shader{
    // VBO实现，提高性能
    private int vboId; // VBO对象ID
    private boolean vboInitialized = false;
    private float accumulatedTime = 0.0f;
    ShaderProgram shaderProgram;
    // 定义包含位置和纹理坐标的顶点数据
    private float[] vertices = {
        // 位置           纹理坐标
        -1.0f, -1.0f, 0.0f, 0.0f,
        1.0f, -1.0f, 1.0f, 0.0f,
        -1.0f, 1.0f, 0.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f
    };

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    @Override
    public void create() {
        // init shader
        String platinumVertexShader = Gdx.files.internal("shader/platinum/platinum.vert").readString();
        String platinumFragmentShader = Gdx.files.internal("shader/platinum/platinum.frag").readString();

        // 创建着色器程序
        shaderProgram = new ShaderProgram(platinumVertexShader, platinumFragmentShader);
        // 检查着色器程序是否编译成功
        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("Platinum Shader", "Shader compilation failed: " + shaderProgram.getLog());
            return;
        }
        // 初始化VBO
        initVBO();
        //
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

    @Override
    public void render(float delta, Texture texture, float x, float y, float width, float height, Matrix4 pro) {
        accumulatedTime += delta;

        getShaderProgram().begin();

        // 设置uniform变量
        getShaderProgram().setUniformf("u_time", accumulatedTime);
        // 使用单位矩阵，因为我们使用标准化设备坐标
        getShaderProgram().setUniformMatrix("u_projTrans", pro);
        // 纹理单元0
        getShaderProgram().setUniformi("u_texture", 0);
        texture.bind();
        // 启用顶点属性
        int positionLocation = getShaderProgram().getAttributeLocation("a_position");
        int texCoordLocation = getShaderProgram().getAttributeLocation("a_texCoord");

        getShaderProgram().enableVertexAttribute(positionLocation);
        getShaderProgram().enableVertexAttribute(texCoordLocation);

        // 绑定VBO并设置顶点属性指针
        Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboId);
        {
            int i = 0;
            vertices[i] = x;
            vertices[i + 1] = y;
            i+=4;
            vertices[i] = x + width;
            vertices[i + 1] = y;
            i+=4;
            vertices[i] = x;
            vertices[i + 1] = y + height;

            i+=4;
            vertices[i] = x + width;
            vertices[i + 1] = y + height;
            Gdx.gl.glBufferData(
                GL20.GL_ARRAY_BUFFER,
                vertices.length * Float.BYTES,
                BufferUtils.newFloatBuffer(vertices.length).put(vertices).flip(),
                GL20.GL_DYNAMIC_DRAW
            );
        }
        // 设置位置属性
        Gdx.gl.glVertexAttribPointer(
            positionLocation,    // 属性位置
            2,                   // 每个属性的组件数
            GL20.GL_FLOAT,       // 数据类型
            false,               // 是否归一化
            4 * Float.BYTES,     // 步长（位置+纹理坐标=4个float）
            0                    // VBO中的偏移量
        );

        // 设置纹理坐标属性
        Gdx.gl.glVertexAttribPointer(
            texCoordLocation,    // 属性位置
            2,                   // 每个属性的组件数
            GL20.GL_FLOAT,       // 数据类型
            false,               // 是否归一化
            4 * Float.BYTES,     // 步长
            2 * Float.BYTES      // VBO中的偏移量（跳过2个float的位置数据）
        );

        // 绘制四边形
        Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4);

        // 禁用顶点属性并解绑VBO
        getShaderProgram().disableVertexAttribute(positionLocation);
        getShaderProgram().disableVertexAttribute(texCoordLocation);
        Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);

        getShaderProgram().end();
    }

    @Override
    public void dispose() {
        // 当着色器渲染器被销毁时清理VBO
        if (vboInitialized) {
            Gdx.gl.glDeleteBuffer(vboId);
            vboInitialized = false;
        }
    }
}
