package dev.haguel.orbistay.util.mapper;

import dev.haguel.orbistay.entity.Country;
import dev.haguel.orbistay.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SharedMapperUtil {
    private final CountryService countryService;

    public Country getCountryById(String countryId) {
        try {
            return countryService.findById(Long.parseLong(countryId));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public LocalDate convertStringToLocalDate(String date) {
        return LocalDate.parse(date);
    }

    public Double convertStringToDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception exception) {
            return null;
        }
    }
}
