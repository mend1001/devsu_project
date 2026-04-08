package com.menditech.bank.account.controller;

import com.menditech.bank.account.dto.common.ApiCommonResponse;
import com.menditech.bank.account.dto.request.ReportRequest;
import com.menditech.bank.account.dto.response.StatementReportResponse;

import com.menditech.bank.account.service.StatementService;
import com.menditech.bank.account.util.ApiResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Operations related to account statements and transaction reports")
public class ReportController {

    private final StatementService statementService;

    @Operation(
            summary = "Generate account statement report",
            description = "Generates a bank statement report for a client within a specific date range, including account balances and movements."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statement report generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Client or account not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ApiCommonResponse<StatementReportResponse>> generateReport(
            @Parameter(description = "Report generation request body", required = true)
            @Valid @RequestBody ReportRequest request
    ) {

        StatementReportResponse response = statementService.generateReport(request);

        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Statement report generated successfully", response)
        );
    }
}
