package com.example.inventoryapp;

public class Book {
    private String ProductName;
    private String Price;
    private String Quantity;
    private String SupplierName;
    private String SupplierPhoneNumber;

    public Book(String productName, String price, String quantity, String supplierName, String supplierPhoneNumber) {
        ProductName = productName;
        Price = price;
        Quantity = quantity;
        SupplierName = supplierName;
        SupplierPhoneNumber = supplierPhoneNumber;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public Double getPrice() {
        try {
            return Double.parseDouble(Price);
        }
        catch (Exception e){
            return null;
        }
    }

    public void setPrice(String price) {
        Price = price;
    }

    public int getQuantity() {

        int bookQuantity = 0;
        if(!Quantity.isEmpty())
        {
            bookQuantity = Integer.parseInt(Quantity);
        }
        return bookQuantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getSupplierName() {
        return SupplierName;
    }

    public void setSupplierName(String supplierName) {
        SupplierName = supplierName;
    }

    public String getSupplierPhoneNumber() {
        return SupplierPhoneNumber;
    }

    public void setSupplierPhoneNumber(String supplierPhoneNumber) {
        SupplierPhoneNumber = supplierPhoneNumber;
    }
}
