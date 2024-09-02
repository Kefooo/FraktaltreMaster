package com.example.fraktaltremaster;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 * En JavaFX-applikasjon som tegner et fraktalt tre på en lerret.
 * Utseendet til treet kan justeres interaktivt ved hjelp av skyveknapper.
 */
public class fraktaltreEin extends Application {

    private static final int BREDDE = 800;
    private static final int HØYDE = 600;
    private static final double MIN_GREINSTØRRELSE = 2.0; // Minimum størrelse for greiner som skal tegnes

    private Slider vinkelSkyveknapp;
    private Slider lengdeSkyveknapp;
    private Slider greinForholdSkyveknapp;
    private Slider minStørrelseSkyveknapp;
    private Slider tilfeldighetSkyveknapp;
    private Slider maksDybdeSkyveknapp;

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
     * @param primærStage Den primære scenen for denne applikasjonen.
     */
    @Override
    public void start(Stage primærStage) {
        primærStage.setTitle("Fraktaltre");

        // Opprett lerret for tegning
        Canvas lerret = new Canvas(BREDDE - 200, HØYDE);
        GraphicsContext gc = lerret.getGraphicsContext2D();

        // Initialiser skyveknapper og etiketter
        vinkelSkyveknapp = opprettSkyveknapp("Vinkel på greiner:", 0, 90, 30);
        lengdeSkyveknapp = opprettSkyveknapp("Lengde på greiner:", 0, 200, 100);
        greinForholdSkyveknapp = opprettSkyveknapp("Forhold mellom greiner:", 0.1, 1.0, 0.7);
        minStørrelseSkyveknapp = opprettSkyveknapp("Minst greinstørrelse:", MIN_GREINSTØRRELSE, 20, MIN_GREINSTØRRELSE);
        tilfeldighetSkyveknapp = opprettSkyveknapp("Tilfeldighet:", 0, 1, 0.2);
        maksDybdeSkyveknapp = opprettSkyveknapp("Maks dybde:", 1, 15, 10);

        // Opprett VBox for skyveknapper og etiketter
        VBox skyveknappBoks = new VBox(
                opprettSkyveknappMedEtikett(vinkelSkyveknapp, "Vinkel på greiner:"),
                opprettSkyveknappMedEtikett(lengdeSkyveknapp, "Lengde på greiner:"),
                opprettSkyveknappMedEtikett(greinForholdSkyveknapp, "Forhold mellom greiner:"),
                opprettSkyveknappMedEtikett(minStørrelseSkyveknapp, "Minst greinstørrelse:"),
                opprettSkyveknappMedEtikett(tilfeldighetSkyveknapp, "Tilfeldighet:"),
                opprettSkyveknappMedEtikett(maksDybdeSkyveknapp, "Maks dybde:")
        );
        skyveknappBoks.setSpacing(10);
        skyveknappBoks.setStyle("-fx-padding: 10;");

        // Opprett BorderPane for layout
        BorderPane rot = new BorderPane();
        rot.setCenter(lerret);
        rot.setLeft(skyveknappBoks);

        Scene scene = new Scene(rot, BREDDE, HØYDE);
        primærStage.setScene(scene);
        primærStage.show();

        // Tegn treet initialt
        tegnTreet(gc);

        // Tegn treet på nytt når skyveknappene justeres
        vinkelSkyveknapp.valueProperty().addListener((a, gammelt, nytt) -> tegnTreet(gc));
        lengdeSkyveknapp.valueProperty().addListener((obs, gammelt, nytt) -> tegnTreet(gc));
        greinForholdSkyveknapp.valueProperty().addListener((obs, gammelt, nytt) -> tegnTreet(gc));
        minStørrelseSkyveknapp.valueProperty().addListener((obs, gammelt, nytt) -> tegnTreet(gc));
        tilfeldighetSkyveknapp.valueProperty().addListener((obs, gammelt, nytt) -> tegnTreet(gc));
        maksDybdeSkyveknapp.valueProperty().addListener((obs, gammelt, nytt) -> tegnTreet(gc));
    }

    /**
     * Oppretter en skyveknapp med spesifiserte parametere.
     *
     * @param etikett Etiketttekst for skyveknappen.
     * @param min Minimumsverdien for skyveknappen.
     * @param maks Maksimumsverdien for skyveknappen.
     * @param initial Initialverdien for skyveknappen.
     * @return En konfigurert Slider-forekomst.
     */
    private Slider opprettSkyveknapp(String etikett, double min, double maks, double initial) {
        Slider skyveknapp = new Slider(min, maks, initial);
        skyveknapp.setPrefWidth(150); // Sett en smalere bredde for skyveknappen
        skyveknapp.setShowTickMarks(true);
        skyveknapp.setShowTickLabels(true);
        skyveknapp.setMajorTickUnit((maks - min) / 10);
        skyveknapp.setMinorTickCount(1);
        skyveknapp.setBlockIncrement((maks - min) / 10);
        return skyveknapp;
    }

    /**
     * Oppretter en VBox som inneholder en etikett og en skyveknapp.
     *
     * @param skyveknapp Skyveknappen som skal legges til i VBox.
     * @param etikett Etiketttekst for skyveknappen.
     * @return En VBox som inneholder etikett og skyveknapp.
     */
    private VBox opprettSkyveknappMedEtikett(Slider skyveknapp, String etikett) {
        Label etikettLabel = new Label(etikett);
        VBox skyveknappBoks = new VBox(etikettLabel, skyveknapp);
        skyveknappBoks.setSpacing(5);
        return skyveknappBoks;
    }

    /**
     * Tegner det fraktale treet på lerretet basert på de nåværende verdiene fra skyveknappene.
     *
     * @param gc GraphicsContext som brukes til å tegne på lerretet.
     */
    private void tegnTreet(GraphicsContext gc) {
        double vinkel = vinkelSkyveknapp.getValue();
        double lengde = lengdeSkyveknapp.getValue();
        double greinForhold = greinForholdSkyveknapp.getValue();
        double minStørrelse = minStørrelseSkyveknapp.getValue();
        double tilfeldighet = tilfeldighetSkyveknapp.getValue();
        int maksDybde = (int) maksDybdeSkyveknapp.getValue();

        gc.clearRect(0, 0, BREDDE - 200, HØYDE); // Rens bare lerretområdet
        gc.setStroke(Paint.valueOf("brown"));
        gc.setLineWidth(2);
        tegnGren(gc, (BREDDE - 200) / 2, HØYDE, -90, lengde, vinkel, greinForhold, minStørrelse, tilfeldighet, maksDybde, 0);
    }

    /**
     * Rekursivt tegner grener av det fraktale treet.
     *
     * @param gc GraphicsContext som brukes til å tegne på lerretet.
     * @param x Start x-koordinat for grenen.
     * @param y Start y-koordinat for grenen.
     * @param vinkel Vinkelen på grenen.
     * @param lengde Lengden på grenen.
     * @param greinVinkel Vinkelen mellom grenene.
     * @param greinForhold Faktoren som grenlengden reduseres med.
     * @param minStørrelse Minimumsstørrelse på grenene som skal tegnes.
     * @param tilfeldighet Graden av tilfeldighet som brukes på vinkler og lengder.
     * @param maksDybde Maksimum rekursjonsdybde.
     * @param dybde Nåværende rekursjonsdybde.
     */
    private void tegnGren(GraphicsContext gc, double x, double y, double vinkel, double lengde, double greinVinkel, double greinForhold, double minStørrelse, double tilfeldighet, int maksDybde, int dybde) {
        if (lengde < minStørrelse || dybde >= maksDybde) return;

        double xEnd = x + lengde * Math.cos(Math.toRadians(vinkel));
        double yEnd = y + lengde * Math.sin(Math.toRadians(vinkel));

        gc.strokeLine(x, y, xEnd, yEnd);

        // Randomiser vinkel og lengde
        double vinkelVariasjon = (Math.random() * 2 - 1) * tilfeldighet * greinVinkel;
        double lengdeVariasjon = (Math.random() * 2 - 1) * tilfeldighet * lengde;

        double venstreVinkel = vinkel + greinVinkel + vinkelVariasjon;
        double høyreVinkel = vinkel - greinVinkel - vinkelVariasjon;

        double nyLengde = lengde * greinForhold + lengdeVariasjon;

        // Rekursive kall
        tegnGren(gc, xEnd, yEnd, venstreVinkel, nyLengde, greinVinkel, greinForhold, minStørrelse, tilfeldighet, maksDybde, dybde + 1);
        tegnGren(gc, xEnd, yEnd, høyreVinkel, nyLengde, greinVinkel, greinForhold, minStørrelse, tilfeldighet, maksDybde, dybde + 1);
    }
}
