/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import javafx.scene.control.Control;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

/**
 *
 * @author npauc
 */

public class ValidationUtils {

    public static GraphicValidationDecoration crearDecorador() {
        return new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                Control control = message.getTarget();
                if (message.getSeverity() == Severity.ERROR) {
                    control.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                } else if (message.getSeverity() == Severity.INFO) {
                    control.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
                } else {
                    control.setStyle(null);
                }
            }
        };
    }

    public static Validator obligatorio(String mensaje) {
        return Validator.createEmptyValidator(mensaje);
    }

    public static Validator <String> soloLetras(String mensajeVacio, String mensajeFormato) {
        return (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) {
                return ValidationResult.fromError(c, mensajeVacio);
            }
            if (!valor.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ `]+$")) {
                return ValidationResult.fromError(c, mensajeFormato);
            }
            return ValidationResult.fromInfo(c, "Correcto");
        };
    }

    public static Validator email(String mensaje) {
        return Validator.createRegexValidator(mensaje, "^(.+)@(.+)\\.(.+)$", Severity.ERROR);
    }

    public static Validator <String> kda(String mensajeVacio, String mensajeFormato) {
        return (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) {
                return ValidationResult.fromError(c, mensajeVacio);
            }
            if (!valor.matches("\\d+/\\d+/\\d+")) {
                return ValidationResult.fromError(c, mensajeFormato);
            }
            return ValidationResult.fromInfo(c, "Correcto");
        };
    }

    public static Validator <String> edad(int min, int max, String mensajeFueraRango, String mensajeNoNumero) {
        return (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) {
                return ValidationResult.fromError(c, "La edad es obligatoria");
            }
            try {
                int edad = Integer.parseInt(valor.trim());
                if (edad < min || edad > max) {
                    return ValidationResult.fromError(c, mensajeFueraRango);
                }
            } catch (NumberFormatException e) {
                return ValidationResult.fromError(c, mensajeNoNumero);
            }
            return ValidationResult.fromInfo(c, "Correcto");
        };
    }

    public static Validator <String> soloLetrasFiltro(String mensajeFormato) {
        return (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) {
                return ValidationResult.fromInfo(c, "Correcto");
            }
            if (!valor.matches("^[a-zA-ZÁÉÍÓÚáéíóúñÑ' ]+$")) {
                return ValidationResult.fromError(c, mensajeFormato);
            }
            return ValidationResult.fromInfo(c, "Correcto");
        };
    }

    public static Validator <String> kdaFiltro(String mensajeFormato) {
        return (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) {
                return ValidationResult.fromInfo(c, "Correcto");
            }
            if (!valor.matches("\\d+/\\d+/\\d+")) {
                return ValidationResult.fromError(c, mensajeFormato);
            }
            return ValidationResult.fromInfo(c, "Correcto");
        };
    }

    public static Validator <String> edadFiltro(int min, int max, String mensajeFueraRango, String mensajeNoNumero) {
        return (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) {
                return ValidationResult.fromInfo(c, "Correcto");
            }
            try {
                int edad = Integer.parseInt(valor.trim());
                if (edad < min || edad > max) {
                    return ValidationResult.fromError(c, mensajeFueraRango);
                }
            } catch (NumberFormatException e) {
                return ValidationResult.fromError(c, mensajeNoNumero);
            }
            return ValidationResult.fromInfo(c, "Correcto");
        };
    }

    public static Validator <String> emailFiltro(String mensaje) {
        return (Control c, String valor) -> {
            if (valor == null || valor.trim().isEmpty()) {
                return ValidationResult.fromInfo(c, "Correcto");
            }
            if (!valor.matches("^(.+)@(.+)\\.(.+)$")) {
                return ValidationResult.fromError(c, mensaje);
            }
            return ValidationResult.fromInfo(c, "Correcto");
        };
    }

    public static boolean todoValido(ValidationSupport... validadores) {
        for (ValidationSupport v : validadores) {
            if (!v.getValidationResult().getErrors().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static void revalidar(ValidationSupport... validadores) {
        for (ValidationSupport v : validadores) {
            v.revalidate();
        }
    }
}
