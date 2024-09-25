package com.braimanm.datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

@XStreamAlias("person")
public class Person extends DataPersistence {
    public String firstName;
    public String lastName;
    public String fullName;
    public int age;
    public String todayDate;
    public String now;
    public Address workAddress;
    public List<Address> knownAddresses;

    @XStreamAlias("address")
    public static class Address {
        public String streetNumber;
        public String streetName;
        public String postalCode;
        public String province;
        public String city;
        public String country;
    }




}
