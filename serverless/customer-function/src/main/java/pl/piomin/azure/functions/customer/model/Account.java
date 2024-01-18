package pl.piomin.azure.functions.customer.model;

public class Account {

    private Long id;
    private String number;
    private Long customerId;
    private int balance;

    public Account() {}

    public Account(String number, Long customerId) {
        this.number = number;
        this.customerId = customerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", customerId=" + customerId +
                ", balance=" + balance +
                '}';
    }
}
