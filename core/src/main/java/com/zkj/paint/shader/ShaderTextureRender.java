package com.zkj.paint.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class ShaderTextureRender extends ShaderRender {
    private Texture texture;

    private Vector2 position = new Vector2(0,100);
    private Vector2 size = new Vector2(200,200);
    public ShaderTextureRender(Shader shader, Texture texture) {
        this.setShader(shader);
        this.texture = texture;
    }

    @Override
    public void create() {
        super.create();
    }

    @Override
    public void render(float delta, Matrix4 pro) {
        this.getShader().render(delta, texture, position.x, position.y, size.x, size.y, pro);
    }


    public Vector2 getPosition () {
        return this.position;
    }
    public Vector2 getSize() {
        return this.size;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
