package com.menditech.bank.account.controller;

import com.menditech.bank.account.dto.common.ApiResponse;
import com.menditech.bank.account.dto.request.ReportRequest;
import com.menditech.bank.account.dto.response.StatementReportResponse;
import com.menditech.bank.account.service.StatementService;
import com.menditech.bank.account.util.ApiResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final StatementService statementService;

    @PostMapping
    public ResponseEntity<ApiResponse<StatementReportResponse>> generateReport(
            @Valid @RequestBody ReportRequest request
    ) {
        StatementReportResponse response = statementService.generateReport(request);

        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Statement report generated successfully", response)
        );
    }
}
