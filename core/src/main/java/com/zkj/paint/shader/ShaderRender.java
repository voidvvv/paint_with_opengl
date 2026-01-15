package com.zkj.paint.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

public abstract class ShaderRender {

    private String name;
    private Shader shader;

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public abstract void render(float delta, Matrix4 pro);

    /**
     * Disposes any resources associated with this shader render.
     * Should be called when the render is no longer needed.
     */
    public void dispose() {
        // Default implementation - can be overridden by subclasses
        if (shader != null) {
            shader.dispose();
        }
    }

    public void create() {

        this.getShader().create();
    };

    @Override
    public String toString() {
        return name;
    }
}
