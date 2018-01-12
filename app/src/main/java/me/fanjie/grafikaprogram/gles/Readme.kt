package me.fanjie.grafikaprogram.gles

/**
 * OpenGL ES ：
 *     EGL环境：
 *         配置可编程管线：
 *             顶点 + 纹理着色器创建管线
 *             获取着色器 参数 句柄
 *             * 启用纹理：
 *                 生成：glGenTextures
 *                 绑定：glBindTexture
 *                 配置：glTexParameteri(xxx)
 *         绘制：
 *             选择一个管线
 *             应用纹理：
 *                 激活：glActiveTexture
 *                 绑定：glBindTexture
 *             传入着色器参数
 *                 普通参数（Uniform、other）：
 *                     对应方法传入：glUniformMatrix4fv（句柄，内容 ...）、glUniform2fv、glUniform1f ...
 *                 阵列参数？指针参数？
 *                     启用参数句柄：glEnableVertexAttribArray（句柄）
 *                     对应方法传入：glVertexAttribPointer（句柄，内容，buf?...）
 *             绘制：glDrawArrays（绘制类型，起点，顶点数？)
 *             释放：disable xxx
 *     Surface渲染：
 */
