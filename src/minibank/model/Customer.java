package minibank.model;

import minibank.model.annotation.Id;
import minibank.model.annotation.MaxLength;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class representing a bank customer (Practicals 2, 3, 7, 11, 13).
 * Implements Cloneable and Serializable.
 */
public class Customer implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    private static long customerCounter = 100;

    @Id
    private final String customerId;

    @MaxLength(50)
    private String name;

    @MaxLength(100)
    private String email;

    @MaxLength(15)
    private String mobile;

    private Address address;

    /**
     * Private static method to generate unique customer IDs (e.g. CUST101, CUST102).
     */
    private static synchronized String generateCustomerId() {
        return "CUST" + (++customerCounter);
    }

    public Customer(String name, String email, String mobile) {
        this.customerId = generateCustomerId();
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.address = null;
    }

    public Customer(String name, String email, String mobile, Address address) {
        this(name, email, mobile);
        this.address = address;
    }

    // Public getters
    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * Deep copy clone method (Practical 3).
     */
    @Override
    public Customer clone() {
        try {
            Customer copy = (Customer) super.clone();
            if (this.address != null) {
                copy.address = this.address.clone();
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer other)) return false;
        return Objects.equals(customerId, other.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return "Customer[" + customerId + " | " + name + " | " + email + " | " + mobile +
                (address != null ? " | " + address : "") + "]";
    }

    /**
     * Public static nested class representing customer address (Practical 3).
     */
    public static class Address implements Serializable, Cloneable {
        private static final long serialVersionUID = 1L;

        private final String line;
        private final String city;
        private final String pincode;

        public Address(String line, String city, String pincode) {
            this.line = line;
            this.city = city;
            this.pincode = pincode;
        }

        public String getLine() {
            return line;
        }

        public String getCity() {
            return city;
        }

        public String getPincode() {
            return pincode;
        }

        @Override
        public Address clone() {
            try {
                return (Address) super.clone();
            } catch (CloneNotSupportedException e) {
                return new Address(this.line, this.city, this.pincode);
            }
        }

        @Override
        public String toString() {
            return line + ", " + city + " - " + pincode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Address other)) return false;
            return Objects.equals(line, other.line) &&
                   Objects.equals(city, other.city) &&
                   Objects.equals(pincode, other.pincode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(line, city, pincode);
        }
    }
}
