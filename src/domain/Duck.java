package domain;

import javafx.beans.property.*;

public class Duck extends User {
    private final TipRata tip;
    private final double viteza;
    private final double rezistenta;
    // TO-DO card

    public Duck(long id, String username, String email, String password, TipRata tip, double viteza, double rezistenta) {
        super(id, username, email, password);
        this.tip =  tip;
        this.viteza = viteza;
        this.rezistenta = rezistenta;
    }

    public TipRata getTip() {
        return tip;
    }
    public double getViteza() {
        return viteza;
    }
    public double getRezistenta() {
        return rezistenta;
    }

    @Override
    public String toString() {
        return super.toString() + " TipRata: " + tip + ", viteza: " + viteza + ", rezistenta: " + rezistenta;
    }

    // Property getters for JavaFX
    public LongProperty idProperty() {
        return new SimpleLongProperty(getId());
    }

    public StringProperty usernameProperty() {
        return new SimpleStringProperty(getUsername());
    }

    public StringProperty emailProperty() {
        return new SimpleStringProperty(getEmail());
    }

    public ObjectProperty<TipRata> tipProperty() {
        return new SimpleObjectProperty<>(tip);
    }

    public DoubleProperty vitezaProperty() {
        return new SimpleDoubleProperty(viteza);
    }

    public DoubleProperty rezistentaProperty() {
        return new SimpleDoubleProperty(rezistenta);
    }
}
