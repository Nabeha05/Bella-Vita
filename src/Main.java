import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

abstract class IcecreamParlor {
    protected String name;
    protected OptionType optionType;
    protected Size size;
    protected double basePrice;

    public IcecreamParlor(String name, OptionType optionType, Size size, double basePrice) {
        this.name = name;
        this.optionType = optionType;
        this.size = size;
        this.basePrice = basePrice;
    }

    public abstract double calculatePrice();
}

class Icecream extends IcecreamParlor {
    private List<Flavor> flavors;
    private List<Topping> toppings;

    public Icecream(List<Flavor> flavors, List<Topping> toppings, OptionType optionType, Size size, double basePrice) {
        super("Custom Ice Cream", optionType, size, basePrice);
        this.flavors = flavors;
        this.toppings = toppings;
    }

    @Override
    public double calculatePrice() {
        double sizeMultiplier = switch (size) {
            case LARGE -> 1.5;
            case MEDIUM -> 1.2;
            case SMALL -> 1.0;
        };
        double toppingPrice = toppings.stream().mapToDouble(Topping::getPrice).sum();
        return (basePrice * sizeMultiplier) + toppingPrice;
    }

    public String getDetails() {
        return size + " " + optionType + " with " + flavors + " and " + toppings + " - $" + String.format("%.2f", calculatePrice());
    }
}

class Flavor {
    private String name;

    public Flavor(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

class Topping {
    private String name;
    private double price;

    public Topping(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name;
    }
}

enum OptionType {
    CONE, CUP;
}

enum Size {
    LARGE, MEDIUM, SMALL;
}

class Order {
    private static final AtomicInteger orderCounter = new AtomicInteger(1);
    private final int orderNumber;
    private final String date;
    private List<Icecream> iceCreams;

    public Order() {
        this.orderNumber = orderCounter.getAndIncrement();
        this.date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date());
        this.iceCreams = new ArrayList<>();
    }

    public void addIceCream(Icecream iceCream) {
        iceCreams.add(iceCream);
    }

    public double getTotalCost() {
        return iceCreams.stream().mapToDouble(Icecream::calculatePrice).sum();
    }

    public String getReceiptDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Order No: ").append(orderNumber).append("\n");
        details.append("Date: ").append(date).append("\n\n");
        details.append(String.format("%-20s %-10s %-10s %-10s\n", "Description", "Qty", "Price", "Total"));
        details.append("----------------------------------------------------\n");

        for (Icecream iceCream : iceCreams) {
            details.append(String.format("%-20s %-10d $%-9.2f $%-9.2f\n",
                    iceCream.getDetails(), 1, iceCream.basePrice, iceCream.calculatePrice()));
        }

        details.append("\nGross Total: $").append(String.format("%.2f", getTotalCost()));
        double serviceCharge = 0.05 * getTotalCost(); // 5% Service Charge
        details.append("\nService Charges: $").append(String.format("%.2f", serviceCharge));
        details.append("\nNet Total: $").append(String.format("%.2f", getTotalCost() + serviceCharge));

        return details.toString();
    }
}

public class Main extends Application {
    private ComboBox<OptionType> optionComboBox;
    private ComboBox<Size> sizeComboBox;
    private ComboBox<String> flavorComboBox;
    private ComboBox<String> toppingComboBox;
    private List<Order> orderHistory;

    @Override
    public void start(Stage primaryStage) {
        orderHistory = new ArrayList<>();
        primaryStage.setTitle("Bella Vita Ice Cream Parlor");

        // Main layout with background image
        StackPane mainLayout = new StackPane();
        VBox contentLayout = new VBox(10);
        contentLayout.setPadding(new Insets(10));

        ImageView backgroundImageView = new ImageView(new Image("file:src/background.png"));
        backgroundImageView.setFitWidth(700);
        backgroundImageView.setFitHeight(700);
        backgroundImageView.setOpacity(0.3);

        VBox overlayLayout = new VBox(10);
        overlayLayout.setPadding(new Insets(10));
        overlayLayout.setStyle("-fx-background-color: rgba(210, 235, 240, 0.9); -fx-border-radius: 10; -fx-background-radius: 10;");

        Label titleLabel = new Label("Bella Vita Ice Cream Parlor");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.rgb(102, 195, 213)); // Light Blue
        titleLabel.setStyle("-fx-font-weight: bold;");

        optionComboBox = new ComboBox<>();
        optionComboBox.getItems().addAll(OptionType.CONE, OptionType.CUP);
        optionComboBox.setPromptText("Select Cone or Cup");
        VBox optionBox = createSection("Choose Option:", optionComboBox);

        sizeComboBox = new ComboBox<>();
        sizeComboBox.getItems().addAll(Size.LARGE, Size.MEDIUM, Size.SMALL);
        sizeComboBox.setPromptText("Select Size");
        VBox sizeBox = createSection("Choose Size:", sizeComboBox);

        flavorComboBox = new ComboBox<>();
        flavorComboBox.getItems().addAll("Vanilla", "Chocolate", "Strawberry", "Cookies & Cream", "Pistachio", "Mango", "Raspberry", "Blueberry");
        flavorComboBox.setPromptText("Select a Flavor");
        VBox flavorBox = createSection("Select Flavors:", flavorComboBox);

        toppingComboBox = new ComboBox<>();
        toppingComboBox.getItems().addAll("Sprinkles", "Chocolate Chips", "Chocolate Syrup", "Oreos", "Brownie Bites", "Caramel", "Peanut Butter", "Candy");
        toppingComboBox.setPromptText("Select a Topping");
        VBox toppingBox = createSection("Select Toppings:", toppingComboBox);

        Button addToOrderButton = new Button("Add to Order");
        addToOrderButton.setStyle("-fx-background-color: rgb(112, 197, 211); -fx-text-fill: white;"); // Soft Teal
        addToOrderButton.setOnAction(e -> confirmAddToOrder());

        Button placeOrderButton = new Button("Place Order");
        placeOrderButton.setStyle("-fx-background-color: rgb(101, 195, 213); -fx-text-fill: white;"); // Aquamarine
        placeOrderButton.setOnAction(e -> showReceipt());

        HBox buttonBox = new HBox(10, addToOrderButton, placeOrderButton);
        buttonBox.setPadding(new Insets(10));

        overlayLayout.getChildren().addAll(titleLabel, optionBox, sizeBox, flavorBox, toppingBox, buttonBox);
        contentLayout.getChildren().add(overlayLayout);
        mainLayout.getChildren().addAll(backgroundImageView, contentLayout);

        Scene scene = new Scene(mainLayout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createSection(String labelText, ComboBox<?> comboBox) {
        Label label = new Label(labelText);
        VBox box = new VBox(5, label, comboBox);
        box.setPadding(new Insets(7));
        box.setStyle("-fx-border-color: rgb(100, 194, 212); -fx-border-radius: 5; -fx-background-color: rgb(210, 235, 240);"); // Sky Blue and Pale Cyan
        return box;
    }

    private void confirmAddToOrder() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Add to Order");
        confirmation.setHeaderText("Do you want to add this item to your order?");
        confirmation.setContentText("Click OK to confirm or Cancel to return.");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                addIceCreamToOrder();
            }
        });
    }

    private void addIceCreamToOrder() {
        List<Flavor> selectedFlavors = new ArrayList<>();
        if (flavorComboBox.getValue() != null) {
            selectedFlavors.add(new Flavor(flavorComboBox.getValue()));
        }

        List<Topping> selectedToppings = new ArrayList<>();
        if (toppingComboBox.getValue() != null) {
            selectedToppings.add(new Topping(toppingComboBox.getValue(), 1.5)); // Assume $1.50 per topping
        }

        OptionType selectedOption = optionComboBox.getValue();
        Size selectedSize = sizeComboBox.getValue();

        if (selectedOption == null || selectedSize == null || selectedFlavors.isEmpty()) {
            showError("Please select at least one flavor, a size, and a cup/cone.");
            return;
        }

        Icecream iceCream = new Icecream(selectedFlavors, selectedToppings, selectedOption, selectedSize, 5.0); // Base price $5
        Order order = new Order();
        order.addIceCream(iceCream);
        orderHistory.add(order);

        showMessage("Success", "Ice Cream added to your order!");
    }

    private void showReceipt() {
        if (orderHistory.isEmpty()) {
            showError("No items in the order!");
            return;
        }

        StringBuilder receiptDetails = new StringBuilder("Bella Vita Ice Cream Parlor\n\n");
        receiptDetails.append("Order Details:\n\n");
        for (Order order : orderHistory) {
            receiptDetails.append(order.getReceiptDetails()).append("\n\n");
        }

        TextArea receiptTextArea = new TextArea(receiptDetails.toString());
        receiptTextArea.setEditable(false);

        Stage receiptStage = new Stage();
        receiptStage.setTitle("Receipt");
        receiptStage.setScene(new Scene(new StackPane(receiptTextArea), 400, 400));
        receiptStage.show();

        // Now write the receipt to a file
        appendReceiptToFile(receiptDetails.toString());
    }

    private void appendReceiptToFile(String receiptContent) {
        String fileName = "order_receipts.txt";
        try (FileWriter fileWriter = new FileWriter(fileName, true); // 'true' for appending to the file
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            // Add a separator between orders (date, order number, etc.)
            printWriter.println("----------------------------------------------------");
            printWriter.println("Receipt generated on: " + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date()));
            printWriter.println(receiptContent); // Write the receipt content
            printWriter.println("----------------------------------------------------\n");

        } catch (IOException e) {
            showError("Error saving receipt to file: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An Error Occurred");
        alert.setContentText(message);
        alert.show();
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
