package com.example.hello.membership.application.port.out;

import com.example.hello.membership.domain.Membership;
import com.example.hello.membership.domain.code.MembershipType;
import java.util.List;

public interface MembershipQueryPort {

  Membership findByUserIdAndMembershipType(String userId, MembershipType membershipType);

  List<Membership> findAllByUserId(String userId);

}
