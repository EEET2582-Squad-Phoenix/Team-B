package com.teamb.credit_card.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.mongodb.lang.Nullable;
import com.teamb.charity.models.Charity;
import com.teamb.credit_card.utils.CreditCardUtils;
import com.teamb.donor.models.Donor;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("creditcards")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class CreditCard implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    @DBRef
    private Donor donorId;

    @DBRef
    private Charity charityId;

    @NonNull
    @Transient
    private String number;

    @NonNull
    private String cardHolder;

    @NotEmpty(message = "Expiry date is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Expiry date must be in the format mm/yy")
    private String expiryDate;

    @Nullable
    private String CVV;

    @Transient
    private CreditCardUtils creditCardUtils;

    public String getEncryptedNumber() {
        return creditCardUtils.encryptAsymmetric(number);
    }

    public void setEncryptedNumber(String encryptedNumber) {
        this.number = encryptedNumber;
    }
}
