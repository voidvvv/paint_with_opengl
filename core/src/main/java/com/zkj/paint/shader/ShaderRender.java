package com.zkj.paint.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

public abstract class ShaderRender {
    private ShaderProgram shaderProgram;
    private String name;

    public ShaderRender(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public abstract void render(float delta, Matrix4 pro);

    /**
     * Disposes any resources associated with this shader render.
     * Should be called when the render is no longer needed.
     */
    public void dispose() {
        // Default implementation - can be overridden by subclasses
        if (shaderProgram != null) {
            shaderProgram.dispose();
        }
    }

    public abstract void create();

    @Override
    public String toString() {
        return name;
    }
}
