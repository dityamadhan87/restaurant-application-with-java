package Menu;

public class Menu {
    private String idMenu;
    private String namaMenu;
    private String hargaMenu;

    public Menu(String idMenu, String namaMenu, String hargaMenu){
        this.idMenu = idMenu;
        this.namaMenu = namaMenu;
        this.hargaMenu = hargaMenu;
    }

    public String getIdMenu() {
        return idMenu;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public String getHargaMenu() {
        return hargaMenu;
    }
}