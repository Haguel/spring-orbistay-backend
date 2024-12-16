package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.GetHotelsRequestDTO;
import dev.haguel.orbistay.dto.GetHotelsResponseDTO;
import dev.haguel.orbistay.exception.HotelsNotFoundException;
import dev.haguel.orbistay.exception.error.ErrorResponse;
import dev.haguel.orbistay.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Hotel")
public class HotelController {
    private final HotelService hotelService;

    @Operation(summary = "Get hotels by criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotels found successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetHotelsResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No hotels found for given criteria",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/get")
    public ResponseEntity<?> getHotels(@RequestBody @Valid GetHotelsRequestDTO getHotelsRequestDTO) throws HotelsNotFoundException {
        log.info("Get hotels request received");
        List<GetHotelsResponseDTO> hotels = hotelService.getHotels(getHotelsRequestDTO);

        log.info("Hotels returned");
        return ResponseEntity.status(200).body(hotels);
    }
}
