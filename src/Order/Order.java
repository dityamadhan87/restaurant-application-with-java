package Order;

import InterfaceRestaurant.ReadData;
import Menu.Menu;
import Pelanggan.Pelanggan;

public class Order implements ReadData{
    private Menu menu;
    private int kuantitas;
    private int subTotalBiayaMakanan;
    private Pelanggan pelanggan = new Pelanggan();

    public Order(Menu menu, int kuantitas) {
        this.menu = menu;
        this.kuantitas = kuantitas;
    }

    public Order(Menu menu, int kuantitas, int subTotalBiayaMakanan) {
        this.menu = menu;
        this.kuantitas = kuantitas;
        this.subTotalBiayaMakanan = subTotalBiayaMakanan;
    }

    public Order(){}

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
        if(pelanggan.getCart().isEmpty()){
            loadCart();
        }
        if (input.startsWith("PRINT")) {
            String[] bagianPrint = input.split(" ", 2);
            String idPelanggan = bagianPrint[1];
            int i = 1;
            boolean cekIdPelanggan = true; 
            int total = 0;
            for(Pelanggan pelanggan : pelanggan.getCart().keySet()){
                if(pelanggan.getIdPelanggan().equals(idPelanggan)){
                    if(cekIdPelanggan){
                        System.out.println("Kode Pelanggan: " + pelanggan.getIdPelanggan());
                        System.out.println("Nama: " + pelanggan.getFullName());
                        System.out.printf("%3s | %-20s | %3s | %8s \n", "No", "Menu", "Qty", "Subtotal");
                        System.out.print("=".repeat(50) + "\n");
                        cekIdPelanggan = false;
                    }
                    if(pelanggan.getIdPelanggan().equals(idPelanggan)){
                        for(Order order : this.pelanggan.getCart().get(pelanggan)){
                            System.out.printf("%c %-3d %-23s %-5d %d\n", ' ',
                            i++, order.getMenu().getNamaMenu(), order.getKuantitas(),
                            order.getSubTotalBiayaMakanan());
                            total += order.getSubTotalBiayaMakanan();
                        }
                        System.out.print("=".repeat(50) + "\n");
                        System.out.printf("%-26s %-8c %d\n", "Total", ':', total);
                    }
                }
            }
        }
    }
    
    @Override
    public void loadCart() throws Exception {
        pelanggan.loadCart();
    }

    @Override
    public void loadMenu() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadMenu'");
    }

    @Override
    public void loadPelanggan() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadPelanggan'");
    }

}
