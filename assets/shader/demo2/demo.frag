// 片段着色器
#extension GL_OES_standard_derivatives : enable
precision mediump float;

uniform sampler2D u_texture; // 输入纹理
uniform float u_goldIntensity; // 镀金强度 (0.0-1.0)
uniform float u_glossiness; // 光泽度 (0.0-1.0)
uniform float u_time; // 时间变量，用于动态效果

varying vec2 v_texCoord; // 从顶点着色器传递的纹理坐标

// 金色的RGB值 - 基础金色
highp vec3 BASE_GOLD = vec3(1.0, 0.843, 0.0);
// 亮金色 - 用于高光
highp vec3 BRIGHT_GOLD = vec3(1.0, 1.0, 0.6);
// 铜金色 - 用于变化
highp vec3 COPPER_GOLD = vec3(1.0, 0.714, 0.42);
// 炫光色 - 用于强烈闪烁
highp vec3 GLITTER_GOLD = vec3(1.0, 1.0, 0.8);

void main() {
    // 采样原始纹理颜色
    vec4 originalColor = texture2D(u_texture, v_texCoord);

    // 计算灰度值 (使用标准亮度公式)
    float gray = dot(originalColor.rgb, vec3(0.299, 0.587, 0.114));

    // 1. 强烈的动态金色变化 - 快速在多种金色间切换
    // 增加速度和幅度，让金色变化更明显
    vec3 dynamicGoldColor = mix(
        mix(BASE_GOLD, COPPER_GOLD, sin(u_time * 2.0) * 0.5 + 0.5),
        GLITTER_GOLD,
        smoothstep(0.7, 1.0, sin(u_time * 3.0) * 0.5 + 0.5) * 0.3
    );

    // 创建金色基础色
    vec3 goldBase = dynamicGoldColor * gray;

    // 混合原始颜色和金色，基于强度参数
    vec3 mixedColor = mix(originalColor.rgb, goldBase, u_goldIntensity);

    // 添加光泽效果
    // 使用纹理坐标的导数创建简单的高光
    vec2 dx = dFdx(v_texCoord);
    vec2 dy = dFdy(v_texCoord);
    float gloss = sqrt(dot(dx, dx) + dot(dy, dy)) * 50.0;
    gloss = smoothstep(0.1, 0.3, gloss) * u_glossiness;
    // 添加动态光泽变化
    gloss *= (sin(u_time * 2.5) * 0.4 + 0.6);

    // 2. 非常明显的动态闪烁效果

    // 快速移动的高光条纹 - 极高速度
    float sparkleTime = u_time * 4.0; // 显著提高速度

    // 水平方向移动的条纹 - 非常密集和快速
    vec2 sparkleUV1 = v_texCoord * 30.0 + vec2(sparkleTime, 0.0);
    float sparkle1 = sin(sparkleUV1.x) * 0.5 + 0.5;
    sparkle1 = smoothstep(0.9, 1.0, sparkle1) * 0.05; // 极高强度

    // 垂直方向移动的条纹 - 非常密集和快速
    vec2 sparkleUV2 = v_texCoord * 30.0 + vec2(0.0, sparkleTime * 1.8);
    float sparkle2 = cos(sparkleUV2.y) * 0.5 + 0.5;
    sparkle2 = smoothstep(0.9, 1.0, sparkle2) * 0.13; // 极高强度

    // 对角线方向的快速闪烁
    vec2 sparkleUV3 = v_texCoord * 40.0 + vec2(sparkleTime * 1.2, sparkleTime * 0.8);
    float sparkle3 = sin(sparkleUV3.x + sparkleUV3.y) * 0.5 + 0.5;
    sparkle3 = smoothstep(0.95, 1.0, sparkle3) * 0.05;

    // 3. 增强的随机闪烁效果 - 极高频率
    float random1 = fract(sin(dot(v_texCoord + vec2(u_time * 5.0), vec2(12.9898, 78.233))) * 43758.5453);
    float randomSparkle1 = smoothstep(0.97, 1.0, random1) * 0.1; // 极高强度和频率

    // 4. 明显的周期性爆发闪烁 - 更频繁
    float burstTime = u_time * 5.0;
    float burst = sin(burstTime) * 0.5 + 0.5;
    burst = smoothstep(0.9, 1.0, burst) * 0.16; // 非常强烈的爆发

    // 5. 无限制的闪烁
    float textureBrightness = 0.5; // 不限制，让所有区域都有闪烁

    // 组合所有闪烁效果 - 极大增强整体强度
    float totalSparkle = (sparkle1 + sparkle2 + sparkle3 + randomSparkle1 + burst) *
                        textureBrightness * u_goldIntensity * 2.0; // 显著提高整体强度

    // 6. 强烈脉动的边缘辉光效果
    vec2 edgeUV = v_texCoord * 2.0 - 1.0;
    float edgeGlow = 1.0 - length(edgeUV);
    edgeGlow = smoothstep(0.0, 0.3, edgeGlow) * 0.8 * u_goldIntensity;
    // 添加强烈的脉动效果
    edgeGlow *= (sin(u_time * 4.0) * 0.5 + 0.5);

    // 7. 添加全屏的微妙脉动效果
    float globalPulse = (sin(u_time * 1.5) * 0.1 + 0.9) * 0.3;

    // 应用所有效果 - 增强整体动态感
    vec3 finalColor = mixedColor * globalPulse +
                     (dynamicGoldColor * gloss * 0.7) +
                     (BRIGHT_GOLD * totalSparkle * 0.8) +
                     (GLITTER_GOLD * totalSparkle * 0.5) +
                     (BASE_GOLD * edgeGlow);

    // 保持原始alpha通道
    gl_FragColor = vec4(finalColor, originalColor.a);
}
