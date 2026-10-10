package com.usanaem.occultic_ntm.entity.client;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

/** Client-only, reloadable material. Owns its program; never assumes program zero on exit. */
@SideOnly(Side.CLIENT)
public final class TarMaterial implements IResourceManagerReloadListener {
    public static final TarMaterial INSTANCE = new TarMaterial();
    private static final float OPACITY = 0.94F;
    // LWJGL 2 requires space for 16 values even when querying a four-component color.
    private final FloatBuffer color = BufferUtils.createFloatBuffer(16);
    private int program;
    private int timeUniform;
    private int surfaceOriginUniform;
    private int lightmapUniform, opacityUniform, fogEnabledUniform, fogModeUniform;
    private boolean attempted;
    private boolean reportedDraw;

    private TarMaterial() { }

    @Override
    public void onResourceManagerReload(IResourceManager manager) {
        if (program != 0) GL20.glDeleteProgram(program);
        program = 0;
        attempted = false;
        reportedDraw = false;
    }

    private void ensureProgram() {
        if (attempted) return;
        attempted = true;
        try {
            // Angelica can supply a modern core context through an LWJGL compatibility
            // layer. An old OpenGL20 capability bit alone must not silently veto GLSL.
            // Try the actual compiler; unsupported contexts enter the logged fallback.
            IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
            program = createProgram(read(manager, "vert"), read(manager, "frag"));
            timeUniform = GL20.glGetUniformLocation(program, "time");
            surfaceOriginUniform = GL20.glGetUniformLocation(program, "surfaceOrigin");
            lightmapUniform = GL20.glGetUniformLocation(program, "lightmap");
            opacityUniform = GL20.glGetUniformLocation(program, "opacity");
            fogEnabledUniform = GL20.glGetUniformLocation(program, "fogEnabled");
            fogModeUniform = GL20.glGetUniformLocation(program, "fogMode");
            FMLLog.info("[Occultic NTM] Living tar shader linked: program=%d, GL=%s, OpenGL20=%s, OpenGL33=%s", program, GL11.glGetString(GL11.GL_VERSION), GLContext.getCapabilities().OpenGL20, GLContext.getCapabilities().OpenGL33);
        } catch (Exception failure) {
            if (program != 0) GL20.glDeleteProgram(program);
            program = 0;
            FMLLog.warning("[Occultic NTM] Tar shader unavailable; using fallback tar material until resource reload: %s", failure.toString());
        }
    }

    private static String read(IResourceManager manager, String suffix) throws Exception {
        ResourceLocation resource = new ResourceLocation("occultic_ntm", "shaders/living_tar." + suffix);
        try (InputStream in = manager.getResource(resource).getInputStream()) {
            return new String(IOUtils.toByteArray(in), StandardCharsets.UTF_8);
        }
    }

    static int createProgram(String vertexSource, String fragmentSource) {
        int vertex = 0, fragment = 0, result = 0;
        boolean linked = false;
        try {
            vertex = compile(GL20.GL_VERTEX_SHADER, vertexSource);
            fragment = compile(GL20.GL_FRAGMENT_SHADER, fragmentSource);
            result = GL20.glCreateProgram();
            GL20.glAttachShader(result, vertex);
            GL20.glAttachShader(result, fragment);
            GL20.glLinkProgram(result);
            if (GL20.glGetProgrami(result, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
                throw new IllegalStateException(GL20.glGetProgramInfoLog(result, 4096));
            linked = true;
            return result;
        } finally {
            if (vertex != 0) GL20.glDeleteShader(vertex);
            if (fragment != 0) GL20.glDeleteShader(fragment);
            if (!linked && result != 0) GL20.glDeleteProgram(result);
        }
    }

    private static int compile(int type, String source) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shader, 4096);
            GL20.glDeleteShader(shader);
            throw new IllegalStateException(log);
        }
        return shader;
    }

    public void render(float age, Runnable draw) {
        render(age, 0, 0, 0, draw);
    }

    public void render(float age, float originX, float originY, float originZ, Runnable draw) {
        render(age, originX, originY, originZ, true, draw);
    }

    /** Ground residue has no base texture, including when shader compilation fails. */
    public void renderPuddle(float age, float originX, float originY, float originZ, Runnable draw) {
        render(age, originX, originY, originZ, false, draw);
    }

    private void render(float age, float originX, float originY, float originZ, boolean texturedFallback, Runnable draw) {
        ensureProgram();
        boolean shaders = program != 0 || GLContext.getCapabilities().OpenGL20 || GLContext.getCapabilities().OpenGL33;
        int previousProgram = shaders ? GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) : 0;
        int previousUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthWrite = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        int srcRGB = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB), dstRGB = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int srcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA), dstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        int alphaFunc = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
        float alphaRef = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
        color.clear(); GL11.glGetFloat(GL11.GL_CURRENT_COLOR, color);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        int texture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        boolean textureEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        try {
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.01F);
            // Near-opaque front surfaces still occlude their own rear faces/limbs.
            GL11.glDepthMask(true);
            GL11.glColor4f(1, 1, 1, OPACITY);
            if (!texturedFallback) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                if (program == 0) {
                    if (shaders) GL20.glUseProgram(0);
                    GL11.glColor4f(.018F, .016F, .014F, OPACITY);
                }
            }
            if (program != 0) {
                GL20.glUseProgram(program);
                GL20.glUniform1i(lightmapUniform, 1);
                GL20.glUniform1f(opacityUniform, OPACITY);
                GL20.glUniform1f(timeUniform, age / 20.0F);
                GL20.glUniform3f(surfaceOriginUniform, originX, originY, originZ);
                GL20.glUniform1i(fogEnabledUniform, GL11.glIsEnabled(GL11.GL_FOG) ? 1 : 0);
                GL20.glUniform1i(fogModeUniform, GL11.glGetInteger(GL11.GL_FOG_MODE));
            }
            draw.run();
            if (!reportedDraw) {
                reportedDraw = true;
                int activeProgram = shaders ? GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) : 0;
                if (program != 0 && activeProgram != program)
                    FMLLog.warning("[Occultic NTM] Living tar draw changed shader program: expected=%d, active=%d", program, activeProgram);
                else FMLLog.info("[Occultic NTM] Living tar direct draw: shader=%d, active=%d, fallback=%s", program, activeProgram, program == 0);
            }
        } finally {
            if (shaders) GL20.glUseProgram(previousProgram);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
            if (textureEnabled) GL11.glEnable(GL11.GL_TEXTURE_2D); else GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(color.get(0), color.get(1), color.get(2), color.get(3));
            GL11.glAlphaFunc(alphaFunc, alphaRef);
            GL11.glDepthMask(depthWrite);
            OpenGlHelper.glBlendFunc(srcRGB, dstRGB, srcAlpha, dstAlpha);
            if (blend) GL11.glEnable(GL11.GL_BLEND); else GL11.glDisable(GL11.GL_BLEND);
            OpenGlHelper.setActiveTexture(previousUnit);
        }
    }
}
