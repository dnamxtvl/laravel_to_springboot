package com.example.hello_sring_boot.dto.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCsvRepresentation {
    @CsvBindByName(column = "First Name")
    private String firstName;

    @CsvBindByName(column = "Last Name")
    private String lastName;

    @CsvBindByName(column = "Age")
    private int email;

    @CsvBindByName(column = "Phone Number")
    private String phoneNumber;
}
