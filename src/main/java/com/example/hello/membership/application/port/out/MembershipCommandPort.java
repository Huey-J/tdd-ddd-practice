package com.example.hello.membership.application.port.out;

import com.example.hello.membership.domain.Membership;

public interface MembershipCommandPort {

  Membership save(Membership membership);

}
