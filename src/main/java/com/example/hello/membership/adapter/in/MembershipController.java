package com.example.hello.membership.adapter.in;

import static com.example.hello.common.MembershipConstants.USER_ID_HEADER;

import com.example.hello.membership.adapter.in.request.MembershipCreateRequestDTO;
import com.example.hello.membership.adapter.in.response.MembershipResponseDTO;
import com.example.hello.membership.adapter.in.response.MembershipResponseDetailDTO;
import com.example.hello.membership.application.port.in.MembershipUseCase;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MembershipController {

  private final MembershipUseCase membershipUseCase;

  @PostMapping("/api/v1/memberships")
  public ResponseEntity<MembershipResponseDTO> addMembership(
      @RequestHeader(USER_ID_HEADER) final String userId,
      @RequestBody @Valid final MembershipCreateRequestDTO dto) {
    final MembershipResponseDTO membershipResponseDTO = membershipUseCase.addMembership(userId,
        dto.getMembershipType(), dto.getPoint());
    return ResponseEntity.status(HttpStatus.CREATED).body(membershipResponseDTO);
  }

  @GetMapping("/api/v1/memberships")
  public ResponseEntity<List<MembershipResponseDetailDTO>> getMembershipList(
      @RequestHeader(USER_ID_HEADER) final String userId) {
    return ResponseEntity.ok(membershipUseCase.getMembershipList(userId));
  }

  @GetMapping("/api/v1/memberships/{id}")
  public ResponseEntity<MembershipResponseDetailDTO> getMembership(
      @RequestHeader(USER_ID_HEADER) final String userId,
      @PathVariable final Long id) {
    return ResponseEntity.ok(membershipUseCase.getMembership(id, userId));
  }
}