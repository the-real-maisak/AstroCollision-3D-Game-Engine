package com.render;


import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ParseObj {
    ArrayList<Triangle> triangles;

    String path;

    public void parse() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter path to obj file: ");
            path = scanner.nextLine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            if (!Paths.get(path).toFile().exists()) {
                System.out.println("File not found! \n loading default file...");
                path = "C:\\Users\\satan\\Desktop\\test.obj";
                if (!Paths.get(path).toFile().exists()) {
                    System.out.println("Default file not found! \n loading default object...");
                    this.triangles = new ArrayList<>() {
                        {
                            add(new Triangle(new Vertex(100, 100, 100,1),
                                    new Vertex(-100, -100, 100,1),
                                    new Vertex(-100, 100, -100,1),
                                    Color.WHITE));

                            add(new Triangle(new Vertex(100, 100, 100,1),
                                    new Vertex(-100, -100, 100,1),
                                    new Vertex(100, -100, -100,1),
                                    Color.RED));

                            add(new Triangle(new Vertex(-100, 100, -100,1),
                                    new Vertex(100, -100, -100,1),
                                    new Vertex(100, 100, 100,1),
                                    Color.BLUE));

                            add(new Triangle(new Vertex(-100, 100, -100,1),
                                    new Vertex(100, -100, -100,1),
                                    new Vertex(-100, -100, 100,1),
                                    Color.YELLOW));
                        }
                    };
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Polygon> polygons = new ArrayList<>();
        ArrayList<Triangle> triangles = new ArrayList<>();
        ArrayList<Vertex> normals = new ArrayList<>();

        List<String> verts = new ArrayList<>();
        List<String> norms = new ArrayList<>();
        List<String> polys = new ArrayList<>();
        try {
            verts = Files.readAllLines(Path.of(path)).stream().filter(s -> s.startsWith("v ")).toList();
            polys = Files.readAllLines(Path.of(path)).stream().filter(s -> s.startsWith("f ")).toList();
            norms = Files.readAllLines(Path.of(path)).stream().filter(s -> s.startsWith("vn ")).toList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        for (String vert : verts) {
            String[] parts = vert.split(" ");
            vertices.add(new Vertex(Double.parseDouble(parts[2])*5, Double.parseDouble(parts[3])*5, Double.parseDouble(parts[4])*5, 1));
        }

        for (String norm : norms) {
            String[] parts = norm.split(" ");
            normals.add(new Vertex(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), 1));
        }

        for (String poly : polys) {
            String[] parts = poly.split(" ");
            polygons.add(new Polygon(
                    new Coordinates(parts[1].split("/")),
                    new Coordinates(parts[2].split("/")),
                    new Coordinates(parts[3].split("/")),
                    new Coordinates(parts[4].split("/"))
            ));
        }
        for (Polygon polygon : polygons) {
            triangles.add(new Triangle(
                    vertices.get((polygon.c1.v) - 1),
                    vertices.get((polygon.c2.v) - 1),
                    vertices.get((polygon.c3.v) - 1),
                    Color.WHITE
            ));
            triangles.add(new Triangle(
                    vertices.get((polygon.c1.v) - 1),
                    vertices.get((polygon.c3.v) - 1),
                    vertices.get((polygon.c4.v) - 1),
                    Color.ORANGE
            ));
        }
        this.triangles = triangles;
    }


    public ArrayList<Triangle> getTriangles() {
        return this.triangles;
    }
}


