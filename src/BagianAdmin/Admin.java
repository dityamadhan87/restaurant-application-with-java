package BagianAdmin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;

import InterfaceRestaurant.ReadData;
import Menu.Menu;
import Pelanggan.*;
import Promotion.*;

public class Admin implements ReadData {

    private Set<Menu> daftarMenu = new LinkedHashSet<>();
    private Set<Pelanggan> daftarPelanggan = new LinkedHashSet<>();
    private Set<Promotion> daftarPromo = new LinkedHashSet<>();

    public Set<Menu> getDaftarMenu() {
        return daftarMenu;
    }

    public Set<Pelanggan> getDaftarPelanggan() {
        return daftarPelanggan;
    }

    public void createPromo(String input) throws Exception {
        if (daftarPromo.isEmpty())
            loadPromo();

        String[] bagianMenu = input.split(" ", 3);
        String dataPromoJenis = bagianMenu[2];
        String[] unitJenisPromo = dataPromoJenis.split(" ", 2);
        String jenisPromo = unitJenisPromo[0];
        String unitDataPromo = unitJenisPromo[1];
        String[] unitPromo = unitDataPromo.split("\\|");
        String kodePromo = unitPromo[0];
        String startDate = unitPromo[1];
        String endDate = unitPromo[2];
        String persenPotongan = unitPromo[3];
        String maksPotongan = unitPromo[4];
        String minPembelian = unitPromo[5];

        String filePath = "D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\DaftarPromo.txt";
        try (PrintWriter output = new PrintWriter(new FileWriter(filePath, true))) {
            Promotion promo;
            if (jenisPromo.equals("DELIVERY"))
                promo = new FreeShippingPromo(kodePromo, startDate, endDate, persenPotongan, maksPotongan,
                        minPembelian);
            else if (jenisPromo.equals("DISCOUNT"))
                promo = new PercentOffPromo(kodePromo, startDate, endDate, persenPotongan, maksPotongan, minPembelian);
            else
                promo = new CashbackPromo(kodePromo, startDate, endDate, persenPotongan, maksPotongan, minPembelian);

            if (daftarPromo.contains(promo)) {
                System.out.println("CREATE PROMO " + jenisPromo + " FAILED: " + kodePromo + " IS EXISTS");
                return;
            }

            daftarPromo.add(promo);
            output.printf("%s %c %-10s %c %-10s %c %-10s %c %-3s %c %-7s %c %s\n", promo.getTipePromo(), '|', kodePromo,
                    '|', startDate, '|', endDate, '|', persenPotongan, '|', maksPotongan, '|', minPembelian);
            System.out.println("CREATE PROMO " + jenisPromo + " SUCCESS: " + kodePromo);
        }
    }

    public void createMenu(String input) throws Exception {
        if (daftarMenu.isEmpty())
            loadMenu();

        String filePath = "D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\DaftarMenu.txt";
        try (PrintWriter output = new PrintWriter(new FileWriter(filePath, true))) {
            String[] bagianMenu = input.split(" ", 3);
            String dataMenu = bagianMenu[2];
            String[] unitDataMenu = dataMenu.split("\\|");
            String idMenu = unitDataMenu[0];
            String namaMenu = unitDataMenu[1];
            String hargaMenu = unitDataMenu[2];

            Menu menu = new Menu(idMenu, namaMenu, hargaMenu);

            if (daftarMenu.contains(menu)) {
                System.out.println("CREATE MENU FAILED: " + idMenu + " IS EXISTS");
                return;
            }

            daftarMenu.add(menu);
            output.printf("%-7s %c %-20s %c %s\n", idMenu, '|', namaMenu, '|', hargaMenu);
            System.out.println("CREATE MENU SUCCESS: " + idMenu + " " + namaMenu);
        }
    }

    public void createGuest(String input) throws Exception {
        if (daftarPelanggan.isEmpty())
            loadPelanggan();

        String filePath = "D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\DaftarPelanggan.txt";
        try (PrintWriter output = new PrintWriter(new FileWriter(filePath, true))) {
            String[] bagianPelanggan = input.split(" ", 3);
            String dataPelanggan = bagianPelanggan[2];
            String[] unitDataPelanggan = dataPelanggan.split("\\|");
            String idPelanggan = unitDataPelanggan[0].trim();
            String namaPelanggan = unitDataPelanggan[1].trim();
            String saldoAwal = unitDataPelanggan[2].trim();

            String firstName;
            String lastName;

            if (namaPelanggan.contains(" ")) {
                firstName = namaPelanggan.substring(0, namaPelanggan.indexOf(' '));
                lastName = namaPelanggan.substring(namaPelanggan.indexOf(' ') + 1);
            } else {
                firstName = namaPelanggan;
                lastName = "";
            }

            Pelanggan pelanggan = new Guest(idPelanggan, firstName, lastName, saldoAwal);

            if (daftarPelanggan.contains(pelanggan)) {
                System.out.println("CREATE GUEST FAILED: " + idPelanggan + " IS EXISTS");
                return;
            }

            daftarPelanggan.add(pelanggan);
            output.printf("%-6s %c %-7s %c %-25s %c %-10s %c %s\n", pelanggan.getTipePelanggan(), '|', idPelanggan, '|',
                    namaPelanggan, '|',
                    pelanggan.getTanggalMenjadiMember(), '|', saldoAwal);
            System.out.println("CREATE GUEST SUCCESS: " + idPelanggan);
        }
    }

    public void createMember(String input) throws Exception {
        if (daftarPelanggan.isEmpty())
            loadPelanggan();

        String filePath = "D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\DaftarPelanggan.txt";
        try (PrintWriter output = new PrintWriter(new FileWriter(filePath, true))) {
            String[] bagianPelanggan = input.split(" ", 3);
            String dataPelanggan = bagianPelanggan[2];
            String[] unitDataPelanggan = dataPelanggan.split("\\|");
            String idPelanggan = unitDataPelanggan[0].trim();
            String namaPelanggan = unitDataPelanggan[1].trim();
            String tanggalMenjadiMember = unitDataPelanggan[2].trim();
            String saldoAwal = unitDataPelanggan[3].trim();

            String firstName;
            String lastName;

            if (namaPelanggan.contains(" ")) {
                firstName = namaPelanggan.substring(0, namaPelanggan.indexOf(' '));
                lastName = namaPelanggan.substring(namaPelanggan.indexOf(' ') + 1);
            } else {
                firstName = namaPelanggan;
                lastName = "";
            }

            Pelanggan pelanggan = new Member(idPelanggan, firstName, lastName, tanggalMenjadiMember, saldoAwal);

            if (daftarPelanggan.contains(pelanggan)) {
                System.out.println("CREATE MEMBER FAILED: " + idPelanggan + " IS EXISTS");
                return;
            }

            daftarPelanggan.add(pelanggan);
            output.printf("%-6s %c %-7s %c %-25s %c %-10s %c %s\n", pelanggan.getTipePelanggan(), '|', idPelanggan, '|',
                    namaPelanggan, '|',
                    tanggalMenjadiMember, '|', saldoAwal);
            System.out.println("CREATE MEMBER SUCCESS: " + idPelanggan + " " + pelanggan.getFullName());
        }
    }

    public void topupSaldoPelanggan(String input) throws Exception {
        if(daftarPelanggan.isEmpty())
            loadPelanggan();

        String[] bagianPelanggan = input.split(" ", 2);
        String pelangganTopup = bagianPelanggan[1];
        String[] unitTopup = pelangganTopup.split(" ");
        String idPelanggan = unitTopup[0].trim();
        String saldoTopup = unitTopup[1].trim();
        Pelanggan pelanggan = new Guest(idPelanggan);
        if (!daftarPelanggan.contains(pelanggan)) {
            System.out.println("TOPUP FAILED: NON EXISTENT CUSTOMER");
            return;
        }
        for (Pelanggan getPelanggan : daftarPelanggan) {
            if (getPelanggan.getIdPelanggan().equals(idPelanggan)) {
                pelanggan = getPelanggan;
                break;
            }
        }

        String filePath = "D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\DaftarPelanggan.txt";
        File file = new File(filePath);
        List<String> lines = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\\|");
                String idPelangganFile = columns[1].trim();
                String saldoFile = columns[4].trim();
                int saldoAkhir = Integer.parseInt(saldoTopup) + Integer.parseInt(saldoFile);

                if (idPelangganFile.equals(idPelanggan)) {
                    line = String.format("%-6s %c %-7s %c %-25s %c %-10s %c %d", pelanggan.getTipePelanggan(), '|',
                            idPelanggan, '|', pelanggan.getFullName(), '|',
                            pelanggan.getTanggalMenjadiMember(), '|', saldoAkhir);
                    System.out
                            .println("TOPUP SUCCESS: " + pelanggan.getFullName() + " " + pelanggan.getSaldoAwal() + "=>"
                                    + saldoAkhir);
                    pelanggan.setSaldoAwal(String.valueOf(saldoAkhir));
                }
                lines.add(line);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        daftarPelanggan.remove(pelanggan);
        daftarPelanggan.add(pelanggan);
    }

    public void readPromo(String input) throws Exception {
        if (daftarPromo.isEmpty())
            loadPromo();

        System.out.println("=".repeat(75));
        System.out.println(" ".repeat(30) + "Daftar Promo");
        System.out.println("=".repeat(75));

        for (Promotion promo : daftarPromo)
            System.out.printf("%-11s %-11s %-13s %-13s %-6s %-9s %s\n", promo.getTipePromo(), promo.getKodePromo(),
                    promo.getStartDate(), promo.getEndDate(), promo.getPersenPotongan(), promo.getMaksPotongan(),
                    promo.getMinPembelian());
    }

    public void readMenu(String input) throws Exception {
        if (daftarMenu.isEmpty())
            loadMenu();

        System.out.println("=".repeat(39));
        System.out.println(" ".repeat(14) + "Daftar Menu");
        System.out.println("=".repeat(39));

        for (Menu menu : daftarMenu)
            System.out.printf("%-11s %-21s %s\n", menu.getIdMenu(), menu.getNamaMenu(), menu.getHargaMenu());
    }

    public void readPelanggan(String input) throws Exception {
        if (daftarPelanggan.isEmpty())
            loadPelanggan();

        System.out.println("=".repeat(71));
        System.out.println(" ".repeat(27) + "Daftar Pelanggan");
        System.out.println("=".repeat(71));

        for (Pelanggan pelanggan : daftarPelanggan)
            System.out.printf("%-10s %-11s %-26s %-14s %s\n", pelanggan.getTipePelanggan(), pelanggan.getIdPelanggan(),
                    pelanggan.getFullName(),
                    pelanggan.getTanggalMenjadiMember(), pelanggan.getSaldoAwal());
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
    public void loadCart() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadCart'");
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