package _Bibliotecapp.controlador;

import _Bibliotecapp.db.LibroDAO;
import _Bibliotecapp.modelo.Libro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainController {

    @FXML private TableView<Libro> tablaLibros;
    @FXML private TableColumn<Libro, String> colReferencia, colTitulo, colAutor, colIsbn, colPrestado, colUsuario, colTelefono, colFecha;
    @FXML private TableColumn<Libro, Integer> colVeces;
    @FXML private TextField txtReferencia, txtTitulo, txtAutor, txtIsbn, txtBusqueda;
    @FXML private Label lblTotalLibros, lblLibrosPrestados;

    private LibroDAO libroDAO;
    private ObservableList<Libro> listaMaestra = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Enlace con el modelo Libro.java
        colReferencia.setCellValueFactory(new PropertyValueFactory<>("referencia"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colPrestado.setCellValueFactory(new PropertyValueFactory<>("prestadoTexto"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuarioPrestamo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefonoPrestamo"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
        colVeces.setCellValueFactory(new PropertyValueFactory<>("vecesPrestado"));

        configurarBuscador();
        
        new Thread(() -> {
            try {
                libroDAO = new LibroDAO();
                handleCargar();
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void configurarBuscador() {
        FilteredList<Libro> filtro = new FilteredList<>(listaMaestra, p -> true);
        txtBusqueda.textProperty().addListener((obs, viejo, nuevo) -> {
            filtro.setPredicate(libro -> {
                if (nuevo == null || nuevo.isEmpty()) return true;
                String low = nuevo.toLowerCase();
                return libro.getTitulo().toLowerCase().contains(low) || 
                       libro.getAutor().toLowerCase().contains(low) ||
                       libro.getReferencia().toLowerCase().contains(low);
            });
        });
        SortedList<Libro> ordenada = new SortedList<>(filtro);
        ordenada.comparatorProperty().bind(tablaLibros.comparatorProperty());
        tablaLibros.setItems(ordenada);
    }

    @FXML
    private void handleInsertar() {
        // 1. Recogemos los datos de todos los cuadros de texto
        String ref = txtReferencia.getText();
        String tit = txtTitulo.getText();
        String aut = txtAutor.getText();
        String isb = txtIsbn.getText();

        // 2. Validación: Referencia y Título son obligatorios
        if (ref.isEmpty() || tit.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos obligatorios");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, introduce al menos la Referencia y el Título del libro.");
            alert.showAndWait();
            return;
        }

        // 3. Iniciamos el proceso en segundo plano para no congelar la ventana
        new Thread(() -> {
            try {
                // Creamos el objeto libro con el nuevo orden de campos
                Libro nuevo = new Libro(ref, tit, aut, isb);
                
                // Intentamos guardarlo en Atlas
                libroDAO.insertar(nuevo);
                
                // Si todo va bien, limpiamos la interfaz desde el hilo principal
                Platform.runLater(() -> {
                    txtReferencia.clear();
                    txtTitulo.clear();
                    txtAutor.clear();
                    txtIsbn.clear();
                    handleCargar(); // Refrescamos la tabla para ver el nuevo libro
                    System.out.println("Libro guardado correctamente: " + tit);
                });

            } catch (Exception e) {
                // SI FALLA (por ejemplo, por el cable de internet o timeout)
                Platform.runLater(() -> {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error de Conexión");
                    errorAlert.setHeaderText("No se pudo enviar el libro a la nube");
                    errorAlert.setContentText("Parece que hay un problema con tu conexión a internet o con MongoDB Atlas.\n\nDetalle técnico: " + e.getMessage());
                    errorAlert.showAndWait();
                });
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleCambiarEstado() {
        Libro sel = tablaLibros.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        if (!sel.isPrestado()) {
            // 1. Pedir Nombre
            TextInputDialog dialNombre = new TextInputDialog();
            dialNombre.setTitle("Préstamo");
            dialNombre.setHeaderText("¿Quién se lleva el libro?");
            dialNombre.setContentText("Nombre:");

            dialNombre.showAndWait().ifPresent(nombre -> {
                // 2. Pedir Teléfono
                TextInputDialog dialTel = new TextInputDialog();
                dialTel.setTitle("Contacto");
                dialTel.setHeaderText("Teléfono de " + nombre);
                dialTel.setContentText("Número:");

                dialTel.showAndWait().ifPresent(telef -> {
                    sel.setPrestado(true);
                    sel.setUsuarioPrestamo(nombre);
                    sel.setTelefonoPrestamo(telef);
                    sel.setFechaPrestamo(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    sel.setVecesPrestado(sel.getVecesPrestado() + 1);
                    actualizarBD(sel);
                });
            });
        } else {
            sel.setPrestado(false);
            sel.setUsuarioPrestamo("-");
            sel.setTelefonoPrestamo("-");
            sel.setFechaPrestamo("-");
            actualizarBD(sel);
        }
    }

    private void actualizarBD(Libro l) {
        new Thread(() -> {
            libroDAO.actualizar(l);
            Platform.runLater(this::handleCargar);
        }).start();
    }

    @FXML
    private void handleEliminar() {
        Libro sel = tablaLibros.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Borrar " + sel.getTitulo() + "?", ButtonType.YES, ButtonType.NO);
        if (alert.showAndWait().get() == ButtonType.YES) {
            new Thread(() -> {
                libroDAO.eliminar(sel.getId());
                Platform.runLater(this::handleCargar);
            }).start();
        }
    }

    @FXML
    private void handleImportarExcel() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        File f = fc.showOpenDialog(null);

        if (f != null) {
            new Thread(() -> {
                try (FileInputStream fis = new FileInputStream(f); Workbook wb = new XSSFWorkbook(fis)) {
                    Sheet s = wb.getSheetAt(0);
                    DataFormatter df = new DataFormatter();
                    for (Row r : s) {
                        if (r.getRowNum() == 0) continue;
                        String ref = df.formatCellValue(r.getCell(0));
                        String tit = df.formatCellValue(r.getCell(1));
                        String aut = df.formatCellValue(r.getCell(2));
                        String isb = df.formatCellValue(r.getCell(3));
                        libroDAO.insertar(new Libro(ref, tit, aut, isb));
                    }
                    Platform.runLater(this::handleCargar);
                } catch (Exception e) { e.printStackTrace(); }
            }).start();
        }
    }

    @FXML
    private void handleCargar() {
        if (libroDAO == null) return;
        List<Libro> lista = libroDAO.obtenerTodos();
        Platform.runLater(() -> {
            listaMaestra.setAll(lista);
            int p = listaMaestra.stream().filter(Libro::isPrestado).count();
            lblTotalLibros.setText("Libros: " + listaMaestra.size());
            lblLibrosPrestados.setText("Prestados: " + p);
        });
    }
}
