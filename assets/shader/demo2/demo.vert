// 顶点着色器
attribute vec2 a_position; // 顶点位置 (vec2)
attribute vec2 a_texCoord; // 纹理坐标

uniform mat4 u_projTrans; // 投影变换矩阵

varying vec2 v_texCoord; // 传递给片段着色器的纹理坐标

void main() {
    // 变换顶点位置 (将vec2转换为vec4进行矩阵乘法)
    gl_Position = u_projTrans * vec4(a_position, 0.0, 1.0);
    // 传递纹理坐标
    v_texCoord = a_texCoord;
}