package com.teamb.payment.models;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.teamb.common.models.PaymentStatus;
import com.teamb.donor.models.Donor;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("payments")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Min(0)
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private Double amount;

    private Date paymentDate;

    private PaymentStatus paymentStatus;

    @NonNull
    private String donationId;

    @DBRef
    @NonNull
    private Donor donorId;

    private Date updatedAt;
}
