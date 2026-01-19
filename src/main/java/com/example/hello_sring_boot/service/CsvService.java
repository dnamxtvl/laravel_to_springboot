package com.example.hello_sring_boot.service;

import com.example.hello_sring_boot.dto.csv.UserCsvRepresentation;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvService {
    public List<UserCsvRepresentation> parseCsv(MultipartFile file) throws Exception {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            // create csv bean reader
            CsvToBean<UserCsvRepresentation> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(UserCsvRepresentation.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            // parse the CSV file and return the list of beans
            return csvToBean.parse();
        }
    }
}
