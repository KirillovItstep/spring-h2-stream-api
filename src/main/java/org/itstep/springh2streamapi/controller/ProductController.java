package org.itstep.springh2streamapi.controller;

import org.itstep.springh2streamapi.model.Customer;
import org.itstep.springh2streamapi.model.Order;
import org.itstep.springh2streamapi.model.Product;
import org.itstep.springh2streamapi.repo.CustomerRepo;
import org.itstep.springh2streamapi.repo.OrderRepo;
import org.itstep.springh2streamapi.repo.ProductRepo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
public class ProductController {
    @Autowired
    ProductRepo productRepo;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    CustomerRepo customerRepo;

    @GetMapping("/products/1")
    public  List<String> products1() {
        //Obtain a list of product with category = "Books" and price > 100
        List<String> result = productRepo.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Books"))
                .filter(p -> p.getPrice() > 100)
                .map(p-> getProductString(p.getName(),p.getCategory(),p.getPrice()))
                .collect(Collectors.toList());
       return result;
    }

    @GetMapping("/products/2")
    public  List<String> products2() {
        //Obtain a list of product with category = "Books" and price > 100 (using Predicate for filter)
        Predicate<Product> categoryFilter = product -> product.getCategory().equalsIgnoreCase("Books");
        Predicate<Product> priceFilter = product -> product.getPrice() > 100;

        List<String> result = productRepo.findAll()
                .stream()
                .filter(product -> categoryFilter.and(priceFilter).test(product))
                .map(p-> getProductString(p.getName(),p.getCategory(),p.getPrice()))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/products/3")
    public  List<String> products3() {
        //Obtain a list of product with category = "Books" and price > 100 (using BiPredicate for filter)
        BiPredicate<Product, String> categoryFilter = (product, category) -> product.getCategory().equalsIgnoreCase(category);

        List<String> result = productRepo.findAll()
                .stream()
                .filter(product -> categoryFilter.test(product, "Books") && product.getPrice() > 100)
                .map(p-> getProductString(p.getName(),p.getCategory(),p.getPrice()))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/orders/1")
    public  List<String> orders1() {
        // Obtain a list of order with product category = Baby
        List<String> result = orderRepo.findAll()
                .stream()
                .filter(o ->
                        o.getProducts()
                                .stream()
                                .anyMatch(p -> p.getCategory().equalsIgnoreCase("Baby"))
                )
                .map(o-> getOrderString(o.getOrderDate(), o.getDeliveryDate(), o.getStatus(), o.getCustomer().getName(), o.getProducts()))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/products/4")
    public  List<String> products4() {
        //Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021
        List<String> result = orderRepo.findAll()
                .stream()
                .filter(o -> o.getCustomer().getTier() == 2)
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 2, 1)) >= 0)
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 4, 1)) <= 0)
                .flatMap(o ->  o.getProducts().stream())
                .distinct()
                .map(p-> getProductString(p.getName(),p.getCategory(),p.getPrice()))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/products/5")
    public  List<String> products5() {
        //Get the 3 cheapest products of "Books" category
        List<String> result = productRepo.findAll()
                             .stream()
                             .filter(p -> p.getCategory().equalsIgnoreCase("Books"))
                            //.sorted((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()))
                            .sorted(Comparator.comparing(Product::getPrice))
                            .limit(3)
                             .map(p-> getProductString(p.getName(),p.getCategory(),p.getPrice()))
                            .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/orders/2")
    public  List<String> orders2() {
        // Get the 3 most recent placed order
        List<String> result = orderRepo.findAll()
                .stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .limit(3)
                .map(o-> getOrderString(o.getOrderDate(), o.getDeliveryDate(), o.getStatus(), o.getCustomer().getName(), o.getProducts()))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/products/6")
    public  List<String> products6() {
        // Get the 3 most recent placed order
        List<String> result = orderRepo.findAll()
                .stream()
                .filter(o -> o.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
                .peek(o -> System.out.println(o.toString()))
                .flatMap(o -> o.getProducts().stream())
                .distinct()
                .map(p-> getProductString(p.getName(),p.getCategory(),p.getPrice()))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/orders/3")
    public   Map<Long, Object> orders3() {
        // Obtain a mapping of order id and the order's product count
        Map<Long, Object> result = orderRepo.findAll()
                .stream()
                .collect(
                        Collectors.toMap(
                                Order::getId,
                                order -> order.getProducts().size())
                );
        return result;
    }

    @GetMapping("/products/7")
    public   DoubleSummaryStatistics products7() {
        // Obtain statistics summary of all products belong to "Books" category
        DoubleSummaryStatistics statistics = productRepo.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Books"))
                .mapToDouble(Product::getPrice)
                .summaryStatistics();
        return statistics;
    }

    @GetMapping("/orders/4")
    public   Map<Long, Integer> orders4() {
        // Obtain a mapping of order id and the order's product count
        Map<Long, Integer>  result = orderRepo.findAll()
                .stream()
                .collect(
                        Collectors.toMap(
                                Order::getId,
                                order -> order.getProducts().size())
                );
        return result;
    }

    @GetMapping("/customers/1")
    public   Map<Customer, List<Order>> customers1() {
        //Obtain a data map of customer and list of orders
        Map<Customer, List<Order>> result = orderRepo.findAll()
                .stream()
                .collect(Collectors.groupingBy(Order::getCustomer));
        return result;
    }

    @GetMapping("/customers/2")
    public HashMap<Long, List<Long>> customers2() {
        //Obtain a data map of customer_id and list of order_id(s)
        HashMap<Long, List<Long>> result = orderRepo.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                order -> order.getCustomer().getId(),
                                HashMap::new,
                                Collectors.mapping(Order::getId, Collectors.toList())));
        return result;
    }

    @GetMapping("/orders/5")
    public Map<Order, Double> orders5() {
        //Obtain a data map with order and its total price
        Map<Order, Double> result = orderRepo.findAll()
                .stream()
                .collect(
                        Collectors.toMap(
                                Function.identity(),
                                order -> order.getProducts().stream()
                                        .mapToDouble(Product::getPrice).sum())
                );
        return result;
    }

    @GetMapping("/orders/6")
    public Map<Long, Double> orders6() {
        //Obtain a data map with order and its total price (using reduce)
        Map<Long, Double> result = orderRepo.findAll()
                .stream()
                .collect(
                        Collectors.toMap(
                                Order::getId,
                                order -> order.getProducts().stream()
                                        .reduce(0D, (acc, product) -> acc + product.getPrice(), Double::sum)
                        ));
        return result;
    }

    @GetMapping("/products/8")
    public Map<String, List<String>> products8() {
        //Obtain a data map of product name by category
        Map<String, List<String>> result = productRepo.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Product::getCategory,
                                Collectors.mapping(Product::getName, Collectors.toList()))
                );
        return result;
    }

    @GetMapping("/products/9")
    public Map<String, Optional<Product>> products9() {
        //Get the most expensive product per category
        Map<String, Optional<Product>> result = productRepo.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Product::getCategory,
                                Collectors.maxBy(Comparator.comparing(Product::getPrice)))
                );
        return result;
    }

    @GetMapping("/products/10")
    public  Map<String, String> products10() {
        //Get the most expensive product (by name) per category
        Map<String, String> result = productRepo.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Product::getCategory,
                                Collectors.collectingAndThen(
                                        Collectors.maxBy(Comparator.comparingDouble(Product::getPrice)),
                                        optionalProduct -> optionalProduct.map(Product::getName).orElse(null)
                                )
                        ));
        return result;
    }

    public String getProductString(String name, String category, Double price) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("name", name);
        jSONObject.put("category", category);
        jSONObject.put("price", price);
        return jSONObject.toString().replaceAll("\\\"", "");
    }

    public String getOrderString(LocalDate orderDate, LocalDate deliveryDate, String status, String customerName, Set<Product> products) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("orderDate", orderDate);
        jSONObject.put("deliveryDate", deliveryDate);
        jSONObject.put("status", status);
        jSONObject.put("customer", customerName);
        JSONArray jsonArray = new JSONArray();
        for (Product product: products) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", product.getName());
            jsonObject.put("category", product.getCategory());
            jsonObject.put("price", product.getPrice());
            jsonArray.put(jsonObject);
        }
        jSONObject.put("products",(Object)jsonArray);
        return jSONObject.toString().replaceAll("\\\"", "");
    }

}
