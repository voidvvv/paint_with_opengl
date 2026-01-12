// 片段着色器
precision mediump float;

uniform float u_time;
uniform vec2 u_resolution;

void main() {
    // 使用位置信息创建基础渐变
    vec2 uv = gl_FragCoord.xy / u_resolution.xy;
    
    // 创建随时间变化的渐变色
    float time = u_time * 0.5;
    vec3 color = vec3(
        sin(uv.x * 5.0 + time) * 0.5 + 0.5,
        cos(uv.y * 5.0 + time + 1.0) * 0.5 + 0.5,
        sin(uv.x * 3.0 + uv.y * 3.0 + time + 2.0) * 0.5 + 0.5
    );
    
    gl_FragColor = vec4(color, 1.0);
}
