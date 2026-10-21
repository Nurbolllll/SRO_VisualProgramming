package app;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddDishController {

    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField priceField;

    private MainController mainController;
    private Stage dialogStage;
    private boolean savedClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            String id = String.valueOf(System.currentTimeMillis());

            Dish newDish = new Dish(id, name, category, price, 15);
            mainController.addDishToMenu(newDish);

            savedClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage += "Введите наименование блюда!\n";
        }
        if (categoryField.getText() == null || categoryField.getText().trim().isEmpty()) {
            errorMessage += "Введите категорию!\n";
        }
        try {
            Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            errorMessage += "Цена должна быть числом!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка ввода");
            alert.setHeaderText("Пожалуйста, исправьте ошибки");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}