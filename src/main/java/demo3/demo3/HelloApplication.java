package demo3.demo3;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelloApplication extends Application {

    private List<Movie> movieList;
    private Movie selectedMovie;
    private int guessesLeft;

    private TextField guessTextField;
    private VBox tilesContainer;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeGame();
        initializeGUI();
        startGame();
    }

    private void initializeGame() {
        movieList = readMoviesFromCSV();
    }

    private void initializeGUI() {
        guessTextField = new TextField();
        guessTextField.setPromptText("Enter your guess");

        Button guessButton = new Button("Guess");
        guessButton.setOnAction(e -> checkGuess());

        tilesContainer = new VBox(5);
        tilesContainer.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(guessTextField);
        root.setCenter(tilesContainer);
        root.setBottom(guessButton);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Movidle Game");
        primaryStage.show();
    }

    private void startGame() {
        guessesLeft = 5;
        selectedMovie = getRandomMovie();
        updateTiles();

        guessTextField.clear();
        guessTextField.requestFocus();
    }

    private void checkGuess() {
        String guess = guessTextField.getText().trim();

        if (guess.equalsIgnoreCase(selectedMovie.getName())) {
            showWinPopup();
            startGame();
        } else {
            guessesLeft--;
            updateTiles();

            if (guessesLeft == 0) {
                showLossPopup();
                startGame();
            }
        }

        guessTextField.clear();
        guessTextField.requestFocus();
    }

    private void updateTiles() {
        tilesContainer.getChildren().clear();
        List<Tile> tiles = generateTiles();

        for (Tile tile : tiles) {
            tilesContainer.getChildren().add(tile);
        }
    }

    private List<Tile> generateTiles() {
        List<Tile> tiles = new ArrayList<>();
        String[] movieName = selectedMovie.getName().split("");

        for (String letter : movieName) {
            Tile tile = new Tile(letter);
            tiles.add(tile);

            if (guessesLeft < 5) {
                if (guessesLeft > 0 && !letter.equalsIgnoreCase(guessTextField.getText())) {
                    tile.setRed();
                } else {
                    tile.setGreen();
                }
            }
        }

        return tiles;
    }

    private void showWinPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText(null);
        alert.setContentText("You found the movie! You win!");
        alert.showAndWait();
    }

    private void showLossPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("You ran out of guesses. Better luck next time!");
        alert.showAndWait();
    }

    private Movie getRandomMovie() {
        Random random = new Random();
        int index = random.nextInt(movieList.size());
        return movieList.get(index);
    }

    private List<Movie> readMoviesFromCSV() {
        List<Movie> movies = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("IMDB_Top250.csv"))) {
            String line;
            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0];
                String year = data[1];
                String genre = data[2];
                String origin = data[3];
                String director = data[4];
                String star = data[5];

                Movie movie = new Movie(name, year, genre, origin, director, star);
                movies.add(movie);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movies;
    }

    private static class Movie {
        private final String name;
        private final String year;
        private final String genre;
        private final String origin;
        private final String director;
        private final String star;

        public Movie(String name, String year, String genre, String origin, String director, String star) {
            this.name = name;
            this.year = year;
            this.genre = genre;
            this.origin = origin;
            this.director = director;
            this.star = star;
        }

        public String getName() {
            return name;
        }

        public String getYear() {
            return year;
        }

        public String getGenre() {
            return genre;
        }

        public String getOrigin() {
            return origin;
        }

        public String getDirector() {
            return director;
        }

        public String getStar() {
            return star;
        }
    }

    private class Tile extends Label {
        public Tile(String letter) {
            super(letter);
            setStyle("-fx-background-color: lightgray; -fx-padding: 5px;");
        }

        public void setGreen() {
            setStyle("-fx-background-color: green; -fx-padding: 5px;");
        }

        public void setRed() {
            setStyle("-fx-background-color: red; -fx-padding: 5px;");
        }
    }
}