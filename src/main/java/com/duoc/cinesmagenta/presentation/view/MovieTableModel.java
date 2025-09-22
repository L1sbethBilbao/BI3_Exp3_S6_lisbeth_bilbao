package com.duoc.cinesmagenta.presentation.view;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.application.dto.MovieResponse;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieTableModel extends AbstractTableModel {
    private final String[] cols = {"ID", "Título", "Director", "Año", "Duración", "Género"};
    private final Class<?>[] types = {Integer.class, String.class, String.class, Integer.class, Integer.class, String.class};

    private final List<MovieResponse> data = new ArrayList<>();
    private final Set<Integer> dirtyRowIdx = new HashSet<>();

    public void setData(List<MovieResponse> list) {
        data.clear();
        if (list != null) data.addAll(list);
        dirtyRowIdx.clear();
        fireTableDataChanged();
    }

    public List<MovieResponse> getData() {
        return data;
    }

    public Set<Integer> dirtyRows() {
        return dirtyRowIdx;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int column) {
        return cols[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    } // no editar ID

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MovieResponse r = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> r.id;
            case 1 -> r.title;
            case 2 -> r.director;
            case 3 -> r.year;
            case 4 -> r.durationMinutes;
            case 5 -> r.genre;
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        MovieResponse r = data.get(rowIndex);
        switch (columnIndex) {
            case 1 -> r.title = aValue.toString();
            case 2 -> r.director = aValue.toString();
            case 3 -> r.year = toInt(aValue, r.year);
            case 4 -> r.durationMinutes = toInt(aValue, r.durationMinutes);
            case 5 -> r.genre = aValue.toString(); // debe ser uno del ENUM
            default -> {
            }
        }
        dirtyRowIdx.add(rowIndex);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    private int toInt(Object v, int fallback) {
        try {
            return Integer.parseInt(String.valueOf(v).trim().replace(".", ""));
        } catch (Exception e) {
            return fallback;
        }
    }

    public MovieResponse getAt(int row) {
        return data.get(row);
    }
}