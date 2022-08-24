package rsp.admin.data.provider;

public record Sort(String fieldName, Order order) {

    public enum Order {
        ACS, DESC
    }
}
