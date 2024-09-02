package com.example.fraktaltremaster;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.crypto.MacSpi;

/**
 * Obligatorisk oppgave - Fraktaltre - Algoritmer og datastrukturer 6124-1 24H
 * FraktaltreTo er en JavaFX-applikasjon som tegner et fraktalt tre på et canvas.
 * Brukeren kan justere treets egenskaper via sliders (Vinkel, Lengde og Reduksjon) og knapper (Opprett og Reset Verdi).
 *
 * @author Kevin Matarewicz
 * @author Abdallah Ndikumana
 */
public class fraktaltreTo extends Application {

    private static final int VINDU_BREDDE = 800; // Bredde på Applikasjons-vinduet
    private static final int VINDU_HØYDE = 600; // Høyde på Applikasjons-vinduet
    private static final double FØRSTE_LENGDE = 150.0; // Default verdi på første lengde
    private static final double GREIN_REDUKSJON = 0.7; // Reduksjon av neste grein
    private static final double REKURSJON_STOPP = 2.0; // Minimum lengde på greiner før rekursjon stopper
    private static final double VINKEL_DEAULT = 30.0; // Standard vinkel på greiner
    private static final double TILFELDIGHET_DEFAULT = 0.2; // Standard tilfeldighetsverdi
    private static final double KANT_BREDDE = 2; // Bredde på kantlinjen rundt canvas
    private static final double STOPP_AVSTAND = 10; // Minimum avstand til kanten for å stoppe rekursjonen
    private static final int RUTE_STØRRELSE = 10; // Størrelse på rutene i grafikken
    private static final int MIN_DYPDE = 1; // Minste antall rekursive repetisjoner
    private static int MAX_DYPDE = 10; // Maksimalt antall rekursive repetisjoner (For å unngå stackoverflow)

    private Slider vinkelSlider; // Brukegrensesnitt Slider for Vinkel av greinene
    private Slider lengdeSlider; // Brukegrensesnitt Slider for Lengde av greinene
    private Slider reduksjonSlider; // Bruekrgrensesnitt Slider for Reduksjonsvariabel for greinene
    private Button randomButton; // Knapp for opprettelse av Treet
    private Button resetButton; // Knapp for å resette innstillingene til default
    private Label greinTellerLabel; // Label for telling av antall rekursjoner/greiner
    private int antallGreiner = 0; // Teller for antall rekursjoner/greiner
    private double randomness = TILFELDIGHET_DEFAULT; // Variabel for tilfeldighetsfaktor i kalkulasjon av verdier

    public static void main(String[] args) {
        launch(args);
    }
    /**
     * Startmetoden er inngangspunktet for JavaFX-applikasjonen og initialiserer brukergrensesnittet for fraktaltre-programmet.
     * Den setter opp hovedvinduet, kontrollene (sliders og knapper), og tegneområdet (canvas) der fraktaltreet vil bli tegnet.
     * Metoden kobler også sammen kontroller og tegnefunksjonen slik at endringer i kontrollene umiddelbart reflekteres i tegningen.
     *
     * @param primaryStage Hovedvinduet (Stage) for applikasjonen, gitt av JavaFX-rammeverket.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fraktaltre");

        Canvas canvas = new Canvas(VINDU_BREDDE, VINDU_HØYDE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Opprette sliders og knapper
        vinkelSlider = lagSliders("Vinkel på greiner:", 0, 90, VINKEL_DEAULT);
        lengdeSlider = lagSliders("Lengde på greiner:", 50, 300, FØRSTE_LENGDE);
        reduksjonSlider = lagSliders("Grein Reduksjon i %:", 0.1, 1.0, GREIN_REDUKSJON);

        // Oppretter tilfeldig fraktaltre utifra innstillinger til brukkeren
        randomButton = new Button("Opprett");
        randomButton.setOnAction(e -> {
            randomness = Math.random();
            treeStamme(gc); // Tegn treet på nytt med nye tilfeldighetsverdier
        });

        // Resetter verdiene på rekursjon tegning av tree og oppretter et nytt tre med default innstillinger
        resetButton = new Button("Reset");


        // En ChoiceBox for å kunne endre rekursjonsDybden.
        // Begrensent tall for å unngå stackoverflow.
        ChoiceBox<Integer> dybdeChoiceBox = new ChoiceBox<>();
        for (int i = MIN_DYPDE; i <= 25; i++) {
            dybdeChoiceBox.getItems().add(i);
        }
        MAX_DYPDE = tilfeldigDybde(); // Lager en tilfeldig dybdeverdi
        dybdeChoiceBox.setValue(MAX_DYPDE); // Setter dybdeverdien som blir brutk visuelt i ChoiceBox
        dybdeChoiceBox.setOnAction(e -> {
            MAX_DYPDE = dybdeChoiceBox.getValue();
            treeStamme(gc);
        });

        // Setter alle verdiene til opprinellige verdier
        resetButton.setOnAction(e -> {
            vinkelSlider.setValue(VINKEL_DEAULT);
            lengdeSlider.setValue(FØRSTE_LENGDE);
            reduksjonSlider.setValue(GREIN_REDUKSJON);
            randomness = TILFELDIGHET_DEFAULT;
            MAX_DYPDE = tilfeldigDybde();
            dybdeChoiceBox.setValue(MAX_DYPDE); // Henter Valuen og oppdaterer ChoiceBox tallet ved reset
            treeStamme(gc); // Tegn treet på nytt med standardverdier
        });

        // Teller antall ganger rekursjonen oppstår og definerer det som antall grein opprettet.
        greinTellerLabel = new Label("Antall greiner: 0");

        VBox brukergrensesnitt = new VBox(
                lagSliderLabel(vinkelSlider, "Vinkel på greiner:"),
                lagSliderLabel(lengdeSlider, "Lengde på greiner:"),
                lagSliderLabel(reduksjonSlider, "Skaleringsfaktor:"),
                randomButton,
                resetButton,
                dybdeChoiceBox,
                greinTellerLabel
        );
        brukergrensesnitt.setSpacing(10);
        brukergrensesnitt.setStyle("-fx-padding: 10;");

        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setLeft(brukergrensesnitt);

        Scene scene = new Scene(root, VINDU_BREDDE, VINDU_HØYDE);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Tegn treet første gang
        treeStamme(gc);

        // Oppdater treet når sliders endres.
        // Bruker en addListner observableValue for å sjekke verdi-endring på de ulike sliderene
        // Dersom det er en endringer opprettes treet på nytt med nye verdier
        vinkelSlider.valueProperty().addListener((observableValue, gammelVerdi, nyVerdi) -> {
            System.out.println("Vinkel slider endret: " + nyVerdi);
            treeStamme(gc);
        });
        lengdeSlider.valueProperty().addListener((obs, gammelVerdi, nyVerdi) -> {
            System.out.println("Lengde slider endret: " + nyVerdi);
            treeStamme(gc);
        });
        reduksjonSlider.valueProperty().addListener((obs, gammelVerdi, nyVerdi) -> {
            System.out.println("Reduksjon slider endret: " + nyVerdi);
            treeStamme(gc);
        });
    }

    /**
     * Lager en slider med spesifikasjonene som oppgis.
     *
     * @param label Teksten som vises over slideren.
     * @param min Minimumsverdien for slideren.
     * @param max Maksimumsverdien for slideren.
     * @param initial Startverdien for slideren.
     * @return En konfigurert slider.
     */
    private Slider lagSliders(String label, double min, double max, double initial) {
        Slider slider = new Slider(min, max, initial);
        slider.setPrefWidth(150);

        // Ticks/Markering for visuell brukergrensesnitt av sliderene
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit((max - min) / 10);
        slider.setMinorTickCount(1);
        slider.setBlockIncrement((max - min) / 10);
        return slider;
    }

    /**
     * Lager en VBox med en label og en slider.
     *
     * @param slider Slideren som skal vises.
     * @param label Teksten som vises over slideren.
     * @return En VBox med label og slider.
     */
    private VBox lagSliderLabel(Slider slider, String label) {
        Label sliderLabel = new Label(label);
        VBox sliderBox = new VBox(sliderLabel, slider);
        sliderBox.setSpacing(5);
        return sliderBox;
    }

    /**
     * Tegner en kant rundt canvasen.
     *
     * @param gc GraphicsContext som brukes til å tegne kantlinjen.
     */
    private void CanvassKant(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, VINDU_BREDDE, KANT_BREDDE); // Øverste kant
        gc.fillRect(0, 0, KANT_BREDDE, VINDU_HØYDE); // Venstre kant
        gc.fillRect(0, VINDU_HØYDE - KANT_BREDDE, VINDU_BREDDE, KANT_BREDDE); // Nederste kant
        gc.fillRect(VINDU_BREDDE - KANT_BREDDE, 0, KANT_BREDDE, VINDU_HØYDE); // Høyre kant
    }

    /**
     * Tegner et rutenett på canvasen.
     *
     * @param gc GraphicsContext som brukes til å tegne rutenettet.
     */
    private void ruteArk(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);

        // Horisontale linjer
        // For-loop som øker y-verdien med RUTE_STØRRELSE til den oppnår VINDU_HØYDE.
        // Tegner en horisontal linje fra x = 0 til x = VINDU_BREDDE ved høyden y.
        // Dette gjøres for hver verdi av y i løkken.
        for (int y = 0; y < VINDU_HØYDE; y += RUTE_STØRRELSE) {
            gc.strokeLine(0, y, VINDU_BREDDE, y);
        }

        // Vertikale linjer
        // For-loop som øker x-verdien med RUTE_STØRRELSE til den oppnår VINDU_BREDDE.
        // Tegner en vertikale linje fra y = 0 til y = VINDU_HØYDE ved bredden x.
        // Dette gjøres for hver verdi av x i løkken.
        for (int x = 0; x < VINDU_BREDDE; x += RUTE_STØRRELSE) {
            gc.strokeLine(x, 0, x, VINDU_HØYDE);
        }
    }

    /**
     * Tegner fraktaltreet på canvasen basert på verdiene fra sliderne.
     *
     * @param gc GraphicsContext som brukes til å tegne treet.
     */
    private void treeStamme(GraphicsContext gc) {
        double vinkel = vinkelSlider.getValue();
        double lengde = lengdeSlider.getValue();
        double greinReduksjon = reduksjonSlider.getValue();

        // Teller antall rekursjoner/greiner
        antallGreiner = 0;

        // Sletter innholdet i canvasen
        gc.clearRect(0, 0, VINDU_BREDDE, VINDU_HØYDE);

        // Tegner rutenett
        ruteArk(gc);

        // Tegner kant/border
        CanvassKant(gc);

        // Farge på greiner og linje tykkelse
        gc.setStroke(Color.BROWN);
        gc.setLineWidth(2);

        // Tegner treet, starter midten av canvasen på bunnen
        greinRekursjon(gc, VINDU_BREDDE / 2, VINDU_HØYDE - KANT_BREDDE, -90, lengde, vinkel, greinReduksjon, REKURSJON_STOPP, randomness, 0);

        // Oppdaterer Grein-label med antall greiner som er på canvasen
        greinTellerLabel.setText("Antall greiner: " + antallGreiner);
    }

    /**
     * Rekursiv metode som tegner en grein av treet. Metoden kaller seg selv for å
     * tegne mindre greiner fra enden av den gjeldende greinen.
     *
     * @param gc           GraphicsContext som brukes til å tegne greinen.
     * @param x            Start x-koordinat for greinen.
     * @param y            Start y-koordinat for greinen.
     * @param vinkel       Vinkelen som greinen strekker seg i.
     * @param lengde       Lengden på greinen.
     * @param greinVinkel  Vinkelen mellom hovedgreinen og sidegreinene.
     * @param skalering       Reduksjonsfaktoren for hver nye grein.
     * @param minsteStr    Minimum lengde for en grein før rekursjonen stopper.
     * @param randomness   Tilfeldighetsfaktor for variasjon i greinvinkler og
     *                     lengder.
     * @param depth        Gjeldende rekursive dybde.
     */
    private void greinRekursjon(GraphicsContext gc, double x, double y, double vinkel, double lengde, double greinVinkel, double skalering, double minsteStr, double randomness, int depth) {
        // Sjekker om maksimal rekursjonsdybde er nådd (Løsning mot stackoverflow).
        if (depth > MAX_DYPDE) {
            return;
        }

        // Sjekker om lengden på greinene er under minimumsgrensen (2 pixler).
        if (lengde < minsteStr) {
            System.out.println(String.format("Rekursjon stopper: Lengden på greinen (%.2f piksler) er mindre enn minimum lengde (%.2f piksler).", lengde, minsteStr));
            return;
        }

        // Beregner endepunktet for greinen basert på vinkelen og lengden ved bruk av trigonometri.
        // Konvertering av vinkelen fra grader til radianer:
        double xEnd = x + lengde * Math.cos(Math.toRadians(vinkel));
        double yEnd = y + lengde * Math.sin(Math.toRadians(vinkel));

        // Sjekker om greinen nærmer seg kanten/border av canvasen
        if (kantSjekk(x, y, xEnd, yEnd)) {
            System.out.println(String.format("Rekursjon stopper: Greinen nærmer seg kanten av canvasen: fra (%.2f, %.2f) til (%.2f, %.2f).", x, y, xEnd, yEnd));
            return;
        }

        // Tegner greinen
        gc.strokeLine(x, y, xEnd, yEnd);
        antallGreiner++;

        // Beregner nye verdier for neste skalering av greiner
        // Litt matematisk kalkulasjon for "Random" verdier

        // Eksempel: (Lengde) 50 * (Skalering) 0.5 = (nyLengde) 25
        double nyLengde = lengde * skalering;

        // Math.random genererer random tall mellom 0.0 og 1.0. (Inklusiv og eksklusiv).
        // Deretter (Math.random() * 2 - 1) Som gjør at tallet oppnår et område mellom -1.0 og 1.0.
        // Randomness er variabel for hvor tilfeldig variasjonene kan bli.
        // greinVinkel har default vinekl som skal varieres.
        // lengde har defautl lengde som skal varieres.
        double vinkelVariasjon = (Math.random() * 2 - 1) * randomness * greinVinkel;
        double lengdeVariasjon = (Math.random() * 2 - 1) * randomness * lengde;

        // Tegner de to nye greinene med justert vinkel og lengde utifra rekursjonen
        greinRekursjon(gc, xEnd, yEnd, vinkel + greinVinkel + vinkelVariasjon, nyLengde + lengdeVariasjon, greinVinkel, skalering, minsteStr, randomness, depth + 1);
        greinRekursjon(gc, xEnd, yEnd, vinkel - greinVinkel - vinkelVariasjon, nyLengde + lengdeVariasjon, greinVinkel, skalering, minsteStr, randomness, depth + 1);
    }

    /**
     * Sjekker om en linje er nær kanten av canvasen.
     *
     * @param x1 Start x-koordinat for linjen.
     * @param y1 Start y-koordinat for linjen.
     * @param x2 Slutt x-koordinat for linjen.
     * @param y2 Slutt y-koordinat for linjen.
     * @return True hvis linjen er nær kanten av canvasen, false ellers.
     */
    private boolean kantSjekk(double x1, double y1, double x2, double y2) {
        double kantTop = y1;
        double kantBunn = VINDU_HØYDE - y2;
        double kantVenstre = x1;
        double kantHoyre = VINDU_BREDDE - x2;
        boolean isNear = Math.min(Math.min(kantTop, kantBunn), Math.min(kantVenstre, kantHoyre)) < STOPP_AVSTAND;
        if (isNear) {
            System.out.println("Linje nær kant: fra (" + x1 + ", " + y1 + ") til (" + x2 + ", " + y2 + ")");
        }
        return isNear;
    }

    // Generer en tilfeldig verdi mellom 1 og 15
    private int tilfeldigDybde() {
        return (int) (Math.random() * (15 - 1 + 1)) + 1;
    }
}
