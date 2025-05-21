package io.hhplus.tdd.point;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Code {
  USER_ID_NOT_FOUND("USER_ID_NOT_FOUND","사용자 아이디가 유효하지 않습니다."),
  POINT_MIN("POINT_MIN", "0보다 작은 포인트는 적립할 수 없습니다."),
  POINT_LIMIT("POINT_LIMIT","보유한 포인트가 사용하고자 하는 포인트보다 적습니다."),
  POINT_MAX("POINT_MAX", "최대 보유 가능 포인트는 총 10,000포인트 입니다.");

  private final String code;
  private final String message;
}
