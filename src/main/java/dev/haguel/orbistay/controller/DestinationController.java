package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.response.GetPopularDestinationsResponseDTO;
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
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetPopularDestinationsResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(EndPoints.Destinations.GET_POPULAR_DESTINATIONS)
    public ResponseEntity<?> getPopularDestinations() {
        log.info("Get popular destinations request received");
        List<GetPopularDestinationsResponseDTO> popularDestinations = destinationService.getPopularDestinations();

        log.info("Popular destinations returned");
        return ResponseEntity.status(200).body(popularDestinations);
    }
}
