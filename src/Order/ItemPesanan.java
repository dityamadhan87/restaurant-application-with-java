package Order;

import Menu.Menu;

public class ItemPesanan {
    private Menu menu;
    private int kuantitas;
    private int subHarga;

    public ItemPesanan(Menu menu, int kuantitas) {
        this.menu = menu;
        this.kuantitas = kuantitas;
    }

    public Menu getMenu() {
        return menu;
    }

    public int getSubHarga(){
        subHarga = Integer.parseInt(menu.getHargaMenu()) * kuantitas;
        return subHarga;
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public void setKuantitas(int kuantitas) {
        this.kuantitas = kuantitas;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((menu == null) ? 0 : menu.hashCode());
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
        ItemPesanan other = (ItemPesanan) obj;
        if (menu == null) {
            if (other.menu != null)
                return false;
        } else if (!menu.equals(other.menu))
            return false;
        return true;
    }
}