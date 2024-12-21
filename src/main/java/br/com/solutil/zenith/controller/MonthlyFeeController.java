package br.com.solutil.zenith.controller;

import br.com.solutil.zenith.dto.MonthlyFeeRequest;
import br.com.solutil.zenith.dto.MonthlyFeeResponse;
import br.com.solutil.zenith.security.services.UserDetailsImpl;
import br.com.solutil.zenith.service.MonthlyFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/monthly-fees")
public class MonthlyFeeController {

    @Autowired
    private MonthlyFeeService monthlyFeeService;

    @PostMapping
    @PreAuthorize("hasRole('TREASURER')")
    public ResponseEntity<MonthlyFeeResponse> createMonthlyFee(@RequestBody MonthlyFeeRequest request) {
        MonthlyFeeResponse response = monthlyFeeService.createMonthlyFee(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TREASURER')")
    public ResponseEntity<MonthlyFeeResponse> updateMonthlyFee(
            @PathVariable Long id,
            @RequestBody MonthlyFeeRequest request) {
        MonthlyFeeResponse response = monthlyFeeService.updateMonthlyFee(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TREASURER')")
    public ResponseEntity<Void> deleteMonthlyFee(@PathVariable Long id) {
        monthlyFeeService.deleteMonthlyFee(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/payment")
    @PreAuthorize("hasRole('TREASURER')")
    public ResponseEntity<MonthlyFeeResponse> processPayment(
            @PathVariable Long id,
            @RequestParam String paymentMethod) {
        MonthlyFeeResponse response = monthlyFeeService.processPayment(id, paymentMethod);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TREASURER', 'ADMIN', 'USER')")
    public ResponseEntity<List<MonthlyFeeResponse>> getAllMonthlyFees() {
        UserDetailsImpl auth = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (auth != null && auth.getMemberId() != null) {
            List<MonthlyFeeResponse> responses = monthlyFeeService.getAllMonthlyFees(auth.getMemberId());
            return ResponseEntity.ok(responses);
        }
        throw new RuntimeException("Current user not found");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TREASURER', 'ADMIN', 'USER')")
    public ResponseEntity<MonthlyFeeResponse> getMonthlyFeeById(@PathVariable Long id) {
        MonthlyFeeResponse response = monthlyFeeService.getMonthlyFeeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasAnyRole('TREASURER', 'ADMIN', 'USER')")
    public ResponseEntity<List<MonthlyFeeResponse>> getMonthlyFeesByMember(@PathVariable Long memberId) {
        List<MonthlyFeeResponse> responses = monthlyFeeService.getMonthlyFeesByMember(memberId);
        return ResponseEntity.ok(responses);
    }
}
