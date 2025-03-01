package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.entity.Country;
import dev.haguel.orbistay.exception.error.ErrorResponse;
import dev.haguel.orbistay.service.CountryService;
import dev.haguel.orbistay.util.EndPoints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Country")
public class CountryController {
    private final CountryService countryService;

    @Operation(summary = "Get all countries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Countries returned successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Country.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(EndPoints.Countries.GET_ALL_COUNTRIES)
    public ResponseEntity<?> getAllCountries() {
        log.info("Getting all countries request received");

        List<Country> countries = countryService.findAll();

        log.info("Countries returned");
        return ResponseEntity.status(200).body(countries);
    }
}
