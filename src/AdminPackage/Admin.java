package AdminPackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Set;

import CustomerPackage.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;
import InterfaceRestaurant.ReadData;
import MenuPackage.Menu;
import Promotion.*;

public class Admin implements ReadData {

    private Set<Menu> listMenu = new LinkedHashSet<>();
    private Set<Customer> listCustomer = new LinkedHashSet<>();
    private Set<Promotion> listPromo = new LinkedHashSet<>();

    public void createPromo(String input) throws Exception {
        String[] promoSplit = input.split(" ", 3);
        String promoTypeData = promoSplit[2];
        String[] promoData = promoTypeData.split(" ", 2);
        String promoTypeUnit = promoData[0];
        String promoIdentity = promoData[1];
        String[] promoIdentityUnit = promoIdentity.split("\\|");
        String promoCode = promoIdentityUnit[0];
        String startDate = promoIdentityUnit[1];
        String endDate = promoIdentityUnit[2];
        String percentDiscount = promoIdentityUnit[3];
        String maxDiscount = promoIdentityUnit[4];
        String minimumPurchase = promoIdentityUnit[5];

        String filePath = "src/DataRestaurant/ListPromo.txt";
        try (BufferedWriter output = new BufferedWriter(new FileWriter(filePath, true))) {
            Promotion promo;
            if (promoTypeUnit.equals("DELIVERY"))
                promo = new FreeShippingPromo(promoCode, startDate, endDate, percentDiscount, maxDiscount,
                        minimumPurchase);
            else if (promoTypeUnit.equals("DISCOUNT"))
                promo = new PercentOffPromo(promoCode, startDate, endDate, percentDiscount, maxDiscount, minimumPurchase);
            else
                promo = new CashbackPromo(promoCode, startDate, endDate, percentDiscount, maxDiscount, minimumPurchase);

            if (listPromo.contains(promo)) {
                System.out.println("CREATE PROMO " + promoTypeUnit + " FAILED: " + promoCode + " IS EXISTS");
                return;
            }
            String line = String.format("%s %c %-10s %c %-10s %c %-10s %c %-4s %c %-7s %c %s\n", promo.getPromoType(),
                    '|', promoCode,
                    '|', startDate, '|', endDate, '|', percentDiscount, '|', maxDiscount, '|', minimumPurchase);

            listPromo.add(promo);
            output.write(line);
            System.out.println("CREATE PROMO " + promoTypeUnit + " SUCCESS: " + promoCode);
        }
    }

    public void createMenu(String input) throws Exception {
        String filePath = "src/DataRestaurant/ListMenu.txt";
        try (BufferedWriter output = new BufferedWriter(new FileWriter(filePath, true))) {
            String[] menuSplit = input.split(" ", 3);
            String menuData = menuSplit[2];
            String[] menuDataUnit = menuData.split("\\|");
            String menuId = menuDataUnit[0];
            String menuName = menuDataUnit[1];
            String menuPrice = menuDataUnit[2];

            Menu menu = new Menu(menuId, menuName, menuPrice);

            if (listMenu.contains(menu)) {
                System.out.println("CREATE MENU FAILED: " + menuId + " IS EXISTS");
                return;
            }

            String line = String.format("%-7s %c %-20s %c %s\n", menuId, '|', menuName, '|', menuPrice);
            listMenu.add(menu);
            output.write(line);
            System.out.println("CREATE MENU SUCCESS: " + menuId + " " + menuName);
        }
    }

    public void createGuest(String input) throws Exception {
        String filePath = "src/DataRestaurant/ListCustomer.txt";
        try (BufferedWriter output = new BufferedWriter(new FileWriter(filePath, true))) {
            String[] customerSplit = input.split(" ", 3);
            String customerData = customerSplit[2];
            String[] customerDataUnit = customerData.split("\\|");
            String customerId = customerDataUnit[0].trim();
            String customerName = customerDataUnit[1].trim();
            String openingBalance = customerDataUnit[2].trim();

            String firstName;
            String lastName;

            if (customerName.contains(" ")) {
                firstName = customerName.substring(0, customerName.indexOf(' '));
                lastName = customerName.substring(customerName.indexOf(' ') + 1);
            } else {
                firstName = customerName;
                lastName = "";
            }

            Customer customer = new Guest(customerId, firstName, lastName, openingBalance);

            if (listCustomer.contains(customer)) {
                System.out.println("CREATE GUEST FAILED: " + customerId + " IS EXISTS");
                return;
            }

            String line = String.format("%-6s %c %-7s %c %-25s %c %-10s %c %s\n", customer.getCustomerType(), '|',
                    customerId, '|',
                    customerName, '|',
                    customer.getMemberDate(), '|', openingBalance);
            listCustomer.add(customer);
            output.write(line);
            System.out.println("CREATE GUEST SUCCESS: " + customerId);
        }
    }

    public void createMember(String input) throws Exception {
        String filePath = "src/DataRestaurant/ListCustomer.txt";
        try (BufferedWriter output = new BufferedWriter(new FileWriter(filePath, true))) {
            String[] customerSplit = input.split(" ", 3);
            String customerData = customerSplit[2];
            String[] customerDataUnit = customerData.split("\\|");
            String customerId = customerDataUnit[0].trim();
            String customerName = customerDataUnit[1].trim();
            String memberDate = customerDataUnit[2].trim();
            String openingBalance = customerDataUnit[3].trim();

            String firstName;
            String lastName;

            if (customerName.contains(" ")) {
                firstName = customerName.substring(0, customerName.indexOf(' '));
                lastName = customerName.substring(customerName.indexOf(' ') + 1);
            } else {
                firstName = customerName;
                lastName = "";
            }

            Customer customer = new Member(customerId, firstName, lastName, memberDate, openingBalance);

            if (listCustomer.contains(customer)) {
                System.out.println("CREATE MEMBER FAILED: " + customerId + " IS EXISTS");
                return;
            }
            String line = String.format("%-6s %c %-7s %c %-25s %c %-10s %c %s\n", customer.getCustomerType(), '|',
                    customerId, '|',
                    customerName, '|',
                    memberDate, '|', openingBalance);

            listCustomer.add(customer);
            output.write(line);
            System.out.println("CREATE MEMBER SUCCESS: " + customerId + " " + customer.getFullName());
        }
    }

    public void topupCustomerBalance(String input) throws Exception {
        String[] customerSplit = input.split(" ", 2);
        String topupData = customerSplit[1];
        String[] topupUnit = topupData.split(" ");
        String customerId = topupUnit[0].trim();
        String topupBalance = topupUnit[1].trim();
        Customer customer = new Guest(customerId); // Default Guest just for checking
        if (!listCustomer.contains(customer)) {
            System.out.println("TOPUP FAILED: NON EXISTENT CUSTOMER");
            return;
        }

        // check object similarity by comparing customer id
        for (Customer getCustomer : listCustomer) {
            if (getCustomer.equals(customer)) {
                customer = getCustomer;
                break;
            }
        }

        String filePath = "src/DataRestaurant/ListCustomer.txt";
        List<String> lines = new LinkedList<>();

        // update balance in file
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\\|");
                String customerIdInFile = columns[1].trim();
                String balanceInFile = columns[4].trim();
                int endBalance = Integer.parseInt(topupBalance) + Integer.parseInt(balanceInFile);

                if (customerIdInFile.equals(customerId)) {
                    line = String.format("%-6s %c %-7s %c %-25s %c %-10s %c %d", customer.getCustomerType(), '|',
                            customerId, '|', customer.getFullName(), '|',
                            customer.getMemberDate(), '|', endBalance);
                    System.out
                            .println("TOPUP SUCCESS: " + customer.getFullName() + " " + customer.getOpeningBalance() + "=>"
                                    + endBalance);
                    customer.setOpeningBalance(String.valueOf(endBalance));
                }
                lines.add(line);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        listCustomer.remove(customer);
        listCustomer.add(customer);
    }

    public void readPromo(String input) throws Exception {
        System.out.println("=".repeat(75));
        System.out.println(" ".repeat(30) + "Promo List");
        System.out.println("=".repeat(75));

        for (Promotion promo : listPromo)
            System.out.printf("%-11s %-11s %-13s %-13s %-6s %-9s %s\n", promo.getPromoType(), promo.getPromoCode(),
                    promo.getStartDate(), promo.getEndDate(), promo.getPercentDiscount(), promo.getMaxDiscount(),
                    promo.getMinimumPurchase());
    }

    public void readMenu(String input) throws Exception {
        System.out.println("=".repeat(39));
        System.out.println(" ".repeat(14) + "Menu List");
        System.out.println("=".repeat(39));

        for (Menu menu : listMenu)
            System.out.printf("%-11s %-21s %s\n", menu.getMenuId(), menu.getMenuName(), menu.getMenuPrice());
    }

    public void readCustomer(String input) throws Exception {
        System.out.println("=".repeat(71));
        System.out.println(" ".repeat(27) + "Customer List");
        System.out.println("=".repeat(71));

        for (Customer customer : listCustomer)
            System.out.printf("%-10s %-11s %-26s %-14s %s\n", customer.getCustomerType(), customer.getCustomerId(),
                    customer.getFullName(),
                    customer.getMemberDate(), customer.getOpeningBalance());
    }

    @Override
    public void loadMenu() throws Exception {
        String filePath = "src/DataRestaurant/ListMenu.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] columns = line.split("\\|");

                String menuId = columns[0].trim();
                String menuName = columns[1].trim();
                String menuPrice = columns[2].trim();

                Menu menu = new Menu(menuId, menuName, menuPrice);

                if (!listMenu.contains(menu))
                    listMenu.add(menu);
            }
        }
    }

    @Override
    public void loadCustomer() throws Exception {
        String filePath = "src/DataRestaurant/ListCustomer.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty())
                    continue;

                String[] columns = line.split("\\|");

                String customerType = columns[0].trim();
                String customerId = columns[1].trim();
                String customerName = columns[2].trim();
                String memberDate = columns[3].trim();
                String openingBalance = columns[4].trim();

                String firstName;
                String lastName;

                if (customerName.contains(" ")) {
                    firstName = customerName.substring(0, customerName.indexOf(' '));
                    lastName = customerName.substring(customerName.indexOf(' ') + 1);
                } else {
                    firstName = customerName;
                    lastName = "";
                }

                Customer customer;

                if (customerType.equals("GUEST"))
                    customer = new Guest(customerId, firstName, lastName, openingBalance);
                else
                    customer = new Member(customerId, firstName, lastName, memberDate, openingBalance);

                if (!listCustomer.contains(customer))
                    listCustomer.add(customer);
            }
        }
    }

    @Override
    public void loadPromo() throws Exception {
        String filePath = "src/DataRestaurant/ListPromo.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty())
                    continue;

                String[] columns = line.split("\\|");

                String promoType = columns[0].trim();
                String promoCode = columns[1].trim();
                String startDate = columns[2].trim();
                String endDate = columns[3].trim();
                String percentDiscount = columns[4].trim();
                String maxDiscount = columns[5].trim();
                String minimumPurchase = columns[6].trim();

                Promotion promo;

                if (promoType.equals("DELIVERY"))
                    promo = new FreeShippingPromo(promoCode, startDate, endDate, percentDiscount, maxDiscount,
                            minimumPurchase);
                else if (promoType.equals("DISCOUNT"))
                    promo = new PercentOffPromo(promoCode, startDate, endDate, percentDiscount, maxDiscount,
                            minimumPurchase);
                else
                    promo = new CashbackPromo(promoCode, startDate, endDate, percentDiscount, maxDiscount,
                            minimumPurchase);

                if (!listPromo.contains(promo))
                    listPromo.add(promo);
            }
        }
    }

    @Override
    public void loadCart() throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'loadCart'");
    }
}