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

    public Menu(String idMenu){
        this.idMenu = idMenu;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idMenu == null) ? 0 : idMenu.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Menu other = (Menu) obj;
        if (idMenu == null) {
            if (other.idMenu != null)
                return false;
        } else if (!idMenu.equals(other.idMenu))
            return false;
        return true;
    }
}