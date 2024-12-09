package InterfaceRestaurant;

public interface ReadData {
    public abstract void loadMenu() throws Exception;
    public abstract void loadPelanggan() throws Exception;
    public abstract void loadCart() throws Exception;
    public abstract void loadPromo() throws Exception;
}