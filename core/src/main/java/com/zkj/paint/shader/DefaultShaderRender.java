package com.zkj.paint.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

public class DefaultShaderRender extends ShaderRender {
    Mesh mesh;

    public DefaultShaderRender(ShaderProgram shaderProgram) {
        super(shaderProgram);
        Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexBufferObjectWithVAO;

        mesh = new Mesh(vertexDataType, false, 10 * 4, 10 * 6,
        new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE));
    }

    @Override
    public void render(float delta, Matrix4 pro) {
        bindShader(delta, pro);

        mesh.render(getShaderProgram(), GL20.GL_TRIANGLES);
    }

    private void bindShader(float delta, Matrix4 pro) {
        getShaderProgram().bind();
        getShaderProgram().setUniformMatrix("u_projTrans", pro);
    }

    @Override
    public void create() {

    }
}
