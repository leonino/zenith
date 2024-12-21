package br.com.solutil.zenith.repository;

import br.com.solutil.zenith.model.MonthlyFee;
import br.com.solutil.zenith.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MonthlyFeeRepository extends JpaRepository<MonthlyFee, Long> {
    List<MonthlyFee> findByMember(User member);
    List<MonthlyFee> findByStatus(String status);
    List<MonthlyFee> findByDueDateBetween(LocalDate startDate, LocalDate endDate);
    List<MonthlyFee> findByReferenceMonth(String referenceMonth);
    List<MonthlyFee> findAllByMemberId(Long memberId);
}
