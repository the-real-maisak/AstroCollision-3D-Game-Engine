package com.render;

public class Vertex {

    double x;
    double y;
    double z;

    Vertex(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Vertex alternate(Vertex in) {
        return new Vertex(-in.x, -in.y, -in.z);
    }
}
