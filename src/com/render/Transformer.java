package com.render;

import java.awt.*;
import java.util.ArrayList;

public class Transformer {

    public static ArrayList<Triangle> inflate(ArrayList<Triangle> tris) {
        ArrayList<Triangle> result = new ArrayList<>();
        for (Triangle t : tris) {
            Vertex m1 =
                    new Vertex((t.v1.x + t.v2.x) / 2, (t.v1.y + t.v2.y) / 2, (t.v1.z + t.v2.z) / 2, 1);
            Vertex m2 =
                    new Vertex((t.v2.x + t.v3.x) / 2, (t.v2.y + t.v3.y) / 2, (t.v2.z + t.v3.z) / 2, 1);
            Vertex m3 =
                    new Vertex((t.v1.x + t.v3.x) / 2, (t.v1.y + t.v3.y) / 2, (t.v1.z + t.v3.z) / 2, 1);
            result.add(new Triangle(t.v1, m1, m3, t.color));
            result.add(new Triangle(t.v2, m1, m2, t.color));
            result.add(new Triangle(t.v3, m2, m3, t.color));
            result.add(new Triangle(m1, m2, m3, t.color));
        }
        for (Triangle t : result) {
            for (Vertex v : new Vertex[]{t.v1, t.v2, t.v3}) {
                double l = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z) / Math.sqrt(30_000);
                v.x /= l;
                v.y /= l;
                v.z /= l;
            }
        }

        return result;
    }

    public static ArrayList<Triangle> sphere(ArrayList<Triangle> tris, int soften) {
        ArrayList<Triangle> result = new ArrayList<>();
        for (Triangle t : tris) {
            Vertex m1 =
                    new Vertex((t.v1.x + t.v2.x) / 2, (t.v1.y + t.v2.y) / 2, (t.v1.z + t.v2.z) / 2, 1);
            Vertex m2 =
                    new Vertex((t.v2.x + t.v3.x) / 2, (t.v2.y + t.v3.y) / 2, (t.v2.z + t.v3.z) / 2, 1);
            Vertex m3 =
                    new Vertex((t.v1.x + t.v3.x) / 2, (t.v1.y + t.v3.y) / 2, (t.v1.z + t.v3.z) / 2, 1);
            result.add(new Triangle(m1, m2, m3, t.color));
            result.add(new Triangle(m1.alternate(m1), m2.alternate(m2), m3.alternate(m3), t.color));
//            Vertex m4 =
//                    new Vertex(-(t.v1.x + t.v2.x) / 2, -(t.v1.y + t.v2.y) / 2, -(t.v1.z + t.v2.z) / 2);
//            Vertex m5 =
//                    new Vertex(-(t.v2.x + t.v3.x) / 2, -(t.v2.y + t.v3.y) / 2, -(t.v2.z + t.v3.z) / 2);
//            Vertex m6 =
//                    new Vertex(-(t.v1.x + t.v3.x) / 2, -(t.v1.y + t.v3.y) / 2, -(t.v1.z + t.v3.z) / 2);
//            result.add(new Triangle(m4, m5, m6, t.color));

        }
        if (soften < 1) {
            soften = 1;
        }
        for (int i = 0; i < soften; i++) {
            result = inflate(result);
        }
        for (Triangle t : result) {
            for (Vertex v : new Vertex[]{t.v1, t.v2, t.v3}) {
                double l = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z) / Math.sqrt(30_000);
                v.x /= l;
                v.y /= l;
                v.z /= l;
            }
        }
        return result;
    }

    private static ArrayList<Triangle> tris = new ArrayList<>() {
        {
            //A
            tris.add(new Triangle(new Vertex(-100, 100, 100, 1),
                    new Vertex(100, 100, 100, 1),
                    new Vertex(-100, 100, -100, 1),
                    Color.PINK));
            //B
            tris.add(new Triangle(new Vertex(100, 100, 100, 1),
                    new Vertex(100, 100, -100, 1),
                    new Vertex(-100, 100, -100, 1),
                    Color.PINK));
            //C
            tris.add(new Triangle(new Vertex(100, -100, 100, 1),
                    new Vertex(100, 100, -100, 1),
                    new Vertex(100, 100, 100, 1),
                    Color.GREEN));
            //D
            tris.add(new Triangle(new Vertex(100, -100, 100, 1),
                    new Vertex(100, -100, -100, 1),
                    new Vertex(100, 100, -100, 1),
                    Color.GREEN));
            //E
            tris.add(new Triangle(new Vertex(-100, -100, 100, 1),
                    new Vertex(100, -100, 100, 1),
                    new Vertex(-100, 100, 100, 1),
                    Color.YELLOW));

            //F
            tris.add(new Triangle(new Vertex(100, -100, 100, 1),
                    new Vertex(100, 100, 100, 1),
                    new Vertex(-100, 100, 100, 1),
                    Color.YELLOW));
            //G
            tris.add(new Triangle(new Vertex(-100, -100, 100, 1),
                    new Vertex(-100, 100, 100, 1),
                    new Vertex(-100, -100, -100, 1),
                    Color.RED));
            //H
            tris.add(new Triangle(new Vertex(-100, 100, 100, 1),
                    new Vertex(-100, 100, -100, 1),
                    new Vertex(-100, -100, -100, 1),
                    Color.RED));
            //I
            tris.add(new Triangle(new Vertex(-100, 100, -100, 1),
                    new Vertex(100, 100, -100, 1),
                    new Vertex(-100, -100, -100, 1),
                    Color.BLUE));
            //J
            tris.add(new Triangle(new Vertex(-100, -100, -100, 1),
                    new Vertex(100, 100, -100, 1),
                    new Vertex(100, -100, -100, 1),
                    Color.BLUE));
            //K
            tris.add(new Triangle(new Vertex(100, -100, 100, 1),
                    new Vertex(-100, -100, 100, 1),
                    new Vertex(-100, -100, -100, 1),
                    Color.WHITE));
            //L
            tris.add(new Triangle(new Vertex(-100, -100, -100, 1),
                    new Vertex(100, -100, -100, 1),
                    new Vertex(100, -100, 100, 1),
                    Color.WHITE));
        }
    };

    private static ArrayList<Triangle> tris2 = new ArrayList<>() {
        {
            add(new Triangle(new Vertex(100, 100, 100, 1),
                    new Vertex(-100, -100, 100, 1),
                    new Vertex(-100, 100, -100, 1),
                    Color.WHITE));

            add(new Triangle(new Vertex(100, 100, 100, 1),
                    new Vertex(-100, -100, 100, 1),
                    new Vertex(100, -100, -100, 1),
                    Color.RED));

            add(new Triangle(new Vertex(-100, 100, -100, 1),
                    new Vertex(100, -100, -100, 1),
                    new Vertex(100, 100, 100, 1),
                    Color.BLUE));

            add(new Triangle(new Vertex(-100, 100, -100, 1),
                    new Vertex(100, -100, -100, 1),
                    new Vertex(-100, -100, 100, 1),
                    Color.YELLOW));
        }
    };

    public static ArrayList<Triangle> getTris2() {
        return tris2;
    }

    public static ArrayList<Triangle> getTris() {
        return tris;
    }
}
