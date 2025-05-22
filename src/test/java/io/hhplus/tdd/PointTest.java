package io.hhplus.tdd;

import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PointTest {

  @Test
  void emptyUserPointHasZeroPoint() {
    long userId = 123L;
    UserPoint empty = UserPoint.empty(userId);

    assertThat(empty.id()).isEqualTo(userId);
    assertThat(empty.point()).isZero();
    assertThat(empty.updateMillis()).isPositive();
  }

  @Test
  void userPointStoresValuesCorrectly() {
    long userId = 123L;
    long point = 1000L;
    long now = System.currentTimeMillis();

    UserPoint userPoint = new UserPoint(userId, point, now);

    assertThat(userPoint.id()).isEqualTo(userId);
    assertThat(userPoint.point()).isEqualTo(point);
    assertThat(userPoint.updateMillis()).isEqualTo(now);
  }
}
