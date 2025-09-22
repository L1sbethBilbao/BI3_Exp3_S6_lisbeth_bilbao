package com.duoc.cinesmagenta.presentation;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.infrastructure.config.AppConfig;
import com.duoc.cinesmagenta.presentation.controller.MovieController;
import com.duoc.cinesmagenta.presentation.view.MainFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        AppConfig config = new AppConfig();
        MovieController controller = new MovieController(
                config.createMovie(), config.updateMovie(), config.deleteMovie(),
                config.getMovieById(), config.listMovies(), config.searchMovies()
        );
        SwingUtilities.invokeLater(() -> new MainFrame(controller).setVisible(true));
    }
}
