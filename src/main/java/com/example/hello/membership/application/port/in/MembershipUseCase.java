package com.example.hello.membership.application.port.in;

import com.example.hello.membership.adapter.in.response.MembershipResponseDTO;
import com.example.hello.membership.domain.code.MembershipType;

public interface MembershipUseCase {

  MembershipResponseDTO addMembership(final String userId, final MembershipType membershipType, final Integer point);

}
