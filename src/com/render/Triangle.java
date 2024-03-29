package com.render;

import java.awt.*;

public class Triangle {
    Vertex v1;
    Vertex v2;
    Vertex v3;
    Color color;

    Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }

    Triangle mirror(Triangle in) {
        return new Triangle(in.v1.alternate(v1), in.v2.alternate(v2), in.v3.alternate(v3), color);
    }

    Triangle XAxis(Triangle in) {
        Vertex in1 = in.v1;
        Vertex in2 = in.v2;
        Vertex in3 = in.v3.alternate(v3);
        return new Triangle(in1, in2, in3, color);
    }
}
