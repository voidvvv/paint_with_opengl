package com.zkj.paint.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.IndexArray;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import java.nio.Buffer;
import java.nio.ShortBuffer;

public class GoldTextureRender extends ShaderRender {
    private Texture texture;

    private Vector2 position = new Vector2(0,100);
    private Vector2 size = new Vector2(200,200);
    GoldTextureShaderRender goldTextureShaderRender;
    public GoldTextureRender(ShaderProgram shaderProgram, Texture texture) {
        super(shaderProgram);
        goldTextureShaderRender = new GoldTextureShaderRender(shaderProgram);
        this.texture = texture;
    }

    @Override
    public void create() {
        goldTextureShaderRender.create();
    }

    @Override
    public void render(float delta, Matrix4 pro) {
        goldTextureShaderRender.render(delta, texture, position.x, position.y, size.x, size.y, pro);
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

    @Override
    public void dispose() {
        goldTextureShaderRender.dispose();
    }

}
