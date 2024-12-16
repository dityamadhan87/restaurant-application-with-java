package Order;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;

import CustomerPackage.*;
import InterfaceRestaurant.ReadData;
import MenuPackage.Menu;
import Promotion.*;

public class Order implements ReadData {
    private int subTotalFoodCost;
    private int shippingCost = 15000;
    private double totalDiscount;
    private int totalPrice;
    private Set<Order> orderList = new LinkedHashSet<>();
    private Set<HistoryData> orderHistory = new LinkedHashSet<>();
    private Set<Menu> listMenu = new LinkedHashSet<>();
    private Set<Customer> listCustomer = new LinkedHashSet<>();
    private Set<Promotion> listPromo = new LinkedHashSet<>();
    private Promotion appliedPromo;
    private Customer customer;
    private Set<OrderItem> item;

    public Order(Customer customer, Set<OrderItem> item, Promotion appliedPromo) {
        this.customer = customer;
        this.item = item;
        this.appliedPromo = appliedPromo;
    }

    public Order() {
    }

    public int getSubTotalFoodCost() {
        subTotalFoodCost = 0;
        for (OrderItem orderItem : item) {
            subTotalFoodCost += Integer.parseInt(orderItem.getMenu().getMenuPrice()) * orderItem.getQuantity();
        }
        return subTotalFoodCost;
    }

    public int getShippingCost() {
        return shippingCost;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public double getTotalPrice() {
        totalPrice = (int) (subTotalFoodCost + shippingCost - totalDiscount);
        return totalPrice;
    }

    public Promotion getAppliedPromo() {
        return appliedPromo;
    }

    public void setAppliedPromo(Promotion appliedPromo) {
        this.appliedPromo = appliedPromo;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Set<OrderItem> getItem() {
        return item;
    }

    public void checkOut(String input) throws Exception {
        String[] checkOutCustomer = input.split(" ", 2);
        String customerId = checkOutCustomer[1];
        Customer customer = new Guest(customerId);

        for (Customer getCustomer : listCustomer) {
            if (getCustomer.equals(customer)) {
                customer = getCustomer;
                break;
            }
        }

        Order existingOrder = null;
        for (Order order : orderList) {
            if (order.getCustomer().equals(customer)) {
                existingOrder = order;
                break;
            }
        }

        if (existingOrder == null) {
            System.out.println("CHECK_OUT FAILED: CUSTOMER HAVE NOT ORDERED");
            return;
        }

        if (Integer.parseInt(customer.getOpeningBalance()) < existingOrder.getTotalPrice()) {
            System.out.println(
                    "CHECK_OUT FAILED: " + customerId + " " + customer.getFullName() + " INSUFFICIENT BALANCE");
            return;
        }

        String filePath = "src/DataRestaurant/OrderHistory.txt";
        File file = new File(filePath);

        int orderNumber = 0;

        if (file.length() > 0) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] columns = line.split("\\|");
                    String orderNumberInFile = columns[0].trim();
                    orderNumber = Integer.parseInt(orderNumberInFile);
                }
            }
        }

        orderNumber += 1;

        int totalQuantity = 0;
        for (OrderItem orderItem : existingOrder.getItem()) {
            totalQuantity += orderItem.getQuantity();
        }

        String promoCode;
        if (existingOrder.getAppliedPromo() == null) {
            promoCode = "-";
        } else {
            promoCode = existingOrder.getAppliedPromo().getPromoCode();
        }

        LocalDate orderDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formattedDate = orderDate.format(formatter);

        HistoryData historyData = new HistoryData(orderNumber, customerId, totalQuantity, existingOrder.getSubTotalFoodCost(),
                promoCode, formattedDate);

        String line = String.format("%-3s %c %-6s %c %-4s %c %-7s %c %-13s %c %s\n", orderNumber, '|',
                existingOrder.getCustomer().getCustomerId(), '|', totalQuantity, '|',
                existingOrder.getSubTotalFoodCost(), '|', promoCode, '|',
                formattedDate);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(line);
        }

        double remainingBalance = Double.parseDouble(customer.getOpeningBalance()) - existingOrder.getTotalPrice();
        String filePathCustomer = "src/DataRestaurant/ListCustomer.txt";
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePathCustomer))) {
            String lineCustomer;
            while ((lineCustomer = reader.readLine()) != null) {
                String[] columns = lineCustomer.split("\\|");
                String customerIdInFile = columns[1].trim();

                if (customerIdInFile.equals(customerId)) {
                    lineCustomer = String.format("%-6s %c %-7s %c %-25s %c %-10s %c %.0f",
                            customer.getCustomerType(), '|',
                            customerId, '|', customer.getFullName(), '|',
                            customer.getMemberDate(), '|', remainingBalance);
                    customer.setOpeningBalance(String.valueOf(remainingBalance));
                }
                lines.add(lineCustomer);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePathCustomer))) {
            for (String lineCustomer : lines) {
                writer.write(lineCustomer);
                writer.newLine();
            }
        }
        orderHistory.add(historyData);
        listCustomer.remove(customer);
        listCustomer.add(customer);

        System.out.println("CHECK_OUT SUCCESS: " + customerId + " " + existingOrder.getCustomer().getFullName());
    }

    public void readPromo(String input) {
        String[] promoCustomerSplit = input.split(" ", 2);
        String customerId = promoCustomerSplit[1];
        Customer customer = new Guest(customerId);

        for (Customer getCustomer : listCustomer) {
            if (getCustomer.equals(customer)) {
                customer = getCustomer;
                break;
            }
        }

        Order existingOrder = null;
        for (Order order : orderList) {
            if (order.getCustomer().equals(customer)) {
                existingOrder = order;
                break;
            }
        }

        if (existingOrder == null) {
            System.out.println("READ_PROMO FAILED: CUSTOMERS HAVE NOT ORDERED");
            return;
        }

        List<Promotion> sortedPromotions = new ArrayList<>(listPromo);

        sortedPromotions.sort(new PromotionComparator(existingOrder));

        System.out.println("=".repeat(66));
        System.out.println(" ".repeat(25) + "Eligible Promo");
        System.out.println("=".repeat(66));

        List<Promotion> notEligiblePromo = new ArrayList<>();

        for (Promotion promo : sortedPromotions) {
            if (!promo.isCustomerEligible(customer)) {
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
            LocalDate expiredDate = LocalDate.parse(promo.getEndDate(), formatter);
            LocalDate startDate = LocalDate.parse(promo.getStartDate(), formatter);
            if (startDate.isAfter(LocalDate.now())) {
                notEligiblePromo.add(promo);
                continue;
            }

            if (LocalDate.now().isAfter(expiredDate)) {
                notEligiblePromo.add(promo);
                continue;
            }
            System.out.printf("%-11s %-11s %-13s %-13s %-6s %s\n", promo.getPromoType(), promo.getPromoCode(),
                    promo.getStartDate(), promo.getEndDate(), promo.getPercentDiscount(),
                    promo.totalDiscount(existingOrder));
        }
        System.out.println("=".repeat(66));
        System.out.println(" ".repeat(24) + "Not Eligible Promo");
        System.out.println("=".repeat(66));

        for (Promotion promo : notEligiblePromo) {
            System.out.printf("%-11s %-11s %-13s %-13s %-6s %s\n", promo.getPromoType(), promo.getPromoCode(),
                    promo.getStartDate(), promo.getEndDate(), promo.getPercentDiscount(),
                    promo.totalDiscount(existingOrder));
        }
    }

    public void applyPromo(String input) {
        String[] customerPromoSplit = input.split(" ", 2);
        String appliedPromo = customerPromoSplit[1];
        String[] appliedPromoUnit = appliedPromo.split(" ");
        String customerId = appliedPromoUnit[0];
        String promoCode = appliedPromoUnit[1];

        Customer customer = new Guest(customerId);
        Promotion promo = new PercentOffPromo(promoCode);

        for (Customer getCustomer : listCustomer) {
            if (getCustomer.getCustomerId().equals(customerId)) {
                customer = getCustomer;
                break;
            }
        }

        for (Promotion promotion : listPromo) {
            if (promotion.getPromoCode().equals(promoCode)) {
                promo = promotion;
                break;
            }
        }

        if (!promo.isCustomerEligible(customer)) {
            System.out.println("APPLY_PROMO FAILED: CUSTOMER IS NOT ELIGIBLE");
            return;
        }

        Order existingOrder = null;
        for (Order order : orderList) {
            if (order.getCustomer().equals(customer)) {
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
        LocalDate expiredDate = LocalDate.parse(promo.getEndDate(), formatter);
        LocalDate startDate = LocalDate.parse(promo.getStartDate(), formatter);

        if (startDate.isAfter(LocalDate.now())) {
            System.out.println("APPLY_PROMO FAILED: PROMO " + promo.getPromoCode() + " NOT YET STARTED");
            return;
        }

        if (LocalDate.now().isAfter(expiredDate)) {
            System.out.println("APPLY_PROMO FAILED: PROMO " + promo.getPromoCode() + " HAS EXPIRED");
            return;
        }

        existingOrder.setAppliedPromo(promo);
        System.out.println("APPLY_PROMO SUCCESS: " + promo.getPromoCode());
    }

    public void makeOrder(String input) throws Exception {
        String[] orderSplit = input.split(" ", 2);
        String orderData = orderSplit[1];
        String[] orderDataUnit = orderData.split(" ");
        String customerId = orderDataUnit[0];
        String menuId = orderDataUnit[1];
        String quantity = orderDataUnit[2];

        Customer customer = new Guest(customerId);
        Menu customerMenu = new Menu(menuId);

        if (!listCustomer.contains(customer)
                || !listMenu.contains(customerMenu)) {
            System.out.println("ADD_TO_CART FAILED: NON EXISTENT CUSTOMER OR MENU");
            return;
        }

        for (Customer getCustomer : listCustomer) {
            if (getCustomer.getCustomerId().equals(customerId)) {
                customer = getCustomer;
                break;
            }
        }

        for (Menu menu : listMenu) {
            if (menu.getMenuId().equals(menuId)) {
                customerMenu = menu;
                break;
            }
        }

        OrderItem item = new OrderItem(customerMenu, Integer.parseInt(quantity));

        String filePath = "src/DataRestaurant/Order.txt";
        File file = new File(filePath);
        List<String> lines = new ArrayList<>();
        boolean isUpdated = false;

        if (file.length() > 0) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] columns = line.split("\\|");
                    String customerIdInFile = columns[1].trim();
                    String menuIdInFile = columns[2].trim();
                    String quantityInFile = columns[3].trim();

                    if (customerIdInFile.equals(customerId) && menuIdInFile.equals(menuId)) {
                        int totalQuantity = Integer.parseInt(quantityInFile) + Integer.parseInt(quantity);
                        item = new OrderItem(customerMenu, totalQuantity);
                        line = String.format("%-6s %c %-7s %c %-5s %c %d", customer.getCustomerType(), '|',
                                customerId, '|', menuId, '|',
                                totalQuantity);
                        isUpdated = true;
                    }
                    lines.add(line);
                }
            }
        }

        Order existingOrder = null;
        for (Order order : orderList) {
            if (order.getCustomer().equals(customer)) {
                existingOrder = order;
                break;
            }
        }

        if (existingOrder == null) {
            existingOrder = new Order(customer, new LinkedHashSet<>(), null);
        }

        if (!isUpdated) {
            lines.add(String.format("%-6s %c %-7s %c %-5s %c %s", customer.getCustomerType(), '|', customerId,
                    '|', menuId, '|', quantity));
            existingOrder.getItem().add(item);
            orderList.add(existingOrder);
            System.out.println(
                    "ADD_TO_CART SUCCESS: " + quantity + " " + customerMenu.getMenuName() + " IS ADDED");
        } else {
            if (orderList.contains(existingOrder)) {
                existingOrder.getItem().remove(item);
                existingOrder.getItem().add(item);
                System.out.println("ADD_TO_CART SUCCESS: " + item.getQuantity() + " " + customerMenu.getMenuName()
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
        String[] orderSplit = input.split(" ", 2);
        String orderData = orderSplit[1];
        String[] orderDataUnit = orderData.split(" ");
        String customerId = orderDataUnit[0];
        String menuId = orderDataUnit[1];
        String quantity = orderDataUnit[2];

        Customer customer = new Guest(customerId);
        Menu customerMenu = new Menu(menuId);
        OrderItem item = new OrderItem(customerMenu, Integer.parseInt(quantity));

        Order existingOrder = null;
        for (Order order : orderList) {
            if (order.getCustomer().equals(customer)) {
                existingOrder = order;
                break;
            }
        }

        if (existingOrder == null) {
            existingOrder = new Order(customer, new LinkedHashSet<>(), null);
        }

        if (!orderList.contains(existingOrder)) {
            System.out.println("REMOVE FROM CART FAILED: CUSTOMERS HAVE NOT ORDERED");
            return;
        }
        if (!existingOrder.getItem().contains(item)) {
            System.out.println("REMOVE FROM CART FAILED: CUSTOMERS HAVE NOT ORDERED THIS MENU");
            return;
        }

        for (Customer getCustomer : listCustomer) {
            if (getCustomer.getCustomerId().equals(customerId)) {
                customer = getCustomer;
                break;
            }
        }

        for (Menu menu : listMenu) {
            if (menu.getMenuId().equals(menuId)) {
                customerMenu = menu;
                break;
            }
        }

        String filePath = "src/DataRestaurant/Order.txt";
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty())
                    continue;

                String[] columns = line.split("\\|");
                String customerIdInFile = columns[1].trim();
                String menuIdInFile = columns[2].trim();
                String quantityInFile = columns[3].trim();
                if (customerIdInFile.equals(customerId) && menuIdInFile.equals(menuId)) {
                    int totalQuantity = Integer.parseInt(quantityInFile) - Integer.parseInt(quantity);
                    if (totalQuantity <= 0) {
                        System.out.println("REMOVE FROM CART: " + customerMenu.getMenuName() + " IS REMOVED");
                        existingOrder.getItem().remove(item);
                        continue;
                    }
                    line = String.format("%-6s %c %-7s %c %-5s %c %d", customer.getCustomerType(), '|',
                            customerId, '|', menuId, '|', totalQuantity);
                    for (OrderItem orderItem : existingOrder.getItem()) {
                        if (orderItem.equals(item)) {
                            orderItem.setQuantity(totalQuantity);
                            break;
                        }
                    }
                    System.out.println("REMOVE_FROM_CART SUCCESS: " + customerMenu.getMenuName()
                            + " QUANTITY IS DECREMENTED");
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
    }

    public void printDetails(String input) {
        String[] printSplit = input.split(" ", 2);
        String customerId = printSplit[1];
        boolean customerIdCheck = true;
        Customer customerOrder = new Guest(customerId);
        String orderNumber = "";
        String orderDate = "";
        if (!orderHistory.isEmpty()) {
            for(HistoryData history : orderHistory){
                if(history.getCustomerId().equals(customerId)){
                    orderNumber = String.valueOf(history.getOrderNumber());
                    orderDate = history.getOrderDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    LocalDate date = LocalDate.parse(orderDate, formatter);
                    formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
                    orderDate = date.format(formatter);
                }
            }
        }
        for (Customer getCustomer : listCustomer) {
            if (getCustomer.getCustomerId().equals(customerId)) {
                customerOrder = getCustomer;
                break;
            }
        }
        for (Order order : orderList) {
            if (order.getCustomer().equals(customerOrder)) {
                if (customerIdCheck) {
                    System.out.println("Customer ID: " + customerOrder.getCustomerId());
                    System.out.println("Name: " + customerOrder.getFullName());
                    System.out.println("Order Number: " + orderNumber);
                    System.out.println("Order Date: " + orderDate);
                    System.out.printf("%3s | %-20s | %3s | %8s \n", "No", "Menu", "Qty", "Subtotal");
                    System.out.print("=".repeat(50) + "\n");
                    customerIdCheck = false;
                }
                int i = 1;
                for (OrderItem item : order.getItem()) {
                    System.out.printf("%c %-3d %-23s %-5d %d\n", ' ',
                            i++, item.getMenu().getMenuName(), item.getQuantity(),
                            item.getSubPrice());
                }
                System.out.print("=".repeat(50) + "\n");
                System.out.printf("%-26s %-8c %d\n", "Total", ':', order.getSubTotalFoodCost());

                if (order.getAppliedPromo() != null && order.getAppliedPromo() instanceof PercentOffPromo) {
                    Promotion customerPromo = order.getAppliedPromo();
                    double totalDiscount = customerPromo.totalDiscount(order);
                    order.setTotalDiscount(totalDiscount);
                    System.out.printf("%-6s %-19s %-8c %.0f\n", "Promo:", customerPromo.getPromoCode(), ':',
                            totalDiscount);
                }

                System.out.printf("%-26s %-8c %d\n", "Shipping cost", ':', shippingCost);

                if (order.getAppliedPromo() != null && order.getAppliedPromo() instanceof FreeShippingPromo) {
                    Promotion customerPromo = order.getAppliedPromo();
                    double totalDiscount = customerPromo.totalDiscount(order);
                    order.setTotalDiscount(totalDiscount);
                    System.out.printf("%-6s %-19s %-8c %.0f\n", "Promo:", customerPromo.getPromoCode(), ':',
                            totalDiscount);
                }

                System.out.print("=".repeat(50) + "\n");
                System.out.printf("%-26s %-8c %.0f\n", "Total", ':', order.getTotalPrice());

                if (order.getAppliedPromo() != null && order.getAppliedPromo() instanceof CashbackPromo) {
                    Promotion customerPromo = order.getAppliedPromo();
                    double totalDiscount = customerPromo.totalDiscount(order);
                    order.setTotalDiscount(totalDiscount);
                    System.out.printf("%-6s %-19s %-8c %.0f\n", "Promo:", customerPromo.getPromoCode(), ':',
                            totalDiscount);
                }

                System.out.printf("%-26s %-8c %s\n", "Balance", ':', customerOrder.getOpeningBalance());
                break;
            }
        }
    }

    @Override
    public void loadCart() throws Exception {
        String filePath = "src/DataRestaurant/Order.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty())
                    continue;

                String[] columns = line.split("\\|");

                String customerId = columns[1].trim();
                String menuId = columns[2].trim();
                String quantity = columns[3].trim();

                Customer customer = listCustomer.stream().filter(p -> p.getCustomerId().equals(customerId))
                        .findFirst().orElse(new Guest(customerId));
                Menu menu = listMenu.stream().filter(m -> m.getMenuId().equals(menuId)).findFirst()
                        .orElse(new Menu(menuId));
                OrderItem item = new OrderItem(menu, Integer.parseInt(quantity));

                Order existingOrder = orderList.stream().filter(order -> order.getCustomer().equals(customer))
                        .findFirst()
                        .orElseGet(() -> {
                            Order newOrder = new Order(customer, new LinkedHashSet<>(), null);
                            orderList.add(newOrder);
                            return newOrder;
                        });
                existingOrder.getItem().add(item);
            }
        }
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customer == null) ? 0 : customer.hashCode());
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
        if (customer == null) {
            if (other.customer != null)
                return false;
        } else if (!customer.equals(other.customer))
            return false;
        return true;
    }
}