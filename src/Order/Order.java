package Order;

import Menu.Menu;
import Pelanggan.Pelanggan;

public class Order {
    private Pelanggan pelanggan = new Pelanggan();
    private Menu menu;
    private int subTotalBiayaMakanan;
    private int kuantitas;

    public Order(Pelanggan pelanggan, Menu menu, int kuantitas) {
        this.pelanggan = pelanggan;
        this.menu = menu;
        this.kuantitas = kuantitas;
    }

    public Order(Pelanggan pelanggan, Menu menu, int kuantitas, int subTotalBiayaMakanan) {
        this.pelanggan = pelanggan;
        this.menu = menu;
        this.kuantitas = kuantitas;
        this.subTotalBiayaMakanan = subTotalBiayaMakanan;
    }

    public Order(){}

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public Menu getMenu() {
        return menu;
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public int getSubTotalBiayaMakanan() {
        subTotalBiayaMakanan = kuantitas * Integer.parseInt(menu.getHargaMenu());
        return subTotalBiayaMakanan;
    }

    public void printDetails(String input) throws Exception{
        pelanggan.loadCart();
        if (input.startsWith("PRINT")) {
            String[] bagianPrint = input.split(" ", 2);
            String idPelanggan = bagianPrint[1];
            int i = 1;
            boolean cekIdPelanggan = true; 
            int total = 0;
            for (Order order : pelanggan.getCart()) {
                if (order.getPelanggan().getIdPelanggan().equals(idPelanggan)) {
                    if(cekIdPelanggan){
                        System.out.println("Kode Pelanggan: " + idPelanggan);
                        System.out.println("Nama: " + order.getPelanggan().getFullName());
                        System.out.printf("%3s | %-20s | %3s | %8s \n", "No", "Menu", "Qty", "Subtotal");
                        System.out.print("=".repeat(50) + "\n");
                        cekIdPelanggan = false;
                    }
                    System.out.printf("%c %-3d %-23s %-5d %d\n", ' ',
                    i++, order.getMenu().getNamaMenu(), order.getKuantitas(),
                    order.getSubTotalBiayaMakanan());
                    total += order.getSubTotalBiayaMakanan();
                }
            }
            System.out.print("=".repeat(50) + "\n");
            System.out.printf("%-26s %-8c %d\n", "Total", ':', total);
        }
    }
}
