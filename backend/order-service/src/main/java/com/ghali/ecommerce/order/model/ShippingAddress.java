package com.ghali.ecommerce.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {

    @Column(name = "shipping_full_name", length = 200)
    private String fullName;

    @Column(name = "shipping_address_line1")
    private String addressLine1;

    @Column(name = "shipping_address_line2")
    private String addressLine2;

    @Column(name = "shipping_city", length = 100)
    private String city;

    @Column(name = "shipping_state", length = 100)
    private String state;

    @Column(name = "shipping_zip_code", length = 20)
    private String zipCode;

    @Column(name = "shipping_country", length = 100)
    private String country;

    @Column(name = "shipping_phone", length = 20)
    private String phone;
}
