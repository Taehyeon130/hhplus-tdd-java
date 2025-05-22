package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.hhplus.tdd.point.Code.POINT_MIN;
import static io.hhplus.tdd.point.Code.USER_ID_NOT_FOUND;


@Service
public class PointService {

  private  PointHistoryTable pointHistoryTable;
  private   UserPointTable userPointTable;
  private final Lock lock = new ReentrantLock();

  /*
   * 유저의 포인트 조회
   * */
  public UserPoint selectUserPointByUserId(Long userId) {
    //userId 유효성체크
    if(userId == null){
      throw new IllegalArgumentException(USER_ID_NOT_FOUND.getMessage());
    }

    UserPoint point = userPointTable.selectById(userId);
    if(point.point() == 0){
      return UserPoint.empty(userId);
    }else{
      return point;
    }
  }

  /*
  * 포인트 충전/이용 내역 조회
  * */
  public List<PointHistory> selectAllHistoryByUserId(Long userId) {
    //userId 유효성 체크
    if(userId == null){
      throw new IllegalArgumentException(USER_ID_NOT_FOUND.getMessage());
    }
    return pointHistoryTable.selectAllByUserId(userId);
  }

  /*
  * 포인트 충전, 사용
  * */
  public UserPoint chargeUsePoint(Long userId, Long amount, TransactionType type) {
    if(userId == null){
      throw new IllegalArgumentException(USER_ID_NOT_FOUND.getMessage());
    }
    lock.lock();
    try {
      if(amount < 0){
        throw new IllegalArgumentException(POINT_MIN.getMessage());
      }
      pointHistoryTable.insert(userId, amount, type,0);
      return userPointTable.insertOrUpdate(userId, amount);
    } finally {
      lock.unlock();
    }
  }
}
