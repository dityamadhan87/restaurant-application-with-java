package CustomerPackage;

import java.util.Scanner;

import AdminPackage.Admin;
import Order.Order;

public class CustomerMain {
    public static void main(String[] args) throws Exception {
        Admin admin = new Admin();
        Order order = new Order();
        admin.loadMenu();
        admin.loadCustomer();
        order.loadMenu();
        order.loadCustomer();
        order.loadPromo();
        order.loadCart();
        Scanner in = new Scanner(System.in);
        while (true) {
            String input = in.nextLine();
            if (input.startsWith("READ MENU"))
                admin.readMenu(input);
            else if (input.startsWith("ADD_TO_CART"))
                order.makeOrder(input);
            else if (input.startsWith("PRINT"))
                order.printDetails(input);
            else if(input.startsWith("REMOVE_FROM_CART"))
                order.removeFromCart(input);
            else if(input.startsWith("TOPUP"))
                admin.topupCustomerBalance(input);
            else if(input.startsWith("APPLY_PROMO"))
                order.applyPromo(input);
            else if(input.startsWith("READ_PROMO"))
                order.readPromo(input);
            else if(input.startsWith("CHECK_OUT"))
                order.checkOut(input);
        }
    }
}