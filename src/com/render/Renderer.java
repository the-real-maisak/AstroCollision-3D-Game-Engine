package com.render;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Renderer {

    public static void main(String[] args) {


        // создание фигуры
        ParseObj obj = new ParseObj();
        obj.parse();
        ArrayList<Triangle> figure = obj.getTriangles();


        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());


        // слайдер горизольтального вращения
        JSlider headingSlider = new JSlider(-180, 180, 0);
        pane.add(headingSlider, BorderLayout.SOUTH);

        // слайдер верт. вращения
        JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(pitchSlider, BorderLayout.EAST);

        // слайдер вращения
        JSlider rollSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(rollSlider, BorderLayout.WEST);

        // слайдер поля зрения
        JSlider fovSlider = new JSlider(1, 179, 60);
        pane.add(fovSlider, BorderLayout.NORTH);

        JSlider positionYSlider = new JSlider(SwingConstants.VERTICAL,-90, 90, 0);
        pane.add(positionYSlider, BorderLayout.CENTER);

        JSlider positionXSlider = new JSlider(-90, 90, 0);
        pane.add(positionXSlider, BorderLayout.CENTER);

        // панель для рендера
        JPanel renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.DARK_GRAY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // отрисовка происходит здесь

                double heading = Math.toRadians(headingSlider.getValue());
                Matrix4 headingTransform = new Matrix4(
                        new double[][]{
                                {Math.cos(heading), 0, Math.sin(heading), 0},
                                {0, 1, 0, 0},
                                {-Math.sin(heading), 0, Math.cos(heading), 0},
                                {0, 0, 0, 1}
                        }
                );

                double pitch = Math.toRadians(pitchSlider.getValue());
                Matrix4 pitchTransform = new Matrix4(
                        new double[][]{
                                {1, 0, 0, 0},
                                {0, Math.cos(pitch), Math.sin(pitch), 0},
                                {0, -Math.sin(pitch), Math.cos(pitch), 0},
                                {0, 0, 0, 1}
                        }
                );

                double roll = Math.toRadians(rollSlider.getValue());
                Matrix4 rollTransform = new Matrix4(
                        new double[][]{
                                {Math.cos(roll), Math.sin(roll), 0, 0},
                                {-Math.sin(roll), Math.cos(roll), 0, 0},
                                {0, 0, 1, 0},
                                {0, 0, 0, 1}
                        }
                );

                Matrix4 panOutTransform = new Matrix4(
                        new double[][]{
                                {1, 0, 0, 0},
                                {0, 1, 0, 0},
                                {0, 0, 1, 0},
                                {0, 0, -400, 1}
                        }
                );

                double YPos = Math.toRadians(positionYSlider.getValue());
                double XPos = Math.toRadians(positionXSlider.getValue());
                Matrix4 positionYTranslation = new Matrix4(
                        new double[][]{
                                {1, 0, 0, 0},
                                {0, 1, 0, 0},
                                {0, 0, 1, 0},
                                {XPos*100, YPos*100, 0, 1}
                        }
                );

                double viewportWidth = getWidth();
                double viewportHeight = getHeight();
                double fovAngle = Math.toRadians(fovSlider.getValue());
                double fov = Math.tan(fovAngle / 2) * 170;

                Matrix4 transform = headingTransform
                        .multiply(pitchTransform)
                        .multiply(rollTransform)
                        .multiply(panOutTransform)
                        .multiply(positionYTranslation)
                        ;


                g2.setColor(Color.BLACK);
//                Path2D path = new Path2D.Double();
//                Path2D path2 = new Path2D.Double();


                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

                double[] zBuffer = new double[img.getWidth() * img.getHeight()];

                // инициализация массива бесконечной глубиной
                Arrays.fill(zBuffer, Double.NEGATIVE_INFINITY);


                for (Triangle t : figure) {
                    Vertex v1 = transform.transform(t.v1);
                    Vertex v2 = transform.transform(t.v2);
                    Vertex v3 = transform.transform(t.v3);


                    v1.x = v1.x / (-v1.z) * fov;
                    v1.y = v1.y / (-v1.z) * fov;
                    v2.x = v2.x / (-v2.z) * fov;
                    v2.y = v2.y / (-v2.z) * fov;
                    v3.x = v3.x / (-v3.z) * fov;
                    v3.y = v3.y / (-v3.z) * fov;

                    // затенение нормали
                    Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z, v2.w - v1.w);
                    Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z, v3.w - v1.w);
                    Vertex norm = new Vertex(
                            ab.y * ac.z - ab.z * ac.y,
                            ab.z * ac.x - ab.x * ac.z,
                            ab.x * ac.y - ab.y * ac.x,
                            1
                    );

                    double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);

                    norm.x /= normalLength;
                    norm.y /= normalLength;
                    norm.z /= normalLength;

                    // проверка на видимость
                    if (norm.z < 0) {
                        continue;
                    }


                    v1.x += viewportWidth / 2;
                    v1.y += viewportHeight / 2;
                    v2.x += viewportWidth / 2;
                    v2.y += viewportHeight / 2;
                    v3.x += viewportWidth / 2;
                    v3.y += viewportHeight / 2;

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
                                    img.setRGB(x, y, Shades.getShade(t.color, norm.z).getRGB());
                                    zBuffer[zIndex] = depth;
                                }
                            }
                        }
                    }
                    // рендер граней треугольника через 2Д графику
//                    path.moveTo(v1.x, v1.y);
//                    path.lineTo(v2.x, v2.y);
//                    path.lineTo(v3.x, v3.y);
//                    path.closePath();
//
//
//
//                    g2.setColor(Color.RED);
//                    path2.moveTo(v1.x, v1.y);
//                    path2.lineTo(v1.x/-norm.z, v1.y/-norm.z);
//                    path2.closePath();
//                    path2.moveTo(v2.x, v2.y);
//                    path2.lineTo(v2.x/-norm.z, v1.y/-norm.z);
//                    path2.closePath();
//                    path2.moveTo(v3.x, v3.y);
//                    path2.lineTo(v3.x/-norm.z, v3.y/-norm.z);
//                    path2.closePath();
                }

                g2.drawImage(img, 0, 0, null);

//                g2.draw(path);
//                g2.draw(path2);
            }
        };
        pane.add(renderPanel, FlowLayout.RIGHT);

        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pitchSlider.addChangeListener(e -> renderPanel.repaint());
        rollSlider.addChangeListener(e -> renderPanel.repaint());
        fovSlider.addChangeListener(e -> renderPanel.repaint());

        positionXSlider.addChangeListener(e -> renderPanel.repaint());
        positionYSlider.addChangeListener(e -> renderPanel.repaint());

        frame.setSize(800, 600);
        frame.setVisible(true);

        //выход при закрытии окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
