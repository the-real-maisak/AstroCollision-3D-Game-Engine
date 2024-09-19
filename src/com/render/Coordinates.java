package com.render;

public class Coordinates {
    int v;
    int vt;
    int vn;


    Coordinates(String[] in) {
        this.v = Integer.parseInt(in[0]);
        this.vt = Integer.parseInt(in[1]);
        this.vn = Integer.parseInt(in[2]);
    }
}
