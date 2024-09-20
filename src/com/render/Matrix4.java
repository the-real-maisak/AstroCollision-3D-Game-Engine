package com.render;

public class Matrix4 {

    double[][] values;

    Matrix4(double[][] values) {
        this.values = values;
    }

    Matrix4 multiply(Matrix4 other) {
        double[][] result = new double[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                for (int i = 0; i < 4; i++) {
                    result[row][col] += this.values[row][i] * other.values[i][col];
                }
            }
        }
        return new Matrix4(result);
    }

    Vertex transform(Vertex in) {
        return new Vertex(
                in.x * values[0][0] + in.y * values[1][0] + in.z * values[2][0] + in.w * values[3][0],
                in.x * values[0][1] + in.y * values[1][1] + in.z * values[2][1] + in.w * values[3][1],
                in.x * values[0][2] + in.y * values[1][2] + in.z * values[2][2] + in.w * values[3][2],
                in.x * values[0][3] + in.y * values[1][3] + in.z * values[2][3] + in.w * values[3][3]);
    }
}
