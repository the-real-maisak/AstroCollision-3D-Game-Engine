package com.render;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Renderer {

    ArrayList<Triangle> tris = new ArrayList<>();
//    double heading = Math.toRadians(headingSlider.getValue());

    public static void main(String[] args) {


        ArrayList<Triangle> tris = new ArrayList<>();
        tris.add(new Triangle(new Vertex(100, 100, 100),
                new Vertex(-100, -100, 100),
                new Vertex(-100, 100, -100),
                Color.WHITE));

        tris.add(new Triangle(new Vertex(100, 100, 100),
                new Vertex(-100, -100, 100),
                new Vertex(100, -100, -100),
                Color.RED));

        tris.add(new Triangle(new Vertex(-100, 100, -100),
                new Vertex(100, -100, -100),
                new Vertex(100, 100, 100),
                Color.BLUE));

        tris.add(new Triangle(new Vertex(-100, 100, -100),
                new Vertex(100, -100, -100),
                new Vertex(-100, -100, 100),
                Color.YELLOW));


        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        // слайдер горизольтального вращения
        JSlider headingSlider = new JSlider(0, 360, 180);
        pane.add(headingSlider, BorderLayout.SOUTH);

        // слайдер верт. вращения
        JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(pitchSlider, BorderLayout.EAST);

        //кнопка выхода
//        JButton exitButton = new JButton("exit");
//        pane.add(exitButton, BorderLayout.NORTH);

        // панель для рендера
        JPanel renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.DARK_GRAY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // рендер (и скорее всего отрисовка) происходят здесь

                double heading = Math.toRadians(headingSlider.getValue());
                Matrix3 headingTransform = new Matrix3(new double[]{Math.cos(heading), 0, Math.sin(heading),
                        0, 1, 0,
                        -Math.sin(heading), 0, Math.cos(heading)});
                double pitch = Math.toRadians(pitchSlider.getValue());

                Matrix3 pitchTransform = new Matrix3(new double[]{1, 0, 0,
                        0, Math.cos(pitch), Math.sin(pitch),
                        0, -Math.sin(pitch), Math.cos(pitch)});
                Matrix3 transform = headingTransform.multiply(pitchTransform);
                g2.translate(getWidth() / 2, getHeight() / 2);
                g2.setColor(Color.BLACK);

                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

                double[] zBuffer = new double[img.getWidth() * img.getHeight()];

                // инициализация массива бесконечной глубиной(очко коляна)
                for (int q = 0; q < zBuffer.length; q++) {
                    zBuffer[q] = Double.NEGATIVE_INFINITY;
                }

                ArrayList<Triangle> figure = sphere(tris, 4);

                for (Triangle t : figure) {
                    Vertex v1 = transform.transform(t.v1);
                    Vertex v2 = transform.transform(t.v2);
                    Vertex v3 = transform.transform(t.v3);

                    // затенение нормали
                    Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
                    Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
                    Vertex norm = new Vertex(ab.y * ac.z - ab.z * ac.y,
                            ab.z * ac.x - ab.x * ac.z,
                            ab.x * ac.y - ab.y * ac.x);
                    double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);

                    norm.x /= normalLength;
                    norm.y /= normalLength;
                    norm.z /= normalLength;

                    double angleCos = Math.abs(norm.z);


                    // рендер граней треугольника через 2Д графику
                    /*
                    Path2D path = new Path2D.Double();
                    path.moveTo(v1.x, v1.y);
                    path.lineTo(v2.x, v2.y);
                    path.lineTo(v3.x, v3.y);
                    path.closePath();
                    g2.draw(path);
                     */


                    // ручная трансляция без 2D графики
                    v1.x += getWidth() / 2;
                    v1.y += getHeight() / 2;
                    v2.x += getWidth() / 2;
                    v2.y += getHeight() / 2;
                    v3.x += getWidth() / 2;
                    v3.y += getHeight() / 2;

                    // расчёт и текстурирование треугольников
                    int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                    int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                    int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                    int maxY = (int) Math.min(img.getHeight() - 1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                    double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

                    for (int y = minY; y <= maxY; y++) {
                        for (int x = minX; x <= maxX; x++) {
                            double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                            double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                            double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                            if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                                double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                                int zIndex = y * img.getWidth() + x;
                                if (zBuffer[zIndex] < depth) {
                                    img.setRGB(x, y, getShade(t.color, angleCos).getRGB());
                                    zBuffer[zIndex] = depth;
                                }
                            }
                        }
                    }

                    g2.drawImage(img, -getWidth() / 2, -getHeight() / 2, null);
                }
            }
        };

        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pitchSlider.addChangeListener(e -> renderPanel.repaint());

        pane.add(renderPanel, BorderLayout.CENTER);
        frame.setSize(400, 400);
        frame.setVisible(true);

        //выход по нажатии кнопки
//        exitButton.addActionListener(e -> System.exit(0));

        //выход при закрытии окна
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });
    }

    public static Color getShade(Color color, double shade) {
        double redLinear = Math.pow(color.getRed(), 2.4 * shade);
        double greenLinear = Math.pow(color.getGreen(), 2.4 * shade);
        double blueLinear = Math.pow(color.getBlue(), 2.4 * shade);

        int red = (int) Math.pow(redLinear, 1 / 2.4);
        int green = (int) Math.pow(greenLinear, 1 / 2.4);
        int blue = (int) Math.pow(blueLinear, 1 / 2.4);

        return new Color(red, green, blue);
    }

    public static ArrayList inflate(ArrayList<Triangle> tris) {
        ArrayList<Triangle> result = new ArrayList<>();
        for (Triangle t : tris) {
            Vertex m1 =
                    new Vertex((t.v1.x + t.v2.x) / 2, (t.v1.y + t.v2.y) / 2, (t.v1.z + t.v2.z) / 2);
            Vertex m2 =
                    new Vertex((t.v2.x + t.v3.x) / 2, (t.v2.y + t.v3.y) / 2, (t.v2.z + t.v3.z) / 2);
            Vertex m3 =
                    new Vertex((t.v1.x + t.v3.x) / 2, (t.v1.y + t.v3.y) / 2, (t.v1.z + t.v3.z) / 2);
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

    public static ArrayList sphere(ArrayList<Triangle> tris, int soften) {
        ArrayList<Triangle> result = new ArrayList<>();
        for (Triangle t : tris) {
            Vertex m1 =
                    new Vertex((t.v1.x + t.v2.x) / 2, (t.v1.y + t.v2.y) / 2, (t.v1.z + t.v2.z) / 2);
            Vertex m2 =
                    new Vertex((t.v2.x + t.v3.x) / 2, (t.v2.y + t.v3.y) / 2, (t.v2.z + t.v3.z) / 2);
            Vertex m3 =
                    new Vertex((t.v1.x + t.v3.x) / 2, (t.v1.y + t.v3.y) / 2, (t.v1.z + t.v3.z) / 2);
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
        if (soften > 6) {
            soften = 5;
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

    public static ArrayList cube(ArrayList<Triangle> tris) {
        ArrayList<Triangle> polygon = new ArrayList<>();

        polygon.add(new Triangle(new Vertex(-100, -100, -100),
                new Vertex(100, -100, -100),
                new Vertex(-100, 100, -100),
                Color.RED));

        polygon.add(new Triangle(new Vertex(100, 100, -100),
                new Vertex(-100, 100, -100),
                new Vertex(100, -100, -100),
                Color.BLUE));

        ArrayList<Triangle> result = new ArrayList<>();
        for (Triangle t : polygon) {
            Vertex m1 =
                    new Vertex((t.v1.x ) / 2, (t.v1.y ) / 2, (t.v1.z ) / 2);
            Vertex m2 =
                    new Vertex((t.v2.x ) / 2, (t.v2.y ) / 2, (t.v2.z ) / 2);
            Vertex m3 =
                    new Vertex((t.v3.x ) / 2, (t.v3.y ) / 2, (t.v3.z ) / 2);
            result.add(new Triangle(m1, m2, m3, t.color));
            result.add(t.mirror(t));
//            result.add(t.XAxis(t));
//            result.add(t.XAxis(t).mirror(t));
//            Vertex m4 =
//                    new Vertex(-(t.v1.x ) / 2, -(t.v1.y ) / 2, -(t.v1.z ) / 2);
//            Vertex m5 =
//                    new Vertex(-(t.v2.x ) / 2, -(t.v2.y ) / 2, -(t.v2.z ) / 2);
//            Vertex m6 =
//                    new Vertex(-(t.v3.x ) / 2, -(t.v3.y ) / 2, -(t.v3.z ) / 2);
//            result.add(new Triangle(m4, m5, m6, t.color));

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
}
