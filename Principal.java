import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Principal extends Application {

	static Dir direct = Dir.esquerda;
	static boolean gameOver = false;
	static int velocidade = 5;
	static int corComida = 0;
	static int largura = 20;
	static int altura = 20;
	static int comidaX = 0;
	static int comidaY = 0;
	static int lados = 25;

	static List<Lados> cobra = new ArrayList<>();
	static Random rand = new Random();

	public enum Dir {
		esquerda, direita, cima, baixo
	}

	public static class Lados {
		int x;
		int y;

		public Lados(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}

	public void start(Stage primaryStage) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Controles do Game");
		alert.setHeaderText(null);
		alert.setContentText("Controle o jogo com o padrao WASD");

		alert.showAndWait();
		try {
			geraComida();

			VBox tela = new VBox();
			Canvas canv = new Canvas(largura * lados, altura * lados);
			GraphicsContext graphContext = canv.getGraphicsContext2D();
			tela.getChildren().add(canv);

			new AnimationTimer() {
				long ultTick = 0;

				public void handle(long sec) {
					if (ultTick == 0) {
						ultTick = sec;
						try {
							tick(graphContext);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						return;
					}

					if (sec - ultTick > 1000000000 / velocidade) {
						ultTick = sec;
						try {
							tick(graphContext);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			}.start();

			Scene cena = new Scene(tela, largura * lados, altura * lados);

			// Controle
			cena.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
				if (key.getCode() == KeyCode.W) {
					direct = Dir.cima;
				}
				if (key.getCode() == KeyCode.A) {
					direct = Dir.esquerda;
				}
				if (key.getCode() == KeyCode.S) {
					direct = Dir.baixo;
				}
				if (key.getCode() == KeyCode.D) {
					direct = Dir.direita;
				}

			});

			cobra.add(new Lados(largura / 2, altura / 2));
			cobra.add(new Lados(largura / 2, altura / 2));
			cobra.add(new Lados(largura / 2, altura / 2));
			primaryStage.setScene(cena);
			primaryStage.setTitle("Jogo da Cobrinha");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// tick
	public static void tick(GraphicsContext graphContext) throws InterruptedException {
		if (gameOver) {
			graphContext.setFill(Color.RED);
			graphContext.setFont(new Font("", 50));
			graphContext.fillText("FIM", 200, 250);
			// Thread.sleep(5000);
			// gameOver = false;
			return;
		}

		for (int i = cobra.size() - 1; i >= 1; i--) {
			cobra.get(i).x = cobra.get(i - 1).x;
			cobra.get(i).y = cobra.get(i - 1).y;
		}

		switch (direct) {
			case cima:
				cobra.get(0).y--;
				if (cobra.get(0).y < 0) {
					gameOver = true;
				}
				break;
			case baixo:
				cobra.get(0).y++;
				if (cobra.get(0).y > altura) {
					gameOver = true;
				}
				break;
			case esquerda:
				cobra.get(0).x--;
				if (cobra.get(0).x < 0) {
					gameOver = true;
				}
				break;
			case direita:
				cobra.get(0).x++;
				if (cobra.get(0).x > largura) {
					gameOver = true;
				}
				break;
		}

		// Come
		if (comidaX == cobra.get(0).x && comidaY == cobra.get(0).y) {
			cobra.add(new Lados(-1, -1));
			geraComida();
		}

		// Se matou
		for (int i = 1; i < cobra.size(); i++) {
			if (cobra.get(0).x == cobra.get(i).x && cobra.get(0).y == cobra.get(i).y) {
				gameOver = true;
			}
		}

		// background
		graphContext.setFill(Color.GRAY);
		graphContext.fillRect(0, 0, largura * lados, altura * lados);

		// Pontuacao
		graphContext.setFill(Color.WHITE);
		graphContext.setFont(new Font("", 30));
		graphContext.fillText("Pontos: " + (velocidade - 6), 10, 30);

		// random corComida
		Color corCobra = Color.WHITE;

		switch (corComida) {
			case 0:
				corCobra = Color.PURPLE;
				break;
			case 1:
				corCobra = Color.LIGHTBLUE;
				break;
			case 2:
				corCobra = Color.YELLOW;
				break;
			case 3:
				corCobra = Color.PINK;
				break;
			case 4:
				corCobra = Color.ORANGE;
				break;
		}
		graphContext.setFill(corCobra);
		graphContext.fillOval(comidaX * lados, comidaY * lados, lados, lados);

		// cobra
		for (Lados canv : cobra) {
			graphContext.setFill(Color.GREEN);
			graphContext.fillRect(canv.x * lados, canv.y * lados, lados, lados);
		}
	}

	// comida
	public static void geraComida() {
		start: while (true) {
			comidaX = rand.nextInt(largura);
			comidaY = rand.nextInt(altura);

			for (Lados canv : cobra) {
				if (canv.x == comidaX && canv.y == comidaY) {
					continue start;
				}
			}
			corComida = rand.nextInt(5);
			velocidade++;
			break;
		}
	}

	public static void main(String[] args) {
		launch(args);

	}
}
