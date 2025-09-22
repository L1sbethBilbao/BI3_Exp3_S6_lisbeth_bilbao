package com.duoc.cinesmagenta.domain.model;

/////Lisbeth_Bilbao_Semana6

import java.time.Year;
import java.util.Objects;

public class Movie {
    private Integer id;             // null si aún no persiste
    private final String title;
    private final String director;
    private final int year;
    private final int durationMinutes;
    private final Genre genre;

    public Movie(Integer id, String title, String director, int year, int durationMinutes, Genre genre) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Título obligatorio");
        if (director == null || director.isBlank()) throw new IllegalArgumentException("Director obligatorio");
        int current = Year.now().getValue();
        if (year < 1888 || year > current) throw new IllegalArgumentException("Año fuera de rango");
        if (durationMinutes <= 0 || durationMinutes > 999) throw new IllegalArgumentException("Duración inválida");
        if (genre == null) throw new IllegalArgumentException("Género obligatorio");

        this.id = id;
        this.title = title;
        this.director = director;
        this.year = year;
        this.durationMinutes = durationMinutes;
        this.genre = genre;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public String getDirector() { return director; }
    public int getYear() { return year; }
    public int getDurationMinutes() { return durationMinutes; }
    public Genre getGenre() { return genre; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie m)) return false;
        return id != null && Objects.equals(id, m.id);
    }
    @Override public int hashCode() { return 31; }

    @Override public String toString() {
        return "Movie{id=" + id + ", title='" + title + "', director='" + director +
                "', year=" + year + ", durationMinutes=" + durationMinutes + ", genre=" + genre + "}";
    }
}