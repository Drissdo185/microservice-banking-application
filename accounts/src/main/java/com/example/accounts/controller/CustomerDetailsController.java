package com.example.accounts.controller;


import com.example.accounts.dto.CustomerDetailsDto;
import com.example.accounts.service.CustomerDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Customer Details REST API",
        description = "REST API to fetch complete customer information from all services"
)
@RestController
@RequestMapping("/api/v1/customer-details")
@Validated
@AllArgsConstructor
public class CustomerDetailsController {

    private CustomerDetailsService customerDetailsService;

    @Operation(
            summary = "Fetch Customer Details REST API",
            description = "REST API to fetch complete customer details including accounts, cards, and loans"
    )
    @GetMapping("/fetch")
    public ResponseEntity<CustomerDetailsDto> fetchCustomerDetails(
            @RequestParam @Pattern(regexp="^[0-9]{10}$", message = "Mobile number must be 10 digits")
            String mobileNumber) {
        
        CustomerDetailsDto customerDetailsDto = customerDetailsService.getCustomerDetails(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(customerDetailsDto);
    }
}