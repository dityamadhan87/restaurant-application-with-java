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
import java.util.ArrayList;
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
    private int subTotalBiayaMakanan;
    private int ongkosKirim = 15000;
    private double totalDiskon;
    private int totalHarga;
    private Map<Pelanggan, Set<ItemPesanan>> cart = new LinkedHashMap<>();
    private Set<Menu> daftarMenu = new LinkedHashSet<>();
    private Set<Pelanggan> daftarPelanggan = new LinkedHashSet<>();
    private Set<Promotion> daftarPromo = new LinkedHashSet<>();
    private Map<Pelanggan, Promotion> appliedPromo = new LinkedHashMap<>();

    public Order(Map<Pelanggan, Set<ItemPesanan>> cart) {
        this.cart = cart;
    }

    public Order() {
    }

    public int getSubTotalBiayaMakanan() {
        subTotalBiayaMakanan = 0;
        for(Set<ItemPesanan> pesananPelanggan : cart.values()){
            for(ItemPesanan item : pesananPelanggan){
                subTotalBiayaMakanan += Integer.parseInt(item.getMenu().getHargaMenu()) * item.getKuantitas();
            }
        }
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

    public Map<Pelanggan, Set<ItemPesanan>> getCart() {
        return cart;
    }

    public void readPromo(String input){
        String[] promoPelanggan = input.split(" ", 2);
        String idPelanggan = promoPelanggan[1];
        Pelanggan pelangganOrder = new Guest(idPelanggan);

        for (Pelanggan getPelanggan : daftarPelanggan) {
            if (getPelanggan.getIdPelanggan().equals(idPelanggan)) {
                pelangganOrder = getPelanggan;
                break;
            }
        }

        Set<ItemPesanan> pesananPelanggan = cart.get(pelangganOrder);
        Map<Pelanggan, Set<ItemPesanan>> cart = new LinkedHashMap<>();
        cart.computeIfAbsent(pelangganOrder, _ -> pesananPelanggan);
        Order order = new Order(cart);

        List<Promotion> sortedPromotions = new ArrayList<>(daftarPromo);

        sortedPromotions.sort(new PromotionComparator(order));

        System.out.println("=".repeat(66));
        System.out.println(" ".repeat(26) + "Daftar Promo");
        System.out.println("=".repeat(66));
        
        for (Promotion promo : sortedPromotions){
            if (!promo.isCustomerEligible(pelangganOrder)) {
                continue;
            }
            if (promo instanceof PercentOffPromo || promo instanceof CashbackPromo) {
                if (!promo.isMinimumPriceEligible(order)) {
                    continue;
                }
            }
            if (promo instanceof FreeShippingPromo) {
                if (!promo.isShippingFeeEligible(order)) {
                    continue;
                }
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate tanggalExpired = LocalDate.parse(promo.getEndDate(), formatter);
            LocalDate startDate = LocalDate.parse(promo.getStartDate(), formatter);
            if (startDate.isAfter(LocalDate.now())) {
                continue;
            }
    
            if (LocalDate.now().isAfter(tanggalExpired)) {
                continue;
            }
            System.out.printf("%-11s %-11s %-13s %-13s %-6s %s\n", promo.getTipePromo(), promo.getKodePromo(),
                    promo.getStartDate(), promo.getEndDate(), promo.getPersenPotongan(), promo.totalDiscount(order));
        }
    }

    public void applyPromo(String input){
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

        Set<ItemPesanan> pesananPelanggan = cart.get(pelangganOrder);
        Map<Pelanggan, Set<ItemPesanan>> cart = new LinkedHashMap<>();
        cart.computeIfAbsent(pelangganOrder, _ -> pesananPelanggan);
        Order order = new Order(cart);

        if (promo instanceof PercentOffPromo || promo instanceof CashbackPromo) {
            if (!promo.isMinimumPriceEligible(order)) {
                System.out.println("APPLY_PROMO FAILED: MINIMUM PRICE NOT MET");
                return;
            }
        }

        if (promo instanceof FreeShippingPromo) {
            if (!promo.isShippingFeeEligible(order)) {
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

        ItemPesanan item = new ItemPesanan(menuOrder, Integer.parseInt(kuantitas));

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
                        item = new ItemPesanan(menuOrder, totalKuantitas);
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
            cart.computeIfAbsent(pelangganOrder, _ -> new LinkedHashSet<>()).add(item);
            System.out.println(
                    "ADD_TO_CART SUCCESS: " + kuantitas + " " + menuOrder.getNamaMenu() + " IS ADDED");
        } else {
            if (cart.containsKey(pelangganOrder)) {
                Set<ItemPesanan> values = cart.get(pelangganOrder);
                values.remove(item);
                values.add(item);
                System.out.println("ADD_TO_CART SUCCESS: " + item.getKuantitas() + " " + menuOrder.getNamaMenu()
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
        ItemPesanan item = new ItemPesanan(menuOrder, Integer.parseInt(kuantitas));

        if (!cart.containsKey(pelangganOrder)) {
            System.out.println("REMOVE FROM CART FAILED: CUSTOMERS HAVE NOT ORDERED");
            return;
        }
        if (!cart.get(pelangganOrder).contains(item)) {
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
                    Set<ItemPesanan> values = cart.get(pelangganOrder);
                    values.remove(item);
                    continue;
                }
                line = String.format("%-6s %c %-7s %c %-5s %c %d", pelangganOrder.getTipePelanggan(), '|', idPelanggan,
                        '|', idMenu, '|',
                        totalKuantitas);
                Set<ItemPesanan> values = cart.get(pelangganOrder);
                for (ItemPesanan itemPesanan : values) {
                    if (itemPesanan.equals(item)) {
                        itemPesanan.setKuantitas(totalKuantitas);
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

    public void printDetails(String input){
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
                Set<ItemPesanan> p = cart.get(pelanggan);
                Map<Pelanggan, Set<ItemPesanan>> cart = new LinkedHashMap<>();
                cart.computeIfAbsent(pelanggan, _ -> p);
                Order order = new Order(cart);

                for (ItemPesanan item : p) {
                    System.out.printf("%c %-3d %-23s %-5d %d\n", ' ',
                            i++, item.getMenu().getNamaMenu(), item.getKuantitas(),
                            item.getSubHarga());
                }
                System.out.print("=".repeat(50) + "\n");
                System.out.printf("%-26s %-8c %d\n", "Total", ':', order.getSubTotalBiayaMakanan());

                if (appliedPromo.containsKey(pelanggan) && appliedPromo.get(pelanggan) instanceof PercentOffPromo) {
                    Promotion promoPelanggan = appliedPromo.get(pelanggan);
                    double totalDiskon = promoPelanggan.totalDiscount(order);
                    order.setTotalDiskon(totalDiskon);
                    System.out.printf("%-6s %-19s %-8c %.0f\n", "Promo:", promoPelanggan.getKodePromo(), ':',
                            totalDiskon);
                }

                System.out.printf("%-26s %-8c %d\n", "Ongkos kirim", ':', ongkosKirim);

                if (appliedPromo.containsKey(pelanggan) && appliedPromo.get(pelanggan) instanceof FreeShippingPromo) {
                    Promotion promoPelanggan = appliedPromo.get(pelanggan);
                    double totalDiskon = promoPelanggan.totalDiscount(order);
                    order.setTotalDiskon(totalDiskon);
                    System.out.printf("%-6s %-19s %-8c %.0f\n", "Promo:", promoPelanggan.getKodePromo(), ':',
                            totalDiskon);
                }

                System.out.print("=".repeat(50) + "\n");
                System.out.printf("%-26s %-8c %.0f\n", "Total", ':', order.getTotalHarga());

                if (appliedPromo.containsKey(pelanggan) && appliedPromo.get(pelanggan) instanceof CashbackPromo) {
                    Promotion promoPelanggan = appliedPromo.get(pelanggan);
                    double totalDiskon = promoPelanggan.totalDiscount(order);
                    order.setTotalDiskon(totalDiskon);
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

            ItemPesanan item = new ItemPesanan(menuCart, Integer.parseInt(kuantitas));
            cart.computeIfAbsent(pelangganCart, _ -> new LinkedHashSet<>()).add(item);
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