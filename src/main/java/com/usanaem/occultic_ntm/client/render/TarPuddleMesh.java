package com.usanaem.occultic_ntm.client.render;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

/** Small cached surface clipped to a liquid outline, with a rounded raised meniscus. */
@SideOnly(Side.CLIENT)
public final class TarPuddleMesh {
    public static final int WEST = 1, EAST = 2, NORTH = 4, SOUTH = 8;
    private static final int RESOLUTION = 20;
    private final int worldX, worldZ, neighbors;
    // Position and normal, six floats per vertex. UVs follow local X/Z for the fallback.
    private final float[] vertices;

    public TarPuddleMesh(int worldX, int worldZ, int neighbors) {
        this.worldX = worldX;
        this.worldZ = worldZ;
        this.neighbors = neighbors;
        List<Point> mesh = new ArrayList<Point>();
        for (int z = 0; z < RESOLUTION; z++) {
            for (int x = 0; x < RESOLUTION; x++) {
                Point a = point(x, z), b = point(x, z + 1);
                Point c = point(x + 1, z + 1), d = point(x + 1, z);
                clip(mesh, a, b, c);
                clip(mesh, a, c, d);
            }
        }
        vertices = new float[mesh.size() * 6];
        int i = 0;
        for (Point p : mesh) {
            vertices[i++] = p.x; vertices[i++] = p.y; vertices[i++] = p.z;
            vertices[i++] = p.nx; vertices[i++] = p.ny; vertices[i++] = p.nz;
        }
    }

    private Point point(int x, int z) {
        float px = (float) x / RESOLUTION, pz = (float) z / RESOLUTION;
        return top(px, pz, field(px, pz));
    }

    private Point top(float x, float z, float field) {
        float dx = (height(this.field(x + .002F, z)) - height(this.field(x - .002F, z))) / .004F;
        float dz = (height(this.field(x, z + .002F)) - height(this.field(x, z - .002F))) / .004F;
        float length = (float) Math.sqrt(dx * dx + 1 + dz * dz);
        return new Point(x, height(field), z, -dx / length, 1 / length, -dz / length, field);
    }

    private static float height(float field) {
        float t = Math.max(0, Math.min(1, field / .18F));
        return .008F + .028F * t * t * (3 - 2 * t);
    }

    private float field(float x, float z) {
        double wx = worldX + (double) x, wz = worldZ + (double) z;
        float wobble = (float) (Math.sin(wx * 17 + wz * 9) * .019
                + Math.sin(wx * 7 - wz * 13) * .016);
        float dx = x - .5F, dz = z - .5F;
        float result = .405F + wobble - (float) Math.sqrt(dx * dx + dz * dz);
        // Matching necks across each shared block edge; world-space wobble meets exactly.
        if ((neighbors & WEST) != 0) result = Math.max(result, .265F + wobble - distance(x, dz, 0, .5F));
        if ((neighbors & EAST) != 0) result = Math.max(result, .265F + wobble - distance(x, dz, .5F, 1));
        if ((neighbors & NORTH) != 0) result = Math.max(result, .265F + wobble - distance(z, dx, 0, .5F));
        if ((neighbors & SOUTH) != 0) result = Math.max(result, .265F + wobble - distance(z, dx, .5F, 1));
        return result;
    }

    private static float distance(float along, float across, float start, float end) {
        float outside = along - Math.max(start, Math.min(end, along));
        return (float) Math.sqrt(outside * outside + across * across);
    }

    private void clip(List<Point> mesh, Point a, Point b, Point c) {
        Point[] source = {a, b, c};
        Point[] polygon = new Point[4];
        int count = 0;
        Point previous = c;
        for (Point current : source) {
            if ((previous.field >= 0) != (current.field >= 0)) {
                float t = previous.field / (previous.field - current.field);
                polygon[count++] = top(previous.x + t * (current.x - previous.x),
                        previous.z + t * (current.z - previous.z), 0);
            }
            if (current.field >= 0) polygon[count++] = current;
            previous = current;
        }
        for (int i = 1; i + 1 < count; i++) {
            mesh.add(polygon[0]); mesh.add(polygon[i]); mesh.add(polygon[i + 1]);
        }
        for (int i = 0; i < count; i++) {
            Point p = polygon[i], q = polygon[(i + 1) % count];
            if (p.field == 0 && q.field == 0) skirt(mesh, p, q);
        }
    }

    private static void skirt(List<Point> mesh, Point p, Point q) {
        float dx = q.x - p.x, dz = q.z - p.z;
        float length = (float) Math.sqrt(dx * dx + dz * dz);
        if (length < .00001F) return;
        Point a = new Point(p.x, p.y, p.z, -dz / length, 0, dx / length, 0);
        Point b = new Point(q.x, q.y, q.z, -dz / length, 0, dx / length, 0);
        Point c = new Point(q.x, .001F, q.z, b.nx, 0, b.nz, 0);
        Point d = new Point(p.x, .001F, p.z, a.nx, 0, a.nz, 0);
        mesh.add(a); mesh.add(d); mesh.add(c);
        mesh.add(a); mesh.add(c); mesh.add(b);
    }

    /** Immediate submission keeps this mesh inside the active material, including under Angelica. */
    public void draw(int brightness) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawing(GL11.GL_TRIANGLES);
        tess.setBrightness(brightness);
        for (int i = 0; i < vertices.length; i += 6) {
            tess.setNormal(vertices[i + 3], vertices[i + 4], vertices[i + 5]);
            tess.addVertexWithUV(vertices[i], vertices[i + 1], vertices[i + 2], vertices[i], vertices[i + 2]);
        }
        tess.draw();
    }

    private static final class Point {
        final float x, y, z, nx, ny, nz, field;
        Point(float x, float y, float z, float nx, float ny, float nz, float field) {
            this.x = x; this.y = y; this.z = z;
            this.nx = nx; this.ny = ny; this.nz = nz; this.field = field;
        }
    }
}
