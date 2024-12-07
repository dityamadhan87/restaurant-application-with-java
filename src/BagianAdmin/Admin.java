package BagianAdmin;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.LinkedHashSet;
import java.util.Set;

import InterfaceRestaurant.ReadData;
import Menu.Menu;
import Pelanggan.Pelanggan;

public class Admin implements ReadData {

    private Set<Menu> daftarMenu = new LinkedHashSet<>();
    private Set<Pelanggan> daftarPelanggan = new LinkedHashSet<>();

    public Set<Menu> getDaftarMenu() {
        return daftarMenu;
    }

    public Set<Pelanggan> getDaftarPelanggan() {
        return daftarPelanggan;
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

    public void createPelanggan(String input) throws Exception {
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

            Pelanggan pelanggan = new Pelanggan(idPelanggan, firstName, lastName, tanggalMenjadiMember, saldoAwal);

            if (daftarPelanggan.contains(pelanggan)) {
                System.out.println("CREATE PELANGGAN FAILED: " + idPelanggan + " IS EXISTS");
                return;
            }

            daftarPelanggan.add(pelanggan);
            output.printf("%-5s %c %-25s %c %-10s %c %s\n", idPelanggan, '|', namaPelanggan, '|',
                    tanggalMenjadiMember, '|', saldoAwal);
            System.out.println("CREATE PELANGGAN SUCCESS: " + idPelanggan);
        }
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

        System.out.println("=".repeat(58));
        System.out.println(" ".repeat(21) + "Daftar Pelanggan");
        System.out.println("=".repeat(58));

        for (Pelanggan pelanggan : daftarPelanggan)
            System.out.printf("%-11s %-24s %-14s %s\n", pelanggan.getIdPelanggan(), pelanggan.getFullName(),
                    pelanggan.getTanggalMenjadiMember(), pelanggan.getSaldoAwal());
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
    public void loadCart() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadCart'");
    }
}