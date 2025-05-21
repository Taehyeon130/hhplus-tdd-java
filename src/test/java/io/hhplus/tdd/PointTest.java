package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

public class PointTest {
  /*
  * 1. id 정보가 없는 경우
  * 2. id 정보가 있는 경우
  * 3. 포인트 충전/이용내역이 없는 경우
  * 4. 포인트 충전/이용내역 조회
  * 5. 잔고부족
  * 6. 충전 성공
  * 7. 충전 실패
  * 8. 사용 성공
  * 9. 사용 실패
  * */
  @InjectMocks
  private PointService pointService;

  @Mock
  private UserPointTable userPointTable;
  @Mock
  private PointHistoryTable pointHistoryTable;

  @Test
  void selectedUserIdInfo() {
    /*
    * 유저 아이디 정보가 존재하는 경우 유저의 보유 포인트를 리턴
    * */
    long userId = 1L;
    UserPoint point = new UserPoint(userId, 100L, System.currentTimeMillis());
    assertThat(point.id(), is(userId));
  }

  @Test
  void selectedUserIdInfoFail() {
    long userId = 1L;
    UserPoint point = pointService.selectUserPointByUserId(userId);
  }
}
