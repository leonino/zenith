package br.com.solutil.zenith.repository;

import br.com.solutil.zenith.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByCim(String cim);
    Optional<Member> findByCpf(String cpf);
    List<Member> findByAtivo(boolean ativo);
    boolean existsByCim(String cim);
    boolean existsByCpf(String cpf);
}
