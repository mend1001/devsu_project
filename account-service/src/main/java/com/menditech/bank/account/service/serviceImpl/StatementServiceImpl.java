package com.menditech.bank.account.service.serviceImpl;

import com.menditech.bank.account.dto.request.ReportRequest;
import com.menditech.bank.account.dto.response.StatementReportResponse;
import java.time.LocalDate;

public interface StatementServiceImpl {
    StatementReportResponse generateReport(ReportRequest request);
}
