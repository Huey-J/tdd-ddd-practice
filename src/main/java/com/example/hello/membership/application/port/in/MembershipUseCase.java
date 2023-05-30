package com.example.hello.membership.application.port.in;

import com.example.hello.membership.adapter.in.response.MembershipResponseDTO;
import com.example.hello.membership.adapter.in.response.MembershipResponseDetailDTO;
import com.example.hello.membership.domain.code.MembershipType;
import java.util.List;

public interface MembershipUseCase {

  MembershipResponseDTO addMembership(final String userId, final MembershipType membershipType, final Integer point);

  List<MembershipResponseDetailDTO> getMembershipList(final String userId);

  MembershipResponseDetailDTO getMembership(final Long id, final String userId);

}
