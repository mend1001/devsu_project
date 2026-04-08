package com.menditech.bank.account.service;

import com.menditech.bank.account.dto.request.ReportRequest;
import com.menditech.bank.account.dto.response.StatementReportResponse;

public interface StatementService {
    StatementReportResponse generateReport(ReportRequest request);
}
