package Pelanggan;

import java.util.Scanner;

import BagianAdmin.Admin;
import Order.Order;

public class PelangganMain {
    public static void main(String[] args) throws Exception {
        Admin admin = new Admin();
        Order order = new Order();
        Scanner in = new Scanner(System.in);
        while (true) {
            String input = in.nextLine();
            if (input.startsWith("READ MENU"))
                admin.readMenu(input);
            else if (input.startsWith("ADD_TO_CART"))
                order.makeOrder(input);
            else if (input.startsWith("PRINT"))
                order.printDetails(input);
            else if(input.startsWith("REMOVE_FROM_CART"));
        }
    }
}