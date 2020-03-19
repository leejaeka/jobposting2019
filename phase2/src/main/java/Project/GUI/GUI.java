package Project.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * The root class of Project.GUI.Project.GUI. It inheritances methods that are used in different -Project.GUI.Project.GUI classes
 */
public abstract class GUI extends Observable {
    // Default width and height for scene
    final int width = 1000;
    final int height = 600;

    public GridPane createGridHelper() {
        GridPane grid = new GridPane();
        grid.setAlignment((Pos.CENTER));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        return grid;
    }

    void clearGridHelper(GridPane grid, List list) {
        List<Node> removeList = new ArrayList<>();
        for (Node n : grid.getChildren()) {
            if (list.contains(n)) {
                removeList.add(n);
            }
        }

        for (Node n : removeList) {
            grid.getChildren().remove(n);
        }
    }

    public void clearGridHelper(GridPane grid, String id) {
        List<Node> removeList = new ArrayList<>();
        for (Node n : grid.getChildren()) {
            if (n.getId() != null && n.getId().equals(id)) {
                removeList.add(n);
            }
        }

        for (Node n : removeList) {
            grid.getChildren().remove(n);
        }
    }
}
