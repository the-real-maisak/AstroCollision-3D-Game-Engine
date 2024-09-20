package com.render;

public class Vertex {

    double x;
    double y;
    double z;
    double w;

    Vertex(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }


    Vertex alternate(Vertex in) {
        return new Vertex(-in.x, -in.y, -in.z, -in.w);
    }
}
