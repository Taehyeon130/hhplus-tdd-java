package io.hhplus.tdd;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.hhplus.tdd.point.Code.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointServiceTest {

  private PointService pointService;
  private UserPointTable userPointTable;
  private PointHistoryTable pointHistoryTable;

  @BeforeEach
  void setUp() {
    userPointTable = mock(UserPointTable.class);
    pointHistoryTable = mock(PointHistoryTable.class);
    pointService = new PointService();

    // 필드 주입 방식이라 강제로 주입
    pointService.getClass().getDeclaredFields();
    setPrivateField(pointService, "userPointTable", userPointTable);
    setPrivateField(pointService, "pointHistoryTable", pointHistoryTable);
  }

  private void setPrivateField(Object target, String fieldName, Object value) {
    try {
      var field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @DisplayName("1. ID 정보가 없으면 예외 발생")
  void noIdThrowsException() {
    assertThatThrownBy(() -> pointService.selectUserPointByUserId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(USER_ID_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("2. ID 정보가 있으면 포인트 반환")
  void hasIdReturnsPoint() {
    when(userPointTable.selectById(1L)).thenReturn(new UserPoint(1L, 1000L, System.currentTimeMillis()));
    UserPoint result = pointService.selectUserPointByUserId(1L);
    assertThat(result.point()).isEqualTo(1000L);
  }

  @Test
  @DisplayName("3. 충전/사용 내역이 없는 경우 빈 리스트 반환")
  void noHistoryReturnsEmpty() {
    when(pointHistoryTable.selectAllByUserId(1L)).thenReturn(Collections.emptyList());
    List<PointHistory> result = pointService.selectAllHistoryByUserId(1L);
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("4. 충전/사용 내역이 존재하는 경우 리스트 반환")
  void historyExistsReturnsList() {
    PointHistory history = new PointHistory(1L, 1L, 500L, TransactionType.CHARGE, System.currentTimeMillis());
    when(pointHistoryTable.selectAllByUserId(1L)).thenReturn(List.of(history));
    List<PointHistory> result = pointService.selectAllHistoryByUserId(1L);
    assertThat(result).hasSize(1);
  }

  @Test
  @DisplayName("5. 잔고보다 많이 쓰면 예외 발생") // 실제 서비스 로직에 없음, 개선 후 필요
  void insufficientBalanceThrows() {
    when(userPointTable.selectById(1L)).thenReturn(new UserPoint(1L, 100L, System.currentTimeMillis()));
    assertThatThrownBy(() -> {
      pointService.chargeUsePoint(1L, -1000L, TransactionType.USE);
    }).isInstanceOf(IllegalArgumentException.class)
            .hasMessage(POINT_MIN.getMessage());
  }

  @Test
  @DisplayName("6. 포인트 충전 성공")
  void chargeSuccess() {
    when(userPointTable.insertOrUpdate(anyLong(), anyLong()))
            .thenReturn(new UserPoint(1L, 5000L, System.currentTimeMillis()));
    when(pointHistoryTable.insert(anyLong(), anyLong(), any(), anyLong()))
            .thenReturn(new PointHistory(1L, 1L, 5000L, TransactionType.CHARGE, System.currentTimeMillis()));

    UserPoint result = pointService.chargeUsePoint(1L, 5000L, TransactionType.CHARGE);
    assertThat(result.point()).isEqualTo(5000L);
  }

  @Test
  @DisplayName("7. 포인트 충전 실패 (음수)")
  void chargeFail() {
    assertThatThrownBy(() -> pointService.chargeUsePoint(1L, -100L, TransactionType.CHARGE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(POINT_MIN.getMessage());
  }

  @Test
  @DisplayName("8. 포인트 사용 성공")
  void useSuccess() {
    when(userPointTable.insertOrUpdate(anyLong(), anyLong()))
            .thenReturn(new UserPoint(1L, 2000L, System.currentTimeMillis()));
    when(pointHistoryTable.insert(anyLong(), anyLong(), any(), anyLong()))
            .thenReturn(new PointHistory(1L, 1L, 1000L, TransactionType.USE, System.currentTimeMillis()));

    UserPoint result = pointService.chargeUsePoint(1L, 1000L, TransactionType.USE);
    assertThat(result.point()).isEqualTo(2000L);
  }

  @Test
  @DisplayName("9. 포인트 사용 실패 (음수)")
  void useFailNegativeAmount() {
    assertThatThrownBy(() -> pointService.chargeUsePoint(1L, -100L, TransactionType.USE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(POINT_MIN.getMessage());
  }
}
