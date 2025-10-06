package engine.basictypes;

public enum Architecture {
    I(5), II(100), III(500), IV(1000);;
    private final int price;

    Architecture(int price) {
        this.price = price;
    }
    public int getPrice() {
        return price;
    }

}
