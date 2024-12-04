package BagianAdmin;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

import Menu.Menu;
import Pelanggan.*;

public class Admin {

    private HashMap<String, Menu> daftarMenu = new HashMap<>();
    private HashMap<String, Pelanggan> daftarPelanggan = new HashMap<>();

    public HashMap<String, Menu> getDaftarMenu() {
        return daftarMenu;
    }

    public HashMap<String, Pelanggan> getDaftarPelanggan() {
        return daftarPelanggan;
    }

    public void createMenu(String input) throws Exception {
        loadMenu();
        String filePath = "D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\DaftarMenu.txt";
        try (PrintWriter output = new PrintWriter(new FileWriter(filePath, true))) {
            if (input.startsWith("CREATE MENU")) {
                String[] bagianMenu = input.split(" ", 3);
                String dataMenu = bagianMenu[2];
                String[] unitDataMenu = dataMenu.split("\\|");
                String idMenu = unitDataMenu[0];
                String namaMenu = unitDataMenu[1];
                String hargaMenu = unitDataMenu[2];

                if (daftarMenu.containsKey(idMenu)) {
                    System.out.println("CREATE MENU FAILED: " + idMenu + " IS EXISTS");
                    return;
                }

                daftarMenu.put(idMenu, new Menu(idMenu, namaMenu, hargaMenu));
                output.printf("%-7s %c %-20s %c %s\n", idMenu, '|', namaMenu, '|', hargaMenu);
                System.out.println("CREATE MENU SUCCESS: " + idMenu + " " + namaMenu);
            }
        }
    }

    public void readMenu(String input) throws Exception {
        if (input.startsWith("READ MENU")) {
            loadMenu();

            System.out.println("=".repeat(39));
            System.out.println(" ".repeat(14) + "Daftar Menu");
            System.out.println("=".repeat(39));

            for (Menu menu : daftarMenu.values()) {
                System.out.printf("%-11s %-21s %s\n", menu.getIdMenu(), menu.getNamaMenu(), menu.getHargaMenu());
            }
        }
    }

    public void loadMenu() throws Exception {
        File file = new File("D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\DaftarMenu.txt");
        Scanner in = new Scanner(file);

        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] columns = line.split("\\|");

            String idMenu = columns[0].trim();
            String namaMenu = columns[1].trim();
            String hargaMenu = columns[2].trim();

            if (daftarMenu.containsKey(idMenu)) {
                return;
            }

            daftarMenu.put(idMenu, new Menu(idMenu, namaMenu, hargaMenu));
        }
    }

    public void createPelanggan(String input) throws Exception {
        loadPelanggan();
        String filePath = "D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\DaftarPelanggan.txt";
        try (PrintWriter output = new PrintWriter(new FileWriter(filePath, true))) {
            if (input.startsWith("CREATE PELANGGAN")) {
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

                if (daftarPelanggan.containsKey(idPelanggan)) {
                    System.out.println("CREATE PELANGGAN: " + idPelanggan + " IS EXISTS");
                    return;
                }

                daftarPelanggan.put(idPelanggan, new Pelanggan(idPelanggan, firstName, lastName, tanggalMenjadiMember,
                        Integer.parseInt(saldoAwal)));
                output.printf("%-5s %c %-25s %c %-10s %c %s\n", idPelanggan, '|', namaPelanggan, '|',
                        tanggalMenjadiMember, '|', saldoAwal);
                System.out.println("CREATE PELANGGAN SUCCESS: " + idPelanggan);
            }
        }
    }

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

            if (daftarPelanggan.containsKey(idPelanggan)) {
                return;
            }

            daftarPelanggan.put(idPelanggan,
                    new Pelanggan(idPelanggan, firstName, lastName, tanggalMenjadiMember, Integer.parseInt(saldoAwal)));
        }
    }

    public void readPelanggan(String input) throws Exception {
        if (input.startsWith("READ PELANGGAN")) {
            loadPelanggan();

            System.out.println("=".repeat(58));
            System.out.println(" ".repeat(21) + "Daftar Pelanggan");
            System.out.println("=".repeat(58));

            for (Pelanggan pelanggan : daftarPelanggan.values()) {
                System.out.printf("%-11s %-24s %-14s %d\n", pelanggan.getIdPelanggan(), pelanggan.getFullName(),
                        pelanggan.getTanggalMenjadiMember(), pelanggan.getSaldoAwal());
            }
        }
    }
}