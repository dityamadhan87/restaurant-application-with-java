package Order;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;

import InterfaceRestaurant.ReadData;
import Menu.Menu;
import Pelanggan.Pelanggan;

public class Order implements ReadData {
    private Menu menu;
    private int kuantitas;
    private int subTotalBiayaMakanan;
    private Map<Pelanggan, Set<Order>> cart = new LinkedHashMap<>();
    private Set<Menu> daftarMenu = new LinkedHashSet<>();
    private Set<Pelanggan> daftarPelanggan = new LinkedHashSet<>();

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

    public Map<Pelanggan, Set<Order>> getCart() {
        return cart;
    }

    public int getSubTotalBiayaMakanan() {
        subTotalBiayaMakanan = kuantitas * Integer.parseInt(menu.getHargaMenu());
        return subTotalBiayaMakanan;
    }

    public void makeOrder(String input) throws Exception {
        loadCart();

        String[] bagianPesanan = input.split(" ", 2);
        String dataPesanan = bagianPesanan[1];
        String[] unitDataPesanan = dataPesanan.split(" ");
        String idPelanggan = unitDataPesanan[0];
        String idMenu = unitDataPesanan[1];
        String kuantitas = unitDataPesanan[2];

        Pelanggan pelangganOrder = new Pelanggan(idPelanggan);
        Menu menuOrder = new Menu(idMenu);

        if (!daftarPelanggan.contains(pelangganOrder)
                || !daftarMenu.contains(menuOrder)) {
            System.out.println("ADD_TO_CART FAILED: NON EXISTENT CUSTOMER OR MENU");
            return;
        }

        for (Menu menu : daftarMenu) {
            if (menu.getIdMenu().equals(idMenu)) {
                menuOrder = menu;
                break;
            }
        }

        Order order = new Order(menuOrder, Integer.parseInt(kuantitas));

        String filePath = "D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\Pesanan.txt";
        File file = new File(filePath);
        List<String> lines = new LinkedList<>();
        boolean isUpdated = false;

        if (file.length() > 0) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] columns = line.split("\\|");
                    String idPelangganFile = columns[0].trim();
                    String idMenuFile = columns[1].trim();
                    String kuantitasFile = columns[2].trim();
                    int totalKuantitas = Integer.parseInt(kuantitasFile) + Integer.parseInt(kuantitas);

                    if (idPelangganFile.equals(idPelanggan) && idMenuFile.equals(idMenu)) {
                        line = String.format("%-4s %-1c %-4s %c %d", idPelanggan, '|', idMenu, '|',
                                totalKuantitas);
                        isUpdated = true;
                        System.out.println("ADD_TO_CART SUCCESS: " + totalKuantitas + " " + menuOrder.getNamaMenu()
                                + " QUANTITY IS INCREMENTED");
                    }
                    lines.add(line);
                }
            }
        }

        if (!isUpdated) {
            lines.add(String.format("%-4s %-1c %-4s %c %s", idPelanggan, '|', idMenu, '|', kuantitas));
            System.out.println(
                    "ADD_TO_CART SUCCESS: " + kuantitas + " " + order.getMenu().getNamaMenu() + " IS ADDED");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        cart.computeIfAbsent(pelangganOrder, _ -> new LinkedHashSet<>()).add(order);
    }

    public void removeFromCart(String input) throws Exception {
        loadCart();

        String[] bagianPesanan = input.split(" ", 2);
        String dataPesanan = bagianPesanan[1];
        String[] unitDataPesanan = dataPesanan.split(" ");
        String idPelanggan = unitDataPesanan[0];
        String idMenu = unitDataPesanan[1];
        String kuantitas = unitDataPesanan[2];

        Pelanggan pelangganOrder = new Pelanggan(idPelanggan);
        Menu menuOrder = new Menu(idMenu);
        Order order = new Order(menuOrder, Integer.parseInt(kuantitas));

        if (!cart.containsKey(pelangganOrder)) {
            System.out.println("REMOVE FROM CART FAILED: CUSTOMERS HAVE NOT ORDERED");
            return;
        }
        if (!cart.get(pelangganOrder).contains(order)) {
            System.out.println("REMOVE FROM CART FAILED: CUSTOMERS HAVE NOT ORDERED THIS MENU");
            return;
        }

        for (Menu menu : daftarMenu) {
            if (menu.getIdMenu().equals(idMenu)) {
                menuOrder = menu;
                break;
            }
        }

        File file = new File("D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\Pesanan.txt");
        Scanner in = new Scanner(file);
        List<String> lines = new LinkedList<>();

        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isEmpty())
                continue;

            String[] columns = line.split("\\|");
            String idPelangganFile = columns[0].trim();
            String idMenuFile = columns[1].trim();
            String kuantitasFile = columns[2].trim();
            int totalKuantitas = Integer.parseInt(kuantitasFile) - Integer.parseInt(kuantitas);
            if (idPelangganFile.equals(idPelanggan) && idMenuFile.equals(idMenu)) {
                if (totalKuantitas <= 0) {
                    System.out.println("REMOVE FROM CART: " + menuOrder.getNamaMenu() + " IS REMOVED");
                    continue;
                }
                line = String.format("%-4s %-1c %-4s %c %d", idPelanggan, '|', idMenu, '|',
                        totalKuantitas);
                System.out.println("REMOVE_FROM_CART SUCCESS: " + menuOrder.getNamaMenu()
                        + " QUANTITY IS DECREMENTED");
            }
            lines.add(line);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        cart.clear();
        loadCart();
    }

    public void printDetails(String input) throws Exception {
        loadCart();

        String[] bagianPrint = input.split(" ", 2);
        String idPelanggan = bagianPrint[1];
        boolean cekIdPelanggan = true;
        for (Pelanggan pelanggan : cart.keySet()) {
            if (pelanggan.getIdPelanggan().equals(idPelanggan)) {
                if (cekIdPelanggan) {
                    System.out.println("Kode Pelanggan: " + pelanggan.getIdPelanggan());
                    System.out.println("Nama: " + pelanggan.getFullName());
                    System.out.printf("%3s | %-20s | %3s | %8s \n", "No", "Menu", "Qty", "Subtotal");
                    System.out.print("=".repeat(50) + "\n");
                    cekIdPelanggan = false;
                }
                int i = 1;
                int total = 0;
                for (Order order : cart.get(pelanggan)) {
                    System.out.printf("%c %-3d %-23s %-5d %d\n", ' ',
                            i++, order.getMenu().getNamaMenu(), order.getKuantitas(),
                            order.getSubTotalBiayaMakanan());
                    total += order.getSubTotalBiayaMakanan();
                }
                System.out.print("=".repeat(50) + "\n");
                System.out.printf("%-26s %-8c %d\n", "Total", ':', total);
                break;
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
            if (line.isEmpty())
                continue;

            String[] columns = line.split("\\|");

            String idPelanggan = columns[0].trim();
            String idMenu = columns[1].trim();
            String kuantitas = columns[2].trim();

            Pelanggan pelangganCart = new Pelanggan(idPelanggan);
            for (Pelanggan pelanggan : daftarPelanggan) {
                if (pelanggan.getIdPelanggan().equals(idPelanggan)) {
                    pelangganCart = pelanggan;
                    break;
                }
            }

            Menu menuCart = new Menu(idMenu);
            for (Menu menu : daftarMenu) {
                if (menu.getIdMenu().equals(idMenu)) {
                    menuCart = menu;
                    break;
                }
            }

            Order order = new Order(menuCart, Integer.parseInt(kuantitas));
            cart.computeIfAbsent(pelangganCart, _ -> new LinkedHashSet<>()).add(order);
        }
    }

    @Override
    public void loadMenu() throws Exception {
        File file = new File("D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\DaftarMenu.txt");
        Scanner in = new Scanner(file);

        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isEmpty())
                continue;

            String[] columns = line.split("\\|");

            String idMenu = columns[0].trim();
            String namaMenu = columns[1].trim();
            String hargaMenu = columns[2].trim();

            Menu menu = new Menu(idMenu, namaMenu, hargaMenu);

            if (daftarMenu.contains(menu))
                return;

            daftarMenu.add(menu);
        }
    }

    @Override
    public void loadPelanggan() throws Exception {
        File file = new File("D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\DaftarPelanggan.txt");
        Scanner in = new Scanner(file);

        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isEmpty())
                continue;

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

            Pelanggan pelanggan = new Pelanggan(idPelanggan, firstName, lastName, tanggalMenjadiMember, saldoAwal);

            if (daftarPelanggan.contains(pelanggan))
                return;

            daftarPelanggan.add(pelanggan);
        }
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
        Order other = (Order) obj;
        if (menu == null) {
            if (other.menu != null)
                return false;
        } else if (!menu.equals(other.menu))
            return false;
        return true;
    }
}