/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import javafx.scene.control.Tooltip;

/**
 *
 * @author npauc
 */

public class TooltipUtils {
    public static Tooltip crear(String texto) {
        Tooltip tooltip = new Tooltip(texto);
        tooltip.setStyle(
            "-fx-background-color: #333;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            
            "-fx-padding: 8 12;"
        );
        return tooltip;
    }
}