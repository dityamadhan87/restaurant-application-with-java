package Order;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.ArrayList;
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
    private Set<Order> kumpulanPesanan = new LinkedHashSet<>();
    private Set<Order> historiPesanan = new LinkedHashSet<>();
    private Set<Menu> daftarMenu = new LinkedHashSet<>();
    private Set<Pelanggan> daftarPelanggan = new LinkedHashSet<>();
    private Set<Promotion> daftarPromo = new LinkedHashSet<>();
    private Promotion appliedPromo;
    private Pelanggan pelanggan;
    private Set<ItemPesanan> item;
    private IdentitasCheckOut idenCheckOut;

    public Order(Pelanggan pelanggan, Set<ItemPesanan> item, Promotion appliedPromo) {
        this.pelanggan = pelanggan;
        this.item = item;
        this.appliedPromo = appliedPromo;
    }

    public Order(IdentitasCheckOut idenCheckOut, Pelanggan pelanggan, Set<ItemPesanan> item, Promotion appliedPromo) {
        this.idenCheckOut = idenCheckOut;
        this.pelanggan = pelanggan;
        this.item = item;
        this.appliedPromo = appliedPromo;
    }
    
    public Order() {
    }

    public IdentitasCheckOut getIdenCheckOut() {
        return idenCheckOut;
    }

    public void setIdenCheckOut(IdentitasCheckOut idenCheckOut) {
        this.idenCheckOut = idenCheckOut;
    }

    public int getSubTotalBiayaMakanan() {
        subTotalBiayaMakanan = 0;
        for (ItemPesanan itemPesanan : item) {
            subTotalBiayaMakanan += Integer.parseInt(itemPesanan.getMenu().getHargaMenu()) * itemPesanan.getKuantitas();
        }
        return subTotalBiayaMakanan;
    }

    public Promotion getAppliedPromo() {
        return appliedPromo;
    }

    public void setAppliedPromo(Promotion appliedPromo) {
        this.appliedPromo = appliedPromo;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public Set<ItemPesanan> getItem() {
        return item;
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

    public Set<Order> getKumpulanPesanan() {
        return kumpulanPesanan;
    }

    public void checkOut(String input) throws Exception{
        String[] checkOutPelanggan = input.split(" ", 2);
        String idPelanggan = checkOutPelanggan[1];
        Pelanggan pelangganOrder = new Guest(idPelanggan);

        for (Pelanggan getPelanggan : daftarPelanggan) {
            if (getPelanggan.equals(pelangganOrder)) {
                pelangganOrder = getPelanggan;
                break;
            } 
        }

        Order existingOrder = null;
        for(Order order : kumpulanPesanan){
            if (order.getPelanggan().equals(pelangganOrder)) {
                existingOrder = order;
                break;
            } 
        }

        if (existingOrder == null) {
            System.out.println("CHECK_OUT FAILED: CUSTOMER HAVE NOT ORDERED");
            return;
        }

        if (Integer.parseInt(pelangganOrder.getSaldoAwal()) < existingOrder.getTotalHarga()) {
            System.out.println("CHECK_OUT FAILED: " + idPelanggan + " " + pelangganOrder.getFullName() + " INSUFFICIENT BALANCE");
            return;
        }

        String filePath = "D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\historiPesanan.txt";
        File file = new File(filePath);
        List<String> lines = new LinkedList<>();
        boolean isUpdated = false;

        int noPesanan = 0;

        if (file.length() > 0) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] columns = line.split("\\|");
                    String nomorPesanan = columns[0].trim();
                    noPesanan = Integer.parseInt(nomorPesanan);
                }
            }
        }

        System.out.println(noPesanan);
    }

    public void readPromo(String input){
        String[] promoPelanggan = input.split(" ", 2);
        String idPelanggan = promoPelanggan[1];
        Pelanggan pelangganOrder = new Guest(idPelanggan);

        for (Pelanggan getPelanggan : daftarPelanggan) {
            if (getPelanggan.equals(pelangganOrder)) {
                pelangganOrder = getPelanggan;
                break;
            } 
        }

        Order existingOrder = null;
        for(Order order : kumpulanPesanan){
            if (order.getPelanggan().equals(pelangganOrder)) {
                existingOrder = order;
                break;
            }
        }

        if (existingOrder == null) {
            System.out.println("READ_PROMO FAILED: CUSTOMERS HAVE NOT ORDERED");
            return;
        }

        List<Promotion> sortedPromotions = new ArrayList<>(daftarPromo);

        sortedPromotions.sort(new PromotionComparator(existingOrder));

        System.out.println("=".repeat(66));
        System.out.println(" ".repeat(25) + "Eligible Promo");
        System.out.println("=".repeat(66));
        
        List<Promotion> notEligiblePromo = new ArrayList<>();

        for (Promotion promo : sortedPromotions){
            if (!promo.isCustomerEligible(pelangganOrder)) {
                notEligiblePromo.add(promo);
                continue;
            }
            if (promo instanceof PercentOffPromo || promo instanceof CashbackPromo) {
                if (!promo.isMinimumPriceEligible(existingOrder)) {
                    notEligiblePromo.add(promo);
                    continue;
                }
            }
            if (promo instanceof FreeShippingPromo) {
                if (!promo.isShippingFeeEligible(existingOrder)) {
                    notEligiblePromo.add(promo);
                    continue;
                }
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate tanggalExpired = LocalDate.parse(promo.getEndDate(), formatter);
            LocalDate startDate = LocalDate.parse(promo.getStartDate(), formatter);
            if (startDate.isAfter(LocalDate.now())) {
                notEligiblePromo.add(promo);
                continue;
            }
    
            if (LocalDate.now().isAfter(tanggalExpired)) {
                notEligiblePromo.add(promo);
                continue;
            }
            System.out.printf("%-11s %-11s %-13s %-13s %-6s %s\n", promo.getTipePromo(), promo.getKodePromo(),
                    promo.getStartDate(), promo.getEndDate(), promo.getPersenPotongan(), promo.totalDiscount(existingOrder));
        }
        System.out.println("=".repeat(66));
        System.out.println(" ".repeat(24) + "Not Eligible Promo");
        System.out.println("=".repeat(66));

        for(Promotion promo : notEligiblePromo){
            System.out.printf("%-11s %-11s %-13s %-13s %-6s %s\n", promo.getTipePromo(), promo.getKodePromo(),
                    promo.getStartDate(), promo.getEndDate(), promo.getPersenPotongan(), promo.totalDiscount(existingOrder));
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

        Order existingOrder = null;
        for(Order order : kumpulanPesanan){
            if (order.getPelanggan().equals(pelangganOrder)) {
                existingOrder = order;
                break;
            }
        }

        if (promo instanceof PercentOffPromo || promo instanceof CashbackPromo) {
            if (!promo.isMinimumPriceEligible(existingOrder)) {
                System.out.println("APPLY_PROMO FAILED: MINIMUM PRICE NOT MET");
                return;
            }
        }

        if (promo instanceof FreeShippingPromo) {
            if (!promo.isShippingFeeEligible(existingOrder)) {
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

        existingOrder.setAppliedPromo(promo);
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

        Order existingOrder = null;
        for(Order order : kumpulanPesanan){
            if (order.getPelanggan().equals(pelangganOrder)) {
                existingOrder = order;
                break;
            }
        }

        if(existingOrder == null){
            existingOrder = new Order(pelangganOrder, new LinkedHashSet<>(), null);
        }

        if (!isUpdated) {
            lines.add(String.format("%-6s %c %-7s %c %-5s %c %s", pelangganOrder.getTipePelanggan(), '|', idPelanggan,
                    '|', idMenu, '|', kuantitas));
            existingOrder.getItem().add(item);
            kumpulanPesanan.add(existingOrder);
            System.out.println(
                    "ADD_TO_CART SUCCESS: " + kuantitas + " " + menuOrder.getNamaMenu() + " IS ADDED");
        } else {
            if (kumpulanPesanan.contains(existingOrder)) {
                existingOrder.getItem().remove(item);
                existingOrder.getItem().add(item);
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

        Order existingOrder = null;
        for(Order order : kumpulanPesanan){
            if (order.getPelanggan().equals(pelangganOrder)) {
                existingOrder = order;
                break;
            }
        }

        if(existingOrder == null){
            existingOrder = new Order(pelangganOrder, new LinkedHashSet<>(), null);
        }

        if (!kumpulanPesanan.contains(existingOrder)) {
            System.out.println("REMOVE FROM CART FAILED: CUSTOMERS HAVE NOT ORDERED");
            return;
        }
        if (!existingOrder.getItem().contains(item)) {
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
                    existingOrder.getItem().remove(item);
                    continue;
                }
                line = String.format("%-6s %c %-7s %c %-5s %c %d", pelangganOrder.getTipePelanggan(), '|', idPelanggan,
                        '|', idMenu, '|',
                        totalKuantitas);
                for (ItemPesanan itemPesanan : existingOrder.getItem()) {
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
        Pelanggan pelangganOrder = new Guest(idPelanggan);
        for (Pelanggan getPelanggan : daftarPelanggan) {
            if (getPelanggan.getIdPelanggan().equals(idPelanggan)) {
                pelangganOrder = getPelanggan;
                break;
            }
        }
        for (Order order : kumpulanPesanan) {
            if (order.getPelanggan().equals(pelangganOrder)) {
                if (cekIdPelanggan) {
                    System.out.println("Kode Pelanggan: " + pelangganOrder.getIdPelanggan());
                    System.out.println("Nama: " + pelangganOrder.getFullName());
                    System.out.printf("%3s | %-20s | %3s | %8s \n", "No", "Menu", "Qty", "Subtotal");
                    System.out.print("=".repeat(50) + "\n");
                    cekIdPelanggan = false;
                }
                int i = 1;
                for (ItemPesanan item : order.getItem()) {
                    System.out.printf("%c %-3d %-23s %-5d %d\n", ' ',
                            i++, item.getMenu().getNamaMenu(), item.getKuantitas(),
                            item.getSubHarga());
                }
                System.out.print("=".repeat(50) + "\n");
                System.out.printf("%-26s %-8c %d\n", "Total", ':', order.getSubTotalBiayaMakanan());

                if (order.getAppliedPromo() != null && order.getAppliedPromo() instanceof PercentOffPromo) {
                    Promotion promoPelanggan = order.getAppliedPromo();
                    double totalDiskon = promoPelanggan.totalDiscount(order);
                    order.setTotalDiskon(totalDiskon);
                    System.out.printf("%-6s %-19s %-8c %.0f\n", "Promo:", promoPelanggan.getKodePromo(), ':',
                            totalDiskon);
                }

                System.out.printf("%-26s %-8c %d\n", "Ongkos kirim", ':', ongkosKirim);

                if (order.getAppliedPromo() != null && order.getAppliedPromo() instanceof FreeShippingPromo) {
                    Promotion promoPelanggan = order.getAppliedPromo();
                    double totalDiskon = promoPelanggan.totalDiscount(order);
                    order.setTotalDiskon(totalDiskon);
                    System.out.printf("%-6s %-19s %-8c %.0f\n", "Promo:", promoPelanggan.getKodePromo(), ':',
                            totalDiskon);
                }

                System.out.print("=".repeat(50) + "\n");
                System.out.printf("%-26s %-8c %.0f\n", "Total", ':', order.getTotalHarga());

                if (order.getAppliedPromo() != null && order.getAppliedPromo() instanceof CashbackPromo) {
                    Promotion promoPelanggan = order.getAppliedPromo();
                    double totalDiskon = promoPelanggan.totalDiscount(order);
                    order.setTotalDiskon(totalDiskon);
                    System.out.printf("%-6s %-19s %-8c %.0f\n", "Promo:", promoPelanggan.getKodePromo(), ':',
                            totalDiskon);
                }

                System.out.printf("%-26s %-8c %s\n", "Saldo", ':', pelangganOrder.getSaldoAwal());
                break;
            }
        }
    }

    @Override
    public void loadCart() throws Exception {
        File file = new File("D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\Pesanan.txt");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty())
                    continue;

                String[] columns = line.split("\\|");

                String idPelanggan = columns[1].trim();
                String idMenu = columns[2].trim();
                String kuantitas = columns[3].trim();

                Pelanggan pelanggan = daftarPelanggan.stream().filter(p -> p.getIdPelanggan().equals(idPelanggan))
                        .findFirst().orElse(new Guest(idPelanggan));
                Menu menu = daftarMenu.stream().filter(m -> m.getIdMenu().equals(idMenu)).findFirst()
                        .orElse(new Menu(idMenu));
                ItemPesanan item = new ItemPesanan(menu, Integer.parseInt(kuantitas));

                Order existingOrder = kumpulanPesanan.stream().filter(order -> order.getPelanggan().equals(pelanggan))
                        .findFirst()
                        .orElseGet(() -> {
                            Order newOrder = new Order(pelanggan, new LinkedHashSet<>(), null);
                            kumpulanPesanan.add(newOrder);
                            return newOrder;
                        });
                existingOrder.getItem().add(item);
            }
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pelanggan == null) ? 0 : pelanggan.hashCode());
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
        if (pelanggan == null) {
            if (other.pelanggan != null)
                return false;
        } else if (!pelanggan.equals(other.pelanggan))
            return false;
        return true;
    }
}