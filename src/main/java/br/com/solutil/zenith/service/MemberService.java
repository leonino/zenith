package br.com.solutil.zenith.service;

import br.com.solutil.zenith.dto.MemberDTO;
import br.com.solutil.zenith.model.Member;
import br.com.solutil.zenith.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public MemberDTO create(MemberDTO memberDTO) {
        if (memberRepository.existsByCim(memberDTO.getCim())) {
            throw new IllegalArgumentException("CIM já cadastrado");
        }
        if (memberRepository.existsByCpf(memberDTO.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        Member member = new Member();
        BeanUtils.copyProperties(memberDTO, member);
        member = memberRepository.save(member);
        
        BeanUtils.copyProperties(member, memberDTO);
        return memberDTO;
    }

    public MemberDTO findById(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Membro não encontrado"));
        
        MemberDTO memberDTO = new MemberDTO();
        BeanUtils.copyProperties(member, memberDTO);
        return memberDTO;
    }

    public List<MemberDTO> findAll() {
        return memberRepository.findAll().stream()
            .map(member -> {
                MemberDTO dto = new MemberDTO();
                BeanUtils.copyProperties(member, dto);
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public MemberDTO update(Long id, MemberDTO memberDTO) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Membro não encontrado"));

        // Verifica se o CIM já existe para outro membro
        if (!member.getCim().equals(memberDTO.getCim()) && 
            memberRepository.existsByCim(memberDTO.getCim())) {
            throw new IllegalArgumentException("CIM já cadastrado");
        }

        // Verifica se o CPF já existe para outro membro
        if (!member.getCpf().equals(memberDTO.getCpf()) && 
            memberRepository.existsByCpf(memberDTO.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        BeanUtils.copyProperties(memberDTO, member, "id");
        member = memberRepository.save(member);
        
        BeanUtils.copyProperties(member, memberDTO);
        return memberDTO;
    }

    @Transactional
    public void delete(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Membro não encontrado"));
        memberRepository.delete(member);
    }

    public List<MemberDTO> findByAtivo(boolean ativo) {
        return memberRepository.findByAtivo(ativo).stream()
            .map(member -> {
                MemberDTO dto = new MemberDTO();
                BeanUtils.copyProperties(member, dto);
                return dto;
            })
            .collect(Collectors.toList());
    }
}
