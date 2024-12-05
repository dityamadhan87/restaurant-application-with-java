package Pelanggan;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import InterfaceRestaurant.ReadData;
import Menu.Menu;
import Order.Order;

public class Pelanggan implements ReadData{
    private String idPelanggan;
    private String firstName;
    private String lastName;
    private String tanggalMenjadiMember;
    private int saldoAwal;
    private HashMap<Pelanggan,LinkedList<Order>> cart = new HashMap<>();
    private HashMap<String, Menu> daftarMenu = new HashMap<>();
    private HashMap<String, Pelanggan> daftarPelanggan = new HashMap<>();

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

    public HashMap<Pelanggan,LinkedList<Order>> getCart() {
        return cart;
    }
    
    public void makeOrder(String input) throws Exception {
        if (cart.isEmpty()) {
            loadCart();
        }
        System.out.println(cart);
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

                output.printf("%-4s %-1c %-4s %c %-1s %c %s\n", idPelanggan, '|', idMenu, '|',
                        kuantitas, '|', order.getSubTotalBiayaMakanan());
                System.out.println(
                        "ADD_TO_CART SUCCESS: " + kuantitas + " " + order.getMenu().getNamaMenu() + " IS ADDED");
            }
        }
    }

    @Override
    public void loadCart() throws Exception{
        if (daftarMenu.isEmpty()) {
            loadMenu();
        }
        if (daftarPelanggan.isEmpty()) {
            loadPelanggan();
        }
        
        File file = new File("D:\\Programming\\java\\restaurant\\src\\DataRestaurant\\Pesanan.txt");
        Scanner in = new Scanner(file);

        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] columns = line.split("\\|");

            String idPelanggan = columns[0].trim();
            String idMenu = columns[1].trim();
            String kuantitas = columns[2].trim();
            String subTotalUnitMenu = columns[3].trim();

            Pelanggan pelanggan = daftarPelanggan.get(idPelanggan);
            Menu menu = daftarMenu.get(idMenu);
            Order order = new Order(menu, Integer.parseInt(kuantitas), Integer.parseInt(subTotalUnitMenu));

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

            if (daftarMenu.containsKey(idMenu)) {
                return;
            }

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

            if (daftarPelanggan.containsKey(idPelanggan)) {
                return;
            }

            daftarPelanggan.put(idPelanggan,
                    new Pelanggan(idPelanggan, firstName, lastName, tanggalMenjadiMember, Integer.parseInt(saldoAwal)));
        }
    }
}