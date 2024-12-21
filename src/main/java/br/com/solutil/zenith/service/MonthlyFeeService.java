package br.com.solutil.zenith.service;

import br.com.solutil.zenith.dto.MonthlyFeeRequest;
import br.com.solutil.zenith.dto.MonthlyFeeResponse;
import br.com.solutil.zenith.model.MonthlyFee;
import br.com.solutil.zenith.model.User;
import br.com.solutil.zenith.repository.MonthlyFeeRepository;
import br.com.solutil.zenith.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MonthlyFeeService {

    @Autowired
    private MonthlyFeeRepository monthlyFeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public MonthlyFeeResponse createMonthlyFee(MonthlyFeeRequest request) {
        User currentUser = getCurrentUser();
        if (!hasRole(currentUser, "ROLE_TREASURER")) {
            throw new AccessDeniedException("Only treasurers can create monthly fees");
        }

        User member = userRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        MonthlyFee monthlyFee = new MonthlyFee();
        monthlyFee.setMember(member);
        monthlyFee.setDueDate(request.getDueDate());
        monthlyFee.setAmount(request.getAmount());
        monthlyFee.setReferenceMonth(request.getReferenceMonth());
        monthlyFee.setStatus("PENDING");
        monthlyFee.setNotes(request.getNotes());
        monthlyFee.setCreatedBy(currentUser);
        monthlyFee.setCreatedAt(LocalDate.now());

        MonthlyFee saved = monthlyFeeRepository.save(monthlyFee);
        return convertToResponse(saved);
    }

    @Transactional
    public MonthlyFeeResponse updateMonthlyFee(Long id, MonthlyFeeRequest request) {
        User currentUser = getCurrentUser();
        if (!hasRole(currentUser, "ROLE_TREASURER")) {
            throw new AccessDeniedException("Only treasurers can update monthly fees");
        }

        MonthlyFee monthlyFee = monthlyFeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Monthly fee not found"));

        User member = userRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        monthlyFee.setMember(member);
        monthlyFee.setDueDate(request.getDueDate());
        monthlyFee.setAmount(request.getAmount());
        monthlyFee.setReferenceMonth(request.getReferenceMonth());
        monthlyFee.setNotes(request.getNotes());
        monthlyFee.setLastModifiedBy(currentUser);
        monthlyFee.setLastModifiedAt(LocalDate.now());

        MonthlyFee updated = monthlyFeeRepository.save(monthlyFee);
        return convertToResponse(updated);
    }

    @Transactional
    public void deleteMonthlyFee(Long id) {
        User currentUser = getCurrentUser();
        if (!hasRole(currentUser, "ROLE_TREASURER")) {
            throw new AccessDeniedException("Only treasurers can delete monthly fees");
        }

        monthlyFeeRepository.deleteById(id);
    }

    @Transactional
    public MonthlyFeeResponse processPayment(Long id, String paymentMethod) {
        User currentUser = getCurrentUser();
        if (!hasRole(currentUser, "ROLE_TREASURER")) {
            throw new AccessDeniedException("Only treasurers can process payments");
        }

        MonthlyFee monthlyFee = monthlyFeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Monthly fee not found"));

        monthlyFee.setStatus("PAID");
        monthlyFee.setPaymentDate(LocalDate.now());
        monthlyFee.setPaymentMethod(paymentMethod);
        monthlyFee.setLastModifiedBy(currentUser);
        monthlyFee.setLastModifiedAt(LocalDate.now());

        MonthlyFee updated = monthlyFeeRepository.save(monthlyFee);
        return convertToResponse(updated);
    }

    public List<MonthlyFeeResponse> getAllMonthlyFees(Long memberId) {
        return monthlyFeeRepository.findAllByMemberId(memberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public MonthlyFeeResponse getMonthlyFeeById(Long id) {
        MonthlyFee monthlyFee = monthlyFeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Monthly fee not found"));
        return convertToResponse(monthlyFee);
    }

    public List<MonthlyFeeResponse> getMonthlyFeesByMember(Long memberId) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        return monthlyFeeRepository.findByMember(member).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private MonthlyFeeResponse convertToResponse(MonthlyFee monthlyFee) {
        MonthlyFeeResponse response = new MonthlyFeeResponse();
        response.setId(monthlyFee.getId());
        response.setMemberId(monthlyFee.getMember().getId());
        response.setMemberName(monthlyFee.getMember().getUsername());
        response.setDueDate(monthlyFee.getDueDate());
        response.setAmount(monthlyFee.getAmount());
        response.setReferenceMonth(monthlyFee.getReferenceMonth());
        response.setStatus(monthlyFee.getStatus());
        response.setPaymentDate(monthlyFee.getPaymentDate());
        response.setPaymentMethod(monthlyFee.getPaymentMethod());
        response.setNotes(monthlyFee.getNotes());
        return response;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    private boolean hasRole(User user, String role) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getName().toString().equals(role));
    }
}
