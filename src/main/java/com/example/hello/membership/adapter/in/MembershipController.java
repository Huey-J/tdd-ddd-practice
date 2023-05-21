package com.example.hello.membership.adapter.in;

import static com.example.hello.membership.adapter.in.MembershipConstants.USER_ID_HEADER;

import com.example.hello.membership.adapter.in.request.MembershipCreateRequestDTO;
import com.example.hello.membership.adapter.in.response.MembershipResponse;
import com.example.hello.membership.application.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MembershipController {

  private final MembershipService membershipService;

  @PostMapping("/api/v1/memberships")
  public ResponseEntity<MembershipResponse> addMembership(
      @RequestHeader(USER_ID_HEADER) final String userId,
      @RequestBody @Valid final MembershipCreateRequestDTO dto) {
    final MembershipResponse membershipResponse = membershipService.addMembership(userId,
        dto.getMembershipType(), dto.getPoint());
    return ResponseEntity.status(HttpStatus.CREATED).body(membershipResponse);
  }

}