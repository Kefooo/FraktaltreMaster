package com.example.fraktaltremaster;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 * En enkel JavaFX-applikasjon som tegner et fraktalt tre på et lerret.
 */
public class basicfraktal extends Application {

    private static final int bredde = 800;
    private static final int høyde = 600;
    private static final double legnde = 100;
    private static final double vinkel = 30;
    private static final double reduksjon = 0.7;
    private static final double minsteGrein = 2.0;
    private static final int rekursjonNivå = 10;

    /**
     * Hovedinngangspunktet for JavaFX-applikasjonen.
     *
     * @param args Kommando-linje argumenter (ikke brukt).
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initialiserer JavaFX-applikasjonen.
     *
     * @param primaryStage Hovedvinduet for denne applikasjonen.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fraktaltre");

        // Opprett lerret for tegning
        Canvas canvas = new Canvas(bredde, høyde);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Tegn fraktaltreet
        stamme(gc);

        // Sett opp scene og vindu
        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        Scene scene = new Scene(root, bredde, høyde);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Tegner fraktaltreet på lerretet med faste parametere.
     *
     * @param gc GraphicsContext som brukes til å tegne på lerretet.
     */
    private void stamme(GraphicsContext gc) {
        gc.setStroke(Paint.valueOf("black"));
        gc.setLineWidth(2);
        greinRekursjon(gc, bredde / 2, høyde, -90, legnde, vinkel, reduksjon, minsteGrein, rekursjonNivå, 0);
    }

    /**
     * Rekursivt tegner greiner av fraktaltreet.
     *
     * @param gc GraphicsContext som brukes til å tegne på lerretet.
     * @param x Start x-koordinat for grenen.
     * @param y Start y-koordinat for grenen.
     * @param angle Vinkel på grenen.
     * @param length Lengde på grenen.
     * @param branchAngle Vinkel mellom greiner.
     * @param branchFactor Faktor som bestemmer hvor mye greinens lengde reduseres.
     * @param minSize Minimum størrelse på greiner som skal tegnes.
     * @param maxDepth Maksimal rekursjonsdybde.
     * @param depth Nåværende rekursjonsdybde.
     */
    private void greinRekursjon(GraphicsContext gc, double x, double y, double angle, double length, double branchAngle, double branchFactor, double minSize, int maxDepth, int depth) {
        if (length < minSize || depth >= maxDepth) return;

        double xEnd = x + length * Math.cos(Math.toRadians(angle));
        double yEnd = y + length * Math.sin(Math.toRadians(angle));

        gc.strokeLine(x, y, xEnd, yEnd);

        // Rekursive kall for å tegne nye greiner
        double newLength = length * branchFactor;

        greinRekursjon(gc, xEnd, yEnd, angle + branchAngle, newLength, branchAngle, branchFactor, minSize, maxDepth, depth + 1);
        greinRekursjon(gc, xEnd, yEnd, angle - branchAngle, newLength, branchAngle, branchFactor, minSize, maxDepth, depth + 1);
    }
}

