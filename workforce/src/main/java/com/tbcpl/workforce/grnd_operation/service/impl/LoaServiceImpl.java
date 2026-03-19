package com.tbcpl.workforce.grnd_operation.service.impl;

import com.tbcpl.workforce.admin.entity.Client;
import com.tbcpl.workforce.admin.repository.ClientRepository;
import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.util.LoaPdfGeneratorUtil;
import com.tbcpl.workforce.grnd_operation.dto.request.LoaRequestDto;
import com.tbcpl.workforce.grnd_operation.dto.response.ClientDropdownDto;
import com.tbcpl.workforce.grnd_operation.dto.response.EmployeeDropdownDto;
import com.tbcpl.workforce.grnd_operation.dto.response.LoaResponseDto;
import com.tbcpl.workforce.grnd_operation.entity.Loa;
import com.tbcpl.workforce.grnd_operation.entity.LoaAssets;
import com.tbcpl.workforce.grnd_operation.entity.enums.LoaStatus;
import com.tbcpl.workforce.grnd_operation.repository.LoaAssetsRepository;
import com.tbcpl.workforce.grnd_operation.repository.LoaRepository;
import com.tbcpl.workforce.grnd_operation.service.LoaService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoaServiceImpl implements LoaService {

    private final LoaRepository       loaRepository;
    private final LoaAssetsRepository loaAssetsRepository;
    private final EmployeeRepository  employeeRepository;
    private final ClientRepository    clientRepository;
    private final JavaMailSender      mailSender;
    private final LoaPdfGeneratorUtil pdfGeneratorUtil;

    private static final String DEPT_ADMIN       = "DEPARTMENT_ADMIN";
    private static final String DEPT_OPERATION   = "DEPARTMENT_OPERATION";
    private static final String ROLE_ADMIN       = "ROLE_ADMIN";
    private static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    private static final String ROLE_MANAGER     = "ROLE_MANAGER";
    private static final String ROLE_ASSOCIATE   = "ROLE_ASSOCIATE";
    private static final String FIELD_ASSOC_ROLE = "FIELD_ASSOCIATE";

    // ─── Dropdowns ──────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDropdownDto> getFieldAssociateDropdown() {
        requireAdminDeptAndAdminRole();
        return employeeRepository.findActiveEmployeesByRoleName(FIELD_ASSOC_ROLE)
                .stream()
                .map(e -> EmployeeDropdownDto.builder()
                        .id(e.getId())
                        .empId(e.getEmpId())
                        .fullName(e.getFullName())
                        .email(e.getEmail())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDropdownDto> getClientDropdown() {
        requireAdminDeptAndAdminRole();
        return clientRepository.findAllByDeletedFalseOrderByClientNameAsc()
                .stream()
                .map(c -> ClientDropdownDto.builder()
                        .clientId(c.getClientId())
                        .clientName(c.getClientName())
                        .build())
                .toList();
    }

    // ─── Create ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public LoaResponseDto createLoa(LoaRequestDto request) {
        requireAdminDeptAndAdminRole();

        Employee employee = resolveFieldAssociate(request.getEmployeeId());
        Client   client   = resolveActiveClient(request.getClientId());

        Loa loa = Loa.builder()
                .employeeId(employee.getId())
                .employeeName(employee.getFullName())
                .employeeEmail(employee.getEmail())
                .clientId(client.getClientId())
                .clientName(client.getClientName())
                .validUpto(request.getValidUpto())
                .status(LoaStatus.DRAFT)
                .build();

        Loa saved = loaRepository.save(loa);
        saved.setLoaNumber(buildLoaNumber(client.getClientId(), employee.getEmpId(), saved.getId()));
        saved = loaRepository.save(saved);

        log.info("LOA created: {} by {}", saved.getLoaNumber(), currentEmpId());
        return toResponseDto(saved);
    }

    // ─── Update ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public LoaResponseDto updateLoa(Long id, LoaRequestDto request) {
        requireAdminDeptAndAdminRole();

        Loa loa = getActiveLoaOrThrow(id);
        if (loa.getStatus() == LoaStatus.FINALIZED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Finalized LOA cannot be edited. Create a new LOA if required.");
        }

        Employee employee = resolveFieldAssociate(request.getEmployeeId());
        Client   client   = resolveActiveClient(request.getClientId());

        boolean keyChanged = !loa.getEmployeeId().equals(employee.getId())
                || !loa.getClientId().equals(client.getClientId());

        loa.setEmployeeId(employee.getId());
        loa.setEmployeeName(employee.getFullName());
        loa.setEmployeeEmail(employee.getEmail());
        loa.setClientId(client.getClientId());
        loa.setClientName(client.getClientName());
        loa.setValidUpto(request.getValidUpto());

        if (keyChanged) {
            loa.setLoaNumber(buildLoaNumber(client.getClientId(), employee.getEmpId(), loa.getId()));
        }

        Loa updated = loaRepository.save(loa);
        log.info("LOA updated: {} by {}", updated.getLoaNumber(), currentEmpId());
        return toResponseDto(updated);
    }

    // ─── Finalize ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public LoaResponseDto finalizeLoa(Long id) {
        requireAdminDeptAndAdminRole();

        Loa loa = getActiveLoaOrThrow(id);
        if (loa.getStatus() == LoaStatus.FINALIZED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "LOA is already finalized.");
        }

        loa.setStatus(LoaStatus.FINALIZED);
        Loa saved = loaRepository.save(loa);
        log.info("LOA finalized: {} by {}", saved.getLoaNumber(), currentEmpId());
        return toResponseDto(saved);
    }

    // ─── Send Mail ──────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public void sendLoaByMail(Long id) {
        requireMailSendAccess();

        Loa loa = getActiveLoaOrThrow(id);
        if (loa.getStatus() != LoaStatus.FINALIZED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "LOA must be finalized before sending via email.");
        }

        LoaAssets assets = loaAssetsRepository.findTopByOrderByIdAsc().orElse(null);
        byte[] pdfBytes  = pdfGeneratorUtil.generateLoaPdf(loa, assets);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(loa.getEmployeeEmail());
            helper.setSubject("Authority Letter – " + loa.getLoaNumber());
            helper.setText(buildMailBody(loa), false);
            helper.addAttachment(
                    loa.getLoaNumber() + ".pdf",
                    new ByteArrayResource(pdfBytes),
                    "application/pdf"
            );
            mailSender.send(message);
            log.info("LOA mail sent: {} → {} by {}",
                    loa.getLoaNumber(), loa.getEmployeeEmail(), currentEmpId());
        } catch (Exception e) {
            log.error("Failed to send LOA mail: {}", loa.getLoaNumber(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to send email. Please try again later.");
        }
    }

    // ─── Preview PDF ────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public byte[] previewLoaPdf(Long id) {
        Loa       loa    = getActiveLoaOrThrow(id);
        LoaAssets assets = loaAssetsRepository.findTopByOrderByIdAsc().orElse(null);
        return pdfGeneratorUtil.generateLoaPdf(loa, assets);
    }

    // ─── List & Get ─────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<LoaResponseDto> getAllLoas(Pageable pageable) {
        return loaRepository.findAllByDeletedFalse(pageable).map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public LoaResponseDto getLoaById(Long id) {
        return toResponseDto(getActiveLoaOrThrow(id));
    }

    // ─── Access Control ─────────────────────────────────────────────────────

    private void requireAdminDeptAndAdminRole() {
        Collection<? extends GrantedAuthority> auth = currentAuthorities();
        boolean deptOk = hasAuthority(auth, DEPT_ADMIN);
        boolean roleOk = hasAuthority(auth, ROLE_ADMIN) || hasAuthority(auth, ROLE_SUPER_ADMIN);
        if (!deptOk || !roleOk) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Access denied. Requires Admin department with Admin or Super Admin role.");
        }
    }

    private void requireMailSendAccess() {
        Collection<? extends GrantedAuthority> auth = currentAuthorities();
        boolean deptOk = hasAuthority(auth, DEPT_ADMIN) || hasAuthority(auth, DEPT_OPERATION);
        boolean roleOk = hasAuthority(auth, ROLE_ASSOCIATE)
                || hasAuthority(auth, ROLE_MANAGER)
                || hasAuthority(auth, ROLE_ADMIN)
                || hasAuthority(auth, ROLE_SUPER_ADMIN);
        if (!deptOk || !roleOk) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Access denied. Requires Admin or Operation department with an eligible role.");
        }
    }

    // ─── Private Helpers ────────────────────────────────────────────────────

    private Employee resolveFieldAssociate(Long employeeId) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Employee not found with id: " + employeeId));
        if (!Boolean.TRUE.equals(emp.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Selected employee is inactive.");
        }
        if (!FIELD_ASSOC_ROLE.equalsIgnoreCase(emp.getRole().getRoleName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only employees with role 'Field Associate' can be assigned to an LOA.");
        }
        return emp;
    }

    private Client resolveActiveClient(Long clientId) {
        return clientRepository.findByClientIdAndDeletedFalse(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Client not found with id: " + clientId));
    }

    private Loa getActiveLoaOrThrow(Long id) {
        return loaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "LOA not found with id: " + id));
    }

    private String buildLoaNumber(Long clientId, String empId, Long loaId) {
        return String.format("LOA-%02d-%s-%04d", clientId, empId, loaId);
    }

    private String buildMailBody(Loa loa) {
        return "Dear " + loa.getEmployeeName() + ",\n\n"
                + "Please find attached your Authority Letter (" + loa.getLoaNumber() + ").\n\n"
                + "This letter authorizes you to act on behalf of True Buddy Consulting Pvt. Ltd. "
                + "for client " + loa.getClientName() + ".\n\n"
                + "This authority letter is valid up to " + loa.getValidUpto() + ".\n\n"
                + "Regards,\nTrue Buddy Consulting Private Limited\ncontact@tbcpl.co.in";
    }

    private LoaResponseDto toResponseDto(Loa loa) {
        return LoaResponseDto.builder()
                .id(loa.getId())
                .loaNumber(loa.getLoaNumber())
                .employeeId(loa.getEmployeeId())
                .employeeName(loa.getEmployeeName())
                .employeeEmail(loa.getEmployeeEmail())
                .clientId(loa.getClientId())
                .clientName(loa.getClientName())
                .validUpto(loa.getValidUpto())
                .status(loa.getStatus())
                .createdAt(loa.getCreatedAt())
                .updatedAt(loa.getUpdatedAt())
                .createdBy(loa.getCreatedBy())
                .build();
    }

    private Collection<? extends GrantedAuthority> currentAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated.");
        }
        return auth.getAuthorities();
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String target) {
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(target));
    }

    private String currentEmpId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
