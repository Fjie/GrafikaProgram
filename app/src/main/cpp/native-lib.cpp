
#include <jni.h>
#include <string>
#include <android/log.h>

#include <GLES3/gl3.h>
#include <GLES2/gl2ext.h>


#include <stdio.h>
#include <stdlib.h>

//#include <glm/mat4x4.hpp> // glm::mat4

#define  LOG_TAG    "xxx"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

static void printGLString(const char *name, GLenum s) {
    const char *v = (const char *) glGetString(s);
    LOGI("GL %s = %s\n", name, v);
}

static bool checkGlError(const char *op) {
    for (GLint error = glGetError(); error; error
                                                    = glGetError()) {
        LOGI("after %s() glError (0x%x)\n", op, error);
    }
    return false;
}

char gVertexShader[] =
        "attribute vec4 vPosition;\n"
                "uniform mat4 uMVPMatrix;\n"
                "void main() {\n"
                "   gl_Position = uMVPMatrix * vPosition;\n"
//                "   gl_Position = vPosition;\n"
                "}\n";

char gFragmentShader[] =
        "precision mediump float;\n"
                "void main() {\n"
                "  gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);\n"
                "}\n";

GLuint loadShader(GLenum shaderType, const char *pSource) {
    GLuint shader = glCreateShader(shaderType);
    if (shader) {
        glShaderSource(shader, 1, &pSource, NULL);
        glCompileShader(shader);
        GLint compiled = 0;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
        if (!compiled) {
            GLint infoLen = 0;
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
            if (infoLen) {
                char *buf = (char *) malloc(infoLen);
                if (buf) {
                    glGetShaderInfoLog(shader, infoLen, NULL, buf);
                    LOGE("Could not compile shader %d:\n%s\n",
                         shaderType, buf);
                    free(buf);
                }
                glDeleteShader(shader);
                shader = 0;
            }
        }
    }
    return shader;
}

// 创建一个编程管线
GLuint createProgram2(const char *pVertexSource, const char *pFragmentSource) {
    GLuint vertexShader = loadShader(GL_VERTEX_SHADER, pVertexSource);
    if (!vertexShader) {
        return 0;
    }

    GLuint pixelShader = loadShader(GL_FRAGMENT_SHADER, pFragmentSource);
    if (!pixelShader) {
        return 0;
    }

    GLuint program = glCreateProgram();
    if (program) {
        glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        glAttachShader(program, pixelShader);
        checkGlError("glAttachShader");
        glLinkProgram(program);
        GLint linkStatus = GL_FALSE;
        glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);
        if (linkStatus != GL_TRUE) {
            GLint bufLength = 0;
            glGetProgramiv(program, GL_INFO_LOG_LENGTH, &bufLength);
            if (bufLength) {
                char *buf = (char *) malloc(bufLength);
                if (buf) {
                    glGetProgramInfoLog(program, bufLength, NULL, buf);
                    LOGE("Could not link program:\n%s\n", buf);
                    free(buf);
                }
            }
            glDeleteProgram(program);
            program = 0;
        }
    }
    return program;
}

GLuint gProgram;
GLuint gvPositionHandle;
GLuint guMVPMatrixHandle;

static int width = 0;
static int height = 0;

bool setupGraphics(int w, int h) {

    width = w;
    height = h;

    printGLString("Version", GL_VERSION);
    printGLString("Vendor", GL_VENDOR);
    printGLString("Renderer", GL_RENDERER);
    printGLString("Extensions", GL_EXTENSIONS);

    LOGI("setupGraphics(%d, %d)", w, h);
    gProgram = createProgram2(gVertexShader, gFragmentShader);
    if (!gProgram) {
        LOGE("Could not create program.");
        return false;
    }
    // 只有个顶点数组句柄
    gvPositionHandle = glGetAttribLocation(gProgram, "vPosition");
    guMVPMatrixHandle = glGetAttribLocation(gProgram, "uMVPMatrix");
    checkGlError("glGetAttribLocation");
    LOGI("glGetAttribLocation(\"vPosition\") = %d\n",
         gvPositionHandle);
    // 视角拿来干毛
    glViewport(0, 0, w, h);
    checkGlError("glViewport");
    return true;
}

// 三角形顶点
const GLfloat gTriangleVertices[] = {
        -0.5f, 0.5f,
        0.5f, 0.5f,
        -0.5f, -0.5f,
        0.5f, -0.5f};

const GLfloat uMVPMatrix[] = {
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f
};


// 正经画图！
void renderFrame(JNIEnv *pEnv, jobject pJobject) {
    static float grey;
    static float mStep = 0.001;
    grey += mStep;
    if (grey > 1.0f || grey < 0.0f) {
        mStep = -mStep;
    }


    /* ------------------搞创作--------------------- */

    int absIndex = (int) (mStep * 1000);
    int xpos = absIndex * width / 120;
    int ypos = absIndex * height / 120;

    int M_BOX_SIZE = 120;
    glScissor(xpos, ypos, M_BOX_SIZE, M_BOX_SIZE);

    /* ------------------------------------------ */

    // 背景
    glClearColor(grey, grey, grey, 1.0f);
    checkGlError("glClearColor");
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    checkGlError("glClear");
    // 用介个管线
    glUseProgram(gProgram);
    checkGlError("glUseProgram");


    glVertexAttribPointer(gvPositionHandle, 2, GL_FLOAT, GL_FALSE, 0, gTriangleVertices);
    glUniformMatrix4fv(guMVPMatrixHandle, 1, false, uMVPMatrix);
    checkGlError("glVertexAttribPointer");
    // 应用顶点
    glEnableVertexAttribArray(gvPositionHandle);
    checkGlError("glEnableVertexAttribArray");
    // 开画
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    checkGlError("glDrawArrays");

//    jclass mClazz = pEnv->GetObjectClass(pJobject);
//    jmethodID mMethod = pEnv -> GetMethodID(mClazz,"showMsg","()");
//    pEnv->CallVoidMethod(mClazz, mMethod);
}

extern "C" {
JNIEXPORT void JNICALL
Java_me_fanjie_grafikaprogram_pages_glesndk_GL2JNILib_init(JNIEnv *env, jobject obj, jint width,
                                                           jint height);
JNIEXPORT void JNICALL
Java_me_fanjie_grafikaprogram_pages_glesndk_GL2JNILib_step(JNIEnv *env, jobject obj);
};

JNIEXPORT void JNICALL
Java_me_fanjie_grafikaprogram_pages_glesndk_GL2JNILib_init(JNIEnv *env, jobject obj, jint width,
                                                           jint height) {
    setupGraphics(width, height);
}

JNIEXPORT void JNICALL
Java_me_fanjie_grafikaprogram_pages_glesndk_GL2JNILib_step(JNIEnv *env, jobject obj) {

    renderFrame(env, obj);
}

extern "C"
JNIEXPORT void JNICALL
Java_me_fanjie_grafikaprogram_pages_glesndk_GL2JNILib_test(JNIEnv *env, jclass type) {
    long n = 0;
    for (int i = 0; i < 100000; ++i) {
        for (int i2 = 0; i < 100000; ++i) {
            n += i + i2;
        }
    }

}