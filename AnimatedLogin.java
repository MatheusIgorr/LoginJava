package Fxx;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class AnimatedLogin extends Application {

    private final Random rng = new Random();
    private double sceneWidth = 980;
    private double sceneHeight = 620;

    @Override
    public void start(Stage stage) {
        // Raiz com gradiente de fundo
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364);");

        // Camada de partículas animadas
        Group particles = makeParticles(28, 9, 50, 12);
        particles.setMouseTransparent(true);

        // Card do formulário - agora dentro de um container para controlar melhor o
        // tamanho
        VBox card = makeLoginCard();

        // Container para controlar o tamanho máximo do card
        StackPane cardContainer = new StackPane(card);
        cardContainer.setAlignment(Pos.CENTER);
        cardContainer.setMaxSize(420, 600); // Tamanho máximo fixo

        // Entrada animada do card
        playCardIntro(card);

        // Layout responsivo
        root.getChildren().addAll(particles, cardContainer);

        // Cena responsiva
        Scene scene = new Scene(root, sceneWidth, sceneHeight);

        // Configuração responsiva
        setupResponsiveBehavior(root, cardContainer, card, particles);

        stage.setTitle("Login • Demo JavaFX");
        stage.setScene(scene);
        stage.setMinWidth(400);
        stage.setMinHeight(600);
        stage.show();

        // Animação suave do gradiente do background (hue shift)
        animateBackgroundHue(root);
    }

    private void setupResponsiveBehavior(StackPane root, StackPane cardContainer, VBox card, Group particles) {
        // Ajustar tamanho do card container com base na largura da raiz
        cardContainer.maxWidthProperty().bind(Bindings.createDoubleBinding(() -> {
            double width = root.getWidth();
            if (width < 500)
                return width - 40.0;
            return 420.0; // Tamanho fixo para telas maiores
        }, root.widthProperty()));

        cardContainer.prefWidthProperty().bind(cardContainer.maxWidthProperty());

        // Manter o card sempre centralizado e com tamanho adequado
        card.maxWidthProperty().bind(cardContainer.widthProperty());
        card.prefWidthProperty().bind(cardContainer.widthProperty());

        // Ajustar padding com base no tamanho
        card.paddingProperty().bind(Bindings.createObjectBinding(() -> {
            double width = cardContainer.getWidth();
            if (width < 400)
                return new Insets(15);
            if (width < 450)
                return new Insets(20);
            return new Insets(28);
        }, cardContainer.widthProperty()));

        // Ajustar espaçamento entre elementos com base no tamanho
        card.spacingProperty().bind(Bindings.createDoubleBinding(() -> {
            double width = cardContainer.getWidth();
            if (width < 400)
                return 10.0;
            if (width < 450)
                return 15.0;
            return 20.0;
        }, cardContainer.widthProperty()));

        // Ajustar tamanho da fonte com base no tamanho do card
        cardContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            double scaleFactor = Math.min(1.0, width / 420);
            double titleSize = Math.max(32, 42 * scaleFactor); // Aumentado o tamanho base
            double fontSize = Math.max(12, 14 * scaleFactor);
            double buttonSize = Math.max(14, 16 * scaleFactor);

            updateFontSizes(card, titleSize, fontSize, buttonSize);
        });

        // Reposicionar partículas quando a janela for redimensionada
        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal.doubleValue() == 0)
                return;
            for (Node node : particles.getChildren()) {
                if (node instanceof Circle) {
                    Circle c = (Circle) node;
                    double relativeX = c.getLayoutX() / oldVal.doubleValue();
                    c.setLayoutX(relativeX * newVal.doubleValue());
                }
            }
        });

        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal.doubleValue() == 0)
                return;
            for (Node node : particles.getChildren()) {
                if (node instanceof Circle) {
                    Circle c = (Circle) node;
                    double relativeY = c.getLayoutY() / oldVal.doubleValue();
                    c.setLayoutY(relativeY * newVal.doubleValue());
                }
            }
        });
    }

    private void updateFontSizes(VBox card, double titleSize, double fontSize, double buttonSize) {
        for (Node node : card.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getText().equals("BEM VINDO")) {
                    label.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, titleSize));
                } else {
                    label.setFont(Font.font("Arial", FontWeight.NORMAL, fontSize));
                }
            } else if (node instanceof Button) {
                ((Button) node).setFont(Font.font("Arial", FontWeight.BOLD, buttonSize));
            } else if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                for (Node child : hbox.getChildren()) {
                    if (child instanceof Label) {
                        ((Label) child).setFont(Font.font("Arial", FontWeight.NORMAL, fontSize));
                    } else if (child instanceof Hyperlink) {
                        ((Hyperlink) child).setFont(Font.font("Arial", FontWeight.NORMAL, fontSize));
                    } else if (child instanceof CheckBox) {
                        ((CheckBox) child).setFont(Font.font("Arial", FontWeight.NORMAL, fontSize));
                    }
                }
            }
        }
    }

    private Group makeParticles(int count, int minRadius, int maxRadius, int speedBase) {
        Group g = new Group();
        for (int i = 0; i < count; i++) {
            int r = rng.nextInt(maxRadius - minRadius + 1) + minRadius;
            Circle c = new Circle(r);
            c.setFill(Color.rgb(255, 255, 255, 0.06));
            c.setStroke(Color.rgb(255, 255, 255, 0.18));
            c.setStrokeWidth(0.6);

            // posição inicial aleatória dentro da área atual
            c.setLayoutX(rng.nextDouble() * sceneWidth);
            c.setLayoutY(rng.nextDouble() * sceneHeight);

            // movimento aleatório
            double dx = rng.nextDouble() * 220 + 80;
            double dy = rng.nextDouble() * 220 + 80;
            TranslateTransition tt = new TranslateTransition(Duration.seconds(speedBase + rng.nextDouble() * 10), c);
            tt.setFromX(-dx / 2);
            tt.setFromY(-dy / 2);
            tt.setToX(dx);
            tt.setToY(dy);
            tt.setAutoReverse(true);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.play();

            g.getChildren().add(c);
        }
        return g;
    }

    private VBox makeLoginCard() {
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(28));
        card.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.3);" +
                        "-fx-background-radius: 24;" +
                        "-fx-border-radius: 30;" +
                        "-fx-border-color: rgba(255,255,255,0.65);" +
                        "-fx-border-width: 2;");
        card.setEffect(new DropShadow(24, Color.rgb(0, 0, 0, 0.2)));

        // Adicionar clip para manter o border-radius durante animações
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(card.widthProperty());
        clip.heightProperty().bind(card.heightProperty());
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        card.setClip(clip);

        // Logo/Título - CENTRALIZADO E GRANDE
        VBox headerBox = new VBox();
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setSpacing(5);

        Label title = new Label("BEM VINDO");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 42));
        title.setTextFill(Color.web("#ffffffff"));
        title.setAlignment(Pos.CENTER);

        // Remover animação do título (comentando a chamada)
        // animateWelcomeTitle(title);

        Label subtitle = new Label("Entre para continuar");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.web("#0f2027"));
        subtitle.setOpacity(0.8);
        subtitle.setAlignment(Pos.CENTER);

        headerBox.getChildren().addAll(title, subtitle);

        TextField user = new TextField();
        user.setPromptText("Usuário ou e-mail");
        stylizeInput(user);

        PasswordField pass = new PasswordField();
        pass.setPromptText("Senha");
        stylizeInput(pass);

        HBox options = new HBox(10);
        options.setAlignment(Pos.CENTER_LEFT);
        CheckBox remember = new CheckBox("Lembrar");
        remember.setTextFill(Color.web("#0f2027"));
        Hyperlink forgot = new Hyperlink("Esqueci a senha");
        forgot.setBorder(Border.EMPTY);
        forgot.setPadding(new Insets(0, 0, 0, 8));
        forgot.setTextFill(Color.web("#0f2027"));
        options.getChildren().addAll(remember, new Region(), forgot);
        HBox.setHgrow(options.getChildren().get(1), Priority.ALWAYS);

        Button login = new Button("Entrar");
        login.setDefaultButton(true);
        login.setPrefHeight(46);
        login.setMaxWidth(Double.MAX_VALUE);
        login.setStyle(
                "-fx-background-radius: 12;" +
                        "-fx-font-size: 16;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-color: linear-gradient(to right, #00b09b, #96c93d);" +
                        "-fx-text-fill: white;");
        addButtonHoverAnimation(login);

        Label feedback = new Label("");
        feedback.setTextFill(Color.web("#c62828"));
        feedback.setOpacity(0);
        feedback.setWrapText(true);

        // Separador ou opção de login social
        HBox separator = new HBox();
        separator.setAlignment(Pos.CENTER);
        separator.setPrefHeight(20);

        Region line1 = new Region();
        line1.setStyle("-fx-background-color: rgba(0,0,0,0.1);");
        line1.setPrefHeight(1);
        HBox.setHgrow(line1, Priority.ALWAYS);

        Label orLabel = new Label("ou");
        orLabel.setTextFill(Color.web("#0f2027"));
        orLabel.setPadding(new Insets(0, 10, 0, 10));

        Region line2 = new Region();
        line2.setStyle("-fx-background-color: rgba(0,0,0,0.1);");
        line2.setPrefHeight(1);
        HBox.setHgrow(line2, Priority.ALWAYS);

        separator.getChildren().addAll(line1, orLabel, line2);

        // Botões de login social
        HBox socialLogin = new HBox(15);
        socialLogin.setAlignment(Pos.CENTER);

        Button googleBtn = createSocialButton("G", "linear-gradient(to right, #00b09b, #96c93d, #bbff00ff)");
        Button facebookBtn = createSocialButton("F", "linear-gradient(to right, #1c5eecff, #1f13cfff)");

        socialLogin.getChildren().addAll(googleBtn, facebookBtn);

        // Link de cadastro
        HBox signupBox = new HBox();
        signupBox.setAlignment(Pos.CENTER);
        Label signupLabel = new Label("Não tem uma conta?");
        Hyperlink signupLink = new Hyperlink("Cadastre-se");
        signupLink.setTextFill(Color.web("#0f2027"));
        signupBox.getChildren().addAll(signupLabel, signupLink);
        HBox.setMargin(signupLink, new Insets(0, 0, 0, 5));

        VBox.setVgrow(login, Priority.NEVER);
        card.getChildren().addAll(headerBox, user, pass, options, login,
                feedback, separator, socialLogin, signupBox);
        return card;
    }

    // Método de animação comentado para remover a animação do título
    /*
     * private void animateWelcomeTitle(Label title) {
     * // Animação de escala (crescer e diminuir)
     * ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(1500),
     * title);
     * scaleTransition.setFromX(1.0);
     * scaleTransition.setFromY(1.0);
     * scaleTransition.setToX(1.2);
     * scaleTransition.setToY(1.2);
     * scaleTransition.setAutoReverse(true);
     * scaleTransition.setCycleCount(Animation.INDEFINITE);
     * 
     * // Animação de translação (subir e descer)
     * TranslateTransition translateTransition = new
     * TranslateTransition(Duration.millis(1500), title);
     * translateTransition.setFromY(0);
     * translateTransition.setToY(-10);
     * translateTransition.setAutoReverse(true);
     * translateTransition.setCycleCount(Animation.INDEFINITE);
     * 
     * // Combinar as animações
     * ParallelTransition parallelTransition = new
     * ParallelTransition(scaleTransition, translateTransition);
     * parallelTransition.play();
     * }
     */

    private Button createSocialButton(String text, String color) {
        Button btn = new Button(text);
        btn.setMinSize(40, 40);
        btn.setPrefSize(40, 40);
        btn.setMaxSize(40, 40);
        btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;");

        btn.setOnMouseEntered(e -> {
            btn.setEffect(new Glow(0.4));
            btn.setScaleX(1.1);
            btn.setScaleY(1.1);
        });

        btn.setOnMouseExited(e -> {
            btn.setEffect(null);
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });

        return btn;
    }

    private void stylizeInput(TextField tf) {
        tf.setPrefHeight(44);
        tf.setMaxWidth(Double.MAX_VALUE);
        tf.setStyle(
                "-fx-background-color: rgba(255,255,255,0.7);" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: rgba(0,0,0,0.06);" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;" +
                        "-fx-padding: 0 15 0 15;" +
                        "-fx-font-size: 14;");
        tf.focusedProperty().addListener((obs, oldV, focused) -> {
            if (focused)
                animateFocus(tf, true);
            else
                animateFocus(tf, false);
        });
    }

    private void animateFocus(Region node, boolean focus) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        if (focus) {
            st.setToX(1.02);
            st.setToY(1.02);
            node.setStyle(node.getStyle().replaceAll("-fx-border-color: [^;]*;",
                    "-fx-border-color: " + toToRgba(Color.web("#42a5f5")) + ";"));
        } else {
            st.setToX(1.0);
            st.setToY(1.0);
            node.setStyle(node.getStyle().replaceAll("-fx-border-color: [^;]*;",
                    "-fx-border-color: " + toToRgba(Color.rgb(0, 0, 0, 0.06)) + ";"));
        }
        st.play();
    }

    private void addButtonHoverAnimation(Button b) {
        b.setOnMouseEntered(ev -> {
            scale(b, 1.0, 1.03);
            b.setEffect(new DropShadow(10, Color.web("#00b09b")));
        });
        b.setOnMouseExited(ev -> {
            scale(b, 1.03, 1.0);
            b.setEffect(null);
        });
    }

    private void scale(Node n, double from, double to) {
        ScaleTransition st = new ScaleTransition(Duration.millis(140), n);
        st.setFromX(from);
        st.setFromY(from);
        st.setToX(to);
        st.setToY(to);
        st.play();
    }

    private void playCardIntro(Node card) {
        card.setOpacity(0);
        card.setScaleX(0.9);
        card.setScaleY(0.9);
        card.setTranslateY(50);

        FadeTransition ft = new FadeTransition(Duration.millis(600), card);
        ft.setFromValue(0);
        ft.setToValue(1);

        ScaleTransition st = new ScaleTransition(Duration.millis(600), card);
        st.setFromX(0.9);
        st.setFromY(0.9);
        st.setToX(1);
        st.setToY(1);

        TranslateTransition tt = new TranslateTransition(Duration.millis(600), card);
        tt.setFromY(50);
        tt.setToY(0);

        ParallelTransition pt = new ParallelTransition(ft, st, tt);
        pt.setInterpolator(Interpolator.EASE_OUT);
        pt.play();
    }

    private void showError(Node card, Label feedback, String msg) {
        feedback.setText(msg);
        feedback.setTextFill(Color.web("#c62828"));
        fadeIn(feedback);
        shake(card);
    }

    private void showSuccess(VBox card, Label feedback, String msg) {
        feedback.setText(msg);
        feedback.setTextFill(Color.web("#2e7d32"));
        fadeIn(feedback);
        animateCardSuccess(card);
    }

    private void fadeIn(Node n) {
        FadeTransition ft = new FadeTransition(Duration.millis(220), n);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void shake(Node n) {
        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(n.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(60), new KeyValue(n.translateXProperty(), -8)),
                new KeyFrame(Duration.millis(120), new KeyValue(n.translateXProperty(), 8)),
                new KeyFrame(Duration.millis(180), new KeyValue(n.translateXProperty(), -6)),
                new KeyFrame(Duration.millis(240), new KeyValue(n.translateXProperty(), 6)),
                new KeyFrame(Duration.millis(300), new KeyValue(n.translateXProperty(), -3)),
                new KeyFrame(Duration.millis(360), new KeyValue(n.translateXProperty(), 0)));
        tl.play();
    }

    private void animateCardSuccess(VBox card) {
        Color from = Color.rgb(255, 255, 255, 0.95);
        Color to = Color.rgb(208, 242, 205, 0.95);
        Timeline tl = new Timeline();
        tl.getKeyFrames().add(new KeyFrame(Duration.ZERO, evt -> setCardBg(card, from)));
        tl.getKeyFrames().add(new KeyFrame(Duration.millis(400), evt -> setCardBg(card, to)));
        tl.getKeyFrames().add(new KeyFrame(Duration.millis(900), evt -> setCardBg(card, from)));
        tl.play();

        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(1.02);
        st.setToY(1.02);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void setCardBg(VBox card, Color c) {
        String style = card.getStyle().replaceAll("-fx-background-color: [^;]*;",
                "-fx-background-color: " + toToRgba(c) + ";");
        if (!style.contains("-fx-background-color:")) {
            style += "-fx-background-color: " + toToRgba(c) + ";";
        }
        card.setStyle(style);
    }

    private void animateBackgroundHue(Pane root) {
        final double[] hue = { 195 };
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(120), e -> {
            hue[0] += 0.3;
            if (hue[0] > 555)
                hue[0] = 195;
            Color c1 = Color.hsb(hue[0] % 360, 0.58, 0.16);
            Color c2 = Color.hsb((hue[0] + 20) % 360, 0.58, 0.28);
            Color c3 = Color.hsb((hue[0] + 40) % 360, 0.58, 0.34);
            root.setStyle(String.format(
                    "-fx-background-color: linear-gradient(to bottom right, %s, %s, %s);",
                    toHex(c1), toHex(c2), toHex(c3)));
        }));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();
    }

    private String toHex(Color c) {
        int r = (int) Math.round(c.getRed() * 255);
        int g = (int) Math.round(c.getGreen() * 255);
        int b = (int) Math.round(c.getBlue() * 255);
        return String.format("#%02x%02x%02x", r, g, b);
    }

    private String toToRgba(Color c) {
        int r = (int) Math.round(c.getRed() * 255);
        int g = (int) Math.round(c.getGreen() * 255);
        int b = (int) Math.round(c.getBlue() * 255);
        String a = String.format("%.3f", c.getOpacity());
        return "rgba(" + r + "," + g + "," + b + "," + a + ")";
    }

    public static void main(String[] args) {
        launch();
    }
}