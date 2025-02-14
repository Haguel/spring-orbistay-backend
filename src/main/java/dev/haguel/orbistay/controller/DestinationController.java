package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.response.GetDestinationsResponseDTO;
import dev.haguel.orbistay.exception.error.ErrorResponse;
import dev.haguel.orbistay.service.DestinationService;
import dev.haguel.orbistay.util.EndPoints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Destination")
public class DestinationController {
    private final DestinationService destinationService;

    @Operation(summary = "Get popular destinations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Popular destinations found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetDestinationsResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(EndPoints.Destinations.GET_POPULAR_DESTINATIONS)
    public ResponseEntity<?> getPopularDestinations() {
        log.info("Get popular destinations request received");
        List<GetDestinationsResponseDTO> popularDestinations = destinationService.getPopularDestinations();

        log.info("Popular destinations returned");
        return ResponseEntity.status(200).body(popularDestinations);
    }

    @Operation(summary = "Get destinations similar to text")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Similar destinations found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetDestinationsResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(EndPoints.Destinations.GET_DESTINATIONS_SIMILAR_TO_TEXT)
    public ResponseEntity<?> getDestinationsSimilarToText(@RequestParam String text) {
        log.info("Get destinations similar to '{}' request received", text);
        List<GetDestinationsResponseDTO> similarDestinations = destinationService.getDestinationsSimilarToText(text);

        log.info("Similar destinations to '{}' returned", text);
        return ResponseEntity.status(200).body(similarDestinations);
    }
}
