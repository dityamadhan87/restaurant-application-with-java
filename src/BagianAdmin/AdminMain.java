package BagianAdmin;

import java.util.Scanner;

public class AdminMain {
    public static void main(String[] args) throws Exception {
        Admin admin = new Admin();
        Scanner in = new Scanner(System.in);
        while(true){
            String input = in.nextLine();
            if (input.startsWith("CREATE MENU")) {
                admin.createMenu(input);
            } else if (input.startsWith("READ MENU")){
                admin.readMenu(input);
            } else if (input.startsWith("READ PELANGGAN")){
                admin.readPelanggan(input);
            } else if(input.startsWith("CREATE PELANGGAN")){
                admin.createPelanggan(input);
            } else{
                in.close();
                break;
            }
        }
    }
}
