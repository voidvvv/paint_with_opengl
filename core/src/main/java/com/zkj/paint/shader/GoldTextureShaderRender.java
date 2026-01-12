package com.zkj.paint.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.IndexArray;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import java.nio.Buffer;
import java.nio.ShortBuffer;

public class GoldTextureShaderRender{
    private VertexBufferObject vbo;
    private float goldIntensity = 0.7f;
    private float glossiness = 0.5f;
    private float accumulatedTime = 0.0f; // 累积时间，用于动态效果
    ShaderProgram shaderProgram;
    // 顶点数据：位置 (x,y) + 纹理坐标 (u,v)
    // 使用标准化设备坐标 (-1 到 1)
    private static final float[] VERTICES = {
        -1, -1, 0, 1, // 左下角
        -1, 1, 0, 0,  // 左上角

        1, 1, 1, 0,    // 右上角
        1, -1, 1, 1,  // 右下角

    };

    final IndexData ebo;

    private Vector2 position = new Vector2(0,100);
    private Vector2 size = new Vector2(200,200);

    public GoldTextureShaderRender(ShaderProgram shaderProgram) {
        this.setShaderProgram(shaderProgram);
        ebo = new IndexArray(6);
    }

    public void create() {
        // 创建VBO
        vbo = new VertexBufferObject(
            false, // 不是静态绘制
            VERTICES.length / 4, // 顶点数量
            new VertexAttributes(
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord")
            )
        );
        vbo.setVertices(VERTICES, 0 , VERTICES.length);
        int i = 0;
        short j = 0;
        short[] indices = new short[6];
        indices[i] = j;
        indices[i + 1] = (short)(j + 1);
        indices[i + 2] = (short)(j + 2);
        indices[i + 3] = (short)(j + 2);
        indices[i + 4] = (short)(j + 3);
        indices[i + 5] = j;
        ebo.setIndices(indices, 0, 6);
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public void setGoldIntensity(float intensity) {
        this.goldIntensity = Math.max(0, Math.min(1, intensity));
    }

    public void setGlossiness(float glossiness) {
        this.glossiness = Math.max(0, Math.min(1, glossiness));
    }

    public void render(float delta,Texture texture,float x, float y, float width, float height, Matrix4 pro) {
        ShaderProgram shader = getShaderProgram();

        // 检查着色器是否可用
        if (shader == null || !shader.isCompiled()) {
            Gdx.app.error("GoldTextureRender", "Shader not compiled or null");
            return;
        }

        // 检查VBO和纹理是否可用
        if (vbo == null) {
            Gdx.app.error("GoldTextureRender", "VBO or texture not initialized");
            return;
        }

        // 检查OpenGL错误
        checkGLError("Before render");

        shader.begin();

        // 检查OpenGL错误
        checkGLError("After shader begin");

        // 更新累积时间 - 增加动画速度
        accumulatedTime += delta * 0.65f; // 加快动画速度，使动态效果更明显

        // 设置uniform变量
        // 由于使用标准化设备坐标，使用单位矩阵
        shader.setUniformMatrix("u_projTrans", pro);
        shader.setUniformi("u_texture", 0); // 使用纹理单元0
        shader.setUniformf("u_goldIntensity", goldIntensity);
        shader.setUniformf("u_glossiness", glossiness);

        // 调试：打印时间值
        if (delta > 0) {
            Gdx.app.log("GoldTextureRender", "u_time: " + accumulatedTime + ", delta: " + delta);
        }

        // 设置时间变量，用于动态闪烁效果
        shader.setUniformf("u_time", accumulatedTime); // 使用累积时间来控制动画

        // 检查OpenGL错误
        checkGLError("After setting uniforms");

        // 绑定纹理
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        texture.bind();

        // 检查OpenGL错误
        checkGLError("After texture binding");
        // set position
        {
            int idx = 0;
            VERTICES[idx] = x;
            VERTICES[idx + 1] = y;
            idx += 4;
            VERTICES[idx] = x;
            VERTICES[idx + 1] = y + height;
            idx += 4;
            VERTICES[idx] = x + width;
            VERTICES[idx + 1] = y + height;
            idx += 4;
            VERTICES[idx] = x + width;
            VERTICES[idx + 1] = y;





        }

        vbo.setVertices(VERTICES, 0 , VERTICES.length);
        // 绘制 - 使用更直接的方式
        vbo.bind(shader);
        ebo.bind();
        // 检查OpenGL错误
        checkGLError("After VBO bind");

        // 设置顶点属性指针
//        shader.enableVertexAttribute("a_position");
//        shader.setVertexAttribute("a_position", 2, GL20.GL_FLOAT, false, 4 * Float.BYTES, 0);
//
//        shader.enableVertexAttribute("a_texCoord");
//        shader.setVertexAttribute("a_texCoord", 2, GL20.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);

        // 检查OpenGL错误
        checkGLError("After setting vertex attributes");

        // 绘制三角形带
//        Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4);
        ShortBuffer buffer = ebo.getBuffer(false);
        int oldPosition = buffer.position();
        int oldLimit = buffer.limit();
        ((Buffer)buffer).position(0);
        Gdx.gl20.glDrawElements(GL20.GL_TRIANGLE_STRIP, 6, GL20.GL_UNSIGNED_SHORT, buffer);
        ((Buffer)buffer).position(oldPosition);
        // 检查OpenGL错误
        checkGLError("After drawArrays");

        // 禁用顶点属性
//        shader.disableVertexAttribute("a_position");
//        shader.disableVertexAttribute("a_texCoord");

        vbo.unbind(shader);

        shader.end();

        // 检查OpenGL错误
        checkGLError("After shader end");
    }

    // 检查OpenGL错误的辅助方法
    private void checkGLError(String message) {
        int error = Gdx.gl.glGetError();
        if (error != GL20.GL_NO_ERROR) {
            String errorMessage = "OpenGL Error at " + message + ": ";
            switch (error) {
                case GL20.GL_INVALID_ENUM:
                    errorMessage += "GL_INVALID_ENUM";
                    break;
                case GL20.GL_INVALID_VALUE:
                    errorMessage += "GL_INVALID_VALUE";
                    break;
                case GL20.GL_INVALID_OPERATION:
                    errorMessage += "GL_INVALID_OPERATION";
                    break;
                case GL20.GL_OUT_OF_MEMORY:
                    errorMessage += "GL_OUT_OF_MEMORY";
                    break;
                default:
                    errorMessage += "Unknown error code: " + error;
                    break;
            }
            Gdx.app.error("GoldTextureRender", errorMessage);
        }
    }

    public Vector2 getPosition () {
        return this.position;
    }
    public Vector2 getSize() {
        return this.size;
    }

    public void dispose() {
        getShaderProgram().dispose();
        vbo.dispose();
    }

}
