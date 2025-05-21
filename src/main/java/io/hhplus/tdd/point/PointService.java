package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.hhplus.tdd.point.Code.USER_ID_NOT_FOUND;


@Service
public class PointService {

  private  PointHistoryTable pointHistoryTable;
  private   UserPointTable userPointTable;
  private final Lock lock = new ReentrantLock();

  /*
   * 유저의 포인트 조회
   * */
  UserPoint selectUserPointByUserId(Long userId) {
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
  List<PointHistory> selectAllHistoryByUserId(Long userId) {
    //userId 유효성 체크
    if(userId == null){
      throw new IllegalArgumentException(USER_ID_NOT_FOUND.getMessage());
    }
    return pointHistoryTable.selectAllByUserId(userId);
  }

  /*
  * 포인트 충전, 사용
  * */
  UserPoint chargeUsePoint(Long userId, Long amount, TransactionType type) {
    if(userId == null){
      throw new IllegalArgumentException(USER_ID_NOT_FOUND.getMessage());
    }
    lock.lock();
    try {
      if(amount < 0){
        throw new IllegalArgumentException("충전 또는 사용 포인트가 0보다 작습니다.");
      }
      pointHistoryTable.insert(userId, amount, type,0);
      return userPointTable.insertOrUpdate(userId, amount);
    } finally {
      lock.unlock();
    }
  }
}
