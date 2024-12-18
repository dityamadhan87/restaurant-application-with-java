package AdminPackage;

import java.util.Scanner;

public class AdminMain {
    public static void main(String[] args) throws Exception {
        Admin admin = new Admin();
        admin.loadMenu();
        admin.loadCustomer();
        admin.loadPromo();
        Scanner in = new Scanner(System.in);
        while (true) {
            String input = in.nextLine();
            if (input.startsWith("CREATE MENU"))
                admin.createMenu(input);
            else if (input.startsWith("READ MENU"))
                admin.readMenu(input);
            else if (input.startsWith("READ PELANGGAN"))
                admin.readCustomer(input);
            else if(input.startsWith("READ PROMO"))
                admin.readPromo(input);
            else if (input.startsWith("CREATE MEMBER"))
                admin.createMember(input);
            else if(input.startsWith("CREATE GUEST"))
                admin.createGuest(input);
            else if(input.startsWith("CREATE PROMO"))
                admin.createPromo(input);
            else {
                in.close();
                break;
            }
        }
    }
}