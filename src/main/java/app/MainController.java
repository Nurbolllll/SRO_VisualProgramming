package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MainController {

    @FXML private TableView<Dish> menuTable;
    @FXML private TableColumn<Dish, String> nameCol;
    @FXML private TableColumn<Dish, String> categoryCol;
    @FXML private TableColumn<Dish, Double> priceCol;

    @FXML private TableView<OrderItem> orderTable;
    @FXML private TableColumn<OrderItem, String> orderNameCol;
    @FXML private TableColumn<OrderItem, Integer> orderQtyCol;
    @FXML private TableColumn<OrderItem, Double> orderPriceCol;

    @FXML private Label totalLabel;
    @FXML private Label statusBarLabel;
    @FXML private TextField searchField;

    private ObservableList<Dish> menuList = FXCollections.observableArrayList();
    private FilteredList<Dish> filteredMenu;

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        menuList.addAll(
                new Dish("1", "Пицца Маргарита", "Пицца", 2500, 10),
                new Dish("2", "Пицца Пепперони", "Пицца", 3100, 10),
                new Dish("3", "Бургер Классический", "Бургеры", 1800, 15),
                new Dish("4", "Бургер Чизбургер", "Бургеры", 2000, 12),
                new Dish("5", "Кола 0.5л", "Напитки", 500, 30),
                new Dish("6", "Фруктовый сок", "Напитки", 700, 20),
                new Dish("7", "Картофель Фри", "Закуски", 800, 25),
                new Dish("8", "Наггетсы куриные", "Закуски", 1100, 18),
                new Dish("9", "Стейк из говядины", "Горячее", 4500, 5),
                new Dish("10", "Куриное филе с рисом", "Горячее", 2800, 8)
        );

        filteredMenu = new FilteredList<>(menuList, b -> true);
        menuTable.setItems(filteredMenu);

        orderNameCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDish().getName()));
        orderQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        orderPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        statusBarLabel.setText("Статус: Загружено 10 блюд в меню.");
    }

    @FXML
    private void handleAddButton() {
        Dish selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean found = false;
            for (OrderItem item : orderTable.getItems()) {
                if (item.getDish().getId().equals(selected.getId())) {
                    item.setQuantity(item.getQuantity() + 1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                orderTable.getItems().add(new OrderItem(selected, 1));
            }
            orderTable.refresh();
            updateTotal();
            statusBarLabel.setText("Статус: Добавлено в заказ — " + selected.getName());
        } else {
            showAlert("Внимание", "Выберите блюдо из меню слева!", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleRemoveButton() {
        OrderItem selectedItem = orderTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            orderTable.getItems().remove(selectedItem);
            updateTotal();
            statusBarLabel.setText("Статус: Позиция удалена из заказа.");
        } else {
            showAlert("Внимание", "Выберите позицию в таблице заказа для удаления!", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleClearOrder() {
        orderTable.getItems().clear();
        updateTotal();
        statusBarLabel.setText("Статус: Заказ очищен.");
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();
        filteredMenu.setPredicate(dish -> {
            if (query.isEmpty()) return true;
            return dish.getName().toLowerCase().contains(query) ||
                    dish.getCategory().toLowerCase().contains(query);
        });
        statusBarLabel.setText("Статус: Выполнен поиск блюд.");
    }

    @FXML
    private void handleOpenAddDialog() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/add-dish-view.fxml"));
            javafx.scene.Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Добавить блюдо");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(menuTable.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(page));

            AddDishController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainController(this);

            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDishToMenu(Dish dish) {
        menuList.add(dish);
        statusBarLabel.setText("Статус: Добавлено новое блюдо — " + dish.getName());
    }

    @FXML
    private void handleCheckout() {
        if (orderTable.getItems().isEmpty()) {
            showAlert("Ошибка", "Ваш заказ пуст, оформление невозможно!", Alert.AlertType.ERROR);
        } else {
            showAlert("Успех", "Заказ успешно оформлен и передан на кухню!", Alert.AlertType.INFORMATION);
            handleClearOrder();
        }
    }

    @FXML
    private void handleAbout() {
        showAlert("О программе", "СРО №1: Система заказа блюд в ресторане (Restaurant Order System).\nРазработано по дисциплине «Визуальное программирование».", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private void updateTotal() {
        double sum = 0;
        for (OrderItem item : orderTable.getItems()) {
            sum += item.getTotalPrice();
        }
        totalLabel.setText(String.format("Итого: %.2f тг", sum));
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}