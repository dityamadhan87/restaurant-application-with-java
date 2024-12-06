package Order;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import InterfaceRestaurant.ReadData;
import Menu.Menu;
import Pelanggan.Pelanggan;

public class Order implements ReadData {
    private Menu menu;
    private int kuantitas;
    private int subTotalBiayaMakanan;
    private HashMap<Pelanggan, LinkedList<Order>> cart = new HashMap<>();
    private HashMap<String, Menu> daftarMenu = new HashMap<>();
    private HashMap<String, Pelanggan> daftarPelanggan = new HashMap<>();

    public Order(Menu menu, int kuantitas) {
        this.menu = menu;
        this.kuantitas = kuantitas;
    }

    public Order() {
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

    public void makeOrder(String input) throws Exception {
        loadCart();

        String filePath = "D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\Pesanan.txt";
        try (PrintWriter output = new PrintWriter(new FileWriter(filePath, true))) {
            if (input.startsWith("ADD_TO_CART")) {
                String[] bagianPesanan = input.split(" ", 2);
                String dataPesanan = bagianPesanan[1];
                String[] unitDataPesanan = dataPesanan.split(" ");
                String idPelanggan = unitDataPesanan[0];
                String idMenu = unitDataPesanan[1];
                String kuantitas = unitDataPesanan[2];

                if (!daftarPelanggan.containsKey(idPelanggan)
                        || !daftarMenu.containsKey(idMenu)) {
                    System.out.println("ADD_TO_CART FAILED: NON EXISTENT CUSTOMER OR MENU");
                    return;
                }
                Pelanggan pelanggan = daftarPelanggan.get(idPelanggan);
                Menu menu = daftarMenu.get(idMenu);
                Order order = new Order(menu, Integer.parseInt(kuantitas));

                cart.computeIfAbsent(pelanggan, _ -> new LinkedList<>()).add(order);

                output.printf("%-4s %-1c %-4s %c %s\n", idPelanggan, '|', idMenu, '|',
                        kuantitas);
                System.out.println(
                        "ADD_TO_CART SUCCESS: " + kuantitas + " " + order.getMenu().getNamaMenu() + " IS ADDED");
            }
        }
    }

    public void printDetails(String input) throws Exception {
        loadCart();

        if (input.startsWith("PRINT")) {
            String[] bagianPrint = input.split(" ", 2);
            String idPelanggan = bagianPrint[1];
            int i = 1;
            boolean cekIdPelanggan = true;
            int total = 0;
            for (Pelanggan pelanggan : cart.keySet()) {
                if (pelanggan.getIdPelanggan().equals(idPelanggan)) {
                    if (cekIdPelanggan) {
                        System.out.println("Kode Pelanggan: " + pelanggan.getIdPelanggan());
                        System.out.println("Nama: " + pelanggan.getFullName());
                        System.out.printf("%3s | %-20s | %3s | %8s \n", "No", "Menu", "Qty", "Subtotal");
                        System.out.print("=".repeat(50) + "\n");
                        cekIdPelanggan = false;
                    }
                    for (Order order : cart.get(pelanggan)) {
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
    @Override
    public void loadCart() throws Exception {
        if (daftarMenu.isEmpty()) 
            loadMenu();

        if (daftarPelanggan.isEmpty()) 
            loadPelanggan();

        cart.clear();
        File file = new File("D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\Pesanan.txt");
        Scanner in = new Scanner(file);

        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] columns = line.split("\\|");

            String idPelanggan = columns[0].trim();
            String idMenu = columns[1].trim();
            String kuantitas = columns[2].trim();

            Pelanggan pelanggan = daftarPelanggan.get(idPelanggan);
            Menu menu = daftarMenu.get(idMenu);
            Order order = new Order(menu, Integer.parseInt(kuantitas));

            cart.computeIfAbsent(pelanggan, _ -> new LinkedList<>()).add(order);
        }
    }

    @Override
    public void loadMenu() throws Exception {
        File file = new File("D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\DaftarMenu.txt");
        Scanner in = new Scanner(file);

        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] columns = line.split("\\|");

            String idMenu = columns[0].trim();
            String namaMenu = columns[1].trim();
            String hargaMenu = columns[2].trim();

            if (daftarMenu.containsKey(idMenu))
                return;

            daftarMenu.put(idMenu, new Menu(idMenu, namaMenu, hargaMenu));
        }
    }

    @Override
    public void loadPelanggan() throws Exception {
        File file = new File("D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\DaftarPelanggan.txt");
        Scanner in = new Scanner(file);

        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] columns = line.split("\\|");

            String idPelanggan = columns[0].trim();
            String namaPelanggan = columns[1].trim();
            String tanggalMenjadiMember = columns[2].trim();
            String saldoAwal = columns[3].trim();

            String firstName;
            String lastName;

            if (namaPelanggan.contains(" ")) {
                firstName = namaPelanggan.substring(0, namaPelanggan.indexOf(' '));
                lastName = namaPelanggan.substring(namaPelanggan.indexOf(' ') + 1);
            } else {
                firstName = namaPelanggan;
                lastName = "";
            }

            if (daftarPelanggan.containsKey(idPelanggan))
                return;

            daftarPelanggan.put(idPelanggan,
                    new Pelanggan(idPelanggan, firstName, lastName, tanggalMenjadiMember, Integer.parseInt(saldoAwal)));
        }
    }
}