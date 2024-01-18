package pl.piomin.azure.functions.account.model;

public class Customer {
    public static final String CUSTOMER_STATUS_NO_ACC = "NO_ACCOUNT";
    public static final String CUSTOMER_STATUS_ACC_ACTIVE = "ACCOUNT_ACTIVE";

    private Long id;
    private String name;
    private int age;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", status='" + status + '\'' +
                '}';
    }
}
