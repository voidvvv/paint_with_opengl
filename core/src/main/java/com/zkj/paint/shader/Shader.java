package com.zkj.paint.shader;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;

public interface Shader {
    void create();
    public void render(float delta, Texture texture, float x, float y, float width, float height, Matrix4 pro);
    void dispose();
}
