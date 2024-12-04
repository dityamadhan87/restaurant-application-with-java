package Pelanggan;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

import BagianAdmin.Admin;
import Menu.Menu;
import Order.Order;

public class Pelanggan {
    private String idPelanggan;
    private String firstName;
    private String lastName;
    private String tanggalMenjadiMember;
    private int saldoAwal;
    private LinkedList<Order> cart = new LinkedList<>();
    private Admin admin = new Admin();

    public Pelanggan(String idPelanggan, String firstName, String lastName, String tanggalMenjadiMember,int saldoAwal) {
        this.idPelanggan = idPelanggan;
        this.firstName = firstName;
        this.lastName = lastName;
        this.tanggalMenjadiMember = tanggalMenjadiMember;
        this.saldoAwal = saldoAwal;
    }

    public Pelanggan(){
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }
    
    public String getTanggalMenjadiMember() {
        return tanggalMenjadiMember;
    }

    public int getSaldoAwal() {
        return saldoAwal;
    }

    public LinkedList<Order> getCart() {
        return cart;
    }

    public void loadCart() throws Exception{
        admin.loadMenu();
        admin.loadPelanggan();
        File file = new File("D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\Pesanan.txt");
        Scanner in = new Scanner(file);

        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] columns = line.split("\\|");

            String idPelanggan = columns[0].trim();
            String idMenu = columns[1].trim();
            String kuantitas = columns[2].trim();

            Pelanggan pelanggan = admin.getDaftarPelanggan().get(idPelanggan);
            Menu menu = admin.getDaftarMenu().get(idMenu);

            cart.add(new Order(pelanggan, menu, Integer.parseInt(kuantitas)));
        }
    }
    
    public void makeOrder(String input) throws Exception {
        admin.loadMenu();
        admin.loadPelanggan();
        String filePath = "D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\Pesanan.txt";
        try (PrintWriter output = new PrintWriter(new FileWriter(filePath, true))) {
            if (input.startsWith("ADD_TO_CART")) {
                String[] bagianPesanan = input.split(" ", 2);
                String dataPesanan = bagianPesanan[1];
                String[] unitDataPesanan = dataPesanan.split(" ");
                String idPelanggan = unitDataPesanan[0];
                String idMenu = unitDataPesanan[1];
                String kuantitas = unitDataPesanan[2];

                if (!admin.getDaftarPelanggan().containsKey(idPelanggan)
                        || !admin.getDaftarMenu().containsKey(idMenu)) {
                    System.out.println("ADD_TO_CART FAILED: NON EXISTENT CUSTOMER OR MENU");
                    return;
                }
                Pelanggan pelanggan = admin.getDaftarPelanggan().get(idPelanggan);
                Menu menu = admin.getDaftarMenu().get(idMenu);
                Order order = new Order(pelanggan, menu, Integer.parseInt(kuantitas));

                cart.add(order);
                output.printf("%-4s %-1c %-4s %c %-1s %c %s\n",order.getPelanggan().getIdPelanggan(),'|', order.getMenu().getIdMenu(), '|',
                        order.getKuantitas(), '|', order.getSubTotalBiayaMakanan());
            }
        }
    }
}