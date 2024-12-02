
// code added by sobia
class Icecream extends IcecreamParlor {
    private List<Flavor> flavors;  // Private field
    private List<Topping> toppings; // Private field

    // Constructor
    public Icecream(List<Flavor> flavors, List<Topping> toppings, OptionType optionType, Size size, double basePrice) {
        super("Custom Ice Cream", optionType, size, basePrice);
        this.flavors = flavors;
        this.toppings = toppings;
    }

    // Getter for flavors
    public List<Flavor> getFlavors() {
        return flavors;
    }

    // Setter for flavors
    public void setFlavors(List<Flavor> flavors) {
        this.flavors = flavors;
    }

    // Getter for toppings
    public List<Topping> getToppings() {
        return toppings;
    }

    // Setter for toppings
    public void setToppings(List<Topping> toppings) {
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


