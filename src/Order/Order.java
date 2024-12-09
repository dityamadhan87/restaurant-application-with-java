package Order;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;

import InterfaceRestaurant.ReadData;
import Menu.Menu;
import Pelanggan.*;
import Promotion.*;

public class Order implements ReadData {
    private Menu menu;
    private int kuantitas;
    private int subTotalBiayaMakanan;
    private int ongkosKirim = 15000;
    private double totalDiskon;
    private int totalHarga;
    private Map<Pelanggan, Set<Order>> cart = new LinkedHashMap<>();
    private Set<Menu> daftarMenu = new LinkedHashSet<>();
    private Set<Pelanggan> daftarPelanggan = new LinkedHashSet<>();
    private Set<Promotion> daftarPromo = new LinkedHashSet<>();
    private Map<Pelanggan, Promotion> appliedPromo = new LinkedHashMap<>();

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

    public void setKuantitas(int kuantitas) {
        this.kuantitas = kuantitas;
    }

    public int getSubTotalBiayaMakanan() {
        subTotalBiayaMakanan = kuantitas * Integer.parseInt(menu.getHargaMenu());
        return subTotalBiayaMakanan;
    }

    public void setSubTotalBiayaMakanan(int subTotalBiayaMakanan) {
        this.subTotalBiayaMakanan = subTotalBiayaMakanan;
    }

    public int getOngkosKirim() {
        return ongkosKirim;
    }

    public void setTotalDiskon(double totalDiskon) {
        this.totalDiskon = totalDiskon;
    }

    public double getTotalHarga() {
        totalHarga = (int) (subTotalBiayaMakanan + ongkosKirim - totalDiskon);
        return totalHarga;
    }

    public Map<Pelanggan, Set<Order>> getCart() {
        return cart;
    }

    public void applyPromo(String input) throws Exception {
        String[] promoPelanggan = input.split(" ", 2);
        String promoDiterapkan = promoPelanggan[1];
        String[] unitDataPromo = promoDiterapkan.split(" ");
        String idPelanggan = unitDataPromo[0];
        String kodePromo = unitDataPromo[1];

        Pelanggan pelangganOrder = new Guest(idPelanggan);
        Promotion promo = new PercentOffPromo(kodePromo);

        for (Pelanggan getPelanggan : daftarPelanggan) {
            if (getPelanggan.getIdPelanggan().equals(idPelanggan)) {
                pelangganOrder = getPelanggan;
                break;
            }
        }

        for (Promotion promotion : daftarPromo) {
            if (promotion.getKodePromo().equals(kodePromo)) {
                promo = promotion;
                break;
            }
        }

        if (!promo.isCustomerEligible(pelangganOrder)) {
            System.out.println("APPLY_PROMO FAILED: CUSTOMER IS NOT ELIGIBLE");
            return;
        }

        Set<Order> pesananPelanggan = cart.get(pelangganOrder);

        if (promo instanceof PercentOffPromo && promo instanceof CashbackPromo) {
            if (!promo.isMinimumPriceEligible(pesananPelanggan)) {
                System.out.println("APPLY_PROMO FAILED: MINIMUM PRICE NOT MET");
                return;
            }
        }

        if (promo instanceof FreeShippingPromo) {
            if (!promo.isShippingFeeEligible(pesananPelanggan)) {
                System.out.println("APPLY_PROMO FAILED: SHIPPING FEE NOT MET");
                return;
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate tanggalExpired = LocalDate.parse(promo.getEndDate(), formatter);
        LocalDate startDate = LocalDate.parse(promo.getStartDate(), formatter);

        if (startDate.isAfter(LocalDate.now())) {
            System.out.println("APPLY_PROMO FAILED: PROMO " + promo.getKodePromo() + " NOT YET STARTED");
            return;
        }

        if (LocalDate.now().isAfter(tanggalExpired)) {
            System.out.println("APPLY_PROMO FAILED: PROMO " + promo.getKodePromo() + " HAS EXPIRED");
            return;
        }

        appliedPromo.put(pelangganOrder, promo);
        System.out.println("APPLY_PROMO SUCCESS: " + promo.getKodePromo());
    }

    public void makeOrder(String input) throws Exception {
        String[] bagianPesanan = input.split(" ", 2);
        String dataPesanan = bagianPesanan[1];
        String[] unitDataPesanan = dataPesanan.split(" ");
        String idPelanggan = unitDataPesanan[0];
        String idMenu = unitDataPesanan[1];
        String kuantitas = unitDataPesanan[2];

        Pelanggan pelangganOrder = new Guest(idPelanggan);
        Menu menuOrder = new Menu(idMenu);

        if (!daftarPelanggan.contains(pelangganOrder)
                || !daftarMenu.contains(menuOrder)) {
            System.out.println("ADD_TO_CART FAILED: NON EXISTENT CUSTOMER OR MENU");
            return;
        }

        for (Pelanggan getPelanggan : daftarPelanggan) {
            if (getPelanggan.getIdPelanggan().equals(idPelanggan)) {
                pelangganOrder = getPelanggan;
                break;
            }
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
                    String idPelangganFile = columns[1].trim();
                    String idMenuFile = columns[2].trim();
                    String kuantitasFile = columns[3].trim();

                    if (idPelangganFile.equals(idPelanggan) && idMenuFile.equals(idMenu)) {
                        int totalKuantitas = Integer.parseInt(kuantitasFile) + Integer.parseInt(kuantitas);
                        order.setKuantitas(totalKuantitas);
                        line = String.format("%-6s %c %-7s %c %-5s %c %d", pelangganOrder.getTipePelanggan(), '|',
                                idPelanggan, '|', idMenu, '|',
                                totalKuantitas);
                        isUpdated = true;
                    }
                    lines.add(line);
                }
            }
        }

        if (!isUpdated) {
            lines.add(String.format("%-6s %c %-7s %c %-5s %c %s", pelangganOrder.getTipePelanggan(), '|', idPelanggan,
                    '|', idMenu, '|', kuantitas));
            cart.computeIfAbsent(pelangganOrder, _ -> new LinkedHashSet<>()).add(order);
            System.out.println(
                    "ADD_TO_CART SUCCESS: " + kuantitas + " " + order.getMenu().getNamaMenu() + " IS ADDED");
        } else {
            if (cart.containsKey(pelangganOrder)) {
                Set<Order> values = cart.get(pelangganOrder);
                values.remove(order);
                values.add(order);
                System.out.println("ADD_TO_CART SUCCESS: " + order.getKuantitas() + " " + menuOrder.getNamaMenu()
                        + " QUANTITY IS INCREMENTED");
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public void removeFromCart(String input) throws Exception {
        String[] bagianPesanan = input.split(" ", 2);
        String dataPesanan = bagianPesanan[1];
        String[] unitDataPesanan = dataPesanan.split(" ");
        String idPelanggan = unitDataPesanan[0];
        String idMenu = unitDataPesanan[1];
        String kuantitas = unitDataPesanan[2];

        Pelanggan pelangganOrder = new Guest(idPelanggan);
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

        for (Pelanggan getPelanggan : daftarPelanggan) {
            if (getPelanggan.getIdPelanggan().equals(idPelanggan)) {
                pelangganOrder = getPelanggan;
                break;
            }
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
            String idPelangganFile = columns[1].trim();
            String idMenuFile = columns[2].trim();
            String kuantitasFile = columns[3].trim();
            if (idPelangganFile.equals(idPelanggan) && idMenuFile.equals(idMenu)) {
                int totalKuantitas = Integer.parseInt(kuantitasFile) - Integer.parseInt(kuantitas);
                if (totalKuantitas <= 0) {
                    System.out.println("REMOVE FROM CART: " + menuOrder.getNamaMenu() + " IS REMOVED");
                    Set<Order> values = cart.get(pelangganOrder);
                    values.remove(order);
                    continue;
                }
                line = String.format("%-6s %c %-7s %c %-5s %c %d", pelangganOrder.getTipePelanggan(), '|', idPelanggan,
                        '|', idMenu, '|',
                        totalKuantitas);
                Set<Order> values = cart.get(pelangganOrder);
                for (Order order2 : values) {
                    if (order2.equals(order)) {
                        order2.setKuantitas(totalKuantitas);
                        break;
                    }
                }
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
    }

    public void printDetails(String input) throws Exception {
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
                setSubTotalBiayaMakanan(total);
                System.out.print("=".repeat(50) + "\n");
                System.out.printf("%-26s %-8c %d\n", "Total", ':', total);

                if (appliedPromo.containsKey(pelanggan) && appliedPromo.get(pelanggan) instanceof PercentOffPromo) {
                    Promotion promoPelanggan = appliedPromo.get(pelanggan);
                    double totalDiskon = promoPelanggan.totalDiscount(cart.get(pelanggan));
                    setTotalDiskon(totalDiskon);
                    System.out.printf("%-6s %-19s %-8c %.0f\n", "Promo:", promoPelanggan.getKodePromo(), ':',
                            totalDiskon);
                }

                System.out.printf("%-26s %-8c %d\n", "Ongkos kirim", ':', ongkosKirim);

                if (appliedPromo.containsKey(pelanggan) && appliedPromo.get(pelanggan) instanceof FreeShippingPromo) {
                    Promotion promoPelanggan = appliedPromo.get(pelanggan);
                    double totalDiskon = promoPelanggan.totalPotonganOngkosKirim(cart.get(pelanggan));
                    setTotalDiskon(totalDiskon);
                    System.out.printf("%-6s %-19s %-8c %.0f\n", "Promo:", promoPelanggan.getKodePromo(), ':',
                            totalDiskon);
                }

                System.out.print("=".repeat(50) + "\n");
                System.out.printf("%-26s %-8c %.0f\n", "Total", ':', getTotalHarga());

                if (appliedPromo.containsKey(pelanggan) && appliedPromo.get(pelanggan) instanceof CashbackPromo) {
                    Promotion promoPelanggan = appliedPromo.get(pelanggan);
                    double totalDiskon = promoPelanggan.totalCashback(cart.get(pelanggan));
                    setTotalDiskon(totalDiskon);
                    System.out.printf("%-6s %-19s %-8c %.0f\n", "Promo:", promoPelanggan.getKodePromo(), ':',
                            totalDiskon);
                }

                System.out.printf("%-26s %-8c %s\n", "Saldo", ':', pelanggan.getSaldoAwal());
                break;
            }
        }
    }

    @Override
    public void loadCart() throws Exception {
        File file = new File("D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\Pesanan.txt");
        Scanner in = new Scanner(file);

        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isEmpty())
                continue;

            String[] columns = line.split("\\|");

            String idPelanggan = columns[1].trim();
            String idMenu = columns[2].trim();
            String kuantitas = columns[3].trim();

            Pelanggan pelangganCart = new Guest(idPelanggan);
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

            String tipePelanggan = columns[0].trim();
            String idPelanggan = columns[1].trim();
            String namaPelanggan = columns[2].trim();
            String tanggalMenjadiMember = columns[3].trim();
            String saldoAwal = columns[4].trim();

            String firstName;
            String lastName;

            if (namaPelanggan.contains(" ")) {
                firstName = namaPelanggan.substring(0, namaPelanggan.indexOf(' '));
                lastName = namaPelanggan.substring(namaPelanggan.indexOf(' ') + 1);
            } else {
                firstName = namaPelanggan;
                lastName = "";
            }

            Pelanggan pelanggan;

            if (tipePelanggan.equals("GUEST"))
                pelanggan = new Guest(idPelanggan, firstName, lastName, saldoAwal);
            else
                pelanggan = new Member(idPelanggan, firstName, lastName, tanggalMenjadiMember, saldoAwal);

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

    @Override
    public void loadPromo() throws Exception {
        File file = new File("D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\DaftarPromo.txt");
        Scanner in = new Scanner(file);

        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isEmpty())
                continue;

            String[] columns = line.split("\\|");

            String jenisPromo = columns[0].trim();
            String kodePromo = columns[1].trim();
            String startDate = columns[2].trim();
            String endDate = columns[3].trim();
            String persenPotongan = columns[4].trim();
            String maksPotongan = columns[5].trim();
            String minPembelian = columns[6].trim();

            Promotion promo;

            if (jenisPromo.equals("DELIVERY"))
                promo = new FreeShippingPromo(kodePromo, startDate, endDate, persenPotongan, maksPotongan,
                        minPembelian);
            else if (jenisPromo.equals("DISCOUNT"))
                promo = new PercentOffPromo(kodePromo, startDate, endDate, persenPotongan, maksPotongan, minPembelian);
            else
                promo = new CashbackPromo(kodePromo, startDate, endDate, persenPotongan, maksPotongan, minPembelian);

            if (daftarPromo.contains(promo))
                return;

            daftarPromo.add(promo);
        }
    }
}