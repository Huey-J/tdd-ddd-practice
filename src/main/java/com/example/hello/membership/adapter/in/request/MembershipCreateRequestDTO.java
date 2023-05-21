package com.example.hello.membership.adapter.in.request;

import com.example.hello.membership.domain.code.MembershipType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)    // 요청 그대로 쓸 것이기 때문에 final 선언 후 NoArgs(force)
public class MembershipCreateRequestDTO {

  @NotNull
  @Min(0)
  private final Integer point;

  @NotNull
  private final MembershipType membershipType;

}
