package InterfaceRestaurant;

public interface ReadData {
    public abstract void loadMenu() throws Exception;
    public abstract void loadCustomer() throws Exception;
    public abstract void loadCart() throws Exception;
    public abstract void loadPromo() throws Exception;
}